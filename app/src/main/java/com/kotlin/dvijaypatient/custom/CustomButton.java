package com.kotlin.dvijaypatient.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatButton;

import com.kotlin.dvijaypatient.R;


/**
 * Created by Pooja Jadhav on 26/05/22, 5:45 PM.
 */
public class CustomButton extends AppCompatButton {

    private static final String TAG = CustomButton.class.getSimpleName();

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomButton);
            String customFont = a.getString(R.styleable.CustomButton_setCustomFont);
            setCustomFont(ctx, customFont);
            a.recycle();
        }
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Unable to load typeface: "+e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }
}