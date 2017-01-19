package com.biglynx.fulfiller.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontBoldHeavy extends TextView {
    public CustomFontBoldHeavy(Context context, AttributeSet set) {
        super(context, set);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Heavy.ttf"));
    }
}
