package com.example.eldercareapp;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class PrayerTimeFetcher {

    public static void getPrayerTimes(Context context,
                                      TextView fajrView,
                                      TextView dhuhrView,
                                      TextView asrView,
                                      TextView maghribView,
                                      TextView ishaView) {

        String url = "https://api.aladhan.com/v1/timingsByCity?city=Abu%20Dhabi&country=United%20Arab%20Emirates&method=16";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("PRAYER_API", "API Response: " + response); //

                        JSONObject json = new JSONObject(response);
                        JSONObject timings = json.getJSONObject("data").getJSONObject("timings");

                        // Set TextViews
                        fajrView.setText("Fajr: " + timings.getString("Fajr"));
                        dhuhrView.setText("Dhuhr: " + timings.getString("Dhuhr"));
                        asrView.setText("Asr: " + timings.getString("Asr"));
                        maghribView.setText("Maghrib: " + timings.getString("Maghrib"));
                        ishaView.setText("Isha: " + timings.getString("Isha"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PRAYER_API", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("PRAYER_API", "Volley Error: " + error.toString());
                });

        queue.add(request);
    }
}
