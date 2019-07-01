package com.example.autocomplete;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AutoCompleteTextView;

public class HashTagAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public HashTagAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public HashTagAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoCompleteTextViewStyle);
    }

    public HashTagAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void replaceText(CharSequence text) {
        Log.d("ㅋㅋㅋ","sdasd");
        clearComposingText();

        HashTagSuggestAdapter adapter = (HashTagSuggestAdapter) getAdapter();
        HashTagSuggestAdapter.HashTagFilter filter = (HashTagSuggestAdapter.HashTagFilter) adapter.getFilter();

        // span은 인력된 전체 문자열
        Editable span = getText();

        // text는 리스트로부터 선택된 단어
        span.replace(filter.start, filter.end, text);
    }
}