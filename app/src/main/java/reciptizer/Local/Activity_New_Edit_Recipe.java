package reciptizer.Local;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.example.reciptizer.R;
import reciptizer.Activity_Main;
import reciptizer.Common.Helpers.AnimHelper;
import reciptizer.Common.Helpers.ConstantHelper;
import reciptizer.Common.Helpers.PhotoHelper;

import java.io.File;


public class Activity_New_Edit_Recipe extends FragmentActivity {

    String[] TABLE1_FILTER, TABLE1_COLUMN_CATEGORY, TABLE1_COLUMN_KITCHEN,
            TABLE1_COLUMN_PREFERENCES, TABLE2_COLUMN_MEASURE;
    String  new_TABLE1_CATEGORY, new_TABLE1_KITCHEN, new_TABLE1_PREFERENCES;
    boolean newRecipeMode, recipeCreated;
    int filterResult_id;
    Spinner spinner_TABLE1_COLUMN_CATEGORY, spinner_TABLE1_COLUMN_KITCHEN,
            spinner_TABLE1_COLUMN_PREFERENCES, spinner_TABLE2_COLUMN_MEASURE;
    EditText editText_TABLE1_COLUMN_RECIPE, editText_TABLE1_COLUMN_TIME, editText_TABLE1_COLUMN_PORTION,
            editText_TABLE2_COLUMN_INGREDIENTS, editText_TABLE2_COLUMN_QUANTITY,
            textView_table3_TABLE3_COLUMN_NUMBER, editText_table3_TABLE3_COLUMN_TEXT;
    LayoutInflater ltInflater;
    LinearLayout linLayout_Table2, linLayout_Table3;
    RowBuilder rowBuilder;
    static final int REQUEST_PHOTO_TABLE1 = 1;
    static final int REQUEST_PHOTO_TABLE3 = 2;
    ImageView imageView_TABLE1_COLUMN_IMG, imageView_PHOTO_CAMERA, imageView_PHOTO_LOAD, imageView_TABLE3_COLUMN_IMG;
    Cursor cursor;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_recipe);

        getResource();

        setStatusBarColor();
        Log.d(Activity_Main.LOG_TAG,"NewRecipe_setStatusBarColor");

        prepareActivityCommon();
        Log.d(Activity_Main.LOG_TAG,"NewRecipe_prepareActivityCommon");

        if (newRecipeMode) {
            Log.d(Activity_Main.LOG_TAG,"NewRecipe_prepareActivityNewRecipe");
            prepareActivityNewRecipe ();
        }
        else {
            prepareActivityEditRecipe ();
            Log.d(Activity_Main.LOG_TAG,"NewRecipe_prepareActivityEditRecipe");
        }

        prepareButtonCreateRecipe();
        Log.d(Activity_Main.LOG_TAG,"NewRecipe_prepareButtonCreateRecipe");
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoHelper ph;
        View view;
        int count;
        if (resultCode != RESULT_OK)
        {
            ph = (((PhotoHelper) imageView_TABLE1_COLUMN_IMG.getTag()));
            ph.clearTemp();
            count = linLayout_Table3.getChildCount();
            for (int idView = 0; idView < count; idView++) {
                view = linLayout_Table3.getChildAt(idView);
                imageView_TABLE3_COLUMN_IMG = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_IMG);
                ph = (((PhotoHelper) imageView_TABLE3_COLUMN_IMG.getTag()));
                ph.clearTemp();
            }
        }
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_PHOTO_TABLE1:
                    ph = (((PhotoHelper) imageView_TABLE1_COLUMN_IMG.getTag()));
                    ph.ImageMaker(imageView_TABLE1_COLUMN_IMG,data);
                    break;
                case REQUEST_PHOTO_TABLE3:
                    count = linLayout_Table3.getChildCount();
                    for (int idView = 0; idView < count; idView++){
                        view = linLayout_Table3.getChildAt(idView);
                        imageView_TABLE3_COLUMN_IMG = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_IMG);
                        ph = (((PhotoHelper) imageView_TABLE3_COLUMN_IMG.getTag()));
                        ph.ImageMaker(imageView_TABLE3_COLUMN_IMG,data);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!recipeCreated&&newRecipeMode) {
            ((PhotoHelper) imageView_TABLE1_COLUMN_IMG.getTag()).deleteFileAll();
            View view;
            int count = linLayout_Table3.getChildCount();
            for (int idView = 0; idView < count; idView++) {
                view = linLayout_Table3.getChildAt(idView);
                imageView_TABLE3_COLUMN_IMG = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_IMG);
                ((PhotoHelper) imageView_TABLE3_COLUMN_IMG.getTag()).deleteFileAll();
            }
        }
    }

    private void getResource () {
        TABLE1_FILTER = getResources().getStringArray(R.array.TABLE1_FILTER);
        TABLE1_COLUMN_CATEGORY = getResources().getStringArray(R.array.TABLE1_COLUMN_CATEGORY);
        TABLE1_COLUMN_KITCHEN = getResources().getStringArray(R.array.TABLE1_COLUMN_KITCHEN);
        TABLE1_COLUMN_PREFERENCES = getResources().getStringArray(R.array.TABLE1_COLUMN_PREFERENCES);
        TABLE2_COLUMN_MEASURE = getResources().getStringArray(R.array.TABLE2_COLUMN_MEASURE);
        new_TABLE1_CATEGORY = TABLE1_COLUMN_CATEGORY[0];
        new_TABLE1_KITCHEN = TABLE1_COLUMN_KITCHEN[0];
        new_TABLE1_PREFERENCES = TABLE1_COLUMN_PREFERENCES[0];

        Intent intent = getIntent();
        if (!intent.hasExtra(DB.TABLE1_COLUMN_ID))
        {
            newRecipeMode = true;
            Log.d(Activity_Main.LOG_TAG,"NewRecipe_getResource: id = null, mode: new recipe");
        }
        else
            {
                filterResult_id = intent.getIntExtra(DB.TABLE1_COLUMN_ID,0);
                Log.d(Activity_Main.LOG_TAG,"NewRecipe_getResource: id = " + filterResult_id + ", mode: edit recipe");
        }
    }

    private void setStatusBarColor () {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NewRecipeSetStatusBarColor));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.NewRecipeSetStatusBarColor));
    }

    private void prepareActivityCommon () {
        ltInflater = getLayoutInflater();
        linLayout_Table2 = findViewById(R.id.New_Recipe_TABLE2_LL);
        linLayout_Table3 = findViewById(R.id.New_Recipe_TABLE3_LL);
        rowBuilder = new RowBuilder();

        final Button button_search = findViewById(R.id.New_Recipe_button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finish();
                    }
                };
                AnimHelper.AnimMenuClick(button_search,adapter);
            }
        });
        final Button button_delete= findViewById(R.id.New_Recipe_button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!newRecipeMode) {
                            DB.deletePosition(filterResult_id);
                            ConstantHelper.isNeedToRefreshFilterCreateRecyclerView = true;
                        }
                        finish();
                    }
                };
                AnimHelper.AnimMenuClick(button_delete,adapter);
            }
        });

        spinner_TABLE1_COLUMN_CATEGORY = findViewById(R.id.New_Recipe_cat1);
        spinner_TABLE1_COLUMN_KITCHEN = findViewById(R.id.New_Recipe_cat2);
        spinner_TABLE1_COLUMN_PREFERENCES = findViewById(R.id.New_Recipe_cat3);
        ArrayAdapter<String> adapterSpinnerCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TABLE1_COLUMN_CATEGORY);
        ArrayAdapter<String> adapterSpinnerKitchen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TABLE1_COLUMN_KITCHEN);
        ArrayAdapter<String> adapterSpinnerPreferences = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TABLE1_COLUMN_PREFERENCES);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerKitchen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerPreferences.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_TABLE1_COLUMN_CATEGORY.setAdapter(adapterSpinnerCategory);
        spinner_TABLE1_COLUMN_KITCHEN.setAdapter(adapterSpinnerKitchen);
        spinner_TABLE1_COLUMN_PREFERENCES.setAdapter(adapterSpinnerPreferences);
        spinner_TABLE1_COLUMN_CATEGORY.setPrompt(TABLE1_FILTER[0]);
        spinner_TABLE1_COLUMN_KITCHEN.setPrompt(TABLE1_FILTER[1]);
        spinner_TABLE1_COLUMN_PREFERENCES.setPrompt(TABLE1_FILTER[2]);
        spinner_TABLE1_COLUMN_CATEGORY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_TABLE1_CATEGORY = TABLE1_COLUMN_CATEGORY[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_TABLE1_COLUMN_KITCHEN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_TABLE1_KITCHEN = TABLE1_COLUMN_KITCHEN[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_TABLE1_COLUMN_PREFERENCES.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_TABLE1_PREFERENCES = TABLE1_COLUMN_PREFERENCES[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ImageView button_Add_Row_Table2 = findViewById(R.id.New_Recipe_Add_Row_Table2);
        button_Add_Row_Table2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowBuilder.newRowTable2().init().empty().create();
                AnimHelper.AnimAddButtonTable2(button_Add_Row_Table2);
            }
        });
        final ImageView button_Add_Row_Table3 = findViewById(R.id.New_Recipe_Add_Row_Table3);
        button_Add_Row_Table3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowBuilder.newRowTable3().init().empty().create();
                AnimHelper.AnimAddButtonTable3(button_Add_Row_Table3);
            }
        });

        editText_TABLE1_COLUMN_RECIPE = findViewById(R.id.New_Recipe_TABLE1_COLUMN_RECIPE);
        editText_TABLE1_COLUMN_RECIPE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((editText_TABLE1_COLUMN_RECIPE.getText()).toString().equals("Название рецепта"))
                    editText_TABLE1_COLUMN_RECIPE.setText("");
            }
        });
        editText_TABLE1_COLUMN_RECIPE.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText_TABLE1_COLUMN_RECIPE.setRawInputType(InputType.TYPE_CLASS_TEXT);

        editText_TABLE1_COLUMN_TIME = findViewById(R.id.New_Recipe_TABLE1_COLUMN_TIME);
        editText_TABLE1_COLUMN_PORTION = findViewById(R.id.New_Recipe_TABLE1_COLUMN_PORTION);
        imageView_TABLE1_COLUMN_IMG = findViewById(R.id.New_Recipe_TABLE1_COLUMN_IMG);

        final PhotoHelper phT1 = new PhotoHelper(Activity_New_Edit_Recipe.this);
        imageView_TABLE1_COLUMN_IMG.setTag(phT1);
        imageView_TABLE1_COLUMN_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DialogFragment dialog = new ImageDialogFragment(phT1);
                        dialog.show(getSupportFragmentManager(), "ImageDialog");
                    }
                };
                AnimHelper.AnimClick(imageView_TABLE1_COLUMN_IMG,adapter);
            }
        });

        imageView_PHOTO_CAMERA = findViewById(R.id.New_Recipe_Photo_Camera);
        imageView_PHOTO_CAMERA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        phT1.setHaveFileCamera(true);
                        phT1.setFileTemp(phT1.createFile("png"));
                        phT1.intentCameraAndSaveImage(REQUEST_PHOTO_TABLE1, phT1.getFileTemp());
                    }
                };
                AnimHelper.AnimPhotoLoadClick (imageView_PHOTO_CAMERA, adapter);
            }
        });

        imageView_PHOTO_LOAD = findViewById(R.id.New_Recipe_Photo_Load);
        imageView_PHOTO_LOAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        phT1.setHaveFileLoad(true);
                        phT1.intentLoader(REQUEST_PHOTO_TABLE1);
                    }
                };
                AnimHelper.AnimPhotoLoadClick (imageView_PHOTO_LOAD, adapter);
            }
        });
    }

    private void prepareActivityNewRecipe () {
        rowBuilder.newRowTable2().init().empty().create();
        rowBuilder.newRowTable3().init().empty().create();
    }

    private void prepareActivityEditRecipe () {
        TextView textViewTitle = findViewById(R.id.New_Recipe_Title);
        textViewTitle.setText("Редактор рецепта");

        cursor = DB.getTable1Pos(filterResult_id);

        cursor.moveToFirst();

        editText_TABLE1_COLUMN_RECIPE.setText("" + cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_RECIPE)));
        editText_TABLE1_COLUMN_TIME.setText("" + cursor.getInt(cursor.getColumnIndex(DB.TABLE1_COLUMN_TIME)));
        editText_TABLE1_COLUMN_PORTION.setText("" + cursor.getInt(cursor.getColumnIndex(DB.TABLE1_COLUMN_PORTION)));

        String img_jpeg = cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_IMG_FULL));
        String img_png = cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_IMG_TITLE));

        PhotoHelper phT1 = (((PhotoHelper) imageView_TABLE1_COLUMN_IMG.getTag()));
        imageView_TABLE1_COLUMN_IMG.getTag();
        if (img_jpeg!=null && img_png!=null) {
            phT1.setHavePhoto(true);
            phT1.setFileCamera(new File(img_jpeg));
            phT1.setFileImageView(new File(img_png));
            imageView_TABLE1_COLUMN_IMG.setImageURI(Uri.parse(img_png));
        }

        String category = cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_CATEGORY));
        for (int i = 0; i < spinner_TABLE1_COLUMN_CATEGORY.getCount(); i++) {
            if (spinner_TABLE1_COLUMN_CATEGORY.getItemAtPosition(i).equals(category)) {
                spinner_TABLE1_COLUMN_CATEGORY.setSelection(i);
                break;
            }
        }

        String kitchen = cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_KITCHEN));
        for (int i = 0; i < spinner_TABLE1_COLUMN_KITCHEN.getCount(); i++) {
            if (spinner_TABLE1_COLUMN_KITCHEN.getItemAtPosition(i).equals(kitchen)) {
                spinner_TABLE1_COLUMN_KITCHEN.setSelection(i);
                break;
            }
        }

        String preferences = cursor.getString(cursor.getColumnIndex(DB.TABLE1_COLUMN_PREFERENCES));
        for (int i = 0; i < spinner_TABLE1_COLUMN_PREFERENCES.getCount(); i++) {
            if (spinner_TABLE1_COLUMN_PREFERENCES.getItemAtPosition(i).equals(preferences)) {
                spinner_TABLE1_COLUMN_PREFERENCES.setSelection(i);
                break;
            }
        }

        cursor.close();

        cursor = DB.getTable2Pos(filterResult_id);
        if (cursor.moveToFirst()) {
            do {
                rowBuilder.newRowTable2().init().data(cursor).create();
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = DB.getTable3Pos(filterResult_id);
        if (cursor.moveToFirst()) {
            do {
                rowBuilder.newRowTable3().init().data(cursor).create();
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void prepareButtonCreateRecipe () {
        final ImageView buttonCreateRecipe = findViewById(R.id.New_Recipe_button_create_recipe);
        buttonCreateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(newRecipeMode){
                            int id = saveTable1 ();
                            Log.d(Activity_Main.LOG_TAG,"saveTable1, new_TABLE1_COLUMN_ID = " + id);
                            saveTable2 (id);
                            Log.d(Activity_Main.LOG_TAG,"saveTable2");
                            saveTable3 (id);
                            Log.d(Activity_Main.LOG_TAG,"saveTable3");
                            recipeCreated = true;
                        }
                        else {
                            saveTable1 ();
                            Log.d(Activity_Main.LOG_TAG,"updateTable1, new_TABLE1_COLUMN_ID = " + filterResult_id);
                            saveTable2 (filterResult_id);
                            Log.d(Activity_Main.LOG_TAG,"updateTable2, new_TABLE1_COLUMN_ID = " + filterResult_id);
                            saveTable3 (filterResult_id);
                            Log.d(Activity_Main.LOG_TAG,"updateTable3, new_TABLE1_COLUMN_ID = " + filterResult_id);
                        }
                        ConstantHelper.isNeedToRefreshFilterCreateRecyclerView = true;
                        finish();
                    }
                };
                AnimHelper.AnimCreateRecipe(buttonCreateRecipe, adapter);
            }
        });
    }

    private int saveTable1 () {
        String recipe = editText_TABLE1_COLUMN_RECIPE.getText().toString();

        Integer time;
        if(editText_TABLE1_COLUMN_TIME.getText().toString().equals(""))
            time = null;
        else
            time = Integer.parseInt(editText_TABLE1_COLUMN_TIME.getText().toString());

        Integer portion;
        if (editText_TABLE1_COLUMN_PORTION.getText().toString().equals(""))
            portion = null;
        else
            portion = Integer.parseInt(editText_TABLE1_COLUMN_PORTION.getText().toString());

        String img_jpeg;
        String img_png;
        PhotoHelper ph = ((PhotoHelper) imageView_TABLE1_COLUMN_IMG.getTag());
        if (ph.isHavePhoto()){
            if (ph.getFileCamera()==null)
                img_jpeg = ph.getFileLoad().getPath();
            else
                img_jpeg = ph.getFileCamera().getPath();
            img_png = ph.getFileImageView().getPath();
        }
        else {
            img_jpeg = null;
            img_png = null;
        }
        if (newRecipeMode)
            return DB.addPositionInTable1(recipe, new_TABLE1_CATEGORY, new_TABLE1_KITCHEN, new_TABLE1_PREFERENCES, time, portion, img_jpeg, img_png);
        else{
            DB.updatePositionInTable1(filterResult_id, recipe, new_TABLE1_CATEGORY, new_TABLE1_KITCHEN, new_TABLE1_PREFERENCES, time, portion, img_jpeg, img_png);
            return -1;
        }
    }

    private void saveTable2 (int id_recipe) {
        View view;
        int count = linLayout_Table2.getChildCount();
        for (int idView = 0; idView < count; idView++){
            view = linLayout_Table2.getChildAt(idView);
            editText_TABLE2_COLUMN_INGREDIENTS = view.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_INGREDIENTS);
            editText_TABLE2_COLUMN_QUANTITY = view.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
            spinner_TABLE2_COLUMN_MEASURE = view.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_MEASURE);
            String ingredients;
            if (editText_TABLE2_COLUMN_INGREDIENTS.getText().toString().equals(""))
                ingredients = "";
            else
                ingredients = editText_TABLE2_COLUMN_INGREDIENTS.getText().toString();
            Integer quantity;
            if (editText_TABLE2_COLUMN_QUANTITY.getText().toString().equals(""))
                quantity = null;
            else
                quantity = Integer.parseInt(editText_TABLE2_COLUMN_QUANTITY.getText().toString());
            String measure = spinner_TABLE2_COLUMN_MEASURE.getSelectedItem().toString();
            if (newRecipeMode)
                DB.addPositionInTable2 (id_recipe,ingredients,quantity,measure);
            else {
                if (view.getTag()!=null) {
                    int id = (int) view.getTag();
                    DB.updatePositionInTable2(id, id_recipe, ingredients, quantity, measure);
                }
                else
                    DB.addPositionInTable2(id_recipe, ingredients, quantity, measure);
            }
        }
    }

    private void saveTable3 (int id_recipe) {
        View view;
        int count = linLayout_Table3.getChildCount();
        for (int idView = 0; idView < count; idView++){
            view = linLayout_Table3.getChildAt(idView);
            textView_table3_TABLE3_COLUMN_NUMBER = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_NUMBER);
            editText_table3_TABLE3_COLUMN_TEXT = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_TEXT);
            imageView_TABLE3_COLUMN_IMG = view.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_IMG);
            Integer number;
            if (textView_table3_TABLE3_COLUMN_NUMBER.getText().toString().equals(""))
                number = null;
            else
                number = Integer.parseInt(textView_table3_TABLE3_COLUMN_NUMBER.getText().toString());
            String text = editText_table3_TABLE3_COLUMN_TEXT.getText().toString();
            String img_jpeg;
            String img_png;
            PhotoHelper ph = ((PhotoHelper) imageView_TABLE3_COLUMN_IMG.getTag());
            if (ph.isHavePhoto()){
                if (ph.getFileCamera()==null)
                    img_jpeg = ph.getFileLoad().getPath();
                else
                    img_jpeg = ph.getFileCamera().getPath();
                img_png = ph.getFileImageView().getPath();
            }
            else {
                img_jpeg = null;
                img_png = null;
            }
            if (newRecipeMode)
                DB.addPositionInTable3 (id_recipe, number, text, img_jpeg, img_png);
            else {
                if (view.getTag()!=null) {
                    int id = (int) view.getTag();
                    DB.updatePositionInTable3 (id, id_recipe, number, text, img_jpeg, img_png);
                }
                else
                    DB.addPositionInTable3 (id_recipe, number, text, img_jpeg, img_png);
            }
        }
    }

    class RowBuilder {
        LayoutInflater ltInflater;
        LinearLayout linLayout_Table2, linLayout_Table3;

        public RowBuilder() {
            ltInflater = getLayoutInflater();
            linLayout_Table2 = findViewById(R.id.New_Recipe_TABLE2_LL);
            linLayout_Table3 = findViewById(R.id.New_Recipe_TABLE3_LL);
        }

        public RowTable2 newRowTable2() {
            return new RowTable2();
        }

        public RowTable3 newRowTable3() {
            return new RowTable3();
        }

        class RowTable2 {
            Spinner spinner_TABLE2_COLUMN_MEASURE;
            View viewRowTable2;
            ArrayAdapter<String> adapterSpinnerMeasure;
            ImageView button_Delete_Row_Table2;
            AnimatorListenerAdapter adapter;
            EditText editText_TABLE2_COLUMN_INGREDIENTS;

            private RowTable2 init () {
                viewRowTable2 = ltInflater.inflate(R.layout.item_new_edit_recipe_table2, linLayout_Table2, false);
                spinner_TABLE2_COLUMN_MEASURE = viewRowTable2.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_MEASURE);
                adapterSpinnerMeasure = new ArrayAdapter<>(Activity_New_Edit_Recipe.this, android.R.layout.simple_spinner_item, TABLE2_COLUMN_MEASURE);
                adapterSpinnerMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_TABLE2_COLUMN_MEASURE.setAdapter(adapterSpinnerMeasure);
                button_Delete_Row_Table2 = viewRowTable2.findViewById(R.id.New_Recipe_Delete_Row_Table2);
                editText_TABLE2_COLUMN_INGREDIENTS = viewRowTable2.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_INGREDIENTS);
                editText_TABLE2_COLUMN_INGREDIENTS.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText_TABLE2_COLUMN_INGREDIENTS.setRawInputType(InputType.TYPE_CLASS_TEXT);
                return this;
            }

            private RowTable2 empty() {
                adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linLayout_Table2.removeView(viewRowTable2);
                    }
                };
                return this;
            }

            private RowTable2 data(Cursor cursor) {
                final int id = cursor.getInt(cursor.getColumnIndex(DB.TABLE2_COLUMN_ID));
                String ingredients = cursor.getString(cursor.getColumnIndex(DB.TABLE2_COLUMN_INGREDIENTS));
                int quantity = cursor.getInt(cursor.getColumnIndex(DB.TABLE2_COLUMN_QUANTITY));
                String measure = cursor.getString(cursor.getColumnIndex(DB.TABLE2_COLUMN_MEASURE));
                viewRowTable2.setTag(id);

                editText_TABLE2_COLUMN_INGREDIENTS.setText("" + ingredients);
                editText_TABLE2_COLUMN_QUANTITY = viewRowTable2.findViewById(R.id.New_Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
                editText_TABLE2_COLUMN_QUANTITY.setText("" + quantity);
                for (int i = 0; i < spinner_TABLE2_COLUMN_MEASURE.getCount(); i++) {
                    if (spinner_TABLE2_COLUMN_MEASURE.getItemAtPosition(i).equals(measure)) {
                        spinner_TABLE2_COLUMN_MEASURE.setSelection(i);
                        break;
                    }
                }
                adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DB.deleteRowTable2(id);
                        linLayout_Table2.removeView(viewRowTable2);
                    }
                };
                return this;
            }

            private void create() {
                button_Delete_Row_Table2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimHelper.AnimNewRecipeDeleteRow(viewRowTable2,adapter);
                    }
                });
                AnimHelper.AnimNewRecipeAddEmptyRow(viewRowTable2);
                linLayout_Table2.addView(viewRowTable2);
            }
        }

        class RowTable3 {
            View viewRowTable3;
            ImageView imageView_Delete_Row_Table3;
            ImageView imageView_TABLE3_COLUMN_IMG;
            AnimatorListenerAdapter adapter_imageView_Delete_Row_Table3;
            PhotoHelper ph;
            ImageView imageView_Photo_Camera;
            ImageView imageView_PHOTO_LOAD;
            EditText editText_table3_TABLE3_COLUMN_TEXT;

            private RowTable3 init () {
                viewRowTable3 = ltInflater.inflate(R.layout.item_new_edit_recipe_table3, linLayout_Table3, false);
                imageView_Delete_Row_Table3 = viewRowTable3.findViewById(R.id.New_Recipe_item_Delete_Row_Table3);
                imageView_TABLE3_COLUMN_IMG = viewRowTable3.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_IMG);
                ph = new PhotoHelper(Activity_New_Edit_Recipe.this);
                imageView_TABLE3_COLUMN_IMG.setTag(ph);
                imageView_TABLE3_COLUMN_IMG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                DialogFragment dialog = new ImageDialogFragment(ph);
                                dialog.show(getSupportFragmentManager(), "ImageDialog");
                            }
                        };
                        AnimHelper.AnimClick(imageView_TABLE3_COLUMN_IMG,adapter);
                    }
                });
                imageView_Photo_Camera = viewRowTable3.findViewById(R.id.New_Recipe_item_Photo_Camera);
                imageView_Photo_Camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ph.setHaveFileCamera(true);
                                ph.setFileTemp(ph.createFile("png"));
                                ph.intentCameraAndSaveImage(REQUEST_PHOTO_TABLE3, ph.getFileTemp());
                            }
                        };
                        AnimHelper.AnimPhotoLoadClick(imageView_Photo_Camera, adapter);
                    }
                });
                imageView_PHOTO_LOAD = viewRowTable3.findViewById(R.id.New_Recipe_item_Photo_Load);
                imageView_PHOTO_LOAD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                ph.setHaveFileLoad(true);
                                ph.intentLoader(REQUEST_PHOTO_TABLE3);
                            }
                        };
                        AnimHelper.AnimPhotoLoadClick(imageView_PHOTO_LOAD, adapter);
                    }
                });
                editText_table3_TABLE3_COLUMN_TEXT = viewRowTable3.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_TEXT);
                editText_table3_TABLE3_COLUMN_TEXT.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText_table3_TABLE3_COLUMN_TEXT.setRawInputType(InputType.TYPE_CLASS_TEXT);
                return this;
            }

            private RowTable3 empty () {
                adapter_imageView_Delete_Row_Table3 = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linLayout_Table3.removeView(viewRowTable3);
                    }
                };
                return this;
            }

            private RowTable3 data (Cursor cursor) {
                final int id = cursor.getInt(cursor.getColumnIndex(DB.TABLE3_COLUMN_ID));
                int number = cursor.getInt(cursor.getColumnIndex(DB.TABLE3_COLUMN_NUMBER));
                String text = cursor.getString(cursor.getColumnIndex(DB.TABLE3_COLUMN_TEXT));
                final String img_jpeg = cursor.getString(cursor.getColumnIndex(DB.TABLE3_COLUMN_IMG_FULL));
                String img_png = cursor.getString(cursor.getColumnIndex(DB.TABLE3_COLUMN_IMG_TITLE));
                viewRowTable3.setTag(id);
                adapter_imageView_Delete_Row_Table3 = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DB.deleteRowTable3(id);
                        linLayout_Table3.removeView(viewRowTable3);
                    }
                };
                if (img_jpeg!=null && img_png!=null) {
                    ph.setHavePhoto(true);
                    ph.setFileCamera(new File(img_jpeg));
                    ph.setFileImageView(new File(img_png));
                    imageView_TABLE3_COLUMN_IMG.setImageURI(Uri.parse(img_png));
                }
                textView_table3_TABLE3_COLUMN_NUMBER = viewRowTable3.findViewById(R.id.New_Recipe_item_table3_TABLE3_COLUMN_NUMBER);
                textView_table3_TABLE3_COLUMN_NUMBER.setText("" + number);
                editText_table3_TABLE3_COLUMN_TEXT.setText("" + text);
                return this;
            }

            private void create() {
                imageView_Delete_Row_Table3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimHelper.AnimNewRecipeDeleteRow(viewRowTable3,adapter_imageView_Delete_Row_Table3);
                    }
                });
                linLayout_Table3.addView(viewRowTable3);
                AnimHelper.AnimNewRecipeAddEmptyRow(viewRowTable3);
            }
        }
    }
}