package com.mapcmarkar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mapcmarkar.R;

/**
 * Created by kamal on 11/14/2017.
 */

public class CircleView  extends FrameLayout {

    private final Path path;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final float radius = context.getResources().getDimension(R.dimen.marker_size) / 2;

        path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CW);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.clipPath(path);
        super.draw(canvas);
    }
}

