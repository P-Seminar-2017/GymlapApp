package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

/**
 * 20.01.2018
 * Created by Lukas S
 */


public class JsonHandlerHomework extends JsonHandler {

    public JsonHandlerHomework(String jsonString) {
        super(jsonString);
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

    public boolean contains(Hausaufgabe h) {
        boolean contains = false;

        for (int i = 0; i < getLength(); i++) {
            if (h.getInternetId() == getID(i)) {
                contains = true;
                break;
            }
        }

        return contains;
    }
}
