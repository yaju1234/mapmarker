package com.mapcmarkar;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.mapcmarkar.restservice.APIHelper;
import com.mapcmarkar.restservice.RestService;
import com.mapcmarkar.utility.Utils;
import com.mapcmarkar.views.ColorMarker;
import com.oguzbabaoglu.fancymarkers.CustomMarker;
import com.oguzbabaoglu.fancymarkers.MarkerManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    boolean isMapLoaded = false;
    private GoogleMap mMap;
    private MarkerManager<ColorMarker> colorMarkerManager;
    private ProgressDialog progressDialog;
    private CameraUpdate cameraPosition;
    private int color[]={};
    private Random randColor;
    private boolean isShowFirstTime=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
         color= new int[]{getResources().getColor(R.color.color1),
                 getResources().getColor(R.color.color2),
                 getResources().getColor(R.color.color3),
                 getResources().getColor(R.color.color4),
                 getResources().getColor(R.color.color5),
                 getResources().getColor(R.color.color6),
                 getResources().getColor(R.color.color7),
                 getResources().getColor(R.color.color8),
                 getResources().getColor(R.color.color9),
                 getResources().getColor(R.color.color10)};
        randColor = new Random();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Loading...");
        if (isNetworkAvailable()){
            doWebServiceCall();
        }else
            Toast.makeText(MapsActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        colorMarkerManager = new MarkerManager<>(googleMap);
        colorMarkerManager.setOnMarkerClickListener(new DisableClick<ColorMarker>());
        mMap = googleMap;
        mMap.setOnMarkerClickListener(colorMarkerManager);

    }

    private static class DisableClick<T extends CustomMarker>
            implements MarkerManager.OnMarkerClickListener<T> {

        @Override
        public boolean onMarkerClick(T marker) {
            return true;
        }
    }


    public void doWebServiceCall() {
        progressDialog.show();
        Call<ResponseBody> getDepartment = RestService.getInstance().restInterface.testmap();

        APIHelper.enqueueWithRetry(getDepartment, new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200 && response != null) {
                    try {

                        String res = response.body().string();
                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            if (jsonArray != null && jsonArray.length() > 0) {
                                // AddressModel model;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //   model=new AddressModel();
                                    JSONObject actor = jsonArray.getJSONObject(i);
                             /*      String NetAutonumber = actor.getString("NetAutonumber");
                                   String clientName = actor.getString("item1");
                                   String address = actor.getString("item3");
                                   String sellername = actor.getString("item4");*/
                                 /*  model.setNetAutonumber(actor.getString("NetAutonumber"));
                                   model.setItem1(actor.getString("item1"));
                                   model.setItem2(actor.getString("item2"));
                                   model.setItem3(actor.getString("item3"));
                                   model.setItem4(actor.getString("item4"));
                                   model.setNetData(actor.getString("NetData"));
                                   model.setNote(actor.getString("Note"));
                                   model.setSubGrup(actor.getInt("SubGrup"));*/
                                    //addressList.add(model);

                                    new GetLatLongTask().execute(actor.getString("item3"), actor.getString("item1"));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }


    private class GetLatLongTask extends AsyncTask<String, JSONObject, JSONObject> {
       String clientName=null;
        protected JSONObject doInBackground(String... urls) {
            JSONObject jsonObject = getLocationInfo(urls[0]);
             clientName=urls[1];
            return jsonObject;
        }


        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            if(s!=null){
                LatLng latLng = Utils.getLatLong(s);
                if(latLng == null){
                    if (!isShowFirstTime) {
                        Toast.makeText(MapsActivity.this, "Unable to get the location", Toast.LENGTH_LONG).show();
                        isShowFirstTime=true;
                    }

                    return;
                }

                IconGenerator iconFactory = new IconGenerator(MapsActivity.this);
                iconFactory.setColor(color[randColor.nextInt(10)]);

                addIcon(iconFactory, clientName, latLng);

                if (!isMapLoaded) {
                    cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 8);

                    mMap.moveCamera(cameraPosition);
                    mMap.animateCamera(cameraPosition);
                    isMapLoaded = true;
                }
            }else {
                if (!isShowFirstTime) {
                    Toast.makeText(MapsActivity.this, "Unable to get the location", Toast.LENGTH_LONG).show();
                    isShowFirstTime=true;
                }
            }

        }

    }


    public JSONObject getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).

                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }
    public  boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
