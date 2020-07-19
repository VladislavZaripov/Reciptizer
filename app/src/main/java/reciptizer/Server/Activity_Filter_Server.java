package reciptizer.Server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.reciptizer.R;
import org.json.JSONObject;
import reciptizer.Activity_Main;
import reciptizer.Common.Helpers.JsonHelper;
import reciptizer.Common.Recipe.BitmapTempSingleton;
import reciptizer.Common.Recipe.RecipeFilter;
import reciptizer.Common.Recipe.Table1;
import reciptizer.Local.Activity_Filter;
import reciptizer.Local.DB;
import java.util.ArrayList;

public class Activity_Filter_Server  extends Activity_Filter {

    @Override
    protected void setStatusBarColor(int colorForStatusBar, int ColorForNavigationBar) {
        super.setStatusBarColor(colorForStatusBar, R.color.FilterRecipeServerSetStatusBarColor);
    }

    @Override
    protected void createLinearLayoutTitle(LinearLayout linearLayoutTitle) {
        super.createLinearLayoutTitle(linearLayoutTitle);

        linearLayoutTitle.setBackgroundResource(R.color.FilterRecipeServerSetStatusBarColor);
    }

    @Override
    protected void createTextViewTitle(TextView textViewTitle) {
        super.createTextViewTitle(textViewTitle);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textViewTitle.getLayoutParams();
        params.setMarginStart(0);
        textViewTitle.setLayoutParams(params);
        textViewTitle.setText("Все рецепты");
    }

    @Override
    protected void createButtonNewRecipe(Button buttonNewRecipe) {

        ((LinearLayout) buttonNewRecipe.getParent()).removeView(buttonNewRecipe);
    }

    @Override
    protected void createEditTextRecipeName(EditText editTextRecipeName) {
        super.createEditTextRecipeName(editTextRecipeName);

        editTextRecipeName.setBackground(getDrawable(R.drawable.search_name_server));
    }

    @Override
    protected void setContentIntoRecyclerView() {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: setContentIntoRecyclerView");

        if (recipeFilter==null) {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JsonHelper jsonHelper = new JsonHelper();
                    recipeFilter = jsonHelper.jsonObjectToRecipeFilter(response);
                    FilterServerAdapter filterServerAdapter = new FilterServerAdapter(recipeFilter);
                    recyclerView.setAdapter(filterServerAdapter);
                }
            };
            ServerAPISingleton.getInstance(this.getApplicationContext()).getFilter(listener);
        }
        else {
            RecipeFilter refreshRecipeFilter = new RecipeFilter(new ArrayList<Table1>());
            for(Table1 table : recipeFilter.table1)
            {
                if ((table.TABLE1_COLUMN_RECIPE.toLowerCase().contains(currentValue_RECIPE.toLowerCase()) || currentValue_RECIPE.equals("Название рецепта")) &&
                        (table.TABLE1_COLUMN_CATEGORY.equals(currentValue_CATEGORY) || currentValue_CATEGORY.equals("Все")) &&
                        (table.TABLE1_COLUMN_KITCHEN.equals(currentValue_KITCHEN) || currentValue_KITCHEN.equals("Все")) &&
                        (table.TABLE1_COLUMN_PREFERENCES.equals(currentValue_PREFERENCES) || currentValue_PREFERENCES.equals("Все")))
                    refreshRecipeFilter.table1.add(table);
            }
            FilterServerAdapter filterServerAdapter = new FilterServerAdapter(refreshRecipeFilter);
            recyclerView.setAdapter(filterServerAdapter);
        }
    }

    class FilterServerAdapter extends RecyclerView.Adapter<FilterServerViewHolder> {
        public final RecipeFilter recipeFilter;

        public FilterServerAdapter(RecipeFilter recipeFilter) {
            this.recipeFilter = recipeFilter;
        }

        @NonNull
        @Override
        public FilterServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new FilterServerViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FilterServerViewHolder holder, int position) {
            Table1 item = recipeFilter.table1.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return recipeFilter.table1.size();
        }
    }

    class FilterServerViewHolder extends FilterViewHolder{

        public FilterServerViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent);
        }

        @Override
        protected void bindImageViewImgTitle(Table1 table1) {
            final String imgName = table1.TABLE1_COLUMN_IMG_TITLE;

            if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(imgName))
                imageViewImgTitle.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(imgName));
            else {
                View view = (View) imageViewImgTitle.getParent();
                final ProgressBar progressBar = view.findViewById(R.id.Filter_result_item_progressBar);
                imageViewImgTitle.setImageResource(R.drawable.no_img);

                if (table1.TABLE1_COLUMN_IMG_TITLE != null && !table1.TABLE1_COLUMN_IMG_TITLE.equals("null")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            Bitmap bitmap = null;
                            if (imageViewImgTitle.getTag().equals(imgName)) {
                                bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                                BitmapTempSingleton.getInstance().setBitmap(imgName, bitmap);
                            }
                            if (imageViewImgTitle.getTag().equals(imgName)) {
                                imageViewImgTitle.setImageBitmap(bitmap);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            imageViewImgTitle.setImageResource(R.drawable.no_img);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    };

                    ServerAPISingleton.getInstance(context).getImage(imgName, listener, errorListener);
                }
            }
            imageViewImgTitle.setTag(imgName);
        }

        @Override
        protected void onClickItemView(int id) {
            Intent intent = new Intent(Activity_Filter_Server.this, Activity_Recipe_Server.class);
            intent.putExtra(DB.TABLE1_COLUMN_ID, String.valueOf(id));
            startActivity(intent);
        }
    }
}