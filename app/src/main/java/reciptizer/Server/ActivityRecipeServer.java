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
import reciptizer.ActivityMain;
import reciptizer.Common.Helpers.JsonHelper;
import reciptizer.Common.Helpers.ToastHelper;
import reciptizer.Common.Recipe.BitmapTempSingleton;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.Table3Row;
import reciptizer.Local.ActivityRecipe;
import reciptizer.Local.SQL;

import static reciptizer.Common.Helpers.AnimHelper.AnimClick;

public class ActivityRecipeServer extends ActivityRecipe {

    Recipe recipeForSave;

    @Override
    public void onSaveRecipeDialogPositiveClick(DialogFragment dialog) {
        if (isRecipeDownloadAllImage(recipeForSave)){
            SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
            ToastHelper.toastRecipeIsSaved(getApplicationContext());
        }
        else {
            ToastHelper.toastLoadImage(this);
            loadImage();
        }
    }

    @Override
    public String setDialogTitle() {
        return "Сохранить в мои рецепты";
    }

    @Override
    protected void setStatusBarColor(int colorForStatusBar, int colorForNavigationBar) {
        super.setStatusBarColor(colorForStatusBar, R.color.RecipeServerSetStatusBarColor);
    }

    @Override
    protected void createLinearLayoutTitle(LinearLayout linearLayoutTitle) {

        linearLayoutTitle.setBackgroundResource(R.color.RecipeServerSetStatusBarColor);
    }

    @Override
    protected void createButtonSearch(Button buttonSearch) {
        super.createButtonSearch(buttonSearch);

        buttonSearch.setBackgroundResource(R.drawable.recipe_server_search);
    }

    @Override
    protected void createButtonSave(Button buttonSave) {
        super.createButtonSave(buttonSave);

        buttonSave.setBackgroundResource(R.drawable.recipe_server_home);
    }

    @Override
    protected void createButtonEdit(Button buttonEdit) {

        ((LinearLayout) buttonEdit.getParent()).removeView(buttonEdit);
    }

