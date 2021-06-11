package it.unimib.musictaste.utils;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;


public class SquareImage extends androidx.appcompat.widget.AppCompatImageView {

    public SquareImage(final Context context) {
        super(context);
    }

    public SquareImage(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImage(final Context context, final AttributeSet attrs,
                       final int defStyle) {
        super(context, attrs, defStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onMeasure(int width, int height) {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int widthScreen = displaymetrics.widthPixels;


        //int widthScreen = displayMetrics.widthPixels;
        super.onMeasure(width, height);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        setMeasuredDimension(measuredHeight, widthScreen);
        setMeasuredDimension(measuredWidth, widthScreen);

    }

}
