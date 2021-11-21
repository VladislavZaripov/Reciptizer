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
import reciptizer.Local.ActivityFilter;
import reciptizer.Server.ActivityFilterServer;
import reciptizer.Local.SQL;
import java.lang.reflect.Field;

import static reciptizer.Common.Helpers.AnimHelper.AnimMainButton;
import static reciptizer.Local.SQL.DB_NAME;

public class ActivityMain extends Activity {

    public static final String LOG_TAG = "myLogs";
    public SQL SQL;
    Button buttonServer, buttonLocal;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDB();
        Log.d(ActivityMain.LOG_TAG,"Main_openDB: path: " + getDatabasePath(DB_NAME).getAbsolutePath());
        overrideFont(getApplicationContext(), "SANS_SERIF", "fonts/Gabriela-Regular.ttf");
        Log.d(ActivityMain.LOG_TAG,"Main_overrideFont");
        prepareColorBar();
        Log.d(ActivityMain.LOG_TAG,"Main_prepareColorBar");
        prepareActivity();
        Log.d(ActivityMain.LOG_TAG,"Main_prepareActivity");
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        SQL.close();
    }

    public void openDB (){
        SQL = new SQL(this);
        SQL.open();
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
        buttonServer = findViewById(R.id.main_button_server);
        buttonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(ActivityMain.this, ActivityFilterServer.class);
                        startActivity(intent);
                    }
                };
                AnimMainButton(buttonServer, adapter);
            }
        });
        buttonLocal = findViewById(R.id.main_button_local);
        buttonLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(ActivityMain.this, ActivityFilter.class);
                        startActivity(intent);
                    }
                };
                AnimMainButton(buttonLocal, adapter);
            }
        });
    }
}