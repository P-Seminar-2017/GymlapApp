package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 08.12.2018 | created by Lukas S
 */
public class OnlineSQLHandlerLesson extends AsyncTask<String, String, String> {
    private String key;
    private String apiDomain;
    private OnlineSQLHandlerLesson.SQLCallback sqlCallback;

    public interface SQLCallback {
        void onDataReceived(JsonHandlerLesson jsonHandler);
    }

    public OnlineSQLHandlerLesson(String key, String apiDomain, OnlineSQLHandlerLesson.SQLCallback callback) {
        this.sqlCallback = callback;
        this.key = key;
        this.apiDomain = apiDomain;
    }

    @Override
    protected String doInBackground(String... school) {
        URL url;
        HttpURLConnection httpConn;
        BufferedReader rd;
        StringBuilder sb;

        try {
            url = new URL(String.format(apiDomain + "?key=%s&school=%s", key, school[0]));
            httpConn = (HttpURLConnection) url.openConnection();

            //TODO Timeout value
            //httpConn.setConnectTimeout(15000);
            //httpConn.setReadTimeout(15000);

            if (httpConn.getResponseCode() == 200) {
                rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();

                return sb.toString();

            } else {
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        this.sqlCallback.onDataReceived(new JsonHandlerLesson(s));

    }
}
