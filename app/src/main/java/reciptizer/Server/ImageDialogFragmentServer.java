package reciptizer.Server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import reciptizer.Common.Recipe.BitmapTempSingleton;
import reciptizer.Local.ImageDialogFragment;

public class ImageDialogFragmentServer extends ImageDialogFragment {


    public ImageDialogFragmentServer(String imgFull, String imgTitle) {
        super(imgFull, imgTitle);
    }

    @Override
    protected void setImageDialog() {
        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(imgTitle))
            imageViewJpeg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(imgTitle));

        if(BitmapTempSingleton.getInstance().getTempBitmap().containsKey(imgFull))
            imageViewJpeg.setImageBitmap(BitmapTempSingleton.getInstance().getTempBitmap().get(imgFull));
        else {
            Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    final Bitmap bitmap_jpg = BitmapFactory.decodeByteArray(response, 0, response.length);
                    BitmapTempSingleton.getInstance().setBitmap(imgFull, bitmap_jpg);
                    imageViewJpeg.setImageBitmap(bitmap_jpg);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            };
            ServerAPISingleton.getInstance(imageViewJpeg.getContext()).getImage(imgFull, listener, errorListener);
        }
    }
}