    @Override
    protected void getRecipeAndSetValues() {
        Response.Listener<JSONObject> listener =  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JsonHelper jsonHelper = new JsonHelper();
                recipe = jsonHelper.jsonObjectToRecipe(response);
                recipeForSave = jsonHelper.jsonObjectToRecipe(response);
                initTable1();
                initTable2();
                initTable3();
            }
        };
        ServerAPISingleton.getInstance(this.getApplicationContext()).getRecipe(filterId, listener);
    }

    @Override
    protected void createImageViewRecipe(final ImageView imageViewImage) {
        imageViewImage.setImageResource(R.drawable.no_img);

        final String img_png = recipe.table1.imageTitle;

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_png)) {
            imageViewImage.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_png));
        }
        else {
            if (img_png!=null&&!img_png.equals("null"))
            {
                final ProgressBar progressBar = findViewById(R.id.recipe_progressBar_image_load);
                progressBar.setVisibility(View.VISIBLE);
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(img_png, bitmap_png);
                        imageViewImage.setImageBitmap(bitmap_png);
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

        final String img_jpeg = recipe.table1.imageFull;
        if (img_jpeg!=null&&!img_jpeg.equals("null")) {
            imageViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragmentServer(img_jpeg, img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick(imageViewImage, adapter);
                }
            });
        }
    }

    @Override
    protected void createImageViewImgSteps(final ImageView imageViewImageTitle, final Table3Row table3Row) {
        final String img_png = table3Row.imageTitle;

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_png)) {
            imageViewImageTitle.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_png));
        }
        else {
            if (img_png == null || img_png.equals("null")) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageViewImageTitle.getLayoutParams();
                params.width = 0;
                params.height = 0;
                params.setMargins(0, 0, 0, 0);
                imageViewImageTitle.setLayoutParams(params);
            } else {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        final Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(img_png, bitmap_png);
                        imageViewImageTitle.setImageBitmap(bitmap_png);
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

        final String img_jpeg = table3Row.imageFull;
        if (img_jpeg!=null&&!img_jpeg.equals("null")) {
            imageViewImageTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            DialogFragment dialog = new ImageDialogFragmentServer(img_jpeg, img_png);
                            dialog.show(getSupportFragmentManager(), "ImageDialog");
                        }
                    };
                    AnimClick(imageViewImageTitle, adapter);
                }
            });
        }
    }

    private void loadImage (){
        if(recipeForSave.table1.imageTitle !=null && !recipeForSave.table1.imageTitle.equals("null"))
            if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipeForSave.table1.imageTitle)) {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(ActivityMain.LOG_TAG, "onResponse | recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE = " + recipeForSave.table1.imageTitle);
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(recipeForSave.table1.imageTitle, bitmap_png);
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(ActivityMain.LOG_TAG, "error | recipeForSave.table1.TABLE1_COLUMN_IMG_TITLE = " + recipeForSave.table1.imageTitle);
                        recipeForSave.table1.imageTitle = null;
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(recipeForSave.table1.imageTitle, listener, errorListener);
            }

        if(recipeForSave.table1.imageFull !=null&&!recipeForSave.table1.imageFull.equals("null"))
            if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipeForSave.table1.imageFull)) {
                Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(ActivityMain.LOG_TAG, "onResponse | recipeForSave.table1.TABLE1_COLUMN_IMG_FULL = " + recipeForSave.table1.imageFull);
                        Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                        BitmapTempSingleton.getInstance().setBitmap(recipeForSave.table1.imageFull, bitmap_png);
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(ActivityMain.LOG_TAG, "error | recipeForSave.table1.TABLE1_COLUMN_IMG_FULL = " + recipeForSave.table1.imageFull);
                        recipeForSave.table1.imageFull = null;
                        if (isRecipeDownloadAllImage(recipeForSave)){
                            SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                            ToastHelper.toastRecipeIsSaved(getApplicationContext());
                        }
                    }
                };
                ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(recipeForSave.table1.imageFull, listener, errorListener);
            }

        for (final Table3Row table3Row : recipeForSave.rowsTable3) {
            if(table3Row.imageTitle !=null&&!table3Row.imageTitle.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.imageTitle)) {
                    Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            Log.d(ActivityMain.LOG_TAG, "onResponse | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.imageTitle);
                            Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                            BitmapTempSingleton.getInstance().setBitmap(table3Row.imageTitle, bitmap_png);
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(ActivityMain.LOG_TAG, "error | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.imageTitle);
                            table3Row.imageTitle = null;
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(table3Row.imageTitle, listener, errorListener);
                }

            if(table3Row.imageFull !=null&&!table3Row.imageFull.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.imageFull)) {
                    Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            Log.d(ActivityMain.LOG_TAG, "onResponse | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.imageFull);
                            Bitmap bitmap_png = BitmapFactory.decodeByteArray(response, 0, response.length);
                            BitmapTempSingleton.getInstance().setBitmap(table3Row.imageFull, bitmap_png);
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(ActivityMain.LOG_TAG, "error | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.imageFull);
                            table3Row.imageFull = null;
                            if (isRecipeDownloadAllImage(recipeForSave)){
                                SQL.addRecipeFromServer(recipeForSave, ActivityRecipeServer.this);
                                ToastHelper.toastRecipeIsSaved(getApplicationContext());
                            }
                        }
                    };
                    ServerAPISingleton.getInstance(this.getApplicationContext()).getImage(table3Row.imageFull, listener, errorListener);
                }
        }
    }

    private boolean isRecipeDownloadAllImage (Recipe recipe){
        if (recipe.table1.imageTitle !=null&&!recipe.table1.imageTitle.equals("null"))
            if (!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipe.table1.imageTitle)) {
                Log.d(ActivityMain.LOG_TAG, "return false | recipe.table1.TABLE1_COLUMN_IMG_TITLE = " + recipe.table1.imageTitle);
                return false;
            }
        if(recipe.table1.imageFull !=null&&!recipe.table1.imageFull.equals("null"))
            if (!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(recipe.table1.imageFull)){
                Log.d(ActivityMain.LOG_TAG, "return false | recipe.table1.TABLE1_COLUMN_IMG_FULL = " + recipe.table1.imageFull);
                return false;}

        for (Table3Row table3Row : recipe.rowsTable3) {

            if(table3Row.imageTitle !=null&&!table3Row.imageTitle.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.imageTitle)){
                    Log.d(ActivityMain.LOG_TAG, "return false | table3Row.TABLE3_COLUMN_IMG_TITLE = " + table3Row.imageTitle);
                    return false;}

            if(table3Row.imageFull !=null&&!table3Row.imageFull.equals("null"))
                if(!BitmapTempSingleton.getInstance().getTempBitmap().containsKey(table3Row.imageFull)){
                    Log.d(ActivityMain.LOG_TAG, "return false | table3Row.TABLE3_COLUMN_IMG_FULL = " + table3Row.imageFull);
                    return false;}
        }

        Log.d(ActivityMain.LOG_TAG, "Recipe is Saved");
        return true;
    }
}