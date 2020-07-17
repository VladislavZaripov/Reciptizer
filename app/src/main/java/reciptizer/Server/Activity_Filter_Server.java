package reciptizer.Server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.*;
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
import java.util.ArrayList;

public class Activity_Filter_Server  extends Activity_Filter {

    RecipeFilter recipeFilter;

    @Override
    protected void prepareActivity() {
        super.prepareActivity();

        LinearLayout linearLayout = findViewById(R.id.Filter_LL_Title);
        linearLayout.removeView(findViewById(R.id.Filter_newRecipe));
        linearLayout.setBackgroundResource(R.color.FilterRecipeServerSetStatusBarColor);

        TextView filter_Title = findViewById(R.id.Filter_Title);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) filter_Title.getLayoutParams();
        params.setMargins(0,0,0,0);
        filter_Title.setLayoutParams(params);
        filter_Title.setText("Все рецепты");

        final EditText filterName = findViewById(R.id.Filter_filterName);
        filterName.setBackground(getDrawable(R.drawable.search_name_server));
    }

    @Override
    protected void createRecyclerView() {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: createRecyclerView");

        if (recipeFilter==null) {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JsonHelper jsonHelper = new JsonHelper();
                    recipeFilter = jsonHelper.jsonObjectToRecipeFilter(response);
                    MyAdapter myAdapter = new MyAdapter((ArrayList<Table1>) recipeFilter.table1);
                    recyclerView.setAdapter(myAdapter);
                }
            };
            ServerAPISingleton.getInstance(this.getApplicationContext()).getFilter(listener);
        }
        else {
            ArrayList<Table1> refreshTable = new ArrayList<>();
            for(Table1 table : recipeFilter.table1)
            {
                if ((table.TABLE1_COLUMN_RECIPE.toLowerCase().contains(RECIPE.toLowerCase()) || RECIPE.equals("Название рецепта")) &&
                        (table.TABLE1_COLUMN_CATEGORY.equals(CATEGORY) || CATEGORY.equals("Все")) &&
                        (table.TABLE1_COLUMN_KITCHEN.equals(KITCHEN) || KITCHEN.equals("Все")) &&
                        (table.TABLE1_COLUMN_PREFERENCES.equals(PREFERENCES) || PREFERENCES.equals("Все")))
                    refreshTable.add(table);
            }
            MyAdapter myAdapter = new MyAdapter(refreshTable);
            recyclerView.setAdapter(myAdapter);
        }
    }

    @Override
    protected Intent getIntentForRecipe() {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: getIntentForRecipe");

        return new Intent(this, Activity_Recipe_Server.class);
    }

    @Override
    protected void bindImageMyViewHolder(final ImageView imageViewImg, Table1 table1) {
        Log.d(Activity_Main.LOG_TAG, this.getClass() + "| method: bindImageMyViewHolder");

        final String imgName = table1.TABLE1_COLUMN_IMG_TITLE;

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(imgName))
            imageViewImg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(imgName));
        else {
            View view = (View) imageViewImg.getParent();
            final ProgressBar progressBar = view.findViewById(R.id.Filter_result_progressBar);
            imageViewImg.setImageResource(R.drawable.no_img);

            if (table1.TABLE1_COLUMN_IMG_TITLE != null && !table1.TABLE1_COLUMN_IMG_TITLE.equals("null")) {
                progressBar.setVisibility(View.VISIBLE);
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Bitmap bitmap = null;
                        if (imageViewImg.getTag().equals(imgName)) {
                            bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                            BitmapTempSingleton.getInstance().setBitmap(imgName, bitmap);
                        }
                        if (imageViewImg.getTag().equals(imgName)) {
                            imageViewImg.setImageBitmap(bitmap);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageViewImg.setImageResource(R.drawable.no_img);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };

                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(imgName, listener, errorListener);
            }
        }
        imageViewImg.setTag(imgName);
    }

    @Override
    protected int getColorForNavigationBar() {
        return R.color.FilterRecipeServerSetStatusBarColor;
    }
}