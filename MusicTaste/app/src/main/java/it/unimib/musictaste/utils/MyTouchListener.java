package it.unimib.musictaste.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import it.unimib.musictaste.R;

public class MyTouchListener implements View.OnTouchListener {
    private EditText editText;

    public MyTouchListener(EditText editText) {
        this.editText = editText;

        setupDrawable(this.editText);
    }

    private void setupDrawable(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_focused, 0, R.drawable.ic_cancel, 0);
                else
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_focused, 0, 0, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (editText.getCompoundDrawables()[2] != null) {
                if (event.getX() >= (editText.getRight() - editText.getLeft() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    editText.setText("");
                }
            }
        }
        return false;

    }
}
