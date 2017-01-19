package com.biglynx.fulfiller.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontSemiBold extends TextView {
    public CustomFontSemiBold(Context context, AttributeSet set) {
        super(context, set);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Semibold.ttf"));
    }
}
