package com.biglynx.fulfiller.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by snehitha.chinnapally on 12/23/2016.
 */
public class EditTextFontMedium extends EditText {
    public EditTextFontMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Medium.ttf"));
    }
}
