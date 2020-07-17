package reciptizer.Local;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import com.example.reciptizer.R;
import reciptizer.Common.Helpers.PhotoHelper;

import java.util.Objects;

public class ImageDialogFragment extends DialogFragment {

    protected String img_jpeg;
    protected String img_png;
    protected ImageView imageViewJpeg;
    private ProgressBar progressBar;

    public ImageDialogFragment(String img_jpeg, String img_png) {
        this.img_jpeg = img_jpeg;
        this.img_png = img_png;
    }

    public ImageDialogFragment(PhotoHelper photoHelper) {
        if (photoHelper.getFileLoad()!=null) {
            img_jpeg = photoHelper.getFileLoad().getPath();
            img_png = photoHelper.getFileImageView().getPath();
        }
        if (photoHelper.getFileCamera()!=null){
            img_jpeg = photoHelper.getFileCamera().getPath();
            img_png = photoHelper.getFileImageView().getPath();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ConstraintLayout view = (ConstraintLayout) Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_recipe_photo, null);
        imageViewJpeg = view.findViewById(R.id.Dialog_recipe_jpeg);
        progressBar = view.findViewById(R.id.Dialog_recipe_progressBar);

        if (img_jpeg == null||img_jpeg.equals("null"))
            imageViewJpeg.setImageResource(R.drawable.no_img);
        else {
            setImageDialog();
        }
        builder.setView(view);
        Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.dialog_animation_fade;
        return dialog;
    }

    protected void setImageDialog () {
        Task task = new Task();
        task.execute();
    }

    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask <Void, Void, Void> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            imageViewJpeg.setImageURI(Uri.parse(img_png));
        }

        @Override
        protected Void doInBackground(Void[] strings) {
            bitmap = BitmapFactory.decodeFile(img_jpeg);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.INVISIBLE);
            imageViewJpeg.setImageBitmap(bitmap);
        }
    }
}