package cherry.android.toast.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import cherry.android.toast.Toaster;

/**
 * Created by ROOT on 2017/8/15.
 */

public final class GlobalToast {
    private GlobalToast() {
        throw new AssertionError("no instance.");
    }

    private static Toaster _toaster;
    private static TextView _textView;

    public static void showToast(@NonNull Context context, @NonNull CharSequence text) {
        if (_toaster == null) {
            _toaster = (Toaster) Toaster.makeToast(context, text);
            _textView = _toaster.getView().findViewById(cherry.android.toast.R.id.tv_toast_text);
        } else {
            _textView.setText(text);
        }
        _toaster.show();
    }
}
