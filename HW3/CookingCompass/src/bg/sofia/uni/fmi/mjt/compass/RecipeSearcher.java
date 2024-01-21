package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.exception.FailedRequestSendingException;
import bg.sofia.uni.fmi.mjt.exception.ForbiddenErrorException;
import bg.sofia.uni.fmi.mjt.queryparams.Health;
import bg.sofia.uni.fmi.mjt.queryparams.MealType;
import bg.sofia.uni.fmi.mjt.recipeparams.HitObject;
import bg.sofia.uni.fmi.mjt.recipeparams.Recipe;
import bg.sofia.uni.fmi.mjt.recipeparams.RecipeResponse;
import bg.sofia.uni.fmi.mjt.uri.URIWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public RecipeSearcher(HttpClient httpClient) {
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
            validateResponse(responseStr);
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestSendingException(e.getMessage(), e.getCause());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        RecipeResponse response = gson.fromJson(responseStr.body(), RecipeResponse.class);
        List<Recipe> result = getRecipesFromResponse(response);
        if (response._links().next() != null) {
            try {
                URI nextPageUri = new URI(response._links().next().href());
                HttpRequest nextPageHttpRequest = HttpRequest.newBuilder().uri(nextPageUri).build();
                RecipeResponse secondPage =
                    gson.fromJson(client.send(nextPageHttpRequest, HttpResponse.BodyHandlers.ofString()).body(),
                        RecipeResponse.class);
                result.addAll(getRecipesFromResponse(secondPage));
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new FailedRequestSendingException(e.getMessage(), e.getCause());
            }
        }
        return result;
    }

    private URI createURI(List<String> keyWords, Set<Health> healthSet, Set<MealType> mealTypesSet) {
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
        throws BadRequestException, ForbiddenErrorException {
        if (responseStr.statusCode() == BAD_REQUEST_STATUS_CODE) {
            throw new BadRequestException("The API call returned bad request!");
        }

        if (responseStr.statusCode() == FORBIDDEN_ERROR_STATUS_CODE) {
            throw new ForbiddenErrorException("The API understands the request but cannot provide additional access!");
        }
    }

    private List<Recipe> getRecipesFromResponse(RecipeResponse response) {
        return response.hits().stream()
            .map(HitObject::recipe)
            .collect(Collectors.toList());
    }
}
