package compass;

import exception.BadRequestException;
import exception.FailedRequestSendingException;
import exception.ForbiddenErrorException;
import queryParams.Health;
import queryParams.MealType;
import recipesParams.Recipe;

import java.util.List;
import java.util.Set;

public interface RecipeSearchAPI {
    List<Recipe> getRecipes(List<String> keyWords, Set<Health> healthSet, Set<MealType> mealTypesSet)
        throws FailedRequestSendingException, BadRequestException, ForbiddenErrorException;
}
