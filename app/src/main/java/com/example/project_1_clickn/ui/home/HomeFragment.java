package com.example.project_1_clickn.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.project_1_clickn.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.plugins.annotation.AnnotationManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MapView mapView;
    private MapboxMap _mapboxMap;
    private Style _style;
    private static ProgressDialog mProgressDialog;
    LocationManager mLocationManager;
    private double currentlocationLatitude;
    private double currentLocationLongitude;
    private static final String ID_ICON_LOCATION = "location";
    private JSONObject eventsJsonObject;
    private JsonObject currentlySelectedEventJsonObject = null;
    private final int jsoncode = 1;
    private String server_ip = "10.104.1.122";
    private String jsonURL = "http://" + server_ip + ":3000/events/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                _mapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        _style = style;
                        style.addImage(ID_ICON_LOCATION,
                                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_location_on_red_24dp)),
                                true);
                        fetchJSON();
                    }
                });
            }
        });
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    public void handleEventSymbolClick(Symbol symbol) {
        currentlySelectedEventJsonObject = symbol.getData().getAsJsonObject();
        FragmentManager fragmentManager = getFragmentManager();
        JoinEvent joinEvent = new JoinEvent(currentlySelectedEventJsonObject);
        joinEvent.show(fragmentManager, "joinEvent");
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchJSON(){

        showSimpleProgressDialog(getActivity(), "Loading...","Fetching Json",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response = "";
                try {
                    HttpURLConnection urlConnection = null;
                    URL url = new URL(jsonURL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000 /* milliseconds */);
                    urlConnection.setConnectTimeout(15000 /* milliseconds */);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    response = sb.toString();
                } catch (Exception e) {
                    response = e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                onTaskCompleted(result, jsoncode);
            }
        }.execute();
    }

    public void onTaskCompleted(String response, int serviceCode) {
        try {
            Log.d("responsejson", response.toString());
            switch (serviceCode) {
                case jsoncode:

                    if (isSuccess(response)) {
                        removeSimpleProgressDialog();  //will remove progress dialog
                        eventsJsonObject = new JSONObject(response);
                        System.out.println(eventsJsonObject.toString());
                        try {
                            JSONArray eventsJsonObjectJSONArray = eventsJsonObject.getJSONArray("data");
                            for (int i = 0; i < eventsJsonObjectJSONArray.length(); i++) {
                                JSONObject eventJsonObject = eventsJsonObjectJSONArray.getJSONObject(i);
                                JSONArray eventJsonObjectCoordinatesArray = eventJsonObject.getJSONObject("location").getJSONArray("coordinates");

                                Gson gson = new Gson();
                                JsonElement eventJsonElement = gson.fromJson(eventJsonObject.toString(), JsonElement.class);
                                SymbolManager symbolManager = new SymbolManager(mapView, _mapboxMap, _style);
                                LatLng latLng = new LatLng(eventJsonObjectCoordinatesArray.getDouble(1), eventJsonObjectCoordinatesArray.getDouble(0));
                                List<SymbolOptions> options = new ArrayList<>();
                                options.add(new  SymbolOptions()
                                        .withLatLng(latLng)
                                        .withIconImage(ID_ICON_LOCATION)
                                        .withData(eventJsonElement)
                                );
                                List<Symbol> symbols = symbolManager.create(options);

                                symbolManager.addClickListener(symbol -> handleEventSymbolClick(symbol));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Error getting response json", Toast.LENGTH_SHORT).show();
                    }
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }
    }

//    public ArrayList<JsonElement> getInfo(String response) {
//        ArrayList<JsonElement> jsonElementArrayList = new ArrayList<>();
//        Gson gson = new Gson();
//        JsonElement element = gson.fromJson(response, JsonElement.class);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getString("status").equals("true")) {
//
//                JSONArray dataArray = jsonObject.getJSONArray("data");
//
//                for (int i = 0; i < dataArray.length(); i++) {
//
//                    EventModel eventModel = new EventModel();
//                    JSONObject dataobj = dataArray.getJSONObject(i);
//                    JSONArray coordinatesarray = dataobj.getJSONObject("location").getJSONArray("coordinates");
//                    eventModel.setEventTitle(dataobj.getString("title"));
//                    eventModel.setEventDescription(dataobj.getString("description"));
//                    eventModel.setEventLongitude(coordinatesarray.getDouble(0));
//                    eventModel.setEventLatitude(coordinatesarray.getDouble(1));
//                    eventModelArrayList.add(eventModel);
//
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return eventModelArrayList;
//    }



    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("status").equals("true")) {
                return true;
            } else {

                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            currentLocationLongitude = location.getLongitude();
//            currentlocationLatitude = location.getLatitude();
//        }
//    };

}