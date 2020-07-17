package reciptizer.Common.Helpers;

import android.animation.*;
import android.graphics.Color;
import android.view.View;
import android.view.ViewAnimationUtils;
import androidx.core.content.ContextCompat;
import com.example.reciptizer.R;

public class AnimHelper {


    public static void AnimNewRecipeAddEmptyRow (View view) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -1000f, 0).setDuration(300));
        set.start();
    }

    public static void AnimNewRecipeDeleteRow (View view, AnimatorListenerAdapter adapter) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, 1000f).setDuration(300));
        set.addListener(adapter);
        set.start();
    }

    public static void AnimPhotoLoadClick (View view, AnimatorListenerAdapter adapter) {
        AnimatorSet set = new AnimatorSet();
        int colorFrom = ContextCompat.getColor(view.getContext(), R.color.AnimColor);
        set.playSequentially(
                ObjectAnimator.ofArgb(view, "backgroundColor", Color.WHITE, colorFrom).setDuration(200),
                ObjectAnimator.ofArgb(view, "backgroundColor", colorFrom, Color.WHITE).setDuration(200));
        set.addListener(adapter);
        set.start();
    }

    public static void AnimMenuClick (View view, AnimatorListenerAdapter adapter) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float radius = view.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0).setDuration(300);
        anim.addListener(adapter);
        anim.start();
    }

    public static void AnimAddButtonTable2 (View view) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat((View)view.getParent(), View.TRANSLATION_Y, -90f, 0f).setDuration(300));
        set.start();
    }

    public static void AnimAddButtonTable3 (View view) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat((View)view.getParent(), View.TRANSLATION_Y, -220f, 0f).setDuration(300));
        set.start();
    }

    public static void AnimCreateRecipe (View view, AnimatorListenerAdapter adapter) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                ObjectAnimator.ofFloat(view, View.ROTATION_X, 0f, 720*3f).setDuration(1000),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, 1000f).setDuration(300));
        set.addListener(adapter);
        set.start();
    }

    public static void AnimClick (View view, AnimatorListenerAdapter adapter) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.5f).setDuration(100),
                ObjectAnimator.ofFloat(view, View.ALPHA, 0.5f, 1f).setDuration(100));
        set.addListener(adapter);
        set.start();
    }

    public static void AnimSpinnerOn (View view) {
        AnimatorSet set = new AnimatorSet();
        set.play (ObjectAnimator.ofFloat(view, View.ROTATION_X, 0f, 360f).setDuration(700));
        set.start();
    }

    public static void AnimMainButton (View view, AnimatorListenerAdapter adapter) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f).setDuration(300));
        set.addListener(adapter);
        set.start();
    }
}