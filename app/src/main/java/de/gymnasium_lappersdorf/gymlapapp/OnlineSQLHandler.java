package de.gymnasium_lappersdorf.gymlapapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 07.01.2018 | created by Lukas S
 *
 * Example:
 *
 *
 * new OnlineSQLHandler(OnlineSQLHandler.RequestTypes.GET, new OnlineSQLHandler.SQLCallback() {
 *
 *      @Override public void onDataReceived(JsonHandler jsonHandler) {
 *          StringBuilder sb = new StringBuilder();
 *          if (jsonHandler.getSuccess()) {
 *              for (int i = 0; i < jsonHandler.getLength(); i++) {
 *                  sb.append(" ").append(jsonHandler.getText(i));
 *              }
 *          } else {
 *              sb.append("Error");
 *          }
 *
 *          Log.d("Debug", sb.toString());
 *
 *      }
 * }).execute("Biologie", "5", "a");
 *
 */

public class OnlineSQLHandler extends AsyncTask<String, String, String> {
    private final String key = "917342346673";
    private SQLCallback sqlCallback;
    private RequestTypes requestType;

    public interface SQLCallback {
        void onDataReceived(JsonHandler jsonHandler);
    }

    public enum RequestTypes {
        ALL, GET, SAVE, DELETE, EDIT
    }


    public OnlineSQLHandler(RequestTypes requestType, SQLCallback callback) {
        this.sqlCallback = callback;
        this.requestType = requestType;
    }

    @Override
    protected String doInBackground(String... data) {
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
                    sb.append("&fach=").append(data[0]);
                    sb.append("&klasse=").append(data[1]);
                    sb.append("&stufe=").append(data[2]);
                    break;
                case EDIT:
                    sb.append("&id=").append(data[0]);
                    sb.append("&text=").append(data[1]);
                    break;
                case SAVE:
                    sb.append("&fach=").append(data[0]);
                    sb.append("&klasse=").append(data[1]);
                    sb.append("&stufe=").append(data[2]);
                    sb.append("&type=").append(data[3]);
                    sb.append("&date=").append(data[4]);
                    sb.append("&text=").append(data[5]);
                    break;
                case DELETE:
                    sb.append("&id=").append(data[0]);
                    break;
            }

            url = new URL(String.format("http://api.lakinator.bplaced.net/request.php?key=%s%s", key, sb.toString()));
            httpConn = (HttpURLConnection) url.openConnection();

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

        this.sqlCallback.onDataReceived(new JsonHandler(s));

    }
}
