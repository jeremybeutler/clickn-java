package com.example.project_1_clickn.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import com.example.project_1_clickn.R;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinEvent extends DialogFragment {
    private JsonObject eventJsonObject = null;
    private String user_oid = "5d474716c6cafee40f94a3e7";
    private String server_ip = "10.104.1.122";
    private String partialJsonURL = "http://" + server_ip + ":3000/users/" + user_oid + "/joinEvent/";
    private String fullJsonURL = "";

    // Create a class constructor for the MyClass class
    public JoinEvent(JsonObject jsonObject) {
        eventJsonObject = jsonObject;  // Set the initial value for the class attribute x
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String eventTitle = eventJsonObject.get("title").getAsString();
        String eventDescription = eventJsonObject.get("description").getAsString();
        String eventId = eventJsonObject.get("_id").getAsString();
        fullJsonURL = partialJsonURL + eventId;
        String prompt = "Join this event?\nTitle: " + eventTitle + "\nDescription: " + eventDescription;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(prompt)
                .setPositiveButton(R.string.join_event, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fetchJSON();
                        partialJsonURL = "";
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        partialJsonURL = "";
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchJSON(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response = "";
                try {
                    HttpURLConnection urlConnection = null;
                    URL url = new URL(fullJsonURL);
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
                System.out.println("Join Event");
                System.out.println(result);
            }
        }.execute();
    }
}