package com.devdivr.awarenesstest.network;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by devdivr on 7/26/16.
 */
public class NetworkTask<T> extends AsyncTask<String, Void, T> {

    private final Class<T> classType;
    private final Callback<T> callback;
    private final Gson gson;

    public NetworkTask(Class<T> classType, Callback<T> callback) {
        this.classType = classType;
        this.callback = callback;
        this.gson = new GsonBuilder().create();
    }

    protected T doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                T t = gson.fromJson(stringBuilder.toString(), classType);
                return t;
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(T object) {
        if (object == null) {
            callback.onFailure();
        } else {
            callback.onSuccess(object);
        }
        callback.onComplete();
    }
}
