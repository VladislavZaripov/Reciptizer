package reciptizer.Server;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.reciptizer.R;
import org.json.JSONObject;
import reciptizer.Activity_Main;
import reciptizer.Common.Helpers.JsonHelper;
import reciptizer.Common.Helpers.ToastHelper;
import reciptizer.Common.Recipe.BitmapTempSingleton;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.Table3Row;
import reciptizer.Local.Activity_Recipe;
import reciptizer.Local.DB;

import static reciptizer.Common.Helpers.AnimHelper.AnimClick;

public class Activity_Recipe_Server extends Activity_Recipe {

    Recipe recipeForSave;

    @Override
    protected void prepareActivity() {
        super.prepareActivity();

        LinearLayout linearLayout = findViewById(R.id.Recipe_LL_Title);
        linearLayout.removeView(findViewById(R.id.Recipe_button_edit));
        linearLayout.setBackgroundResource(R.color.RecipeServerSetStatusBarColor);

        final Button button_search = findViewById(R.id.Recipe_button_search);
        button_search.setBackgroundResource(R.drawable.recipe_server_search);

        final Button button_save = findViewById(R.id.Recipe_button_save);
        button_save.setBackgroundResource(R.drawable.recipe_server_home);
    }

    @Override
    protected void getRecipeAndSetValues() {
        Response.Listener<JSONObject> listener =  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JsonHelper jsonHelper = new JsonHelper();
                recipe = jsonHelper.jsonObjectToRecipe(response);
                recipeForSave = jsonHelper.jsonObjectToRecipe(response);
                createTable1();
                createTable2();
                createTable3();
            }
        };
        ServerAPISingleton.getInstance(this.getApplicationContext()).getRecipe(filterResult_id, listener);
    }

    @Override
    protected void setImageTable1(final ImageView imageView_TABLE1_COLUMN_IMG) {
        imageView_TABLE1_COLUMN_IMG.setImageResource(R.drawable.no_img);

        final String img_png = recipe.table1.TABLE1_COLUMN_IMG_TITLE;

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_png)) {
            imageView_TABLE1_COLUMN_IMG.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_png));
        }
        else {
            if (img_png!=null&&!img_png.equals("null"))
            {
                final ProgressBar progressBar = findViewById(R.id.Recipe_progressBar);
                progressBar.setVisibility(View.VISIBLE);
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(img_png, bitmap_png);
                        imageView_TABLE1_COLUMN_IMG.setImageBitmap(bitmap_png);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(img_png, listener, errorListener);
            }
        }

        final String img_jpeg = recipe.table1.TABLE1_COLUMN_IMG_FULL;
        if (img_jpeg!=null&&!img_jpeg.equals("null")) {
            imageView_TABLE1_COLUMN_IMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragment_Server(img_jpeg, img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick(imageView_TABLE1_COLUMN_IMG, adapter);
                }
            });
        }
    }

    @Override
    protected void setImageTable3(final ImageView imageViewImg, final Table3Row table3Row) {
        final String img_png = table3Row.TABLE3_COLUMN_IMG_TITLE;

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_png)) {
            imageViewImg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_png));
        }
        else {
            if (img_png == null || img_png.equals("null")) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageViewImg.getLayoutParams();
                params.width = 0;
                params.height = 0;
                params.setMargins(0, 0, 0, 0);
                imageViewImg.setLayoutParams(params);
            } else {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        final Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(img_png, bitmap_png);
                        imageViewImg.setImageBitmap(bitmap_png);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(img_png, listener, errorListener);
            }
        }

        final String img_jpeg = table3Row.TABLE3_COLUMN_IMG_FULL;
        if (img_jpeg!=null&&!img_jpeg.equals("null")) {
            imageViewImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragment_Server(img_jpeg, img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick(imageViewImg, adapter);
                }
            });
        }
    }

    @Override
    public void onSaveRecipeDialogPositiveClick(DialogFragment dialog) {
        if (isRecipeDownloadAllImage(recipeForSave)){
            DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
            ToastHelper.toastRecipeIsSaved(getApplicationContext());
        }
        else {
            ToastHelper.toastLoadImage(this);
            loadImage();
        }
    }

    @Override
    protected int getColorForNavigationBar() {
        return R.color.RecipeServerSetStatusBarColor;
    }

    @Override
    public String setDialogTitle() {
        return "Сохранить в мои рецепты";
    }

    private void loadImage (){
        if(recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE!=null && !recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE.equals("null"))
            if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE)) {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(Activity_Main.LOG_TAG, "onResponse | recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE = " + recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE);
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE, bitmap_png);
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Activity_Main.LOG_TAG, "error | recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE = " + recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE);
                        recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE = null;
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE, listener, errorListener);
            }

        if(recipeForSave.table1.TABLE1_COLUMN_IMG_FULL!=null&&!recipeForSave.table1.TABLE1_COLUMN_IMG_FULL.equals("null"))
            if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipeForSave.table1.TABLE1_COLUMN_IMG_FULL)) {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(Activity_Main.LOG_TAG, "onResponse | recipeForSave.table1.TABLE1_COLUMN_IMG_FULL = " + recipeForSave.table1.TABLE1_COLUMN_IMG_FULL);
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(recipeForSave.table1.TABLE1_COLUMN_IMG_FULL, bitmap_png);
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Activity_Main.LOG_TAG, "error | recipeForSave.table1.TABLE1_COLUMN_IMG_FULL = " + recipeForSave.table1.TABLE1_COLUMN_IMG_FULL);
                        recipeForSave.table1.TABLE1_COLUMN_IMG_FULL = null;
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(recipeForSave.table1.TABLE1_COLUMN_IMG_FULL, listener, errorListener);
            }

        for (final Table3Row table3Row : recipeForSave.rowsTable3) {
            if(table3Row.TABLE3_COLUMN_IMG_TITLE!=null&&!table3Row.TABLE3_COLUMN_IMG_TITLE.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.TABLE3_COLUMN_IMG_TITLE)) {
                    Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            Log.d(Activity_Main.LOG_TAG, "onResponse | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.TABLE3_COLUMN_IMG_TITLE);
                            Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                            BitmapTempSingleton.getInstance().setBitmap(table3Row.TABLE3_COLUMN_IMG_TITLE, bitmap_png);
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(Activity_Main.LOG_TAG, "error | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.TABLE3_COLUMN_IMG_TITLE);
                            table3Row.TABLE3_COLUMN_IMG_TITLE = null;
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(table3Row.TABLE3_COLUMN_IMG_TITLE, listener, errorListener);
                }

            if(table3Row.TABLE3_COLUMN_IMG_FULL!=null&&!table3Row.TABLE3_COLUMN_IMG_FULL.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.TABLE3_COLUMN_IMG_FULL)) {
                    Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            Log.d(Activity_Main.LOG_TAG, "onResponse | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.TABLE3_COLUMN_IMG_FULL);
                            Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                            BitmapTempSingleton.getInstance().setBitmap(table3Row.TABLE3_COLUMN_IMG_FULL, bitmap_png);
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(Activity_Main.LOG_TAG, "error | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.TABLE3_COLUMN_IMG_FULL);
                            table3Row.TABLE3_COLUMN_IMG_FULL = null;
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                DB.addRecipeFromServer(recipeForSave, Activity_Recipe_Server.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(table3Row.TABLE3_COLUMN_IMG_FULL, listener, errorListener);
                }
        }
    }

    private boolean isRecipeDownloadAllImage (Recipe recipe){
        if (recipe.table1.TABLE1_COLUMN_IMG_TITLE!=null&&!recipe.table1.TABLE1_COLUMN_IMG_TITLE.equals("null"))
            if (!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipe.table1.TABLE1_COLUMN_IMG_TITLE)) {
                Log.d(Activity_Main.LOG_TAG, "return false | recipe.table1.TABLE1_COLUMN_IMG_TITLE = " + recipe.table1.TABLE1_COLUMN_IMG_TITLE);
                return false;
            }
        if(recipe.table1.TABLE1_COLUMN_IMG_FULL!=null&&!recipe.table1.TABLE1_COLUMN_IMG_FULL.equals("null"))
            if (!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipe.table1.TABLE1_COLUMN_IMG_FULL)){
                Log.d(Activity_Main.LOG_TAG, "return false | recipe.table1.TABLE1_COLUMN_IMG_FULL = " + recipe.table1.TABLE1_COLUMN_IMG_FULL);
                return false;}

        for (Table3Row table3Row : recipe.rowsTable3) {

            if(table3Row.TABLE3_COLUMN_IMG_TITLE!=null&&!table3Row.TABLE3_COLUMN_IMG_TITLE.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.TABLE3_COLUMN_IMG_TITLE)){
                    Log.d(Activity_Main.LOG_TAG, "return false | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.TABLE3_COLUMN_IMG_TITLE);
                    return false;}

            if(table3Row.TABLE3_COLUMN_IMG_FULL!=null&&!table3Row.TABLE3_COLUMN_IMG_FULL.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.TABLE3_COLUMN_IMG_FULL)){
                    Log.d(Activity_Main.LOG_TAG, "return false | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.TABLE3_COLUMN_IMG_FULL);
                    return false;}
        }

        Log.d(Activity_Main.LOG_TAG, "Recipe is Saved");
        return true;
    }
}