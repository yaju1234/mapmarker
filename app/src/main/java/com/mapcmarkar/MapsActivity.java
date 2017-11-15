package com.mapcmarkar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.mapcmarkar.adapter.InvalidAddressAdapter;
import com.mapcmarkar.model.InvalidAddress;
import com.mapcmarkar.restservice.APIHelper;
import com.mapcmarkar.restservice.RestService;
import com.mapcmarkar.utility.Utils;
import com.mapcmarkar.views.ColorMarker;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    boolean isMapLoaded = false;
    private GoogleMap mMap;
    private MarkerManager<ColorMarker> colorMarkerManager;
    private ProgressDialog progressDialog;
    private CameraUpdate cameraPosition;
    private boolean isShowFirstTime=false;
    private CoordinatorLayout coordinator;
    private ArrayList<InvalidAddress> invalidAddressList=new ArrayList<>();
    private Map<String ,Integer> colorMap=new HashMap<>();
    private Dialog dialog;
    private ListView listItem;
    private InvalidAddressAdapter addressAdapter;
    private int count=0;
    private int totalItem;
    private String information="";
    private String sellerAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dialog=new Dialog(MapsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        coordinator=(CoordinatorLayout)findViewById(R.id.coordinator);
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
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
       String info[]= marker.getSnippet().split("~");
       String msg="Seller: "+info[0]+"\nAddress: "+info[2];

        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(info[1])
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        //Toast.makeText(getApplicationContext(), ""+, Toast.LENGTH_LONG).show();

        return false;
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
                                totalItem=jsonArray.length();
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

                                    new GetLatLongTask().execute(actor.getString("item3"), actor.getString("item1"),actor.getString("item4"));
                                }
                              /*  showSnackbar(invalidAddressList);
                                if (listItem!=null)
                                    addressAdapter.notifyDataSetChanged();*/

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
        String sellerName=null;
        protected JSONObject doInBackground(String... urls) {
            clientName=urls[1];
            sellerName=urls[2];
            sellerAddress=urls[0];
            JSONObject jsonObject = getLocationInfo(urls[0],sellerName);
            return jsonObject;
        }


        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            if(s!=null){
                LatLng latLng = Utils.getLatLong(s);
                if(latLng == null){
                    if (!isShowFirstTime) {
                        if (invalidAddressList!=null&&invalidAddressList.size()>0)
                            // showSnackbar(invalidAddressList,sellerName);
                            //Toast.makeText(MapsActivity.this, "Unable to get the location", Toast.LENGTH_LONG).show();
                            isShowFirstTime=true;
                    }

                    return;
                }
                IconGenerator iconFactory = new IconGenerator(MapsActivity.this);

                if (colorMap.containsKey(sellerName)){
                    iconFactory.setColor(colorMap.get(sellerName));
                }else {
                    colorMap.put(sellerName,Utils.randomColor());
                    iconFactory.setColor(Utils.randomColor());
                }

                addIcon(iconFactory, clientName, latLng,sellerName,sellerAddress);

                if (!isMapLoaded) {
                    cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 8);

                    mMap.moveCamera(cameraPosition);
                    mMap.animateCamera(cameraPosition);
                    isMapLoaded = true;
                }
            }else {
                if (!isShowFirstTime) {
                    if (invalidAddressList!=null&&invalidAddressList.size()>0)
                        // showSnackbar(invalidAddressList,sellerName);
                        //Toast.makeText(MapsActivity.this, "Unable to get the location", Toast.LENGTH_LONG).show();
                        isShowFirstTime=true;
                }
            }

        }

    }


    public JSONObject getLocationInfo(String address1,String sellerName) {
        StringBuilder stringBuilder = new StringBuilder();
        String address;

        try {
            address = address1.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("https://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false&key=AIzaSyDqneUplTRHo8Ac6GC2ZdEN7ciX9fSLL74");


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
            System.out.println("total...."+totalItem);
            count=count+1;
            System.out.println("count...."+count);
            jsonObject = new JSONObject(stringBuilder.toString());
            String status=jsonObject.getString("status");
            if (count==totalItem){
                showSnackbar(invalidAddressList);
                if (listItem!=null)
                    addressAdapter.notifyDataSetChanged();
            }
            if (status.equalsIgnoreCase("ZERO_RESULTS")){
                InvalidAddress invalidAddress=new InvalidAddress(address1,sellerName);
                invalidAddressList.add(invalidAddress);

                return null;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position,String sellName,String address) {
        information=sellName+"~"+text+"~"+address;
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).

                position(position).snippet(information).
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
    public void showSnackbar(final ArrayList<InvalidAddress> invalidAddressList){
        Snackbar snackbar = Snackbar
                .make(coordinator, "Unable to find location for seller  ", Snackbar.LENGTH_INDEFINITE)
                .setAction("VIEW ALL", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showInvalidAddressDialog(invalidAddressList);
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    void showInvalidAddressDialog(ArrayList<InvalidAddress> invalidAddressList){
        dialog.setContentView(R.layout.dialog_invalid_address);
        listItem = (ListView) dialog.findViewById(R.id.listItem);
        addressAdapter=new InvalidAddressAdapter(MapsActivity.this,invalidAddressList);
        listItem.setAdapter(addressAdapter);
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
            }
        });dialog.show();
    }

}
