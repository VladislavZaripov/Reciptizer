package reciptizer.Common.Helpers;

import android.content.Context;

public class ConverterHelper {
    public static int dpToPx (int dp, Context context)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}