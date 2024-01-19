import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import compass.RecipeSearchAPI;
import compass.RecipeSearcher;
import exception.BadRequestException;
import exception.FailedRequestSendingException;
import exception.ForbiddenErrorException;
import queryParams.Health;
import queryParams.MealType;
import recipesParams.RecipeResponse;
import uri.URIWrapper;

public class Main {

    public static String getRecipeString() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        String schema = "https";
        String host = "api.edamam.com";
        String path = "/api/recipes/v2";
        String appId = "1006fbf6";
        String appKey = "82b0fad1d7c56f8e8edc5d563f4caa25";
        String typeQuery = "type=public&q=beef mushroom cream";
        String healthQuery = "&health=alcohol-free&health=dairy-free&health=pork-free";
        String mealTypeQuery = "&mealType=Dinner&mealType=Lunch";

        // Creating URI object
        URI uri = new URI(schema, host, path, typeQuery + "&app_id=" + appId +
            "&app_key=" + appKey + healthQuery + mealTypeQuery, null);
//        URI urlURI = new URI("https://api.edamam.com/api/recipes/v2?type=public&q=beef%20mushroom%20cream&app_id=1006fbf6&app_key=82b0fad1d7c56f8e8edc5d563f4caa25&health=alcohol-free&health=dairy-free&health=pork-free&mealType=Dinner&mealType=Lunch");
//        System.out.println(urlURI.getScheme());
//        System.out.println(urlURI.getHost());
//        System.out.println(urlURI.getPath());
//        System.out.println(urlURI.getQuery());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
         return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void main(String[] args)
        throws  FailedRequestSendingException,
        ForbiddenErrorException, BadRequestException {
        List<String> keyWords = List.of("beef", "mushroom", "cream");
        Set<Health> healthSet = Set.of(Health.ALCOHOL_FREE, Health.DAIRY_FREE, Health.PORK_FREE);
        Set<MealType> mealTypes = Set.of(MealType.DINNER, MealType.LUNCH);
        HttpClient client = HttpClient.newBuilder().build();
        RecipeSearchAPI searcher = new RecipeSearcher(client);

        System.out.println(searcher.getRecipes(keyWords, healthSet, mealTypes));

////        URIWrapper.URIWrapperBuilder builder = URIWrapper.builder();
////        builder = builder.setKeyWords(List.of("ala", "bala"));
////        URI uriii = builder.build().getUri();
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        RecipeResponse response = gson.fromJson(getRecipeString(), RecipeResponse.class);
//
////        System.out.println(response);
//        String responseJsonString = gson.toJson(response.hits().getFirst().recipe());
//        System.out.println(responseJsonString);
//
//        if(response._links().next() != null) {
//            HttpClient nextPageClient = HttpClient.newBuilder().build();
//            URI nextPageUri = new URI(response._links().next().href());
//            HttpRequest nextPageHttpRequest = HttpRequest.newBuilder().uri(nextPageUri).build();
//
//            RecipeResponse secondPage =
//                gson.fromJson(nextPageClient.send(nextPageHttpRequest, HttpResponse.BodyHandlers.ofString()).body(),
//                    RecipeResponse.class);
////            System.out.println(secondPage);
//            String secondPageJsonString = gson.toJson(secondPage);
//            System.out.println(secondPageJsonString);
//        }
    }
}
