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
import java.util.List;

import static reciptizer.Common.Helpers.AnimHelper.AnimClick;
import static reciptizer.Common.Helpers.AnimHelper.AnimMenuClick;

public class Activity_Recipe extends FragmentActivity implements SaveRecipeDialogFragment.SaveRecipeDialogListener {

    protected Recipe recipe;
    protected Integer filterResult_id;

    Intent intent;
    int portion;
    LinearLayout linLayout_Table2;
    TextView textView_TABLE2_COLUMN_QUANTITY;
    TextView textView_TABLE2_COLUMN_MEASURE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getStringExtra();
        Log.d(Activity_Main.LOG_TAG, "ShowRecipe_getStringExtra: id = " + filterResult_id);
        prepareActivity();
        Log.d(Activity_Main.LOG_TAG, "ShowRecipe_prepareActivity");
        getRecipeAndSetValues();
        Log.d(Activity_Main.LOG_TAG, "getRecipeAndSetValues");
        setStatusBarColor();
        Log.d(Activity_Main.LOG_TAG,"ShowRecipe_setStatusBarColor");
    }

    @Override
    public void onSaveRecipeDialogPositiveClick(DialogFragment dialog) {
        ServerAPISingleton.getInstance(this.getApplicationContext()).sendRecipe(recipe);
    }

    @Override
    public String setDialogTitle() {
        return "Поделиться рецептом";
    }

    private void getStringExtra() {
        intent = getIntent();
        filterResult_id = Integer.parseInt(intent.getStringExtra(DB.TABLE1_COLUMN_ID));
    }

    protected void prepareActivity() {

        final Button button_search = findViewById(R.id.Recipe_button_search);
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
                AnimMenuClick (button_search, adapter);
            }
        });

        final Button button_save = findViewById(R.id.Recipe_button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
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
                AnimMenuClick (button_save, adapter);
            }
        });

        final Button button_edit = findViewById(R.id.Recipe_button_edit);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        intent = new Intent(Activity_Recipe.this, Activity_New_Edit_Recipe.class);
                        intent.putExtra(DB.TABLE1_COLUMN_ID, filterResult_id);
                        startActivity(intent);
                        finish();
                    }
                };
                AnimMenuClick (button_edit, adapter);
            }
        });
    }

    protected void getRecipeAndSetValues() {
        recipe = DB.getRecipe(filterResult_id);
        createTable1();
        createTable2();
        createTable3();
    }

    protected void createTable1() {
        TextView textView_TABLE1_COLUMN_RECIPE = findViewById(R.id.Recipe_TABLE1_COLUMN_RECIPE);
        TextView textView_TABLE1_COLUMN_CATEGORY = findViewById(R.id.Recipe_TABLE1_COLUMN_CATEGORY);
        TextView textView_TABLE1_COLUMN_KITCHEN = findViewById(R.id.Recipe_TABLE1_COLUMN_KITCHEN);
        TextView textView_TABLE1_COLUMN_PREFERENCES = findViewById(R.id.Recipe_TABLE1_COLUMN_PREFERENCES);
        TextView textView_TABLE1_COLUMN_TIME = findViewById(R.id.Recipe_TABLE1_COLUMN_TIME);
        Spinner spinner_TABLE1_COLUMN_PORTION = findViewById(R.id.Recipe_TABLE1_COLUMN_PORTION);
        final ImageView imageView_TABLE1_COLUMN_IMG = findViewById(R.id.Recipe_TABLE1_COLUMN_IMG);

        textView_TABLE1_COLUMN_RECIPE.setText("" + recipe.table1.TABLE1_COLUMN_RECIPE);
        textView_TABLE1_COLUMN_CATEGORY.setText("" + recipe.table1.TABLE1_COLUMN_CATEGORY);
        textView_TABLE1_COLUMN_KITCHEN.setText("" + recipe.table1.TABLE1_COLUMN_KITCHEN);
        textView_TABLE1_COLUMN_PREFERENCES.setText("" + recipe.table1.TABLE1_COLUMN_PREFERENCES);
        textView_TABLE1_COLUMN_TIME.setText("" + recipe.table1.TABLE1_COLUMN_TIME);

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

        setImageTable1(imageView_TABLE1_COLUMN_IMG);
    }
    protected void setImageTable1(final ImageView imageView_TABLE1_COLUMN_IMG) {
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

    protected void createTable2() {
        List<Table2Row> rowsTable2 = recipe.rowsTable2;

        for (Table2Row table2Row : rowsTable2) {
            String ingredients = table2Row.TABLE2_COLUMN_INGREDIENTS;
            int quantity = table2Row.TABLE2_COLUMN_QUANTITY;
            String measure = table2Row.TABLE2_COLUMN_MEASURE;

            linLayout_Table2 = findViewById(R.id.Recipe_LL_TABLE_2);
            LayoutInflater ltInflater = getLayoutInflater();
            View viewRowTable2 = ltInflater.inflate(R.layout.item_recipe_table2, linLayout_Table2, false);

            TextView textView_TABLE2_COLUMN_INGREDIENTS = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_INGREDIENTS);
            textView_TABLE2_COLUMN_INGREDIENTS.setText("" + ingredients);

            textView_TABLE2_COLUMN_QUANTITY = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
            textView_TABLE2_COLUMN_QUANTITY.setText("" + quantity);
            textView_TABLE2_COLUMN_QUANTITY.setTag(quantity);

            textView_TABLE2_COLUMN_MEASURE = viewRowTable2.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_MEASURE);
            textView_TABLE2_COLUMN_MEASURE.setText("" + measure);
            textView_TABLE2_COLUMN_MEASURE.setTag(measure);

            linLayout_Table2.addView(viewRowTable2);
        }
    }

    protected void createTable3() {
        List<Table3Row> rowsTable3 = recipe.rowsTable3;

        for (Table3Row table3Row : rowsTable3) {
            int number = table3Row.TABLE3_COLUMN_NUMBER;
            String text = table3Row.TABLE3_COLUMN_TEXT;

            LinearLayout linLayout_Table3 = findViewById(R.id.Recipe_LL_TABLE_3);
            LayoutInflater ltInflater = getLayoutInflater();
            View viewRowTable3 = ltInflater.inflate(R.layout.item_recipe_table3, linLayout_Table3, false);

            TextView textViewNumber = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_NUMBER);
            TextView textViewText = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_TEXT);
            final ImageView imageViewImg = viewRowTable3.findViewById(R.id.Recipe_item_table3_TABLE3_COLUMN_IMG);

            textViewNumber.setText("" + number);
            textViewText.setText("" + text);

            setImageTable3(imageViewImg, table3Row);

            linLayout_Table3.addView(viewRowTable3);
        }
    }
    protected void setImageTable3(final ImageView imageViewImg, Table3Row table3Row){
        final String img_png = table3Row.TABLE3_COLUMN_IMG_TITLE;
        if (img_png==null||img_png.equals("null")){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageViewImg.getLayoutParams();
            params.width = 0;
            params.height = 0;
            params.setMargins(0,0,0,0);
            imageViewImg.setLayoutParams(params);
        }
        else {
            imageViewImg.setImageURI(Uri.parse(img_png));

            final String img_jpeg = table3Row.TABLE3_COLUMN_IMG_FULL;
            imageViewImg.setOnClickListener(new View.OnClickListener() {
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
                    AnimClick (imageViewImg, adapter);
                }
            });
        }
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, getColorForStatusBar()));
        window.setNavigationBarColor(ContextCompat.getColor(this, getColorForNavigationBar()));
    }

    protected int getColorForStatusBar (){
        return R.color.RecipeSetStatusBarColor;
    }

    protected int getColorForNavigationBar (){
        return R.color.RecipeSetStatusBarColor;
    }

    private void calculateTable2 (String newPortion) {
        double k = (Double.parseDouble(newPortion)) / (double) portion;
        DecimalFormat format = new DecimalFormat("#.#");
        format.setRoundingMode(RoundingMode.HALF_UP);
        View view;
        int count = 0;
        try {
            count = linLayout_Table2.getChildCount();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        for (int idView = 0; idView < count; idView++){
            view = linLayout_Table2.getChildAt(idView);

            textView_TABLE2_COLUMN_QUANTITY = view.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_QUANTITY);
            textView_TABLE2_COLUMN_MEASURE = view.findViewById(R.id.Recipe_item_table2_TABLE2_COLUMN_MEASURE);

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
}