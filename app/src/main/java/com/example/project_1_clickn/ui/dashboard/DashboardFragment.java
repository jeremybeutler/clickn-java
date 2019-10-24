package com.example.project_1_clickn.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import com.example.project_1_clickn.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private String user_oid = "5d474716c6cafee40f94a3e7";
    private String server_ip = "10.104.1.122";
    private String jsonURL = "http://" + server_ip + ":3000/users/" + user_oid + "/activeEvents";
    private final int jsoncode = 1;
    private ListView listView;
    private static ProgressDialog mProgressDialog;
    ArrayList<EventModel> eventModelArrayList;
    private EventAdapter eventAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        listView = root.findViewById(R.id.lv);
        fetchJSON();
        return root;
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
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                if (isSuccess(response)) {
                    if (!isEmpty(response)) {
                        removeSimpleProgressDialog();  //will remove progress dialog
                        eventModelArrayList = getInfo(response);
                        eventAdapter = new EventAdapter(getActivity(), eventModelArrayList);
                        listView.setAdapter(eventAdapter);
                    } else {
                        Toast.makeText(getActivity(), "No events for given user", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Error getting response json", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public ArrayList<EventModel> getInfo(String response) {
        ArrayList<EventModel> eventModelArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {

                JSONArray dataArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {

                    EventModel eventModel = new EventModel();
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    JSONArray coordinatesarray = dataobj.getJSONObject("location").getJSONArray("coordinates");
                    eventModel.setEventTitle(dataobj.getString("title"));
                    eventModel.setEventDescription(dataobj.getString("description"));
                    eventModel.setEventLongitude(coordinatesarray.getDouble(0));
                    eventModel.setEventLatitude(coordinatesarray.getDouble(1));
                    eventModelArrayList.add(eventModel);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventModelArrayList;
    }



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

    public boolean isEmpty(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getJSONArray("data").length() == 0) {
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
}