package reciptizer.Common.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.FileProvider;
import reciptizer.ActivityMain;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoHelper {

    private final Activity activity;
    private boolean haveFileCamera;
    private boolean haveFileLoad;
    private boolean havePhoto;
    private File fileTemp;
    private File fileCamera;
    private File fileLoad;
    private File fileImageView;

    public PhotoHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean isHaveFileCamera() {
        return haveFileCamera;
    }
    public void setHaveFileCamera(boolean makePhoto) {
        this.haveFileCamera = makePhoto;
    }
    public boolean isHaveFileLoad() {
        return haveFileLoad;
    }
    public void setHaveFileLoad(boolean haveFileLoad) {
        this.haveFileLoad = haveFileLoad;
    }
    public boolean isHavePhoto() {
        return havePhoto;
    }
    public void setHavePhoto(boolean havePhoto) {
        this.havePhoto = havePhoto;
    }

    public void setFileTemp(File fileTemp) {
        this.fileTemp = fileTemp;
    }
    public void setFileLoad(File fileLoad) {
        this.fileLoad = fileLoad;
    }
    public void setFileCamera(File fileCamera) {
        this.fileCamera = fileCamera;
    }
    public void setFileImageView(File fileImageView) {
        this.fileImageView = fileImageView;
    }

    public File getFileTemp() {
        return fileTemp;
    }
    public File getFileLoad() {
        return fileLoad;
    }
    public File getFileCamera() {
        return fileCamera;
    }
    public File getFileImageView() {
        return fileImageView;
    }

    public void deleteFileCamera() {
        boolean result = false;
        if(fileCamera != null)
            result = fileCamera.delete();
        Log.d(ActivityMain.LOG_TAG, "FullJPEGCamera delete: " + result);
        fileCamera = null;
    }
    public void deleteFileLoad() {
        boolean result = false;
        if(fileLoad != null)
            result = fileLoad.delete();
        Log.d(ActivityMain.LOG_TAG, "FullJPEGLoad delete: " + result);
        fileLoad = null;
    }
    public void deleteFileTemp() {
        boolean result = false;
        if(fileTemp != null)
            result = fileTemp.delete();
        Log.d(ActivityMain.LOG_TAG, "tempFullJpegCamera delete: " + result);
        fileTemp = null;
    }
    public void deleteFileImageView() {
        boolean result = false;
        if(fileImageView != null)
            result = fileImageView.delete();
        Log.d(ActivityMain.LOG_TAG, "DecodePngForImageView delete: " + result);
        fileImageView = null;
    }
    public void deleteFileAll() {
        deleteFileCamera();
        deleteFileLoad();
        deleteFileTemp();
        deleteFileImageView();
    }

    public void intentCameraAndSaveImage(int requestCode, File tempFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoURI(tempFile));
                activity.startActivityForResult(takePictureIntent, requestCode);
                Log.d(ActivityMain.LOG_TAG,"Camera is intended");
        }
    }
    public void intentLoader(int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        if (photoPickerIntent.resolveActivity(activity.getPackageManager()) != null) {
                photoPickerIntent.setType("image/*");
                activity.startActivityForResult(photoPickerIntent, requestCode);
                Log.d(ActivityMain.LOG_TAG,"Loader is intended");
        }
    }

    public File createFile (String format){
        File file = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = format+"_" + timeStamp + "_";
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            file = File.createTempFile(imageFileName, "." + format, storageDir);

            Log.d(ActivityMain.LOG_TAG,"File is created: " + file);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(ActivityMain.LOG_TAG,"File is not created");
        }
        return file;
    }
    public void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            Log.d(ActivityMain.LOG_TAG,"File was saved");
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(ActivityMain.LOG_TAG,"PNGFile wasn't saved");
        }
    }

    public Bitmap getBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();

        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri))
        {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream,null, options);
            options.inSampleSize = calculateBitmapSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri))
        {
            bitmap = BitmapFactory.decodeStream(inputStream,null, options);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public Bitmap getBitmapFromFile (File JpegFile, int reqWidth, int reqHeight) {
        String path = JpegFile.getAbsolutePath();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateBitmapSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
    public Bitmap getBitmapFromResource (int id) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(activity.getResources(), id, options);
    }
    public int calculateBitmapSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        Log.d(ActivityMain.LOG_TAG,"calculateBitmapSize: " + inSampleSize);


        return inSampleSize;
    }
    public Bitmap rotateBitmap(Bitmap bitmap, int degree){
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Uri getPhotoURI (File file) {
        return FileProvider.getUriForFile(activity,"RecipeZaripov.fileprovider", file);
    }

    public void ImageMaker (ImageView imageView, Intent data) {
        if (isHaveFileCamera() || isHaveFileLoad()) {
            int degree;
            deleteFileCamera();
            deleteFileLoad();
            Bitmap bitmap;
            if(isHaveFileCamera()) {
                setFileCamera(createFile("png"));
                degree = photoDegree(getFileTemp());
                bitmap = getBitmapFromFile(getFileTemp(), 900, 1);
            }
            else {
                Uri selectedImage = data.getData();
                degree = photoDegree(selectedImage);
                setFileLoad(createFile("png"));
                bitmap = getBitmapFromUri(selectedImage,900,1);
            }

            Bitmap rotateBitmap = rotateBitmap(bitmap, degree);

            if (isHaveFileCamera()) {
                saveBitmapToFile(rotateBitmap, getFileCamera());
                deleteFileTemp();
            }
            else
                saveBitmapToFile(rotateBitmap, getFileLoad());
            deleteFileImageView();
            setFileImageView(createFile("png"));

            Bitmap bitmapView;

            if (isHaveFileCamera())
                bitmapView = getBitmapFromFile(getFileCamera(), 275, 275);
            else
                bitmapView = getBitmapFromFile(getFileLoad(), 275,275);

            imageView.setImageBitmap(bitmapView);
            saveBitmapToFile(bitmapView, getFileImageView());
            setHaveFileCamera(false);
            setHaveFileLoad(false);
            setHavePhoto(true);
        }
    }

    public int photoDegree (Object object) {
        ExifInterface exif = null;
        int degree = 0;
        if (object instanceof Uri) {
            try (InputStream inputStream = activity.getContentResolver().openInputStream((Uri) object)) {
                if (inputStream != null) {
                    exif = new ExifInterface(inputStream);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (object instanceof File) {
            try {
                exif = new ExifInterface(((File)object).getPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (exif!=null) {
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
        }
        return degree;
    }

    public void clearTemp () {
        setHaveFileCamera(false);
        setHaveFileLoad(false);
        deleteFileTemp();
    }
}