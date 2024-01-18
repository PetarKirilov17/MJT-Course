package recipesParams;

import java.util.List;

public record RecipeResponse(LinkObject _links, List<HitObject> hits) {
}
