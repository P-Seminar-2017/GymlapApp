package de.gymnasium_lappersdorf.gymlapapp.Home;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leon on 16.12.2017.
 */

public class TextInternetConnection extends AsyncTask<String, Object, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse response = null;

    public TextInternetConnection(AsyncResponse response) {
        this.response = response;
    }


    @Override
    protected String doInBackground(String... strings) {
        return getContent(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        response.processFinish(s);
    }

    public static HttpURLConnection getConnection(String url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setReadTimeout(30000);
            con.setRequestProperty("User-Agent", "RedditLightApp");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }

    //get the content of a HttpsURLConnection
    public static String getContent(String url) {
        HttpURLConnection con = getConnection(url);
        if (con == null) return null;
        StringBuffer sb = new StringBuffer();
        try {
            String tmp = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((tmp = br.readLine()) != null)
                sb.append(tmp).append("\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
