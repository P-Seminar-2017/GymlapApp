package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 20.01.2018
 * Created by Lukas S
 */


public class JsonHandler {
    private boolean success;
    private String jsonString;
    private JSONObject[] dataArray;

    public JsonHandler(String jsonString) {
        this.jsonString = jsonString;

        try {
            JSONObject obj = new JSONObject(jsonString);
            success = obj.getBoolean("success");

            if (success) {
                JSONArray tempArr = obj.getJSONArray("data");
                dataArray = new JSONObject[tempArr.length()];

                for (int i = 0; i < dataArray.length; i++) {
                    dataArray[i] = tempArr.getJSONObject(i);
                }

            } else {
                dataArray = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

    }

    public int getLength() {
        return dataArray.length;
    }

    public boolean getSuccess() {
        return success;
    }


    public long getID(int position) {
        return getLong(position, "id");
    }

    public String getFach(int position) {
        return getString(position, "fach");
    }

    //Klasse entspricht z.B. 9 oder 10
    public String getKlasse(int position) {
        return getString(position, "klasse");
    }

    //Stufe entspricht z.B. a oder 1m3
    public String getStufe(int position) {
        return getString(position, "stufe");
    }

    public String getType(int position) {
        return getString(position, "type");
    }

    public long getDate(int position) {
        return getLong(position, "date");
    }

    public String getText(int position) {
        return getString(position, "text");
    }


    private int getInt(int position, String name) {
        if (!success) return -1;

        try {
            return dataArray[position].getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String getString(int position, String name) {
        if (!success) return null;

        try {
            return dataArray[position].getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private long getLong(int position, String name) {
        if (!success) return -1;

        try {
            return dataArray[position].getLong(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String toString() {
        return jsonString;
    }
}
