package upwork.sowl.com.upwork.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import upwork.sowl.com.upwork.R;

/**
 * Created by evgenii on 6/24/17.
 */

public class ConfirmTrackingDialog extends AppCompatDialogFragment {

    private ConfirmListener confirmListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_tracking_title)
                .setMessage(R.string.confirm_tracking_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmListener.onConfirmed();
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConfirmListener) {
            this.confirmListener = (ConfirmListener) context;
        }
    }

    interface ConfirmListener {
        void onConfirmed();
    }
}
