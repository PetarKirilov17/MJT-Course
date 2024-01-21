package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.exception.FailedRequestSendingException;
import bg.sofia.uni.fmi.mjt.exception.ForbiddenErrorException;
import bg.sofia.uni.fmi.mjt.queryparams.Health;
import bg.sofia.uni.fmi.mjt.queryparams.MealType;
import bg.sofia.uni.fmi.mjt.recipeparams.Recipe;

import java.util.List;
import java.util.Set;

public interface RecipeSearchAPI {
    List<Recipe> getRecipes(List<String> keyWords, Set<Health> healthSet, Set<MealType> mealTypesSet)
        throws FailedRequestSendingException, BadRequestException, ForbiddenErrorException;
}
