package reciptizer.Local;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.reciptizer.R;
import reciptizer.ActivityMain;
import reciptizer.Common.Helpers.PhotoHelper;
import reciptizer.Common.Recipe.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SQL {

    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static DBHelper DB_HELPER;
    private static SQLiteDatabase DB;
    public static Cursor CURSOR;
    public static ContentValues CV;
    public Activity activity;

    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "DataBaseRecipe v.7";
    public static final String TABLE1_NAME = "Recipe";
    public static final String TABLE1_COLUMN_ID = "_id";
    public static final String TABLE1_COLUMN_RECIPE = "Recipe";
    public static final String TABLE1_COLUMN_CATEGORY = "Category";
    public static final String TABLE1_COLUMN_KITCHEN = "Kitchen";
    public static final String TABLE1_COLUMN_PREFERENCES = "Preferences";
    public static final String TABLE1_COLUMN_TIME = "Time";
    public static final String TABLE1_COLUMN_PORTION = "Portion";
    public static final String TABLE1_COLUMN_IMG_FULL = "MainImgFull";
    public static final String TABLE1_COLUMN_IMG_TITLE = "MainImgTitle";

    public static final String TABLE2_NAME = "Ingredients";
    public static final String TABLE2_COLUMN_ID = "_id";
    public static final String TABLE2_COLUMN_ID_RECIPE = "Ingredients_id";
    public static final String TABLE2_COLUMN_INGREDIENTS = "Ingredients";
    public static final String TABLE2_COLUMN_QUANTITY = "Quantity";
    public static final String TABLE2_COLUMN_MEASURE = "Measure";

    public static final String TABLE3_NAME = "Steps";
    public static final String TABLE3_COLUMN_ID = "_id";
    public static final String TABLE3_COLUMN_ID_RECIPE = "Steps_id";
    public static final String TABLE3_COLUMN_NUMBER = "Number";
    public static final String TABLE3_COLUMN_TEXT = "Text";
    public static final String TABLE3_COLUMN_IMG_FULL = "ImgFull";
    public static final String TABLE3_COLUMN_IMG_TITLE = "ImgTitle";

    private static final String TABLE1_CREATE = "create table " + TABLE1_NAME +
            "(" +
            TABLE1_COLUMN_ID + " integer primary key autoincrement, " +
            TABLE1_COLUMN_RECIPE + " text, " +
            TABLE1_COLUMN_CATEGORY + " text, " +
            TABLE1_COLUMN_KITCHEN + " text, " +
            TABLE1_COLUMN_PREFERENCES + " text, " +
            TABLE1_COLUMN_TIME + " integer, " +
            TABLE1_COLUMN_PORTION + " integer, " +
            TABLE1_COLUMN_IMG_FULL + " text," +
            TABLE1_COLUMN_IMG_TITLE + " text" +
            ");";

    private static final String TABLE2_CREATE = "create table " + TABLE2_NAME +
            "(" +
            TABLE2_COLUMN_ID + " integer primary key autoincrement, " +
            TABLE2_COLUMN_ID_RECIPE + " integer, " +
            TABLE2_COLUMN_INGREDIENTS + " text, " +
            TABLE2_COLUMN_QUANTITY + " integer, " +
            TABLE2_COLUMN_MEASURE + " text" +
            ");";

    private static final String TABLE3_CREATE = "create table " + TABLE3_NAME +
            "(" +
            TABLE3_COLUMN_ID + " integer primary key autoincrement, " +
            TABLE3_COLUMN_ID_RECIPE + " integer, " +
            TABLE3_COLUMN_NUMBER + " integer, " +
            TABLE3_COLUMN_TEXT + " text, " +
            TABLE3_COLUMN_IMG_FULL + " text, " +
            TABLE3_COLUMN_IMG_TITLE + " text" +
            ");";

    public SQL(Context context) {
        this.context = context;
    }

    public void open() {
        DB_HELPER = new DBHelper(context, DB_NAME, null, DB_VERSION);
        DB = DB_HELPER.getWritableDatabase();
        Log.d(ActivityMain.LOG_TAG,"DB_open");
    }

    public void close() {
        if (DB_HELPER != null) DB_HELPER.close();
        Log.d(ActivityMain.LOG_TAG,"DB_close");
    }

    public static RecipeFilter getRecipeFilterResult (String recipeFilterName, String category, String kitchen, String preferences) {
        Log.d(ActivityMain.LOG_TAG,"recipe = " + recipeFilterName + "; category = " + category + "; kitchen = " + kitchen + "; preferences = " + preferences);

        String selection = null;
        String [] selectionArgs = null;
        ArrayList <String> strings = new ArrayList<>();

        int sumFilters = 0;

        if (!recipeFilterName.equals("") && !recipeFilterName.equals("Название рецепта")){
            selection = TABLE1_COLUMN_RECIPE + " like ?";
            strings.add("%"+recipeFilterName+"%");
            sumFilters++;
        }

        if (!category.equals("Все")){
            if (sumFilters>0) {
                selection = selection.concat(" AND " + TABLE1_COLUMN_CATEGORY + " = ?");
            }
            else {
                selection = TABLE1_COLUMN_CATEGORY + " = ?";
            }
            strings.add(category);
            sumFilters++;
        }

        if (!kitchen.equals("Все")){
            if (sumFilters>0) {
                selection = selection.concat(" AND " + TABLE1_COLUMN_KITCHEN + " = ?");
            }
            else {
                selection = TABLE1_COLUMN_KITCHEN + " = ?";
            }
            strings.add(kitchen);
            sumFilters++;
        }

        if (!preferences.equals("Все")){
            if (sumFilters>0) {
                selection = selection.concat(" AND " + TABLE1_COLUMN_PREFERENCES + " = ?");
            }
            else {
                selection = TABLE1_COLUMN_PREFERENCES + " = ?";
            }
            strings.add(preferences);
            sumFilters++;
        }

        if (sumFilters > 0){
            selectionArgs = new String[sumFilters];
            selectionArgs = strings.toArray(selectionArgs);
        }

        List <Table1> table1 = new ArrayList<>();

        CURSOR = DB.query(TABLE1_NAME, null, selection, selectionArgs, null, null, TABLE1_COLUMN_ID + " DESC");
        if (CURSOR.moveToFirst()) {
            do {
                int id = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_ID));
                String recipe = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_RECIPE));
                String img_title = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_IMG_TITLE));
                int time = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_TIME));
                table1.add(new Table1(id, recipe,null,null,null,time,null,null, img_title));
            } while (CURSOR.moveToNext());
        }
        CURSOR.close();

        return new RecipeFilter(table1);
    }

    public static Cursor getTable1Pos (int id){
        return CURSOR = DB.query(TABLE1_NAME, null, TABLE1_COLUMN_ID + " = ?", new String[] {Integer.toString(id)}, null, null, null);
    }

    public static Cursor getTable2Pos (int id){
        return CURSOR = DB.query(TABLE2_NAME, null, TABLE2_COLUMN_ID_RECIPE + " = ?", new String[] {Integer.toString(id)}, null, null, null);
    }

    public static Cursor getTable3Pos (int id){
        return CURSOR = DB.query(TABLE3_NAME, null, TABLE3_COLUMN_ID_RECIPE + " = ?", new String[] {Integer.toString(id)}, null, null, null);
    }

    public static Recipe getRecipe (int idRecipe) {
        Table1 table1 = getRecipeTable1(idRecipe);
        List<Table2Row> table2Rows = getRecipeTable2(idRecipe);
        List<Table3Row> table3Rows = getRecipeTable3(idRecipe);
        return new Recipe(table1,table2Rows,table3Rows);
    }

    private static Table1 getRecipeTable1 (int idRecipe) {
        CURSOR = DB.query(TABLE1_NAME, null, TABLE1_COLUMN_ID + " = ?", new String[] {Integer.toString(idRecipe)}, null, null, null);

        CURSOR.moveToFirst();
        String recipe = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_RECIPE));
        String category = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_CATEGORY));
        String kitchen = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_KITCHEN));
        String preferences = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_PREFERENCES));
        Integer time = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_TIME));
        Integer portion = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_PORTION));
        String img_full = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_IMG_FULL));
        String img_title = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_IMG_TITLE));
        CURSOR.close();

        return new Table1(idRecipe,recipe,category,kitchen,preferences,time,portion,img_full,img_title);
    }

    private static List<Table2Row> getRecipeTable2 (int idRecipe) {
        CURSOR = DB.query(TABLE2_NAME, null, TABLE2_COLUMN_ID_RECIPE + " = ?", new String[] {Integer.toString(idRecipe)}, null, null, null);
        List<Table2Row> table2Rows = new ArrayList<>();

        CURSOR.moveToFirst();
        if (CURSOR.moveToFirst()) {
            do {
                Integer id = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE2_COLUMN_ID));
                String ingredients = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE2_COLUMN_INGREDIENTS));
                Integer quantity = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE2_COLUMN_QUANTITY));
                String measure = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE2_COLUMN_MEASURE));
                table2Rows.add(new Table2Row(id, idRecipe, ingredients, quantity, measure));
            } while (CURSOR.moveToNext());
        }
        CURSOR.close();

        return table2Rows;
    }

    private static List<Table3Row> getRecipeTable3 (int idRecipe) {
        CURSOR = DB.query(TABLE3_NAME, null, TABLE3_COLUMN_ID_RECIPE + " = ?", new String[] {Integer.toString(idRecipe)}, null, null, null);
        List<Table3Row> table3Rows = new ArrayList<>();

        CURSOR.moveToFirst();
        if (CURSOR.moveToFirst()) {
            do {
                Integer id = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_ID));
                Integer number = CURSOR.getInt(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_NUMBER));
                String text = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_TEXT));
                String img_full = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_FULL));
                String img_title = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_TITLE));
                table3Rows.add(new Table3Row(id, idRecipe,number,text,img_full,img_title));

            } while (CURSOR.moveToNext());
        }
        CURSOR.close();

        return table3Rows;
    }


    public static void addRecipeFromServer (final Recipe recipe, Activity activity){
        final PhotoHelper ph = new PhotoHelper(activity);

        Log.d(ActivityMain.LOG_TAG, "addRecipeFromServer: recipe.table1.TABLE1_COLUMN_IMG_TITLE = " + recipe.table1.imageTitle);

        if (recipe.table1.imageTitle !=null && !recipe.table1.imageTitle.equals("null"))
        {
            Bitmap bitmap_png = BitmapTempSingleton.getInstance().getTempBitmap().get(recipe.table1.imageTitle);
            File file = ph.createFile("png");
            recipe.table1.imageTitle = file.toString();
            ph.saveBitmapToFile(bitmap_png, file);
        }

        Log.d(ActivityMain.LOG_TAG, "addRecipeFromServer: recipe.table1.TABLE1_COLUMN_IMG_TITLE = " + recipe.table1.imageFull);
        if (recipe.table1.imageFull !=null && !recipe.table1.imageFull.equals("null"))
        {
            Bitmap bitmap_png = BitmapTempSingleton.getInstance().getTempBitmap().get(recipe.table1.imageFull);
            File file = ph.createFile("png");
            recipe.table1.imageFull = file.toString();
            ph.saveBitmapToFile(bitmap_png, file);
        }

        for (final Table3Row table3Row : recipe.rowsTable3) {

            Log.d(ActivityMain.LOG_TAG, "addRecipeFromServer: table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.imageTitle);
            if (table3Row.imageTitle !=null && !table3Row.imageTitle.equals("null"))
            {
                Bitmap bitmap_png = BitmapTempSingleton.getInstance().getTempBitmap().get(table3Row.imageTitle);
                File file = ph.createFile("png");
                table3Row.imageTitle = file.toString();
                ph.saveBitmapToFile(bitmap_png, file);
            }

            Log.d(ActivityMain.LOG_TAG, "addRecipeFromServer: table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.imageFull);
            if (table3Row.imageFull !=null&&!table3Row.imageFull.equals("null"))
            {
                Bitmap bitmap_png = BitmapTempSingleton.getInstance().getTempBitmap().get(table3Row.imageFull);
                File file = ph.createFile("png");
                table3Row.imageFull = file.toString();
                ph.saveBitmapToFile(bitmap_png, file);
            }
        }
            addRecipe(recipe);
    }

    public static void addRecipe (Recipe recipe){
        int id_recipe = addPositionInTable1(recipe);
        addPositionInTable2(recipe,id_recipe);
        addPositionInTable3(recipe,id_recipe);
    }

    private static int addPositionInTable1 (Recipe recipe){
        CV = new ContentValues();
        CV.put(TABLE1_COLUMN_RECIPE, recipe.table1.recipe);
        CV.put(TABLE1_COLUMN_CATEGORY, recipe.table1.category);
        CV.put(TABLE1_COLUMN_KITCHEN, recipe.table1.kitchen);
        CV.put(TABLE1_COLUMN_PREFERENCES, recipe.table1.preferences);
        CV.put(TABLE1_COLUMN_TIME, recipe.table1.time);
        CV.put(TABLE1_COLUMN_PORTION, recipe.table1.portion);
        CV.put(TABLE1_COLUMN_IMG_FULL, recipe.table1.imageFull);
        CV.put(TABLE1_COLUMN_IMG_TITLE, recipe.table1.imageTitle);
        int id = (int) DB.insert(TABLE1_NAME, null, CV);
        CV.clear();
        return id;
    }
    private static void addPositionInTable2 (Recipe recipe, int id_recipe) {
        for(Table2Row table2Row : recipe.rowsTable2) {
            CV = new ContentValues();
            CV.put(TABLE2_COLUMN_ID_RECIPE, id_recipe);
            CV.put(TABLE2_COLUMN_INGREDIENTS, table2Row.ingredient);
            CV.put(TABLE2_COLUMN_QUANTITY, table2Row.quantity);
            CV.put(TABLE2_COLUMN_MEASURE, table2Row.measure);
            DB.insert(TABLE2_NAME, null, CV);
            CV.clear();
        }
    }
    private static void addPositionInTable3 (Recipe recipe, int id_recipe) {
        for(Table3Row table3Row : recipe.rowsTable3) {
            CV = new ContentValues();
            CV.put(TABLE3_COLUMN_ID_RECIPE, id_recipe);
            CV.put(TABLE3_COLUMN_NUMBER, table3Row.number);
            CV.put(TABLE3_COLUMN_TEXT, table3Row.text);
            CV.put(TABLE3_COLUMN_IMG_FULL, table3Row.imageFull);
            CV.put(TABLE3_COLUMN_IMG_TITLE, table3Row.imageTitle);
            DB.insert(TABLE3_NAME, null, CV);
            CV.clear();
        }
    }


    public static int addPositionInTable1 (String recipe, String category, String kitchen, String preferences, Integer time, Integer portion, String img_jpeg, String img_png){
        CV = new ContentValues();
        CV.put(TABLE1_COLUMN_RECIPE, recipe);
        CV.put(TABLE1_COLUMN_CATEGORY, category);
        CV.put(TABLE1_COLUMN_KITCHEN, kitchen);
        CV.put(TABLE1_COLUMN_PREFERENCES, preferences);
        CV.put(TABLE1_COLUMN_TIME, time);
        CV.put(TABLE1_COLUMN_PORTION, portion);
        CV.put(TABLE1_COLUMN_IMG_FULL, img_jpeg);
        CV.put(TABLE1_COLUMN_IMG_TITLE, img_png);
        int id = (int) DB.insert(TABLE1_NAME, null, CV);
        CV.clear();
        return id;
    }

    public static void addPositionInTable2 (Integer id_recipe, String ingredients, Integer quantity, String measure) {
        CV = new ContentValues();
        CV.put(TABLE2_COLUMN_ID_RECIPE, id_recipe);
        CV.put(TABLE2_COLUMN_INGREDIENTS, ingredients);
        CV.put(TABLE2_COLUMN_QUANTITY, quantity);
        CV.put(TABLE2_COLUMN_MEASURE, measure);
        DB.insert(TABLE2_NAME, null, CV);
        CV.clear();
    }

    public static void addPositionInTable3 (Integer id_recipe, Integer number, String text, String img_jpeg, String img_png) {
        CV = new ContentValues();
        CV.put(TABLE3_COLUMN_ID_RECIPE, id_recipe);
        CV.put(TABLE3_COLUMN_NUMBER, number);
        CV.put(TABLE3_COLUMN_TEXT, text);
        CV.put(TABLE3_COLUMN_IMG_FULL, img_jpeg);
        CV.put(TABLE3_COLUMN_IMG_TITLE, img_png);
        DB.insert(TABLE3_NAME, null, CV);
        CV.clear();
    }


    public static void updatePositionInTable1 (Integer id, String recipe, String category, String kitchen, String preferences, Integer time, Integer portion, String img_jpeg, String img_png){
        CV = new ContentValues();
        CV.put(TABLE1_COLUMN_RECIPE, recipe);
        CV.put(TABLE1_COLUMN_CATEGORY, category);
        CV.put(TABLE1_COLUMN_KITCHEN, kitchen);
        CV.put(TABLE1_COLUMN_PREFERENCES, preferences);
        CV.put(TABLE1_COLUMN_TIME, time);
        CV.put(TABLE1_COLUMN_PORTION, portion);
        CV.put(TABLE1_COLUMN_IMG_FULL, img_jpeg);
        CV.put(TABLE1_COLUMN_IMG_TITLE, img_png);
        DB.update(TABLE1_NAME, CV,TABLE1_COLUMN_ID + "= ?", new String[] {id.toString()});
        CV.clear();
    }

    public static void updatePositionInTable2 (Integer id, Integer id_recipe, String ingredients, Integer quantity, String measure) {
        CV = new ContentValues();
        CV.put(TABLE2_COLUMN_ID_RECIPE, id_recipe);
        CV.put(TABLE2_COLUMN_INGREDIENTS, ingredients);
        CV.put(TABLE2_COLUMN_QUANTITY, quantity);
        CV.put(TABLE2_COLUMN_MEASURE, measure);
        DB.update(TABLE2_NAME, CV,TABLE2_COLUMN_ID + "= ?", new String[] {id.toString()});
        CV.clear();
    }

    public static void updatePositionInTable3 (Integer id,  Integer id_recipe, Integer number, String text, String img_jpeg, String img_png) {
        CV = new ContentValues();
        CV.put(TABLE3_COLUMN_ID_RECIPE, id_recipe);
        CV.put(TABLE3_COLUMN_NUMBER, number);
        CV.put(TABLE3_COLUMN_TEXT, text);
        CV.put(TABLE3_COLUMN_IMG_FULL, img_jpeg);
        CV.put(TABLE3_COLUMN_IMG_TITLE, img_png);
        DB.update(TABLE3_NAME, CV,TABLE3_COLUMN_ID + "= ?", new String[] {id.toString()});
        CV.clear();
    }

    public static void deleteRowTable2 (int id){
        int delCount;
        delCount = DB.delete(TABLE2_NAME, TABLE2_COLUMN_ID + " = " + id, null);
        Log.d(ActivityMain.LOG_TAG, "deleteRowTable2: id = " +id + ", count = " + delCount);
    }

    public static void deleteRowTable3 (int id){
        CURSOR = DB.query(TABLE3_NAME, null, TABLE3_COLUMN_ID + " = ?", new String[] {Integer.toString(id)}, null, null, null);
        String uri;
        if (CURSOR.moveToFirst()) {
            do {
                uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_FULL));
                if (uri!=null) {
                    boolean result;
                    Uri i = Uri.parse(uri);
                    File file = new File(Objects.requireNonNull(i.getPath()));
                    result = file.delete();
                    Log.d(ActivityMain.LOG_TAG, "TABLE3_COLUMN_IMG_JPEG File delete: " + result);
                }
                uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_TITLE));
                if (uri!=null) {
                    boolean result;
                    Uri i = Uri.parse(uri);
                    File file = new File(Objects.requireNonNull(i.getPath()));
                    result = file.delete();
                    Log.d(ActivityMain.LOG_TAG, "TABLE3_COLUMN_IMG_PNG File delete: " + result);
                }
            } while (CURSOR.moveToNext());
        }

        CURSOR.close();

        int delCount;
        delCount = DB.delete(TABLE3_NAME, TABLE3_COLUMN_ID + " = " + id, null);
        Log.d(ActivityMain.LOG_TAG, "deleteRowTable2: id = " +id + ", count = " + delCount);
    }

    public static void deletePosition (int id){
        String uri;

        CURSOR = DB.query(TABLE1_NAME, null, TABLE1_COLUMN_ID + " = ?", new String[] {Integer.toString(id)}, null, null, null);
        if (CURSOR.moveToFirst()) {
            uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_IMG_FULL));
            if (uri != null) {
                boolean result;
                Uri i = Uri.parse(uri);
                File file = new File(Objects.requireNonNull(i.getPath()));
                result = file.delete();
                Log.d(ActivityMain.LOG_TAG, "TABLE1_COLUMN_IMG_JPEG File delete: " + result);
            }
            uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE1_COLUMN_IMG_TITLE));
            if (uri != null) {
                boolean result;
                Uri i = Uri.parse(uri);
                File file = new File(Objects.requireNonNull(i.getPath()));
                result = file.delete();
                Log.d(ActivityMain.LOG_TAG, "TABLE1_COLUMN_IMG_PNG File delete: " + result);
            }
        }
        CURSOR.close();

        CURSOR = getTable3Pos(id);
        if (CURSOR.moveToFirst()) {
            do {
                uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_FULL));
                if (uri!=null) {
                    boolean result;
                    Uri i = Uri.parse(uri);
                    File file = new File(Objects.requireNonNull(i.getPath()));
                    result = file.delete();
                    Log.d(ActivityMain.LOG_TAG, "TABLE3_COLUMN_IMG_JPEG File delete: " + result);
                }
                uri = CURSOR.getString(CURSOR.getColumnIndex(SQL.TABLE3_COLUMN_IMG_TITLE));
                if (uri!=null) {
                    boolean result;
                    Uri i = Uri.parse(uri);
                    File file = new File(Objects.requireNonNull(i.getPath()));
                    result = file.delete();
                    Log.d(ActivityMain.LOG_TAG, "TABLE3_COLUMN_IMG_PNG File delete: " + result);
                }
            } while (CURSOR.moveToNext());
        }
        CURSOR.close();

        int delCount;
        delCount = DB.delete(TABLE1_NAME, TABLE1_COLUMN_ID + " = " + id, null);
        Log.d(ActivityMain.LOG_TAG, "deletePosition Table1: id = " +id + ", count = " + delCount);
        delCount = DB.delete(TABLE2_NAME, TABLE2_COLUMN_ID_RECIPE + " = " + id, null);
        Log.d(ActivityMain.LOG_TAG, "deletePosition Table2: id_recipe = " +id + ", count = " + delCount);
        delCount = DB.delete(TABLE3_NAME, TABLE3_COLUMN_ID_RECIPE + " = " + id, null);
        Log.d(ActivityMain.LOG_TAG, "deletePosition Table3: id_recipe = " +id + ", count = " + delCount);
    }

    class DBHelper extends SQLiteOpenHelper {


        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            activity = (Activity) context;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE1_CREATE);
            Log.d(ActivityMain.LOG_TAG, "TABLE1_CREATE");
            db.execSQL(TABLE2_CREATE);
            Log.d(ActivityMain.LOG_TAG, "TABLE2_CREATE");
            db.execSQL(TABLE3_CREATE);
            Log.d(ActivityMain.LOG_TAG, "TABLE3_CREATE");

            SampleRecipe sampleRecipe = new SampleRecipe();
            sampleRecipe.execute();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SampleRecipe extends AsyncTask <Object,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            CV = new ContentValues();
            int i;
            publishProgress(1);
            {
                i = saveTable1("Салат с тунцом","Салаты","Европейская","Рыбные блюда",15,2, R.drawable.x_recipe_1_title);
                saveTable2(i, "Консервированный тунец",1,"банка");
                saveTable2(i, "Салат айсберг",1,"шт");
                saveTable2(i, "Помидоры черри",10,"шт");
                saveTable2(i, "Перепелиные яйца",6,"шт");
                saveTable2(i, "Оливки",10,"шт");
                saveTable2(i, "Каперсы",10,"шт");
                saveTable2(i, "Соус Песто",1,"по вк");
                saveTable2(i, "Оливковое масло",2,"ст.л");
                saveTable2(i, "Дижонская горчица",1,"ч.л");
                saveTable2(i, "Мёд",1,"ч.л");
                saveTable3(i,1,"Поставьте варить перепелиные яйца в холодную воду, после закипания варите 5 минут.",null);
                saveTable3(i,2,"После варки остудите яйца в холодной воде, почистите и порежьте пополам.",null);
                saveTable3(i,3,"Нарвите листья салата Айсберг и сложите по центру тарелки горкой, сверху выложите половину банки тунца.",null);
                saveTable3(i,4,"Украсьте салат яйцами, половинками черри, оливками и каперсами.",R.drawable.x_recipe_1_step_4);
                saveTable3(i,5,"Выложите соус Песто сверху на тунец.",null);
                saveTable3(i,6,"Смешайте оливковое масло, горчицу и мед и полейте полученным соусом овощи.",null);
            }
            publishProgress(2);
            {
                i = saveTable1("Чиабатта с оливками и вялеными томатами","Выпечка","Европейская","Все",60,6,R.drawable.x_recipe_4_title);
                saveTable2(i,"Мука",4,"ст");
                saveTable2(i,"Соль",2,"ч.л");
                saveTable2(i,"Сахар",2,"ч.л");
                saveTable2(i,"Вода",2,"ст");
                saveTable2(i,"Сухие дрожжи",1,"ч.л");
                saveTable2(i,"Оливки",1,"по вк");
                saveTable2(i,"Вяленые томаты",1,"по вк");
                saveTable3(i,1,"Смешиваем сухие ингредиенты, добавляем воду, замешиваем тесто.",null);
                saveTable3(i,2,"Полученую опару ставим в теплое место на 12-18 часов.",null);
                saveTable3(i,3,"Тесто выкладываем в форму и смешиваем с нарезанными кубиками оливки и томаты, даем тесту постоять в форме еще 2 часа.",null);
                saveTable3(i,4,"Духовку разогреваем до 220 градусов, перед тем как поставить тесто в печь снижаем градус до 200.",null);
                saveTable3(i,5,"Выпекаем 45 - 60 минут, в зависимости от вашей духовки.",null);
                saveTable3(i,6,"Дайте хлебу постоять под полотенцем минут 15, тогда корочка будет хрустящей, но не грубой, после храните хлеб в пакете, чтобы избежать зачерствления.",R.drawable.x_recipe_4_step_6);
            }
            publishProgress(3);
            {
                i = saveTable1("Пирог со шпинатом и фетой","Выпечка","Европейская","Все",35,4,R.drawable.x_recipe_3_title);
                saveTable2(i, "Кедровые орешки",100,"гр");
                saveTable2(i, "Яйца",5,"шт");
                saveTable2(i, "Фета",300,"гр");
                saveTable2(i, "Чеддер",50,"гр");
                saveTable2(i, "Молодой шпинат",400,"гр");
                saveTable2(i, "Тесто Фило",270,"гр");
                saveTable2(i, "Орегано",1,"щеп");
                saveTable2(i, "Цедра лимона",1,"шт");
                saveTable2(i, "Оливковое масло",1,"по вк");
                saveTable2(i, "Сливочное масло",1,"по вк");
                saveTable3(i,1,"Обжарьте кедровые орешки.",null);
                saveTable3(i,2,"Разбейте в миску яйца и раскрошите фету, натрите чеддер. Добавьте туда щепотку орегано, цедру лимона и немного оливкового масла. Когда орехи подрумянятся, добавьте их в яичную смесь и все тщательно перемешайте.",null);
                saveTable3(i,3,"Поставьте сковороду на огонь, налейте в нее немного оливкового масла и положите кусочек сливочного, выложите шпинат. Обжаривайте помешивая.",null);
                saveTable3(i,4,"Застелите рабочую поверхность листом бумаги для выпечки, длиной примерно 50 см. Слегка смажьте бумагу оливковым маслом, сожмите ее и снова разгладьте. Выложите на бумагу 4 листа теста фило большим прямоугольником, располагая их в нахлест, чтобы они почти полностью покрыли лист бумаги. Смажьте тесто оливковым маслом, посыпьте щепоткой соли. Повторяйте пока не получится тройной слой листов фило.",null);
                saveTable3(i,5,"Когда шпинат полностью привянет, добавьте его в яичную смесь. Аккуратно перенесите бумагу со слоем теста в форму, оставив края свисать, прижмите к углу между дном и бортами, влейте яичную смесь. Сложите тесто поверх начинки.",null);
                saveTable3(i,6,"Поставьте пирог в духовку, разогретую до 200 грудусов на 18-20 минут, пока пирог не зарумянится и не станет хрустящим.",R.drawable.x_recipe_3_step_6);
            }
            publishProgress(4);
            {
                i =saveTable1("Фахитас с курицей","Основные блюда","Латиноамериканская","Все",30,6,R.drawable.x_recipe_2_title);
                saveTable2(i, "Куриная грудка",800,"гр");
                saveTable2(i, "Сладкий перец",3,"шт");
                saveTable2(i, "Красный лук",1,"шт");
                saveTable2(i, "Мексиканская лепешка",1,"уп");
                saveTable2(i, "Сметана",1,"по вк");
                saveTable2(i, "Соус Гуакомоле",1,"по вк");
                saveTable2(i, "Приправа Фахитас",1,"шт");
                saveTable3(i,1,"Нарежьте курицу тонкими полосками, также порежьте полосками овощи.",null);
                saveTable3(i,2,"Обжарьте на сковороде до полной готовности, в конце добавьте приправу \"Фахитас\".",null);
                saveTable3(i,3,"Положите несоклько ложек курицы с овощами на лепешку, сверху добавьте ложку сметаны и соус гуакомоле, заверните в конверт.",null);
            }
            publishProgress(5);
            {
                i=saveTable1("Испанская паэлья","Паста и ризотто","Европейская","Все",60,6,R.drawable.x_recipe_5_title);
                saveTable2(i, "Морепродукты по вкусу",400,"гр");
                saveTable2(i, "Красный сладкий перец",1,"шт");
                saveTable2(i, "Помидоры",200,"гр");
                saveTable2(i, "Морковь",1,"шт");
                saveTable2(i, "Лук",1,"шт");
                saveTable2(i, "Корень сельдерея",100,"гр");
                saveTable2(i, "Зубчик чеснока",2,"шт");
                saveTable2(i, "Оливковое масло",3,"ст.л");
                saveTable2(i, "Рис круглозерный",1,"ст");
                saveTable2(i, "Мясной бульон или вода",1,"л");
                saveTable2(i, "Молотая паприка",1,"по вкусу");
                saveTable3(i,1,"Обжарьте морепродукты в течении 4-7 минут. Отставьте.",null);
                saveTable3(i,2,"Порежьте овощи, помидоры ошпарьте в кипятке и снимите кожицу. Сначала обжарьте лук и чеснок, далее добавьте морковь, сельдерей, помидоры и перец. Приправьте специями.",null);
                saveTable3(i,3,"Добавьте рис к овощам и залейте все бульоном или водой. Доведите до кипения, затем убавьте огонь и готовьте в течении 25-30 минут пока рис не станет мягким. В самом конце положите морепродукты и еще немного подержите.",null);
            }
            publishProgress(6);
            {
                i = saveTable1("Чизкейк","Выпечка","Европейская","Все",95,6,R.drawable.x_recipe_6_title);
                saveTable2(i, "Сливки 30%",100,"мл");
                saveTable2(i, "Сыр Филадельфия",525,"гр");
                saveTable2(i, "Сметана 20%",4,"ст.л");
                saveTable2(i, "Сахар",1,"ст");
                saveTable2(i, "Ванильный экстракт",2,"ч.л");
                saveTable2(i, "Яйцо куриное",3,"шт");
                saveTable2(i, "Мука",1.5,"ст");
                saveTable2(i, "Сливочное масло",120,"гр");
                saveTable2(i, "Какао-порошок",3,"ст.л");
                saveTable2(i, "Молотая корица",1,"ч.л");
                saveTable3(i,1,"Муку смешать с холодным маслом, порезанным на маленькие кусочки, добавить чуть больше половины стакана сахара, корицу и какао. Равномерно перемешать и распределить полученую массу по разъемной форме диаметром 24 см.",null);
                saveTable3(i,2,"Разогреть духовку до 150 градусов, поместить форму с основой в духовку, на нижнюю полку на 15 минут.",null);
                saveTable3(i,3,"Смешать миксером сыр Филадельфия, сливки, сметану, остаток сахара, ванильный экстракт и яйца. Вылить начинку в основу и выпекать 1 час 15 минут. Оставить на 15 минут в выключенной духовке и еще на 15 минут при открытой дверце. Остудить, подавать холодным.",null);
            }
            publishProgress(7);
            {
                i = saveTable1("Куриная печень с шампинонами в сливочном соусе","Основные блюда","Европейская","Все",25,4,R.drawable.x_recipe_7_title);

                saveTable2(i, "Куриная печень",500,"гр");
                saveTable2(i, "Шампиньоны",400,"гр");
                saveTable2(i, "Лук репчатый",1,"шт");
                saveTable2(i, "Сливки 30%",300,"гр");
                saveTable2(i, "Соль и перец",0,"по вкусу");
                saveTable2(i, "Подсолнечное масло",0,"по вкусу");


                saveTable3(i,1,"Порежьте куриную печень на крупные кусочки.На раскаленной сковороде обжарьте печень в течении 5-7 минут. Разрежьте кусочек, чтобы проверить готовность, срез должен быть равномерно серым. Переложите готовую печень в отдельную емкость.",null);
                saveTable3(i,2,"Обжарьте лук до золотистого цвета, к луку положите нарезанные шампиньоны и жарьте до испарения влаги.",R.drawable.x_recipe_7_step_2);
                saveTable3(i,3,"К грибам и луку влейте сливки, добавьте печень, приправьте солью и перцем и тушите, после закипания сливок, 2 минуты и можно снимать с огня.",null);
                saveTable3(i,4,"В качестве гарнира лучше всего подойдет рис.",null);
            }
            publishProgress(8);
            {
                i = saveTable1("Паста с консервированным тунцом и томатами","Паста и ризотто","Европейская","Рыбные блюда",30,4,R.drawable.x_recipe_8_title);

                saveTable2(i, "Консервированный тунец",2,"банки");
                saveTable2(i, "Томаты резаные в собственном соку",500,"гр");
                saveTable2(i, "Паста тальятелле",0,"по вкусу");
                saveTable2(i, "Лук репчатый",1,"шт");
                saveTable2(i, "Зубчик чеснока",2,"шт");
                saveTable2(i, "Базилик",0,"по вкусу");
                saveTable2(i, "Сахар",1,"ч.л");
                saveTable2(i, "Соль и перец",0,"по вкусу");
                saveTable2(i, "Подсолнечное масло",0,"по вкусу");


                saveTable3(i,1,"Обжарьте мелко нарезанные лук и чеснок до золотистого цвета.",null);
                saveTable3(i,2,"Добавьте к луку тунец вместе с соком, крупные кусочки разомните вилкой, оставьте покипеть пару минут.",null);
                saveTable3(i,3,"Добавьте томаты, обязательно в томатный соус положите 1 чайную ложку сахара, чтобы кислота ушла, приправьте базиликом, солью и перцем и тушите на среднем огне 7-10 минут.",null);
                saveTable3(i,4,"Сварите порцию пасты по инструкции и выложите соус сверху, украсив листиками базилика.",null);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(activity,"Загружаем рецепты "+ values[0] +"/8.",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Toast.makeText(activity,"Рецепты загружены, обновите поиск.",Toast.LENGTH_LONG).show();
        }

        private int saveTable1 (String recipe, String category, String kitchen, String preferences, Integer time, Integer portion, Integer img) {
            CV.put(TABLE1_COLUMN_RECIPE, recipe);
            CV.put(TABLE1_COLUMN_CATEGORY, category);
            CV.put(TABLE1_COLUMN_KITCHEN, kitchen);
            CV.put(TABLE1_COLUMN_PREFERENCES, preferences);
            CV.put(TABLE1_COLUMN_TIME, time);
            CV.put(TABLE1_COLUMN_PORTION, portion);
            if (img!=null) {
                savePhoto(1,img);
            }
            int i = (int) DB.insert(TABLE1_NAME, null, CV);
            CV.clear();
            return i;
        }

        private void saveTable2 (int i, String ingredients, double quantity, String measure) {
            CV.put(TABLE2_COLUMN_ID_RECIPE, i);
            CV.put(TABLE2_COLUMN_INGREDIENTS, ingredients);
            CV.put(TABLE2_COLUMN_QUANTITY, quantity);
            CV.put(TABLE2_COLUMN_MEASURE, measure);
            DB.insert(TABLE2_NAME, null, CV);
            CV.clear();
        }

        private void saveTable3 (int i, int number, String text, Integer img) {
            CV.put(TABLE3_COLUMN_ID_RECIPE, i);
            CV.put(TABLE3_COLUMN_NUMBER, number);
            CV.put(TABLE3_COLUMN_TEXT, text);
            if (img!=null) {
                savePhoto(3,img);
            }
            DB.insert(TABLE3_NAME, null, CV);
            CV.clear();
        }

        private void savePhoto(int table, Integer img){
            String COLUMN_IMG_FULL = "";
            String COLUMN_IMG_TITLE = "";
            if (table == 1) {
                COLUMN_IMG_FULL = TABLE1_COLUMN_IMG_FULL;
                COLUMN_IMG_TITLE = TABLE1_COLUMN_IMG_TITLE;
            }
            if (table == 3) {
                COLUMN_IMG_FULL = TABLE3_COLUMN_IMG_FULL;
                COLUMN_IMG_TITLE = TABLE3_COLUMN_IMG_TITLE;
            }
            PhotoHelper ph = new PhotoHelper(activity);
            Bitmap bitmap = ph.getBitmapFromResource(img);
            ph.setFileCamera(ph.createFile("png"));
            ph.saveBitmapToFile(bitmap, ph.getFileCamera());
            ph.setFileImageView(ph.createFile("png"));
            Bitmap bitmapView = ph.getBitmapFromFile(ph.getFileCamera(), 275, 275);
            ph.saveBitmapToFile(bitmapView, ph.getFileImageView());
            CV.put(COLUMN_IMG_FULL, ph.getFileCamera().toString());
            CV.put(COLUMN_IMG_TITLE, ph.getFileImageView().toString());
        }

    }
}