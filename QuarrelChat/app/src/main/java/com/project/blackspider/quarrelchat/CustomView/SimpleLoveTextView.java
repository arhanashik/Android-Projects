package com.project.blackspider.quarrelchat.CustomView;

import android.graphics.Typeface;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Mr blackSpider on 8/9/2017.
 */

public class SimpleLoveTextView extends EmojiconTextView {

    public SimpleLoveTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public SimpleLoveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public SimpleLoveTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        // Just Change your font name
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/simple_love.ttf");
        setTypeface(myTypeface);
    }
}
