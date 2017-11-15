package com.mapcmarkar.utility;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 14/11/17.
 */

public class Utils {
    public static LatLng getLatLong(JSONObject jsonObject) {


        try {
            double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
            LatLng latLng = new LatLng(latitude, longitute);
            return latLng;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;


    }


}
