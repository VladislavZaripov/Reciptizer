package reciptizer.Common.Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.RecipeFilter;

public class JsonHelper {

    public String objectToJson (Object object){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(object);
    }

    public JSONObject recipeToJsonObject (Recipe recipe){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(objectToJson(recipe));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public Recipe jsonObjectToRecipe (JSONObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(),Recipe.class);
    }

    public RecipeFilter jsonObjectToRecipeFilter (JSONObject jsonObject){
        Gson gson= new Gson();
        return gson.fromJson(jsonObject.toString(),RecipeFilter.class);
    }
}