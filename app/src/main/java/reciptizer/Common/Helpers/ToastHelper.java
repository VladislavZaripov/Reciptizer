package reciptizer.Common.Helpers;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    public static void toastLoadImage (Context context) {
        Toast.makeText(context,"Загрузка изображений",Toast.LENGTH_LONG).show();
    }

    public static void toastRecipeIsSaved (Context context) {
        Toast.makeText(context,"Рецепт сохранён",Toast.LENGTH_LONG).show();
    }

    public static void toastNoConnection (Context context) {
        Toast.makeText(context,"Нет соединения с сервером",Toast.LENGTH_LONG).show();
    }
}

