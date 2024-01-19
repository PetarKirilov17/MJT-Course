package compass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.BadRequestException;
import exception.FailedRequestSendingException;
import exception.ForbiddenErrorException;
import queryParams.Health;
import queryParams.MealType;
import recipesParams.HitObject;
import recipesParams.Recipe;
import recipesParams.RecipeResponse;
import uri.URIWrapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeSearcher implements RecipeSearchAPI {
    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int FORBIDDEN_ERROR_STATUS_CODE = 403;

    private HttpClient client;

    public RecipeSearcher(HttpClient httpClient){
        this.client = httpClient;
    }

    @Override
    public List<Recipe> getRecipes(List<String> keyWords, Set<Health> healthSet, Set<MealType> mealTypesSet)
        throws FailedRequestSendingException, BadRequestException, ForbiddenErrorException {
        URI uri = createURI(keyWords, healthSet, mealTypesSet);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        HttpResponse<String> responseStr;
        try {
            responseStr = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new FailedRequestSendingException(e.getMessage(), e.getCause());
        }
        validateResponse(responseStr);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        RecipeResponse response = gson.fromJson(responseStr.body(), RecipeResponse.class);
        List<Recipe> result = response.hits().stream()
            .map(HitObject::recipe)
            .collect(Collectors.toList());

        return result;
    }

    private URI createURI(List<String> keyWords, Set<Health> healthSet, Set<MealType> mealTypesSet){
        URIWrapper.URIWrapperBuilder builder = URIWrapper.builder();
        if (keyWords != null && keyWords.size() > 0) {
            builder.setKeyWords(keyWords);
        }
        if (mealTypesSet != null && mealTypesSet.size() > 0) {
            builder.setMealTypes(mealTypesSet);
        }
        if (healthSet != null && healthSet.size() > 0) {
            builder.setHealthTypes(healthSet);
        }
        return builder.build().getUri();
    }

    private void validateResponse(HttpResponse<String> responseStr)
        throws FailedRequestSendingException, BadRequestException, ForbiddenErrorException {
        if (responseStr == null) {
            throw new FailedRequestSendingException("No valid response!");
        }

        if (responseStr.statusCode() == BAD_REQUEST_STATUS_CODE) {
            throw new BadRequestException("The API call returned bad request!");
        }

        if(responseStr.statusCode() == FORBIDDEN_ERROR_STATUS_CODE){
            throw new ForbiddenErrorException("The API understands the request but cannot provide additional access!");
        }
    }
}
