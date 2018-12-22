package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 08.12.2018 | created by Lukas S
 */
public abstract class JsonHandler {
    protected boolean success;
    protected String jsonString;
    protected JSONObject[] dataArray;
    protected String error;
    protected int errorCode;

    public JsonHandler(String jsonString) {
        this.jsonString = jsonString;

        try {
            JSONObject obj = new JSONObject(jsonString);
            success = obj.getBoolean("success");

            if (success) {
                error = "none";
                errorCode = 200;
                JSONArray tempArr = obj.getJSONArray("data");
                dataArray = new JSONObject[tempArr.length()];

                for (int i = 0; i < dataArray.length; i++) {
                    dataArray[i] = tempArr.getJSONObject(i);
                }

            } else {
                error = obj.getString("error");
                errorCode = obj.getInt("errorcode");
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

    public String getError() {
        return error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    protected int getInt(int position, String name) {
        if (!success || position < 0 || position >= getLength()) return -1;

        try {
            return dataArray[position].getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected String getString(int position, String name) {
        if (!success || position < 0 || position >= getLength()) return null;

        try {
            return dataArray[position].getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String[] getStringArray(int position, String name) {
        if (!success || position < 0 || position >= getLength()) return null;

        String[] retArr = new String[0];

        try {
            JSONArray arr = dataArray[position].getJSONArray(name);
            retArr = new String[arr.length()];

            for (int i = 0; i < retArr.length; i++) {
                retArr[i] = arr.getString(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retArr;
    }

    protected int[] getIntArray(int position, String name) {
        if (!success || position < 0 || position >= getLength()) return null;

        int[] retArr = new int[0];

        try {
            JSONArray arr = dataArray[position].getJSONArray(name);
            retArr = new int[arr.length()];

            for (int i = 0; i < retArr.length; i++) {
                retArr[i] = arr.getInt(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retArr;
    }

    protected long getLong(int position, String name) {
        if (!success || position < 0 || position >= getLength()) return -1;

        try {
            return dataArray[position].getLong(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return jsonString;
    }
}
