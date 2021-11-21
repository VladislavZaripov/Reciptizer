package reciptizer.Common.Recipe;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class BitmapTempSingleton {

    private static BitmapTempSingleton INSTANCE;
    private Map<String, Bitmap> tempBitmap;

    public BitmapTempSingleton() {
        tempBitmap = getTempBitmap();
    }

    public Map<String, Bitmap> getTempBitmap() {
        if (tempBitmap == null) {
            tempBitmap = new HashMap<>();
        }
        return tempBitmap;
    }

    public void setBitmap (String nameImage,Bitmap bitmap) {
        getTempBitmap().put(nameImage,bitmap);
    }

    public static synchronized BitmapTempSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BitmapTempSingleton();
        }
        return INSTANCE;
    }
}