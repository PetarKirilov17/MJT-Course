package recipesParams;

import java.util.List;

public record RecipeResponse(int from, int to, int count, LinkObject _links, List<HitObject> hits) {
}
