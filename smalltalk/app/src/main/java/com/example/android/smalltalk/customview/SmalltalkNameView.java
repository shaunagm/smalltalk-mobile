package com.example.android.smalltalk.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by shauna on 9/1/15.
 */
public class SmalltalkNameView extends EditText {

    OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
        }
    };

    public SmalltalkNameView(Context context) {
        super(context);
        setOnFocusChangeListener(mFocusChangeListener);
    }

    public SmalltalkNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnFocusChangeListener(mFocusChangeListener);
    }

    public SmalltalkNameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnFocusChangeListener(mFocusChangeListener);
    }


}
