package com.mapcmarkar.views;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.mapcmarkar.R;
import com.oguzbabaoglu.fancymarkers.BitmapGenerator;
import com.oguzbabaoglu.fancymarkers.CustomMarker;

import java.util.Random;

/**
 * Created by kamal on 11/14/2017.
 */

public class ColorMarker extends CustomMarker {

    private static final int TINT = 0x66B2FF;

    private LatLng position;
    private View colorView;

    public ColorMarker(Context context, LatLng position) {
        this.position = position;

        colorView = LayoutInflater.from(context).inflate(R.layout.view_color_marker, null);
    }

    @Override
    public boolean onStateChange(boolean selected) {

        return selected;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {

        colorView.setBackgroundColor(generateRandomColor());
        return BitmapGenerator.fromView(colorView);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    private int generateRandomColor() {

        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        red = (red + Color.red(TINT)) >> 1;
        green = (green + Color.green(TINT)) >> 1;
        blue = (blue + Color.blue(TINT)) >> 1;

        return Color.rgb(red, green, blue);
    }
}
