package recipesParams;

import java.util.List;

public record Recipe(String label, List<String> dietLabels, List<String> healthLabels, List<String> ingredientLines,
                     double totalWeight, List<String> cuisineType, List<String> mealType, List<String> dishType) {
}
