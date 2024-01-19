package uri;

import queryParams.Health;
import queryParams.MealType;

import java.net.URI;
import java.util.List;
import java.util.Set;

public class URIWrapper implements URIWrapperAPI {
    private static final String APP_ID = "&app_id=1006fbf6";
    private static final String APP_KEY = "&app_key=82b0fad1d7c56f8e8edc5d563f4caa25";
    private static final String SCHEMA = "https";
    private static final String HOST = "api.edamam.com";
    private static final String PATH = "/api/recipes/v2";
    private static final String TYPE = "type=public";

    private String queryTextPart;
    private String healthPart;
    private String mealTypePart;

    @Override
    public URI getUri() {

        URI result = null;
        try {
            //TODO : change with custom exception
            result =
                new URI(SCHEMA, HOST, PATH, TYPE + queryTextPart + APP_ID + APP_KEY + healthPart + mealTypePart, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static URIWrapperBuilder builder() {
        return new URIWrapperBuilder();
    }

    private URIWrapper(URIWrapperBuilder builder) {
        this.queryTextPart = builder.queryTextPart;
        this.healthPart = builder.healthPart;
        this.mealTypePart = builder.mealTypePart;
    }

    //Builder Class
    public static class URIWrapperBuilder {
        private String queryTextPart;
        private String healthPart;
        private String mealTypePart;

        private URIWrapperBuilder() {
        }

        public URIWrapperBuilder setKeyWords(List<String> keyWords) {
            this.queryTextPart = "&q=" + String.join(" ", keyWords);
            return this;
        }

        public URIWrapperBuilder setHealthTypes(Set<Health> healthTypes) {
            StringBuilder stringBuilder = new StringBuilder();
            for (var el : healthTypes) {
                stringBuilder.append("&health=");
                stringBuilder.append(el.toString());
            }
            this.healthPart = stringBuilder.toString();
            return this;
        }

        public URIWrapperBuilder setMealTypes(Set<MealType> mealTypes) {
            StringBuilder stringBuilder = new StringBuilder();
            for (var el : mealTypes) {
                stringBuilder.append("&mealType=");
                stringBuilder.append(el.toString());
            }
            this.mealTypePart = stringBuilder.toString();
            return this;
        }

        public URIWrapper build() {
            return new URIWrapper(this);
        }
    }
}
