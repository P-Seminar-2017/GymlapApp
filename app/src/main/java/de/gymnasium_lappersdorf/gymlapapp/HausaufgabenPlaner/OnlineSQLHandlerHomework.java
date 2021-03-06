package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 20.01.2018
 * Created by Lukas S
 */


public class OnlineSQLHandlerHomework extends AsyncTask<Hausaufgabe, String, String> {
    private final String key = "12345";
    private String apiDomain;
    private SQLCallback sqlCallback;
    private RequestTypes requestType;

    public interface SQLCallback {
        void onDataReceived(JsonHandlerHomework jsonHandler);
    }

    public enum RequestTypes {
        ALL, GET, SAVE, DELETE, EDIT
    }

    public OnlineSQLHandlerHomework(String apiDomain, RequestTypes requestType, SQLCallback callback) {
        this.sqlCallback = callback;
        this.requestType = requestType;
        this.apiDomain = apiDomain;
    }

    @Override
    protected String doInBackground(Hausaufgabe... data) {
        URL url;
        HttpURLConnection httpConn;
        BufferedReader rd;
        StringBuilder sb;

        try {
            sb = new StringBuilder();

            switch (requestType) {
                case ALL:
                    break;
                case GET:
                    sb.append("&fach=").append(data[0].getFach());
                    sb.append("&klasse=").append(data[0].getStufe());
                    sb.append("&stufe=").append(data[0].getStufe());
                    break;
                case EDIT:
                    sb.append("&id=").append(data[0].getInternetId());
                    sb.append("&text=").append(data[0].getText());
                    break;
                case SAVE:
                    sb.append("&fach=").append(data[0].getFach());
                    sb.append("&klasse=").append(data[0].getStufe());
                    sb.append("&stufe=").append(data[0].getKurs());
                    sb.append("&type=").append(data[0].getType());
                    sb.append("&date=").append(data[0].getTimestamp());
                    sb.append("&text=").append(data[0].getText());
                    break;
                case DELETE:
                    sb.append("&id=").append(data[0].getInternetId());
                    break;
            }

            url = new URL(String.format(apiDomain + "?key=%s%s", key, sb.toString()));
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

        this.sqlCallback.onDataReceived(new JsonHandlerHomework(s));

    }
}