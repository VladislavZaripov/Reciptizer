package reciptizer.Local;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reciptizer.R;
import reciptizer.Activity_Main;
import reciptizer.Common.Helpers.ConstantHelper;
import reciptizer.Common.Recipe.RecipeFilter;
import reciptizer.Common.Recipe.Table1;
import java.util.ArrayList;

import static reciptizer.Common.Helpers.AnimHelper.*;

public class Activity_Filter extends Activity {
    private String[] TABLE1_FILTER;
    private String[] TABLE1_COLUMN_CATEGORY;
    private String[] TABLE1_COLUMN_KITCHEN;
    private String[] TABLE1_COLUMN_PREFERENCES;
    private Spinner spinner_TABLE1_COLUMN_CATEGORY, spinner_TABLE1_COLUMN_KITCHEN, spinner_TABLE1_COLUMN_PREFERENCES;
    private ImageView imageView_TABLE1_COLUMN_CATEGORY,imageView_TABLE1_COLUMN_KITCHEN,imageView_TABLE1_COLUMN_PREFERENCES;
    private Intent intent;
    private boolean isSpinnerCreated;

    protected String RECIPE, CATEGORY, KITCHEN, PREFERENCES;
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: onCreate");

        getResource();

        prepareActivity();

        createSpinner();

        setStatusBarColor ();

