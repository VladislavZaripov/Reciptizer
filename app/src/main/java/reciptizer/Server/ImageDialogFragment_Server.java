package reciptizer.Server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import reciptizer.Common.Recipe.BitmapTempSingleton;
import reciptizer.Local.ImageDialogFragment;

public class ImageDialogFragment_Server extends ImageDialogFragment {


    public ImageDialogFragment_Server(String img_jpeg, String img_png) {
        super(img_jpeg, img_png);
    }

    @Override
    protected void setImageDialog() {
        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_png))
            imageViewJpeg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_png));

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(img_jpeg))
            imageViewJpeg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(img_jpeg));
        else {
            Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    final Bitmap bitmap_jpg = BitmapFactory.decodeByteArray(response, 0, response.length);
                    BitmapTempSingleton.getInstance().setBitmap(img_jpeg, bitmap_jpg);
                    imageViewJpeg.setImageBitmap(bitmap_jpg);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            };
            ServerAPISingleton.getInstance(imageViewJpeg.getContext()).getImage(img_jpeg, listener, errorListener);
        }
    }
}