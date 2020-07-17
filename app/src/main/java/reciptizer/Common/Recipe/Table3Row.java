package reciptizer.Common.Recipe;

public class Table3Row {
    public Integer TABLE3_COLUMN_ID;
    public Integer TABLE3_COLUMN_ID_RECIPE;
    public Integer TABLE3_COLUMN_NUMBER;
    public String TABLE3_COLUMN_TEXT;
    public String TABLE3_COLUMN_IMG_FULL;
    public String TABLE3_COLUMN_IMG_TITLE;

    public Table3Row(Integer TABLE3_COLUMN_ID, Integer TABLE3_COLUMN_ID_RECIPE,
                     Integer TABLE3_COLUMN_NUMBER, String TABLE3_COLUMN_TEXT,
                     String TABLE3_COLUMN_IMG_FULL, String TABLE3_COLUMN_IMG_TITLE) {
        this.TABLE3_COLUMN_ID = TABLE3_COLUMN_ID;
        this.TABLE3_COLUMN_ID_RECIPE = TABLE3_COLUMN_ID_RECIPE;
        this.TABLE3_COLUMN_NUMBER = TABLE3_COLUMN_NUMBER;
        this.TABLE3_COLUMN_TEXT = TABLE3_COLUMN_TEXT;
        this.TABLE3_COLUMN_IMG_FULL = TABLE3_COLUMN_IMG_FULL;
        this.TABLE3_COLUMN_IMG_TITLE = TABLE3_COLUMN_IMG_TITLE;
    }
}