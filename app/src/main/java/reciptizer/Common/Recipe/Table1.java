package reciptizer.Common.Recipe;

public class Table1 {
    public Integer id;
    public String recipe;
    public String category;
    public String kitchen;
    public String preferences;
    public Integer time;
    public Integer portion;
    public String imageFull;
    public String imageTitle;

    public Table1(Integer id, String recipe,
                  String category, String kitchen,
                  String preferences, Integer time,
                  Integer portion, String imageFull,
                  String imageTitle) {
        this.id = id;
        this.recipe = recipe;
        this.category = category;
        this.kitchen = kitchen;
        this.preferences = preferences;
        this.time = time;
        this.portion = portion;
        this.imageFull = imageFull;
        this.imageTitle = imageTitle;
    }
}