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
import reciptizer.Activity_Main;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.Table2Row;
import reciptizer.Common.Recipe.Table3Row;
import reciptizer.Server.ServerAPISingleton;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static reciptizer.Common.Helpers.AnimHelper.AnimClick;
import static reciptizer.Common.Helpers.AnimHelper.AnimMenuClick;

public class Activity_Recipe extends FragmentActivity implements SaveRecipeDialogFragment.SaveRecipeDialogListener {

    protected Recipe recipe;
    protected Integer filterResult_id;
    private int portion;
    private LinearLayout linLayout_Table2;

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
        ServerAPISingleton.getInstance(this.getApplicationContext()).sendRecipe(recipe);

        if (recipe.table1.TABLE1_COLUMN_IMG_TITLE!=null&&!recipe.table1.TABLE1_COLUMN_IMG_TITLE.equals("null"))
            ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(recipe.table1.TABLE1_COLUMN_IMG_TITLE);

        if (recipe.table1.TABLE1_COLUMN_IMG_FULL!=null&&!recipe.table1.TABLE1_COLUMN_IMG_FULL.equals("null"))
            ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(recipe.table1.TABLE1_COLUMN_IMG_FULL);

        for (Table3Row table3Row : recipe.rowsTable3) {
            if (table3Row.TABLE3_COLUMN_IMG_TITLE!=null&&!table3Row.TABLE3_COLUMN_IMG_TITLE.equals("null"))
                ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(table3Row.TABLE3_COLUMN_IMG_TITLE);

            if (table3Row.TABLE3_COLUMN_IMG_FULL!=null&&!table3Row.TABLE3_COLUMN_IMG_FULL.equals("null"))
                ServerAPISingleton.getInstance(this.getApplicationContext()).sendImage(table3Row.TABLE3_COLUMN_IMG_FULL);
        }
    }

    @Override
    public String setDialogTitle() {
        return "Поделиться рецептом";
    }

    private void getIdRecipeFromIntent() {
        Intent intent = getIntent();
        filterResult_id = Integer.parseInt(intent.getStringExtra(DB.TABLE1_COLUMN_ID));

        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: setIdRecipeFromIntent | id = " + filterResult_id);
    }

    protected void setStatusBarColor (int colorForStatusBar, int ColorForNavigationBar) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorForStatusBar));
        window.setNavigationBarColor(ContextCompat.getColor(this, ColorForNavigationBar));
    }

    private void initPanel() {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: initPanel");

        LinearLayout linearLayoutTitle = findViewById(R.id.Recipe_LinearLayout_Title);
        createLinearLayoutTitle(linearLayoutTitle);

        Button buttonSearch = findViewById(R.id.Recipe_button_search);
        createButtonSearch(buttonSearch);

        Button buttonSave = findViewById(R.id.Recipe_button_save);
        createButtonSave(buttonSave);

        Button buttonEdit = findViewById(R.id.Recipe_button_edit);
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
                        Intent intent = new Intent(Activity_Recipe.this, Activity_New_Edit_Recipe.class);
                        intent.putExtra(DB.TABLE1_COLUMN_ID, filterResult_id);
                        startActivity(intent);
                        finish();
                    }
                };
                AnimMenuClick (buttonEdit, adapter);
            }
        });
    }

    protected void getRecipeAndSetValues() {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: getRecipeAndSetValues");

        recipe = DB.getRecipe(filterResult_id);
        initTable1();
        initTable2();
        initTable3();
    }

    protected void initTable1() {
        TextView textView_TABLE1_COLUMN_RECIPE = findViewById(R.id.Recipe_textView_TABLE1_COLUMN_RECIPE);
        createTextViewRecipe(textView_TABLE1_COLUMN_RECIPE);

        TextView textView_TABLE1_COLUMN_CATEGORY = findViewById(R.id.Recipe_textView_TABLE1_COLUMN_CATEGORY);
        createTextViewCategory(textView_TABLE1_COLUMN_CATEGORY);

        TextView textView_TABLE1_COLUMN_KITCHEN = findViewById(R.id.Recipe_textView_TABLE1_COLUMN_KITCHEN);
        createTextViewKitchen(textView_TABLE1_COLUMN_KITCHEN);

        TextView textView_TABLE1_COLUMN_PREFERENCES = findViewById(R.id.Recipe_textView_TABLE1_COLUMN_PREFERENCES);
        createTextViewPreferences(textView_TABLE1_COLUMN_PREFERENCES);

        TextView textView_TABLE1_COLUMN_TIME = findViewById(R.id.Recipe_textView_TABLE1_COLUMN_TIME);
        createTextViewTime(textView_TABLE1_COLUMN_TIME);


        Spinner spinner_TABLE1_COLUMN_PORTION = findViewById(R.id.Recipe_spinner_TABLE1_COLUMN_PORTION);
        createSpinnerPortion(spinner_TABLE1_COLUMN_PORTION);

        ImageView imageView_TABLE1_COLUMN_IMG_TITLE = findViewById(R.id.Recipe_imageView_TABLE1_COLUMN_IMG_TITLE);
        createImageViewRecipe(imageView_TABLE1_COLUMN_IMG_TITLE);
    }

    protected void createTextViewRecipe (TextView textView_TABLE1_COLUMN_RECIPE){
        textView_TABLE1_COLUMN_RECIPE.setText("" + recipe.table1.TABLE1_COLUMN_RECIPE);
    }

    protected void createTextViewCategory (TextView textView_TABLE1_COLUMN_CATEGORY){
        textView_TABLE1_COLUMN_CATEGORY.setText("" + recipe.table1.TABLE1_COLUMN_CATEGORY);
    }

    protected void createTextViewKitchen (TextView textView_TABLE1_COLUMN_KITCHEN){
        textView_TABLE1_COLUMN_KITCHEN.setText("" + recipe.table1.TABLE1_COLUMN_KITCHEN);
    }

    protected void createTextViewPreferences (TextView textView_TABLE1_COLUMN_PREFERENCES){
        textView_TABLE1_COLUMN_PREFERENCES.setText("" + recipe.table1.TABLE1_COLUMN_PREFERENCES);
    }

    protected void createTextViewTime (TextView textView_TABLE1_COLUMN_TIME){
        textView_TABLE1_COLUMN_TIME.setText("" + recipe.table1.TABLE1_COLUMN_TIME);
    }

    protected void createSpinnerPortion (Spinner spinner_TABLE1_COLUMN_PORTION){
        final String [] TABLE1_PORTION  = getResources().getStringArray(R.array.TABLE1_COLUMN_PORTION);
        ArrayAdapter<String> adapterSpinnerPortion = new ArrayAdapter<>(this, R.layout.spinner_item_recipe, TABLE1_PORTION);
        spinner_TABLE1_COLUMN_PORTION.setAdapter(adapterSpinnerPortion);

        portion = recipe.table1.TABLE1_COLUMN_PORTION;
        for (int i = 0; i < spinner_TABLE1_COLUMN_PORTION.getCount(); i++) {
            if (spinner_TABLE1_COLUMN_PORTION.getItemAtPosition(i).equals(""+ portion)) {
                spinner_TABLE1_COLUMN_PORTION.setSelection(i);
                break;
            }
        }

        spinner_TABLE1_COLUMN_PORTION.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTable2 (TABLE1_PORTION[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void createImageViewRecipe(final ImageView imageView_TABLE1_COLUMN_IMG) {
        final String img_png = recipe.table1.TABLE1_COLUMN_IMG_TITLE;
        if (img_png==null||img_png.equals("null"))
        {
            imageView_TABLE1_COLUMN_IMG.setImageResource(R.drawable.no_img);
        }
        else
            {
            imageView_TABLE1_COLUMN_IMG.setImageURI(Uri.parse(img_png));

            final String img_jpeg = recipe.table1.TABLE1_COLUMN_IMG_FULL;
            imageView_TABLE1_COLUMN_IMG.setOnClickListener(new View.OnClickListener() {
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
                    AnimClick(imageView_TABLE1_COLUMN_IMG, adapter);
                }
            });
        }
    }

    protected void initTable2() {
        linLayout_Table2 = findViewById(R.id.Recipe_linearLayout_TABLE_2);
        LayoutInflater ltInflater = getLayoutInflater();

        for (Table2Row table2Row : recipe.rowsTable2) {
            View viewRowTable2 = ltInflater.inflate(R.layout.item_recipe_table2, linLayout_Table2, false);

            TextView textView_TABLE2_COLUMN_INGREDIENTS = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_INGREDIENTS);
            createTextViewIngredients(textView_TABLE2_COLUMN_INGREDIENTS,table2Row);

            TextView textView_TABLE2_COLUMN_QUANTITY = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
            createTextViewQuantity(textView_TABLE2_COLUMN_QUANTITY,table2Row);

            TextView textView_TABLE2_COLUMN_MEASURE = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_MEASURE);
            createTextViewMeasure(textView_TABLE2_COLUMN_MEASURE,table2Row);

            linLayout_Table2.addView(viewRowTable2);
        }
    }

    protected void createTextViewIngredients(TextView textView_TABLE2_COLUMN_INGREDIENTS, Table2Row table2Row){
        String ingredients = table2Row.TABLE2_COLUMN_INGREDIENTS;
        textView_TABLE2_COLUMN_INGREDIENTS.setText("" + ingredients);
    }

    protected void createTextViewQuantity(TextView textView_TABLE2_COLUMN_QUANTITY, Table2Row table2Row){
        int quantity = table2Row.TABLE2_COLUMN_QUANTITY;
        textView_TABLE2_COLUMN_QUANTITY.setText("" + quantity);
        textView_TABLE2_COLUMN_QUANTITY.setTag(quantity);
    }

    protected void createTextViewMeasure(TextView textView_TABLE2_COLUMN_MEASURE, Table2Row table2Row){
        String measure = table2Row.TABLE2_COLUMN_MEASURE;
        textView_TABLE2_COLUMN_MEASURE.setText("" + measure);
        textView_TABLE2_COLUMN_MEASURE.setTag(measure);
    }

    private void calculateTable2 (String newPortion) {
        double k = (Double.parseDouble(newPortion)) / (double) portion;
        DecimalFormat format = new DecimalFormat("#.#");
        format.setRoundingMode(RoundingMode.HALF_UP);
        View view;
        int count = linLayout_Table2.getChildCount();

        for (int idView = 0; idView < count; idView++){
            view = linLayout_Table2.getChildAt(idView);

            TextView textView_TABLE2_COLUMN_QUANTITY = view.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
            TextView textView_TABLE2_COLUMN_MEASURE = view.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_MEASURE);

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
        LinearLayout linLayout_Table3 = findViewById(R.id.Recipe_linearLayout_TABLE_3);
        LayoutInflater ltInflater = getLayoutInflater();

        for (Table3Row table3Row : recipe.rowsTable3) {
            View viewRowTable3 = ltInflater.inflate(R.layout.item_recipe_table3, linLayout_Table3, false);

            TextView textView_TABLE3_COLUMN_NUMBER = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_NUMBER);
            createTextViewNumber(textView_TABLE3_COLUMN_NUMBER, table3Row);

            TextView textView_TABLE3_COLUMN_TEXT = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_TEXT);
            createTextViewText(textView_TABLE3_COLUMN_TEXT, table3Row);

            ImageView imageView_TABLE3_COLUMN_IMG_TITLE = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_IMG_TITLE);
            createImageViewImgSteps(imageView_TABLE3_COLUMN_IMG_TITLE, table3Row);

            linLayout_Table3.addView(viewRowTable3);
        }
    }

    protected void createTextViewNumber(TextView textView_TABLE3_COLUMN_NUMBER, Table3Row table3Row){
        int number = table3Row.TABLE3_COLUMN_NUMBER;
        textView_TABLE3_COLUMN_NUMBER.setText("" + number);
    }

    protected void createTextViewText(TextView textView_TABLE3_COLUMN_TEXT, Table3Row table3Row){
        String text = table3Row.TABLE3_COLUMN_TEXT;
        textView_TABLE3_COLUMN_TEXT.setText("" + text);
    }

    protected void createImageViewImgSteps(final ImageView imageView_TABLE3_COLUMN_IMG_TITLE, Table3Row table3Row){
        final String img_png = table3Row.TABLE3_COLUMN_IMG_TITLE;
        if (img_png==null||img_png.equals("null")){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView_TABLE3_COLUMN_IMG_TITLE.getLayoutParams();
            params.width = 0;
            params.height = 0;
            params.setMargins(0,0,0,0);
            imageView_TABLE3_COLUMN_IMG_TITLE.setLayoutParams(params);
        }
        else {
            imageView_TABLE3_COLUMN_IMG_TITLE.setImageURI(Uri.parse(img_png));

            final String img_jpeg = table3Row.TABLE3_COLUMN_IMG_FULL;
            imageView_TABLE3_COLUMN_IMG_TITLE.setOnClickListener(new View.OnClickListener() {
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
                    AnimClick (imageView_TABLE3_COLUMN_IMG_TITLE, adapter);
                }
            });
        }
    }
}