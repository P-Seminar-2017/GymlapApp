package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

/**
 * 08.12.2018 | created by Lukas S
 */
public class JsonHandlerLesson extends JsonHandler {

    public JsonHandlerLesson(String jsonString) {
        super(jsonString);
    }

    public String getFach(int position) {
        return getString(position, "fach");
    }

    //z.B. ["a", "b", "d"]
    public String[] getUnterstufen(int position) {
        return getStringArray(position, "unterstufe");
    }

    //z.B. [11, 12]
    public int[] getOberstufen(int position) {
        return getIntArray(position, "oberstufe");
    }

    //z.B. "d" oder "inf"
    public String getKuerzel(int position) {
        return getString(position, "kurskuerzel");
    }

    public int getKursanzahl(int position, int oberstufe) {
        if (oberstufe < 11 || oberstufe > 13) return -1;

        return getInt(position, "kursanzahl_" + oberstufe);
    }
}