        createRecyclerView();
    }

    private void getResource () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: getResource");

        TABLE1_FILTER = getResources().getStringArray(R.array.TABLE1_FILTER);
        TABLE1_COLUMN_CATEGORY = getResources().getStringArray(R.array.TABLE1_COLUMN_CATEGORY);
        TABLE1_COLUMN_KITCHEN = getResources().getStringArray(R.array.TABLE1_COLUMN_KITCHEN);
        TABLE1_COLUMN_PREFERENCES = getResources().getStringArray(R.array.TABLE1_COLUMN_PREFERENCES);
        RECIPE = "";
        CATEGORY = TABLE1_COLUMN_CATEGORY[0];
        KITCHEN = TABLE1_COLUMN_KITCHEN[0];
        PREFERENCES = TABLE1_COLUMN_PREFERENCES[0];
    }

    protected void prepareActivity () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: prepareActivity");

        final EditText filterName = findViewById(R.id.Filter_filterName);
        filterName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((filterName.getText()).toString().equals("Название рецепта"))
                    filterName.setText("");
            }
        });
        filterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                RECIPE = filterName.getText().toString();
                createRecyclerView();
            }
        });
        final Button buttonNewRecipe = findViewById(R.id.Filter_newRecipe);
        buttonNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        intent = new Intent(Activity_Filter.this, Activity_New_Edit_Recipe.class);
                        startActivity(intent);
                    }
                };
                AnimMenuClick(buttonNewRecipe,adapter);
            }
        });
        imageView_TABLE1_COLUMN_CATEGORY = findViewById(R.id.ImageView_Filter_cat1);
        imageView_TABLE1_COLUMN_CATEGORY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_TABLE1_COLUMN_CATEGORY.performClick ();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_CATEGORY);
            }
        });
        imageView_TABLE1_COLUMN_KITCHEN = findViewById(R.id.ImageView_Filter_cat2);
        imageView_TABLE1_COLUMN_KITCHEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_TABLE1_COLUMN_KITCHEN.performClick ();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_KITCHEN);
            }
        });
        imageView_TABLE1_COLUMN_PREFERENCES = findViewById(R.id.ImageView_Filter_cat3);
        imageView_TABLE1_COLUMN_PREFERENCES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_TABLE1_COLUMN_PREFERENCES.performClick ();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_PREFERENCES);
            }
        });
        recyclerView = findViewById(R.id.Filter_RV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void createSpinner () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: createSpinner");
        isSpinnerCreated = false;

        spinner_TABLE1_COLUMN_CATEGORY = findViewById(R.id.Filter_cat1);
        spinner_TABLE1_COLUMN_KITCHEN = findViewById(R.id.Filter_cat2);
        spinner_TABLE1_COLUMN_PREFERENCES = findViewById(R.id.Filter_cat3);
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

        spinner_TABLE1_COLUMN_CATEGORY.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerCreated = true;
                v.performClick();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_CATEGORY);
                return false;
            }
        });
        spinner_TABLE1_COLUMN_KITCHEN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerCreated = true;
                v.performClick();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_KITCHEN);
                return false;
            }
        });
        spinner_TABLE1_COLUMN_PREFERENCES.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerCreated = true;
                v.performClick();
                AnimSpinnerOn(imageView_TABLE1_COLUMN_PREFERENCES);
                return false;
            }
        });
        spinner_TABLE1_COLUMN_CATEGORY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerCreated) {
                    CATEGORY = TABLE1_COLUMN_CATEGORY[position];
                    createRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_TABLE1_COLUMN_KITCHEN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerCreated) {
                    KITCHEN = TABLE1_COLUMN_KITCHEN[position];
                    createRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_TABLE1_COLUMN_PREFERENCES.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerCreated) {
                    PREFERENCES = TABLE1_COLUMN_PREFERENCES[position];
                    createRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setStatusBarColor () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: setStatusBarColor");

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,getColorForStatusBar()));
        window.setNavigationBarColor(ContextCompat.getColor(this,getColorForNavigationBar()));
    }

    protected int getColorForStatusBar (){
        return R.color.FilterRecipeSetStatusBarColor;
    }

    protected int getColorForNavigationBar (){
        return R.color.RecipeSetStatusBarColor;
    }

    protected void createRecyclerView () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: createRecyclerView");

        RecipeFilter recipeFilter = DB.getRecipeFilterResult(RECIPE,CATEGORY,KITCHEN,PREFERENCES);

        MyAdapter myAdapter = new MyAdapter((ArrayList<Table1>)recipeFilter.table1);
        recyclerView.setAdapter(myAdapter);
    }

    protected class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private final ArrayList <Table1> dataset;

        public MyAdapter(ArrayList <Table1> dataset) {
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new MyViewHolder (layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Table1 item = dataset.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewRecipe;
        private final TextView textViewTime;
        private final ImageView imageViewImg;
        Context context;

        public MyViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_filter, parent,false));
            textViewRecipe = itemView.findViewById(R.id.Filter_result_item_recipe);
            textViewTime = itemView.findViewById(R.id.Filter_result_item_time);
            imageViewImg = itemView.findViewById(R.id.Filter_result_item_img);
            context = textViewRecipe.getContext();
        }

        public void bind (Table1 table1) {
            textViewRecipe.setText("" + table1.TABLE1_COLUMN_RECIPE);
            textViewTime.setText("" + table1.TABLE1_COLUMN_TIME);

            bindImageMyViewHolder(imageViewImg,table1);

            final int id = table1.TABLE1_COLUMN_ID;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            intent = getIntentForRecipe();
                            intent.putExtra(DB.TABLE1_COLUMN_ID, String.valueOf(id));
                            startActivity(intent);
                        }
                    };
                    AnimClick (itemView, adapter);
                }
            });
        }
    }

    protected Intent getIntentForRecipe () {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: getIntentForRecipe");

        return new Intent(this, Activity_Recipe.class);
    }

    protected void bindImageMyViewHolder (ImageView imageViewImg, Table1 table1){
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: bindImageMyViewHolder");

        if (table1.TABLE1_COLUMN_IMG_TITLE==null||table1.TABLE1_COLUMN_IMG_TITLE.equals("null")){
            imageViewImg.setImageResource(R.drawable.no_img);
        }
        else {
            imageViewImg.setImageURI(Uri.parse(table1.TABLE1_COLUMN_IMG_TITLE));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ConstantHelper.isNeedToRefreshFilterCreateRecyclerView)
        {
            createRecyclerView ();
            ConstantHelper.isNeedToRefreshFilterCreateRecyclerView = false;
        }

    }
}