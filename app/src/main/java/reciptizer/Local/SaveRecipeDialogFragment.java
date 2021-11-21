package reciptizer.Local;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SaveRecipeDialogFragment extends DialogFragment{

    public interface SaveRecipeDialogListener {
        void onSaveRecipeDialogPositiveClick(DialogFragment dialog);
        String setDialogTitle();
    }

    SaveRecipeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SaveRecipeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SaveRecipeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(listener.setDialogTitle());
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onSaveRecipeDialogPositiveClick(SaveRecipeDialogFragment.this);
            }
        });
        adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return adb.create();
    }
}
