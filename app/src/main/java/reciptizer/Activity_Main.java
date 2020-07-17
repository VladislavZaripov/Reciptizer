package reciptizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Typeface;
import com.example.reciptizer.R;
import reciptizer.Local.Activity_Filter;
import reciptizer.Server.Activity_Filter_Server;
import reciptizer.Local.DB;

import java.lang.reflect.Field;

import static reciptizer.Common.Helpers.AnimHelper.AnimMainButton;

public class Activity_Main extends Activity {

    public static final String LOG_TAG = "myLogs";
    public DB db;
    Button button1, button2;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDB();
        Log.d(Activity_Main.LOG_TAG,"Main_openDB: path: " + getDatabasePath(DB.DB_NAME).getAbsolutePath());
        overrideFont(getApplicationContext(), "SANS_SERIF", "fonts/Gabriela-Regular.ttf");
        Log.d(Activity_Main.LOG_TAG,"Main_overrideFont");
        prepareColorBar();
        Log.d(Activity_Main.LOG_TAG,"Main_prepareColorBar");
        prepareActivity();
        Log.d(Activity_Main.LOG_TAG,"Main_prepareActivity");
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        db.close();
    }

    public void openDB (){
        db = new DB(this);
        db.open();
    }

    public static void overrideFont (Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {e.printStackTrace();}
    }

    private void prepareColorBar (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor( R.color.setStatusBarColor));
            window.setNavigationBarColor(getColor(R.color.setStatusBarColor));
        }
    }

    private void prepareActivity () {
        button1 = findViewById(R.id.Main_button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(Activity_Main.this, Activity_Filter_Server.class);
                        startActivity(intent);
                    }
                };
                AnimMainButton(button1, adapter);
            }
        });
        button2 = findViewById(R.id.Main_button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(Activity_Main.this, Activity_Filter.class);
                        startActivity(intent);
                    }
                };
                AnimMainButton(button2, adapter);
            }
        });
    }
}