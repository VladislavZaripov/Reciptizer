package reciptizer.Common.Recipe;

public class Table3Row {
    public Integer id;
    public Integer idRecipe;
    public Integer number;
    public String text;
    public String imageFull;
    public String imageTitle;

    public Table3Row(Integer id, Integer idRecipe,
                     Integer number, String text,
                     String imageFull, String imageTitle) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.number = number;
        this.text = text;
        this.imageFull = imageFull;
        this.imageTitle = imageTitle;
    }
}