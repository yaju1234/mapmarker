package com.mapcmarkar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Loading...");
        doWebServiceCall();

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
        protected JSONObject doInBackground(String... urls) {
            JSONObject jsonObject = getLocationInfo(urls[0]);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            if(s!=null){
                LatLng latLng = Utils.getLatLong(s);
                if(latLng == null){
                    return;
                }
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title("kamal");

                mMap.addMarker(markerOptions);
                if (!isMapLoaded) {
                    cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 6);

                    mMap.moveCamera(cameraPosition);
                    mMap.animateCamera(cameraPosition);
                    isMapLoaded = true;
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

}
