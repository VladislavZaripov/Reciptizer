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
import reciptizer.ActivityMain;
import reciptizer.Common.Helpers.ConstantHelper;
import reciptizer.Common.Recipe.RecipeFilter;
import reciptizer.Common.Recipe.Table1;

import static reciptizer.Common.Helpers.AnimHelper.*;

public class ActivityFilter extends Activity {

    protected String valueRecipe, valueCategory, valueKitchen, valuePreferences;
    protected RecyclerView recyclerView;
    protected RecipeFilter recipeFilter;

    private String[] resourceCategory;
    private String[] resourceKitchen;
    private String[] resourcePreferences;
    private Spinner spinnerCategory, spinnerKitchen, spinnerPreferences;
    private ImageView imageViewCategory, imageViewKitchen, imageViewPreferences;
    private boolean isSpinnersWereClicked = false;
    boolean isOpenFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: onCreate");

        setStatusBarColor (R.color.FilterRecipeSetStatusBarColor,R.color.RecipeSetStatusBarColor);

        getResource ();

        setStartValues ();

        initLayout ();

        setContentIntoRecyclerView ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: onResume");

        if (ConstantHelper.IS_NEED_TO_REFRESH_FILTER_RECYCLERVIEW)
        {
            setContentIntoRecyclerView();
            ConstantHelper.IS_NEED_TO_REFRESH_FILTER_RECYCLERVIEW = false;
        }
    }

    protected void setStatusBarColor (int colorForStatusBar, int ColorForNavigationBar) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorForStatusBar));
        window.setNavigationBarColor(ContextCompat.getColor(this, ColorForNavigationBar));
    }

    private void getResource () {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: getResource");

        resourceCategory = getResources().getStringArray(R.array.category);
        resourceKitchen = getResources().getStringArray(R.array.kitchen);
        resourcePreferences = getResources().getStringArray(R.array.preferences);
    }

    private void setStartValues () {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: setStartValues");

        valueRecipe = "";
        valueCategory = resourceCategory[0];
        valueKitchen = resourceKitchen[0];
        valuePreferences = resourcePreferences[0];
    }

    private void initLayout () {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: initLayout");

        LinearLayout linearLayoutTitle = findViewById(R.id.filter_linearLayout_title);
        createLinearLayoutTitle(linearLayoutTitle);

        TextView textViewTitle = findViewById(R.id.filter_textView_title);
        createTextViewTitle(textViewTitle);

        EditText editTextRecipeName = findViewById(R.id.filter_editText_name);
        createEditTextRecipeName(editTextRecipeName);

        Button buttonNewRecipe = findViewById(R.id.filter_button_new);
        createButtonNewRecipe(buttonNewRecipe);

        imageViewCategory = findViewById(R.id.filter_imageView_category);
        createImageViewCategory(imageViewCategory);

        spinnerCategory = findViewById(R.id.filter_spinner_category);
        createSpinnerCategory(spinnerCategory);

        imageViewKitchen = findViewById(R.id.filter_imageView_kitchen);
        createImageViewKitchen(imageViewKitchen);

        spinnerKitchen = findViewById(R.id.filter_spinner_kitchen);
        createSpinnerKitchen(spinnerKitchen);

        imageViewPreferences = findViewById(R.id.filter_imageView_preferences);
        createImageViewPreferences(imageViewPreferences);

        spinnerPreferences = findViewById(R.id.filter_spinner_preferences);
        createSpinnerPreferences (spinnerPreferences);

        ImageView imageViewWrap = findViewById(R.id.filter_imageview_wrap);
        createImageViewWrap(imageViewWrap);

        recyclerView = findViewById(R.id.filter_recyclerView_recipes);
        createRecyclerView(recyclerView);
    }

    protected void createTextViewTitle (TextView textViewTitle) {}

    protected void createEditTextRecipeName (final EditText editTextRecipeName) {
        editTextRecipeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((editTextRecipeName.getText()).toString().equals("Название рецепта"))
                    editTextRecipeName.setText("");
            }
        });
        editTextRecipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                valueRecipe = editTextRecipeName.getText().toString();
                setContentIntoRecyclerView();
            }
        });
    }

    protected void createButtonNewRecipe (final Button buttonNewRecipe) {
        buttonNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(ActivityFilter.this, ActivityConstructor.class);
                        startActivity(intent);
                    }
                };
                AnimMenuClick(buttonNewRecipe,adapter);
            }
        });
    }

    protected void createLinearLayoutTitle (LinearLayout linearLayoutTitle) {}

    protected void createImageViewCategory (final ImageView imageViewCategory){
        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCategory.performClick ();
                AnimSpinnerOn(imageViewCategory);
            }
        });

    }

    protected void createSpinnerCategory (Spinner spinnerCategory){
        ArrayAdapter<String> adapterSpinnerCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourceCategory);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(adapterSpinnerCategory);

        spinnerCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnersWereClicked = true;
                v.performClick();
                AnimSpinnerOn(imageViewCategory);
                return false;
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnersWereClicked) {
                    valueCategory = resourceCategory[position];
                    setContentIntoRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void createImageViewKitchen (final ImageView imageViewKitchen) {
        imageViewKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerKitchen.performClick ();
                AnimSpinnerOn(imageViewKitchen);
            }
        });
    }

    protected void createSpinnerKitchen (Spinner spinnerKitchen) {
        ArrayAdapter<String> adapterSpinnerKitchen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourceKitchen);
        adapterSpinnerKitchen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerKitchen.setAdapter(adapterSpinnerKitchen);

        spinnerKitchen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnersWereClicked = true;
                v.performClick();
                AnimSpinnerOn(imageViewKitchen);
                return false;
            }
        });

        spinnerKitchen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnersWereClicked) {
                    valueKitchen = resourceKitchen[position];
                    setContentIntoRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void createImageViewPreferences (final ImageView imageViewPreferences) {
        imageViewPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPreferences.performClick ();
                AnimSpinnerOn(imageViewPreferences);
            }
        });
    }

    protected void createSpinnerPreferences (Spinner spinnerPreferences ) {
        ArrayAdapter<String> adapterSpinnerPreferences = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resourcePreferences);
        adapterSpinnerPreferences.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPreferences.setAdapter(adapterSpinnerPreferences);

        spinnerPreferences.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnersWereClicked = true;
                v.performClick();
                AnimSpinnerOn(imageViewPreferences);
                return false;
            }
        });

        spinnerPreferences.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnersWereClicked) {
                    valuePreferences = resourcePreferences[position];
                    setContentIntoRecyclerView();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void createImageViewWrap (ImageView imageViewWrap) {
        LinearLayout filter_linearLayout_filter = findViewById(R.id.filter_linearLayout_filter);

        isOpenFilter = true;

        imageViewWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter_linearLayout_filter.getVisibility() == View.GONE) filter_linearLayout_filter.setVisibility(View.VISIBLE);
                else filter_linearLayout_filter.setVisibility(View.GONE);
            }
        });
    }

    protected void createRecyclerView(final RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    protected void setContentIntoRecyclerView() {
        Log.d(ActivityMain.LOG_TAG, this.getClass() + "| method: setContentIntoRecyclerView");

        recipeFilter = SQL.getRecipeFilterResult(valueRecipe, valueCategory, valueKitchen, valuePreferences);

        FilterAdapter filterAdapter = new FilterAdapter(recipeFilter);
        recyclerView.setAdapter(filterAdapter);
    }

    protected class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {
        public final RecipeFilter recipeFilter;

        public FilterAdapter(RecipeFilter recipeFilter) {
            this.recipeFilter = recipeFilter;
        }

        @NonNull
        @Override
        public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new FilterViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FilterViewHolder holder, int position) {
            Table1 item = recipeFilter.table1.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return recipeFilter.table1.size();
        }
    }

    protected class FilterViewHolder extends RecyclerView.ViewHolder {
        protected final TextView textViewRecipeName;
        protected final TextView textViewTime;
        protected final ImageView imageViewImgTitle;
        protected Context context;

        public FilterViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_filter, parent,false));
            textViewRecipeName = itemView.findViewById(R.id.item_filter_textView_name);
            textViewTime = itemView.findViewById(R.id.item_filter_textView_time);
            imageViewImgTitle = itemView.findViewById(R.id.item_filter_imageView_image);
            context = textViewRecipeName.getContext();
        }

        public void bind (Table1 table1) {
            bindTextViewRecipeName(table1);

            bindTextViewTime(table1);

            bindImageViewImgTitle(table1);

            bindItemView(table1);
        }

        protected void bindTextViewRecipeName(Table1 table1){
            textViewRecipeName.setText("" + table1.recipe);
        }

        protected void bindTextViewTime(Table1 table1){
            textViewTime.setText("" + table1.time);
        }

        protected void bindImageViewImgTitle(Table1 table1){
            if (table1.imageTitle ==null||table1.imageTitle.equals("null")){
                imageViewImgTitle.setImageResource(R.drawable.no_img);
            }
            else {
                imageViewImgTitle.setImageURI(Uri.parse(table1.imageTitle));
            }
        }

        protected void bindItemView (Table1 table1) {
            final int id = table1.id;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            onClickItemView(id);
                        }
                    };
                    AnimClick (itemView, adapter);
                }
            });
        }

        protected void onClickItemView(int id) {
            Intent intent = new Intent(ActivityFilter.this, ActivityRecipe.class);
            intent.putExtra(SQL.TABLE1_COLUMN_ID, String.valueOf(id));
            startActivity(intent);
        }
    }
}