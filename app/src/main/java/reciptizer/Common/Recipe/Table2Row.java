package reciptizer.Common.Recipe;

public class Table2Row {
    public Integer id;
    public Integer idRecipe;
    public String ingredient;
    public Integer quantity;
    public String measure;

    public Table2Row(Integer id, Integer idRecipe,
                     String ingredient, Integer quantity,
                     String measure) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measure = measure;
    }
}