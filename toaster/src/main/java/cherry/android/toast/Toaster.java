package cherry.android.toast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/6/20.
 */

public final class Toaster extends Toast {

    @ColorInt
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    @ColorInt
    private static int ERROR_COLOR = Color.parseColor("#D50000");
    @ColorInt
    private static int INFO_COLOR = Color.parseColor("#3F51B5");
    @ColorInt
    private static int SUCCESS_COLOR = Color.parseColor("#388E3C");
    @ColorInt
    private static int WARNING_COLOR = Color.parseColor("#FFA900");
    @ColorInt
    private static int NORMAL_COLOR = Color.parseColor("#353A3E");

    private static final Typeface DEFAULT_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);

    private static final int DEFAULT_TEXT_SIZE = 16;

    private WindowManager.LayoutParams mParams;

    public Toaster(Context context) {
        super(context);
    }

    public Toaster(Context context, @StyleRes int animationStyle) {
        super(context);
        reflectAnimationStyle();
        mParams.windowAnimations = animationStyle;
    }

    private void reflectAnimationStyle() {
        try {
            Field mTNField = Toast.class.getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(this);

            mTNField.setAccessible(false);
            Class mTNClass = mTNField.getType();
            Field mParamsField = mTNClass.getDeclaredField("mParams");
            mParamsField.setAccessible(true);
            mParams = (WindowManager.LayoutParams) mParamsField.get(mTN);
            mParamsField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {
        private Context context;
        private Drawable icon;
        private CharSequence message;
        @ColorInt
        private int textColor;
        private int textSize;
        @ColorInt
        private int tintColor;
        private int duration;
        private int gravity;
        private int xOffset;
        private int yOffset;
        private int animationStyle;


        public Builder(@NonNull Context context) {
            this.context = context;
            this.tintColor = -1;
            this.gravity = Gravity.BOTTOM;
            this.duration = LENGTH_SHORT;
            this.animationStyle = -1;
            this.textColor = DEFAULT_TEXT_COLOR;
            this.textSize = DEFAULT_TEXT_SIZE;
        }

        public Builder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder setIconId(@DrawableRes int id) {
            this.icon = ContextCompat.getDrawable(this.context, id);
            return this;
        }

        public Builder setMessage(@NonNull CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setTextColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        public Builder setTextColorRes(@ColorRes int colorResId) {
            this.textColor = ContextCompat.getColor(this.context, colorResId);
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setTintColor(@ColorInt int color) {
            this.tintColor = color;
            return this;
        }

        public Builder setTintColorRes(@ColorRes int colorResId) {
            this.tintColor = ContextCompat.getColor(this.context, colorResId);
            return this;
        }

        public Builder setGravity(int gravity, int xOffset, int yOffset) {
            this.gravity = gravity;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setAnimationStyle(@StyleRes int animationStyle) {
            this.animationStyle = animationStyle;
            return this;
        }

        public Toast build() {
            return makeToast(this);
        }
    }

    private static final Toast makeToast(Builder builder) {
        final Context context = builder.context;
        Toast toast;
        if (builder.animationStyle != -1) {
            toast = new Toaster(context, builder.animationStyle);
        } else {
            toast = new Toaster(context);
        }
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_base_toast, null);
        ImageView iconView = contentView.findViewById(R.id.iv_toast_icon);
        TextView textView = contentView.findViewById(R.id.tv_toast_text);

        //Toast background
        Drawable backgroundDrawable;
        if (builder.tintColor != -1) {
            backgroundDrawable = tint9PatchDrawable(context, R.drawable.ic_toast_frame, builder.tintColor);
        } else {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_toast_frame);
        }
        ViewCompat.setBackground(contentView, backgroundDrawable);

        //icon view
        if (builder.icon == null) {
            iconView.setVisibility(View.GONE);
        } else {
            ViewCompat.setBackground(iconView, builder.icon);
        }

        //text content view
        textView.setTextColor(builder.textColor);
        textView.setTypeface(DEFAULT_TOAST_TYPEFACE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, builder.textSize);
        textView.setText(builder.message);

        //toast
        toast.setView(contentView);
        toast.setGravity(builder.gravity, builder.xOffset, builder.yOffset);
        toast.setDuration(builder.duration);
        return toast;
    }

    public static final Toast makeToast(@NonNull Context context,
                                        @NonNull CharSequence message) {
        return makeToast(context, message, null, -1);
    }

    public static final Toast makeToast(@NonNull Context context,
                                        @NonNull CharSequence message,
                                        Drawable icon,
                                        @ColorInt int tintColor) {
        return makeToast(context, message, icon, tintColor, LENGTH_SHORT);
    }

    public static final Toast makeToast(@NonNull Context context,
                                        @NonNull CharSequence message,
                                        int duration) {
        return makeToast(context, message, null, -1, duration);
    }

    public static final Toast makeToast(@NonNull Context context,
                                        @NonNull CharSequence message,
                                        Drawable icon,
                                        @ColorInt int tintColor,
                                        int duration) {
        return new Builder(context).setMessage(message)
                .setIcon(icon)
                .setTintColor(tintColor)
                .setDuration(duration)
                .setAnimationStyle(R.style.BaseToastAnimation)
                .setGravity(Gravity.TOP, 0, getDefaultToastYOffset(context))
                .build();
    }

    public static final Toast normal(@NonNull Context context,
                                     @NonNull CharSequence message) {
        return normal(context, message, null);
    }

    public static final Toast normal(@NonNull Context context,
                                     @NonNull CharSequence message,
                                     Drawable icon) {
        return makeToast(context, message, icon, NORMAL_COLOR);
    }

    public static final Toast iInfo(@NonNull Context context,
                                    @NonNull CharSequence message) {
        return info(context, message, ContextCompat.getDrawable(context, R.drawable.ic_info_outline_white_48dp));
    }

    public static final Toast info(@NonNull Context context,
                                   @NonNull CharSequence message) {
        return info(context, message, null);
    }

    public static final Toast info(@NonNull Context context,
                                   @NonNull CharSequence message,
                                   Drawable icon) {
        return makeToast(context, message, icon, INFO_COLOR);
    }

    public static final Toast iWarning(@NonNull Context context,
                                       @NonNull CharSequence message) {
        return warning(context, message, ContextCompat.getDrawable(context, R.drawable.ic_warning_outline_white));
    }

    public static final Toast warning(@NonNull Context context,
                                      @NonNull CharSequence message) {
        return warning(context, message, null);
    }

    public static final Toast warning(@NonNull Context context,
                                      @NonNull CharSequence message,
                                      Drawable icon) {
        return makeToast(context, message, icon, WARNING_COLOR);
    }

    public static final Toast iError(@NonNull Context context,
                                     @NonNull CharSequence message) {
        return error(context, message, ContextCompat.getDrawable(context, R.drawable.ic_error_outline_white_48dp));
    }

    public static final Toast error(@NonNull Context context,
                                    @NonNull CharSequence message) {
        return error(context, message, null);
    }

    public static final Toast error(@NonNull Context context,
                                    @NonNull CharSequence message,
                                    Drawable icon) {
        return makeToast(context, message, icon, ERROR_COLOR);
    }

    public static final Toast iSuccess(@NonNull Context context,
                                       @NonNull CharSequence message) {
        return success(context, message, ContextCompat.getDrawable(context, R.drawable.ic_check_white_48dp));
    }

    public static final Toast success(@NonNull Context context,
                                      @NonNull CharSequence message) {
        return success(context, message, null);
    }

    public static final Toast success(@NonNull Context context,
                                      @NonNull CharSequence message,
                                      Drawable icon) {
        return makeToast(context, message, icon, SUCCESS_COLOR);
    }

    private static Drawable tint9PatchDrawable(Context context, @DrawableRes int resId, @ColorInt int tint) {
        NinePatchDrawable drawable = (NinePatchDrawable) ContextCompat.getDrawable(context, resId);
        drawable.setColorFilter(tint, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    private static int getDefaultToastYOffset(Context context) {
        int yOffset = 0;
        int id = context.getResources().getIdentifier("toast_y_offset", "dimen", "android");
        if (id != 0) {
            yOffset = context.getResources().getDimensionPixelSize(id);
        }
        return yOffset;
    }
}