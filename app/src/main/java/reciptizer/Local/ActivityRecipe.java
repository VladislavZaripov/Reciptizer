package reciptizer.Local;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.example.reciptizer.R;
import reciptizer.ActivityMain;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.Table2Row;
import reciptizer.Common.Recipe.Table3Row;
import reciptizer.Server.ServerAPISingleton;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static reciptizer.Common.Helpers.AnimHelper.AnimClick;
import static reciptizer.Common.Helpers.AnimHelper.AnimMenuClick;

public class ActivityRecipe extends FragmentActivity implements SaveRecipeDialogFragment.SaveRecipeDialogListener {

    protected Recipe recipe;
    protected Integer filterId;
    private int portion;
    private LinearLayout linLayoutIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getIdRecipeFromIntent ();

        setStatusBarColor (R.color.RecipeSetStatusBarColor,R.color.RecipeSetStatusBarColor);

        initPanel ();

        getRecipeAndSetValues ();
    }

    @Override
    public void onSaveRecipeDialogPositiveClick(DialogFragment dialog) {
        if (recipe.table1.imageTitle !=null&&!recipe.table1.imageTitle.equals("null"))
            ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(recipe.table1.imageTitle);

        if (recipe.table1.imageFull !=null&&!recipe.table1.imageFull.equals("null"))
            ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(recipe.table1.imageFull);

        for (Table3Row table3Row : recipe.rowsTable3) {
            if (table3Row.imageTitle !=null&&!table3Row.imageTitle.equals("null"))
                ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(table3Row.imageTitle);

            if (table3Row.imageFull !=null&&!table3Row.imageFull.equals("null"))
                ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(table3Row.imageFull);
        }

        ServerAPISingleton.getInstance(this.getApplicationContext()).sendRecipe(recipe);
    }

    @Override
    public String setDialogTitle() {
        return "Поделиться рецептом";
    }

    private void getIdRecipeFromIntent() {
        Intent intent = getIntent();
        filterId = Integer.parseInt(intent.getStringExtra(SQL.TABLE1_COLUMN_ID));

        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: setIdRecipeFromIntent | id = " + filterId);
    }

    protected void setStatusBarColor (int colorForStatusBar, int colorForNavigationBar) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorForStatusBar));
        window.setNavigationBarColor(ContextCompat.getColor(this, colorForNavigationBar));
    }

    private void initPanel() {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: initPanel");

        LinearLayout linearLayoutTitle = findViewById(R.id.recipe_linearLayout_title);
        createLinearLayoutTitle(linearLayoutTitle);

        Button buttonSearch = findViewById(R.id.recipe_button_search);
        createButtonSearch(buttonSearch);

        Button buttonSave = findViewById(R.id.recipe_button_save);
        createButtonSave(buttonSave);

        Button buttonEdit = findViewById(R.id.recipe_button_edit);
        createButtonEdit(buttonEdit);
    }

    protected void createLinearLayoutTitle (LinearLayout linearLayoutTitle){

    }

    protected void createButtonSearch (final Button buttonSearch) {
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
                AnimMenuClick (buttonSearch, adapter);
            }
        });
    }

    protected void createButtonSave (final  Button buttonSave) {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        SaveRecipeDialogFragment dialog = new SaveRecipeDialogFragment();
                        dialog.show(getSupportFragmentManager(), "RecipeDialog");
                    }
                };
                AnimMenuClick (buttonSave, adapter);
            }
        });
    }

    protected void createButtonEdit (final Button buttonEdit) {
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(ActivityRecipe.this, ActivityConstructor.class);
                        intent.putExtra(SQL.TABLE1_COLUMN_ID, filterId);
                        startActivity(intent);
                        finish();
                    }
                };
                AnimMenuClick (buttonEdit, adapter);
            }
        });
    }

    protected void getRecipeAndSetValues() {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: getRecipeAndSetValues");

        recipe = SQL.getRecipe(filterId);
        initTable1();
        initTable2();
        initTable3();
    }

    protected void initTable1() {
        TextView textViewRecipe = findViewById(R.id.recipe_textView_name);
        createTextViewRecipe(textViewRecipe);

        TextView textViewCategory = findViewById(R.id.recipe_textView_category);
        createTextViewCategory(textViewCategory);

        TextView textViewKitchen = findViewById(R.id.recipe_textView_kitchen);
        createTextViewKitchen(textViewKitchen);

        TextView textViewPreferences = findViewById(R.id.recipe_textView_preferences);
        createTextViewPreferences(textViewPreferences);

        TextView textViewTime = findViewById(R.id.recipe_textView_time);
        createTextViewTime(textViewTime);

        Spinner spinnerPortion = findViewById(R.id.recipe_spinner_portion);
        createSpinnerPortion(spinnerPortion);

        ImageView imageViewImage = findViewById(R.id.recipe_imageView_image);
        createImageViewRecipe(imageViewImage);
    }

    protected void createTextViewRecipe (TextView textViewRecipe){
        textViewRecipe.setText("" + recipe.table1.recipe);
    }

    protected void createTextViewCategory (TextView textViewCategory){
        textViewCategory.setText("" + recipe.table1.category);
    }

    protected void createTextViewKitchen (TextView textViewKitchen){
        textViewKitchen.setText("" + recipe.table1.kitchen);
    }

    protected void createTextViewPreferences (TextView textViewPreferences){
        textViewPreferences.setText("" + recipe.table1.preferences);
    }

    protected void createTextViewTime (TextView textViewTime){
        textViewTime.setText("" + recipe.table1.time);
    }

    protected void createSpinnerPortion (Spinner spinnerPortion){
        final String [] TABLE1_PORTION  = getResources().getStringArray(R.array.portion);
        ArrayAdapter<String> adapterSpinnerPortion = new ArrayAdapter<>(this, R.layout.spinner_filter, TABLE1_PORTION);
        spinnerPortion.setAdapter(adapterSpinnerPortion);

        portion = recipe.table1.portion;
        for (int i = 0; i < spinnerPortion.getCount(); i++) {
            if (spinnerPortion.getItemAtPosition(i).equals(""+ portion)) {
                spinnerPortion.setSelection(i);
                break;
            }
        }

        spinnerPortion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTable2 (TABLE1_PORTION[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void createImageViewRecipe(final ImageView imageViewImage) {
        final String img_png = recipe.table1.imageTitle;
        if (img_png==null||img_png.equals("null"))
        {
            imageViewImage.setImageResource(R.drawable.no_img);
        }
        else
            {
            imageViewImage.setImageURI(Uri.parse(img_png));

            final String img_jpeg = recipe.table1.imageFull;
            imageViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragment(img_jpeg, img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick(imageViewImage, adapter);
                }
            });
        }
    }

    protected void initTable2() {
        linLayoutIngredients = findViewById(R.id.recipe_linearLayout_ingredients);
        LayoutInflater ltInflater = getLayoutInflater();

        for (Table2Row table2Row : recipe.rowsTable2) {
            View viewRowTable2 = ltInflater.inflate(R.layout.item_recipe_ingredients, linLayoutIngredients, false);

            TextView textViewIngredients = viewRowTable2.findViewById(R.id.item_recipe_ingredients_textView_ingredient);
            createTextViewIngredients(textViewIngredients,table2Row);

            TextView textViewQuantity = viewRowTable2.findViewById(R.id.item_recipe_ingredients_textView_quantity);
            createTextViewQuantity(textViewQuantity,table2Row);

            TextView textViewMeasure = viewRowTable2.findViewById(R.id.item_recipe_ingredients_textView_measure);
            createTextViewMeasure(textViewMeasure,table2Row);

            linLayoutIngredients.addView(viewRowTable2);
        }
    }

    protected void createTextViewIngredients(TextView textViewIngredients, Table2Row table2Row){
        String ingredients = table2Row.ingredient;
        textViewIngredients.setText("" + ingredients);
    }

    protected void createTextViewQuantity(TextView textViewQuantity, Table2Row table2Row){
        int quantity = table2Row.quantity;
        textViewQuantity.setText("" + quantity);
        textViewQuantity.setTag(quantity);
    }

    protected void createTextViewMeasure(TextView textViewMeasure, Table2Row table2Row){
        String measure = table2Row.measure;
        textViewMeasure.setText("" + measure);
        textViewMeasure.setTag(measure);
    }

    private void calculateTable2 (String newPortion) {
        double k = (Double.parseDouble(newPortion)) / (double) portion;
        DecimalFormat format = new DecimalFormat("#.#");
        format.setRoundingMode(RoundingMode.HALF_UP);
        View view;
        int count = linLayoutIngredients.getChildCount();

        for (int idView = 0; idView < count; idView++){
            view = linLayoutIngredients.getChildAt(idView);

            TextView textView_TABLE2_COLUMN_QUANTITY = view.findViewById(R.id.item_recipe_ingredients_textView_quantity);
            TextView textView_TABLE2_COLUMN_MEASURE = view.findViewById(R.id.item_recipe_ingredients_textView_measure);

            double quantity = Double.parseDouble(textView_TABLE2_COLUMN_QUANTITY.getTag().toString());
            String measure = textView_TABLE2_COLUMN_MEASURE.getTag().toString();

            switch (measure){
                case "кг":
                    if (quantity*k<1)
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity*k*1000));
                        textView_TABLE2_COLUMN_MEASURE.setText("гр");
                    }
                    else
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity * k));
                        textView_TABLE2_COLUMN_MEASURE.setText(measure);
                    }
                    break;
                case "гр":
                    if (quantity*k>1000)
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity*k/1000));
                        textView_TABLE2_COLUMN_MEASURE.setText("кг");
                    }
                    else
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity * k));
                        textView_TABLE2_COLUMN_MEASURE.setText(measure);
                    }
                    break;
                case "л":
                    if (quantity*k<1)
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity*k*1000));
                        textView_TABLE2_COLUMN_MEASURE.setText("мл");
                    }
                    else
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity * k));
                        textView_TABLE2_COLUMN_MEASURE.setText(measure);
                    }
                    break;
                case "мл":
                    if (quantity*k>1000)
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity*k/1000));
                        textView_TABLE2_COLUMN_MEASURE.setText("л");
                    }
                    else
                    {
                        textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity * k));
                        textView_TABLE2_COLUMN_MEASURE.setText(measure);
                    }
                    break;
                case "ст":
                case "ст.л":
                case "ч.л":
                case "шт":
                case "уп":
                case "банка":
                    textView_TABLE2_COLUMN_QUANTITY.setText(format.format(quantity*k));
                    break;
                case "по вк":
                case "щеп":
                    break;
            }
        }
    }

    protected void initTable3() {
        LinearLayout linLayout_Table3 = findViewById(R.id.recipe_linearLayout_steps);
        LayoutInflater ltInflater = getLayoutInflater();

        for (Table3Row table3Row : recipe.rowsTable3) {
            View viewRowTable3 = ltInflater.inflate(R.layout.item_recipe_steps, linLayout_Table3, false);

            TextView textViewNumber = viewRowTable3.findViewById(R.id.item_recipe_steps_textView_number);
            createTextViewNumber(textViewNumber, table3Row);

            TextView textViewText = viewRowTable3.findViewById(R.id.item_recipe_steps_textView_text);
            createTextViewText(textViewText, table3Row);

            ImageView imageViewImageTitle = viewRowTable3.findViewById(R.id.item_recipe_steps_imageView_image);
            createImageViewImgSteps(imageViewImageTitle, table3Row);

            linLayout_Table3.addView(viewRowTable3);
        }
    }

    protected void createTextViewNumber(TextView textViewNumber, Table3Row table3Row){
        int number = table3Row.number;
        textViewNumber.setText("" + number);
    }

    protected void createTextViewText(TextView textViewText, Table3Row table3Row){
        String text = table3Row.text;
        textViewText.setText("" + text);
    }

    protected void createImageViewImgSteps(final ImageView imageViewImageTitle, Table3Row table3Row){
        final String img_png = table3Row.imageTitle;
        if (img_png==null||img_png.equals("null")){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageViewImageTitle.getLayoutParams();
            params.width = 0;
            params.height = 0;
            params.setMargins(0,0,0,0);
            imageViewImageTitle.setLayoutParams(params);
        }
        else {
            imageViewImageTitle.setImageURI(Uri.parse(img_png));

            final String img_jpeg = table3Row.imageFull;
            imageViewImageTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragment(img_jpeg,img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick (imageViewImageTitle, adapter);
                }
            });
        }
    }
}