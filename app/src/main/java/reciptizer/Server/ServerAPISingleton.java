package reciptizer.Server;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import reciptizer.Activity_Main;
import org.json.JSONObject;
import reciptizer.Common.Helpers.JsonHelper;
import reciptizer.Common.Helpers.ToastHelper;
import reciptizer.Common.Recipe.Recipe;

public class ServerAPISingleton {

    private static ServerAPISingleton instance;
    private RequestQueue requestQueue;
    private static Context context;
    String URL ="http://192.168.1.44:8080";

    public ServerAPISingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized ServerAPISingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ServerAPISingleton(context);
        }
        return instance;
    }

    public void getRecipe (Integer recipeId, Response.Listener<JSONObject> listener){
        final String urlGetRecipe = URL + "/ReciptizerServer/get?getRecipe=" + recipeId;
        Log.d(Activity_Main.LOG_TAG,urlGetRecipe);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, urlGetRecipe, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper.toastNoConnection(context);
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
        final String urlGetImage = URL + "/ReciptizerServer/get?getImage=" + imgName;
        Log.d(Activity_Main.LOG_TAG,urlGetImage);

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
        final String urlGetFilter = URL + "/ReciptizerServer/get?getFilter=filter";
        Log.d(Activity_Main.LOG_TAG,urlGetFilter);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, urlGetFilter, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper.toastNoConnection(context);
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
        final String urlPostRecipe = URL + "/ReciptizerServer/post?postRecipe=recipe";
        Log.d(Activity_Main.LOG_TAG,urlPostRecipe);

        JsonHelper jsonHelper = new JsonHelper();
        JSONObject jsonObject = jsonHelper.recipeToJsonObject(recipe);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPostRecipe, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ToastHelper.toastRecipeIsSaved(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper.toastNoConnection(context);
                    }
                });

        requestQueue.add(jsonObjectRequest);
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
}