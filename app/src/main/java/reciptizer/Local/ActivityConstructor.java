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
import reciptizer.ActivityMain;
import reciptizer.Common.Helpers.AnimHelper;
import reciptizer.Common.Helpers.ConstantHelper;
import reciptizer.Common.Helpers.PhotoHelper;

import java.io.File;


public class ActivityConstructor extends FragmentActivity {

    String[] resourceCategory, resourceKitchen, resourcePreferences, resourceMeasure;
    String  valueCategory, valueKitchen, valuePreferences;
    boolean newRecipeMode, recipeCreated;
    int filterId;
    Spinner spinnerCategory, spinnerKitchen, spinnerPreferences, spinnerMeasure;
    EditText editTextRecipe, editTextTime, editTextPortion,editTextIngredients,
             editTextQuantity, textViewNumber, editTextText;
    LayoutInflater ltInflater;
    LinearLayout linearLayoutIngredients, linearLayoutSteps;
    RowBuilder rowBuilder;
    static final int REQUEST_PHOTO_TABLE1 = 1;
    static final int REQUEST_PHOTO_TABLE3 = 2;
    ImageView imageViewImage, imageViewCamera, imageViewLoad, imageViewImageStep;
    Cursor cursor;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constructor);

        getResource();

        setStatusBarColor();
        Log.d(ActivityMain.LOG_TAG,"NewRecipe_setStatusBarColor");

        prepareActivityCommon();
        Log.d(ActivityMain.LOG_TAG,"NewRecipe_prepareActivityCommon");

        if (newRecipeMode) {
            Log.d(ActivityMain.LOG_TAG,"NewRecipe_prepareActivityNewRecipe");
            prepareActivityNewRecipe ();
        }
        else {
            prepareActivityEditRecipe ();
            Log.d(ActivityMain.LOG_TAG,"NewRecipe_prepareActivityEditRecipe");
        }

        prepareButtonCreateRecipe();
        Log.d(ActivityMain.LOG_TAG,"NewRecipe_prepareButtonCreateRecipe");
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoHelper ph;
        View view;
        int count;
        if (resultCode != RESULT_OK)
        {
            ph = (((PhotoHelper) imageViewImage.getTag()));
            ph.clearTemp();
            count = linearLayoutSteps.getChildCount();
            for (int idView = 0; idView < count; idView++) {
                view = linearLayoutSteps.getChildAt(idView);
                imageViewImageStep = view.findViewById(R.id.item_constructor_steps_imageView_image);
                ph = (((PhotoHelper) imageViewImageStep.getTag()));
                ph.clearTemp();
            }
        }
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_PHOTO_TABLE1:
                    ph = (((PhotoHelper) imageViewImage.getTag()));
                    ph.ImageMaker(imageViewImage,data);
                    break;
                case REQUEST_PHOTO_TABLE3:
                    count = linearLayoutSteps.getChildCount();
                    for (int idView = 0; idView < count; idView++){
                        view = linearLayoutSteps.getChildAt(idView);
                        imageViewImageStep = view.findViewById(R.id.item_constructor_steps_imageView_image);
                        ph = (((PhotoHelper) imageViewImageStep.getTag()));
                        ph.ImageMaker(imageViewImageStep,data);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!recipeCreated&&newRecipeMode) {
            ((PhotoHelper) imageViewImage.getTag()).deleteFileAll();
            View view;
            int count = linearLayoutSteps.getChildCount();
            for (int idView = 0; idView < count; idView++) {
                view = linearLayoutSteps.getChildAt(idView);
                imageViewImageStep = view.findViewById(R.id.item_constructor_steps_imageView_image);
                ((PhotoHelper) imageViewImageStep.getTag()).deleteFileAll();
            }
        }
    }

    private void getResource () {
        resourceCategory = getResources().getStringArray(R.array.category);
        resourceKitchen = getResources().getStringArray(R.array.kitchen);
        resourcePreferences = getResources().getStringArray(R.array.preferences);
        resourceMeasure = getResources().getStringArray(R.array.measure);
        valueCategory = resourceCategory[0];
        valueKitchen = resourceKitchen[0];
        valuePreferences = resourcePreferences[0];

        Intent intent = getIntent();
        if (!intent.hasExtra(SQL.TABLE1_COLUMN_ID))
        {
            newRecipeMode = true;
            Log.d(ActivityMain.LOG_TAG,"NewRecipe_getResource: id = null, mode: new recipe");
        }
        else
            {
                filterId = intent.getIntExtra(SQL.TABLE1_COLUMN_ID,0);
                Log.d(ActivityMain.LOG_TAG,"NewRecipe_getResource: id = " + filterId + ", mode: edit recipe");
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
        linearLayoutIngredients = findViewById(R.id.constructor_linearLayout_ingredients);
        linearLayoutSteps = findViewById(R.id.constructor_linearLayout_steps);
        rowBuilder = new RowBuilder();

        final Button buttonSearch = findViewById(R.id.constructor_button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finish();
                    }
                };
                AnimHelper.AnimMenuClick(buttonSearch,adapter);
            }
        });
        final Button buttonDelete= findViewById(R.id.constructor_button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!newRecipeMode) {
                            SQL.deletePosition(filterId);
                            ConstantHelper.IS_NEED_TO_REFRESH_FILTER_RECYCLERVIEW = true;
                        }
                        finish();
                    }
                };
                AnimHelper.AnimMenuClick(buttonDelete,adapter);
            }
        });

        spinnerCategory = findViewById(R.id.constructor_spinner_category);
        spinnerKitchen = findViewById(R.id.constructor_spinner_kitchen);
        spinnerPreferences = findViewById(R.id.constructor_spinner_preferences);
        ArrayAdapter<String> adapterSpinnerCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourceCategory);
        ArrayAdapter<String> adapterSpinnerKitchen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourceKitchen);
        ArrayAdapter<String> adapterSpinnerPreferences = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourcePreferences);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerKitchen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerPreferences.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinnerCategory);
        spinnerKitchen.setAdapter(adapterSpinnerKitchen);
        spinnerPreferences.setAdapter(adapterSpinnerPreferences);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valueCategory = resourceCategory[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerKitchen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valueKitchen = resourceKitchen[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valuePreferences = resourcePreferences[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ImageView buttonAddIngredient = findViewById(R.id.constructor_imageView_add_ingredient);
        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowBuilder.newRowTable2().init().empty().create();
                AnimHelper.AnimAddButtonIngredients(buttonAddIngredient);
            }
        });
        final ImageView buttonAddStep = findViewById(R.id.new_recipe_imageView_add_step);
        buttonAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowBuilder.newRowTable3().init().empty().create();
                AnimHelper.AnimAddButtonSteps(buttonAddStep);
            }
        });

        editTextRecipe = findViewById(R.id.constructor_editText_name);
        editTextRecipe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((editTextRecipe.getText()).toString().equals("Название рецепта"))
                    editTextRecipe.setText("");
            }
        });
        editTextRecipe.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextRecipe.setRawInputType(InputType.TYPE_CLASS_TEXT);

        editTextTime = findViewById(R.id.constructor_editText_time);
        editTextPortion = findViewById(R.id.constructor_editText_portion);
        imageViewImage = findViewById(R.id.constructor_imageView_image);

        final PhotoHelper phT1 = new PhotoHelper(ActivityConstructor.this);
        imageViewImage.setTag(phT1);
        imageViewImage.setOnClickListener(new View.OnClickListener() {
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
                AnimHelper.AnimClick(imageViewImage,adapter);
            }
        });

        imageViewCamera = findViewById(R.id.constructor_imageView_camera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
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
                AnimHelper.AnimPhotoLoadClick (imageViewCamera, adapter);
            }
        });

        imageViewLoad = findViewById(R.id.constructor_imageView_load);
        imageViewLoad.setOnClickListener(new View.OnClickListener() {
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
                AnimHelper.AnimPhotoLoadClick (imageViewLoad, adapter);
            }
        });
    }

    private void prepareActivityNewRecipe () {
        rowBuilder.newRowTable2().init().empty().create();
        rowBuilder.newRowTable3().init().empty().create();
    }

    private void prepareActivityEditRecipe () {
        TextView textViewTitle = findViewById(R.id.constructor_textView_title);
        textViewTitle.setText("Редактор рецепта");

        cursor = SQL.getTable1Pos(filterId);

        cursor.moveToFirst();

        editTextRecipe.setText("" + cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_RECIPE)));
        editTextTime.setText("" + cursor.getInt(cursor.getColumnIndex(SQL.TABLE1_COLUMN_TIME)));
        editTextPortion.setText("" + cursor.getInt(cursor.getColumnIndex(SQL.TABLE1_COLUMN_PORTION)));

        String imgFull = cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_IMG_FULL));
        String imgTitle = cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_IMG_TITLE));

        PhotoHelper phT1 = (((PhotoHelper) imageViewImage.getTag()));
        imageViewImage.getTag();
        if (imgFull !=null && imgTitle!=null) {
            phT1.setHavePhoto(true);
            phT1.setFileCamera(new File(imgFull));
            phT1.setFileImageView(new File(imgTitle));
            imageViewImage.setImageURI(Uri.parse(imgTitle));
        }

        String category = cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_CATEGORY));
        for (int i = 0; i < spinnerCategory.getCount(); i++) {
            if (spinnerCategory.getItemAtPosition(i).equals(category)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        String kitchen = cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_KITCHEN));
        for (int i = 0; i < spinnerKitchen.getCount(); i++) {
            if (spinnerKitchen.getItemAtPosition(i).equals(kitchen)) {
                spinnerKitchen.setSelection(i);
                break;
            }
        }

        String preferences = cursor.getString(cursor.getColumnIndex(SQL.TABLE1_COLUMN_PREFERENCES));
        for (int i = 0; i < spinnerPreferences.getCount(); i++) {
            if (spinnerPreferences.getItemAtPosition(i).equals(preferences)) {
                spinnerPreferences.setSelection(i);
                break;
            }
        }

        cursor.close();

        cursor = SQL.getTable2Pos(filterId);
        if (cursor.moveToFirst()) {
            do {
                rowBuilder.newRowTable2().init().data(cursor).create();
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = SQL.getTable3Pos(filterId);
        if (cursor.moveToFirst()) {
            do {
                rowBuilder.newRowTable3().init().data(cursor).create();
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void prepareButtonCreateRecipe () {
        final ImageView buttonCreateRecipe = findViewById(R.id.constructor_imageView_create);
        buttonCreateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(newRecipeMode){
                            int id = saveTable1 ();
                            Log.d(ActivityMain.LOG_TAG,"saveTable1, new_TABLE1_COLUMN_ID = " + id);
                            saveTable2 (id);
                            Log.d(ActivityMain.LOG_TAG,"saveTable2");
                            saveTable3 (id);
                            Log.d(ActivityMain.LOG_TAG,"saveTable3");
                            recipeCreated = true;
                        }
                        else {
                            saveTable1 ();
                            Log.d(ActivityMain.LOG_TAG,"updateTable1, new_TABLE1_COLUMN_ID = " + filterId);
                            saveTable2 (filterId);
                            Log.d(ActivityMain.LOG_TAG,"updateTable2, new_TABLE1_COLUMN_ID = " + filterId);
                            saveTable3 (filterId);
                            Log.d(ActivityMain.LOG_TAG,"updateTable3, new_TABLE1_COLUMN_ID = " + filterId);
                        }
                        ConstantHelper.IS_NEED_TO_REFRESH_FILTER_RECYCLERVIEW = true;
                        finish();
                    }
                };
                AnimHelper.AnimCreate(buttonCreateRecipe, adapter);
            }
        });
    }

    private int saveTable1 () {
        String recipe = editTextRecipe.getText().toString();

        Integer time;
        if(editTextTime.getText().toString().equals(""))
            time = null;
        else
            time = Integer.parseInt(editTextTime.getText().toString());

        Integer portion;
        if (editTextPortion.getText().toString().equals(""))
            portion = null;
        else
            portion = Integer.parseInt(editTextPortion.getText().toString());

        String imgFull;
        String imgTitle;
        PhotoHelper ph = ((PhotoHelper) imageViewImage.getTag());
        if (ph.isHavePhoto()){
            if (ph.getFileCamera()==null)
                imgFull = ph.getFileLoad().getPath();
            else
                imgFull = ph.getFileCamera().getPath();
            imgTitle = ph.getFileImageView().getPath();
        }
        else {
            imgFull = null;
            imgTitle = null;
        }
        if (newRecipeMode)
            return SQL.addPositionInTable1(recipe, valueCategory, valueKitchen, valuePreferences, time, portion, imgFull, imgTitle);
        else{
            SQL.updatePositionInTable1(filterId, recipe, valueCategory, valueKitchen, valuePreferences, time, portion, imgFull, imgTitle);
            return -1;
        }
    }

    private void saveTable2 (int id_recipe) {
        View view;
        int count = linearLayoutIngredients.getChildCount();
        for (int idView = 0; idView < count; idView++){
            view = linearLayoutIngredients.getChildAt(idView);
            editTextIngredients = view.findViewById(R.id.item_constructor_ingredients_editText_ingredient);
            editTextQuantity = view.findViewById(R.id.item_constructor_ingredients_editText_quantity);
            spinnerMeasure = view.findViewById(R.id.item_constructor_ingredients_spinner_measure);
            String ingredients;
            if (editTextIngredients.getText().toString().equals(""))
                ingredients = "";
            else
                ingredients = editTextIngredients.getText().toString();
            Integer quantity;
            if (editTextQuantity.getText().toString().equals(""))
                quantity = null;
            else
                quantity = Integer.parseInt(editTextQuantity.getText().toString());
            String measure = spinnerMeasure.getSelectedItem().toString();
            if (newRecipeMode)
                SQL.addPositionInTable2 (id_recipe,ingredients,quantity,measure);
            else {
                if (view.getTag()!=null) {
                    int id = (int) view.getTag();
                    SQL.updatePositionInTable2(id, id_recipe, ingredients, quantity, measure);
                }
                else
                    SQL.addPositionInTable2(id_recipe, ingredients, quantity, measure);
            }
        }
    }

    private void saveTable3 (int id_recipe) {
        View view;
        int count = linearLayoutSteps.getChildCount();
        for (int idView = 0; idView < count; idView++){
            view = linearLayoutSteps.getChildAt(idView);
            textViewNumber = view.findViewById(R.id.item_constructor_steps_editText_number);
            editTextText = view.findViewById(R.id.item_constructor_steps_editText_text);
            imageViewImageStep = view.findViewById(R.id.item_constructor_steps_imageView_image);
            Integer number;
            if (textViewNumber.getText().toString().equals(""))
                number = null;
            else
                number = Integer.parseInt(textViewNumber.getText().toString());
            String text = editTextText.getText().toString();
            String imgFull;
            String imgTitle;
            PhotoHelper ph = ((PhotoHelper) imageViewImageStep.getTag());
            if (ph.isHavePhoto()){
                if (ph.getFileCamera()==null)
                    imgFull = ph.getFileLoad().getPath();
                else
                    imgFull = ph.getFileCamera().getPath();
                imgTitle = ph.getFileImageView().getPath();
            }
            else {
                imgFull = null;
                imgTitle = null;
            }
            if (newRecipeMode)
                SQL.addPositionInTable3 (id_recipe, number, text, imgFull, imgTitle);
            else {
                if (view.getTag()!=null) {
                    int id = (int) view.getTag();
                    SQL.updatePositionInTable3 (id, id_recipe, number, text, imgFull, imgTitle);
                }
                else
                    SQL.addPositionInTable3 (id_recipe, number, text, imgFull, imgTitle);
            }
        }
    }

    class RowBuilder {
        LayoutInflater ltInflater;
        LinearLayout linearLayoutIngredients, linearLayoutSteps;

        public RowBuilder() {
            ltInflater = getLayoutInflater();
            linearLayoutIngredients = findViewById(R.id.constructor_linearLayout_ingredients);
            linearLayoutSteps = findViewById(R.id.constructor_linearLayout_steps);
        }

        public RowTable2 newRowTable2() {
            return new RowTable2();
        }

        public RowTable3 newRowTable3() {
            return new RowTable3();
        }

        class RowTable2 {
            Spinner spinnerMeasure;
            View viewRowTable2;
            ArrayAdapter<String> adapterSpinnerMeasure;
            ImageView imageViewDeleteIngredient;
            AnimatorListenerAdapter adapterImageViewDeleteIngredient;
            EditText editTextIngredient;

            private RowTable2 init () {
                viewRowTable2 = ltInflater.inflate(R.layout.item_constructor_ingredients, linearLayoutIngredients, false);
                spinnerMeasure = viewRowTable2.findViewById(R.id.item_constructor_ingredients_spinner_measure);
                adapterSpinnerMeasure = new ArrayAdapter<>(ActivityConstructor.this, android.R.layout.simple_spinner_item, resourceMeasure);
                adapterSpinnerMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMeasure.setAdapter(adapterSpinnerMeasure);
                imageViewDeleteIngredient = viewRowTable2.findViewById(R.id.item_constructor_ingredients_imageView_delete);
                editTextIngredient = viewRowTable2.findViewById(R.id.item_constructor_ingredients_editText_ingredient);
                editTextIngredient.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editTextIngredient.setRawInputType(InputType.TYPE_CLASS_TEXT);
                return this;
            }

            private RowTable2 empty() {
                adapterImageViewDeleteIngredient = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutIngredients.removeView(viewRowTable2);
                    }
                };
                return this;
            }

            private RowTable2 data(Cursor cursor) {
                final int id = cursor.getInt(cursor.getColumnIndex(SQL.TABLE2_COLUMN_ID));
                String ingredients = cursor.getString(cursor.getColumnIndex(SQL.TABLE2_COLUMN_INGREDIENTS));
                int quantity = cursor.getInt(cursor.getColumnIndex(SQL.TABLE2_COLUMN_QUANTITY));
                String measure = cursor.getString(cursor.getColumnIndex(SQL.TABLE2_COLUMN_MEASURE));
                viewRowTable2.setTag(id);

                editTextIngredient.setText("" + ingredients);
                editTextQuantity = viewRowTable2.findViewById(R.id.item_constructor_ingredients_editText_quantity);
                editTextQuantity.setText("" + quantity);
                for (int i = 0; i < spinnerMeasure.getCount(); i++) {
                    if (spinnerMeasure.getItemAtPosition(i).equals(measure)) {
                        spinnerMeasure.setSelection(i);
                        break;
                    }
                }
                adapterImageViewDeleteIngredient = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        SQL.deleteRowTable2(id);
                        linearLayoutIngredients.removeView(viewRowTable2);
                    }
                };
                return this;
            }

            private void create() {
                imageViewDeleteIngredient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimHelper.AnimConstructorDeleteRow(viewRowTable2, adapterImageViewDeleteIngredient);
                    }
                });
                AnimHelper.AnimConstructorAddRow(viewRowTable2);
                linearLayoutIngredients.addView(viewRowTable2);
            }
        }

        class RowTable3 {
            View viewRowTable3;
            ImageView imageViewDeleteStep;
            ImageView imageViewImage;
            AnimatorListenerAdapter adapterImageViewDeleteStep;
            PhotoHelper ph;
            ImageView imageViewCamera;
            ImageView imageViewLoad;
            EditText editTextText;

            private RowTable3 init () {
                viewRowTable3 = ltInflater.inflate(R.layout.item_constructor_steps, linearLayoutSteps, false);
                imageViewDeleteStep = viewRowTable3.findViewById(R.id.item_constructor_steps_imageView_delete);
                imageViewImage = viewRowTable3.findViewById(R.id.item_constructor_steps_imageView_image);
                ph = new PhotoHelper(ActivityConstructor.this);
                imageViewImage.setTag(ph);
                imageViewImage.setOnClickListener(new View.OnClickListener() {
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
                        AnimHelper.AnimClick(imageViewImage,adapter);
                    }
                });
                imageViewCamera = viewRowTable3.findViewById(R.id.item_constructor_steps_imageView_camera);
                imageViewCamera.setOnClickListener(new View.OnClickListener() {
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
                        AnimHelper.AnimPhotoLoadClick(imageViewCamera, adapter);
                    }
                });
                imageViewLoad = viewRowTable3.findViewById(R.id.item_constructor_steps_imageView_load);
                imageViewLoad.setOnClickListener(new View.OnClickListener() {
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
                        AnimHelper.AnimPhotoLoadClick(imageViewLoad, adapter);
                    }
                });
                editTextText = viewRowTable3.findViewById(R.id.item_constructor_steps_editText_text);
                editTextText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editTextText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                return this;
            }

            private RowTable3 empty () {
                adapterImageViewDeleteStep = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearLayoutSteps.removeView(viewRowTable3);
                    }
                };
                return this;
            }

            private RowTable3 data (Cursor cursor) {
                final int id = cursor.getInt(cursor.getColumnIndex(SQL.TABLE3_COLUMN_ID));
                int number = cursor.getInt(cursor.getColumnIndex(SQL.TABLE3_COLUMN_NUMBER));
                String text = cursor.getString(cursor.getColumnIndex(SQL.TABLE3_COLUMN_TEXT));
                final String imgFull = cursor.getString(cursor.getColumnIndex(SQL.TABLE3_COLUMN_IMG_FULL));
                String imgTitle = cursor.getString(cursor.getColumnIndex(SQL.TABLE3_COLUMN_IMG_TITLE));
                viewRowTable3.setTag(id);
                adapterImageViewDeleteStep = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        SQL.deleteRowTable3(id);
                        linearLayoutSteps.removeView(viewRowTable3);
                    }
                };
                if (imgFull!=null && imgTitle!=null) {
                    ph.setHavePhoto(true);
                    ph.setFileCamera(new File(imgFull));
                    ph.setFileImageView(new File(imgTitle));
                    imageViewImage.setImageURI(Uri.parse(imgTitle));
                }
                textViewNumber = viewRowTable3.findViewById(R.id.item_constructor_steps_editText_number);
                textViewNumber.setText("" + number);
                editTextText.setText("" + text);
                return this;
            }

            private void create() {
                imageViewDeleteStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimHelper.AnimConstructorDeleteRow(viewRowTable3, adapterImageViewDeleteStep);
                    }
                });
                linearLayoutSteps.addView(viewRowTable3);
                AnimHelper.AnimConstructorAddRow(viewRowTable3);
            }
        }
    }
}