package com.biglynx.fulfiller.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextFontLight extends EditText {
    public EditTextFontLight(Context context, AttributeSet set) {
        super(context, set);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Regular.ttf"));
    }
}
