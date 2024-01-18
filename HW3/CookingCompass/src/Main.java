import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import recipesParams.RecipeResponse;

public class Main {

    public static String getRecipeString() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        String schema = "https";
        String host = "api.edamam.com";
        String path = "/api/recipes/v2";
        String appId = "1006fbf6";
        String appKey = "82b0fad1d7c56f8e8edc5d563f4caa25";

        // Creating URI object
        URI uri = new URI(schema, host, path, "type=public&q=chicken" + "&app_id=" + appId +
            "&app_key=" + appKey, null);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
         return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        RecipeResponse response = gson.fromJson(getRecipeString(), RecipeResponse.class);

        System.out.println(response);

        HttpClient nextPageClient = HttpClient.newBuilder().build();
        URI nextPageUri = new URI(response._links().next().href());
        HttpRequest nextPageHttpRequest = HttpRequest.newBuilder().uri(nextPageUri).build();

        RecipeResponse secondPage = gson.fromJson(nextPageClient.send(nextPageHttpRequest, HttpResponse.BodyHandlers.ofString()).body(), RecipeResponse.class);
        System.out.println(secondPage);
    }
}
