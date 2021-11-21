package reciptizer.Server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import reciptizer.ActivityMain;
import org.json.JSONObject;
import reciptizer.Common.Helpers.JsonHelper;
import reciptizer.Common.Helpers.ToastHelper;
import reciptizer.Common.Recipe.Recipe;
import reciptizer.Common.Recipe.Table1;
import reciptizer.Common.Recipe.Table2Row;
import reciptizer.Common.Recipe.Table3Row;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerAPISingleton {

    private static ServerAPISingleton INSTANCE;
    private RequestQueue requestQueue;
    private static Context CONTEXT;
    String URL ="http://192.168.1.44:8080";

    public ServerAPISingleton(Context context) {
        this.CONTEXT = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(CONTEXT.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized ServerAPISingleton getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ServerAPISingleton(context);
        }
        return INSTANCE;
    }

    public void getRecipe (Integer recipeId, Response.Listener<JSONObject> listener){
        final String urlGetRecipe = URL + "/ReciptizerServer/getRecipe/" + recipeId;
        Log.d(ActivityMain.LOG_TAG,urlGetRecipe);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, urlGetRecipe, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper.toastNoConnection(CONTEXT);
                    }
                });


        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void getImage (String imgName, Response.Listener<byte[]> listener,Response.ErrorListener errorListener){
        final String urlGetImage = URL + "/ReciptizerServer/getImage/" + imgName;
        Log.d(ActivityMain.LOG_TAG,urlGetImage);

        ImageRequest imageRequest = new ImageRequest(urlGetImage,listener,errorListener);

        imageRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        requestQueue.add(imageRequest);
    }

    public void getFilter (Response.Listener<JSONObject> listener){
        final String urlGetFilter = URL + "/ReciptizerServer/getFilter";
        Log.d(ActivityMain.LOG_TAG,urlGetFilter);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, urlGetFilter, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper.toastNoConnection(CONTEXT);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void sendRecipe (Recipe recipe){
        final String urlPostRecipe = URL + "/ReciptizerServer/postRecipe";
        Log.d(ActivityMain.LOG_TAG,urlPostRecipe);

        Recipe recipeForServer =  prepareRecipeForServer(recipe);

        JsonHelper jsonHelper = new JsonHelper();
        JSONObject jsonObject = jsonHelper.recipeToJsonObject(recipeForServer);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPostRecipe, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ToastHelper.toastRecipeIsSaved(CONTEXT);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //ToastHelper.toastNoConnection(CONTEXT);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void sendImage (final String imgPath){
        String[] imgName = imgPath.split("/");
        final String urlPostImage = URL + "/ReciptizerServer/postImage/" + imgName[imgName.length-1];
        Log.d(ActivityMain.LOG_TAG,urlPostImage);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlPostImage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {

                return convertBitmapToByteArray(imgPath);
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);

    }

    public class ImageRequest extends Request<byte[]> {

        private final Response.Listener<byte[]> listener;
        private final Response.ErrorListener errorListener;

        public ImageRequest(String url, Response.Listener<byte[]> listener,Response.ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            this.listener = listener;
            this.errorListener = errorListener;
        }

        @Override
        protected void deliverResponse(byte[] response) {
            listener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            errorListener.onErrorResponse(error);
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    public static byte[] convertBitmapToByteArray(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path, null);

        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }finally {
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Recipe prepareRecipeForServer (Recipe currentRecipe){
        Table1 table1;
        {
            Integer id = null;
            String recipe = currentRecipe.table1.recipe;
            String category = currentRecipe.table1.category;
            String kitchen = currentRecipe.table1.kitchen;
            String preferences = currentRecipe.table1.preferences;
            Integer time = currentRecipe.table1.time;
            Integer portion = currentRecipe.table1.portion;
            String imageFull = null;
            if (currentRecipe.table1.imageFull != null)
                imageFull = currentRecipe.table1.imageFull.split("/")[currentRecipe.table1.imageFull.split("/").length - 1];
            String imageTitle = null;
            if (currentRecipe.table1.imageTitle != null)
                imageTitle = currentRecipe.table1.imageTitle.split("/")[currentRecipe.table1.imageTitle.split("/").length - 1];
            table1 = new Table1(id,recipe,category,kitchen,preferences,time,portion,imageFull,imageTitle);
        }

        List<Table2Row> rowsTable2 = new ArrayList<>();
        for(Table2Row table2Row : currentRecipe.rowsTable2)
        {
            Integer id = null;
            Integer idRecipe = null;
            String ingredient = table2Row.ingredient;
            Integer quantity = table2Row.quantity;
            String measure = table2Row.measure;
            rowsTable2.add(new Table2Row(id,idRecipe,ingredient,quantity,measure));
        }

        List<Table3Row> rowsTable3 = new ArrayList<>();
        for(Table3Row table3Row : currentRecipe.rowsTable3)
        {
            Integer id = null;
            Integer idRecipe = null;
            Integer number = table3Row.number;
            String text = table3Row.text;
            String imageFull = null;
            if(table3Row.imageFull!=null)
                imageFull = table3Row.imageFull.split("/")[table3Row.imageFull.split("/").length-1];
            String imageTitle = null;
            if(table3Row.imageTitle!=null)
                imageTitle = table3Row.imageTitle.split("/")[table3Row.imageTitle.split("/").length-1];
            rowsTable3.add(new Table3Row(id,idRecipe,number,text,imageFull,imageTitle));
        }
        return new Recipe(table1,rowsTable2,rowsTable3);
    }
}