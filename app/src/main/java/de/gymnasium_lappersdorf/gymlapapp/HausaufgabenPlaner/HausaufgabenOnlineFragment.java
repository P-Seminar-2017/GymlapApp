package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 07.12.2018 | created by Lukas S
 */
public class HausaufgabenOnlineFragment extends HausaufgabenTabFragment {

    //Internet
    private JsonHandlerHomework jsonHandler;
    private OnlineSQLHandlerHomework onlineSQLHandlerHomework;
    private Snackbar snackbarConn, snackBarAPIKey;
    private SwipeRefreshLayout refreshLayout;

    //Dialog API Key
    private AlertDialog apikeyDialog;
    private TextInputEditText keyInput;

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createAPIKeyDialog();

        recyclerView = v.findViewById(R.id.homework_rv);

        homeworkRvAdapter = new HomeworkRvAdapter(new Hausaufgabe[0], getActivity());
        recyclerView.setAdapter(homeworkRvAdapter);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        countLabel = v.findViewById(R.id.homework_count_label);

        fab = v.findViewById(R.id.add_homework_fab);
        fab.setVisibility(View.INVISIBLE);
        fabContainer = v.findViewById(R.id.fab_container);

        snackbarConn = Snackbar.make(fabContainer, "Keine Verbindung", Snackbar.LENGTH_INDEFINITE);

        snackBarAPIKey = Snackbar.make(fabContainer, "Offline Modus", Snackbar.LENGTH_INDEFINITE);
        snackBarAPIKey.setAction("Online", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAPIKeyDialog();
            }
        });

        stufe = 5;
        klasse = KLASSEN_ARRAY[0];

        updateDataset();
        updateLabel();
    }

    @Override
    public void updateDataset() {
        dbh = new HausaufgabenDatabaseHandler(v.getContext());
        homeworks = new ArrayList<>();

        if (dbh.getHomeworkCount() > 0) {
            //load content of db
            Hausaufgabe[] hw_db = dbh.getAllHomeworks();

            Collections.addAll(homeworks, hw_db);

            //Removing all local homeworks
            Iterator it = homeworks.iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (!temp.isFromInternet()) {
                    it.remove();
                }
            }
        } else {
            initDownload();
        }

        filter(stufe, klasse);
    }

    //creating dialog once for better performance
    private void createAPIKeyDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_api_key_dialog, null);

        keyInput = dialogView.findViewById(R.id.homework_input_api_key);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("API Schlüssel eintragen");

        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAPIKey(keyInput.getText().toString());
                initDownload();
            }
        });
        builder.setNegativeButton("Zurück", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                initDownload();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                initDownload();
            }
        });

        apikeyDialog = builder.create();
    }

    private void showAPIKeyDialog() {
        //Default value
        String defKey = loadAPIKey();
        if (defKey != null) keyInput.setText(defKey);

        apikeyDialog.show();
    }

    //checks for internet connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void initDownload() {
        if (isNetworkConnected(getActivity())) {
            refreshLayout.setRefreshing(true);
            onlineSQLHandlerHomework = new OnlineSQLHandlerHomework(loadAPIKey(), "http://api.lakinator.bplaced.net/request.php", OnlineSQLHandlerHomework.RequestTypes.ALL, new OnlineSQLHandlerHomework.SQLCallback() {
                @Override
                public void onDataReceived(JsonHandlerHomework jsonHandler) {
                    HausaufgabenOnlineFragment.this.jsonHandler = jsonHandler;
                    processData();
                }
            });

            onlineSQLHandlerHomework.execute();
        } else {
            snackbarConn.show();
            refreshLayout.setRefreshing(false);
        }
    }

    private void processData() {

        if (jsonHandler.getSuccess()) {

            for (int i = 0; i < jsonHandler.getLength(); i++) {
                Hausaufgabe.Types hw_type;
                Hausaufgabe neu;

                switch (jsonHandler.getType(i)) {
                    default:
                        hw_type = Hausaufgabe.Types.DATE;
                        break;
                    case "DATE":
                        hw_type = Hausaufgabe.Types.DATE;
                        break;
                    case "NEXT":
                        hw_type = Hausaufgabe.Types.NEXT;
                        break;
                    case "NEXT2":
                        hw_type = Hausaufgabe.Types.NEXT2;
                        break;
                }

                //TODO if "next" or "next2" -> calculate time with values from stundenplan

                //Text from database is URI encoded
                String text = jsonHandler.getText(i);
                String fach = jsonHandler.getFach(i);
                try {
                    text = URLDecoder.decode(text.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
                    fach = URLDecoder.decode(fach.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                neu = new Hausaufgabe(
                        fach,
                        text,
                        jsonHandler.getDate(i),
                        Integer.parseInt(jsonHandler.getKlasse(i)),
                        jsonHandler.getStufe(i),
                        Hausaufgabe.Types.DATE);

                neu.setInternetId(jsonHandler.getID(i));

                if (homeworks.indexOf(neu) != -1) {
                    //Homework alredy exists -> update it
                    int index = homeworks.indexOf(neu);
                    neu.setDatabaseId(homeworks.get(index).getDatabaseId());
                    neu.setInternetId(homeworks.get(index).getInternetId());
                    neu.setDone(homeworks.get(index).isDone());

                    homeworks.set(index, neu);
                    //Updating database
                    dbh.updateHomework(homeworks.get(index));
                } else {
                    homeworks.add(neu);

                    //Add new homework to database
                    long id = dbh.addHomework(homeworks.get(i));
                    homeworks.get(i).setDatabaseId(id);
                }

            }

            //Removing all internet homeworks that aren't mentioned in the new data
            Iterator it = homeworks.iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (!jsonHandler.contains(temp) && temp.isFromInternet()) {
                    it.remove();
                    dbh.deleteHomework(temp);
                    Log.d("kjsbcd", "rnewjjwnewn");
                }
            }

            snackBarAPIKey.dismiss();
            filter(stufe, klasse);
        } else {
            //No success
            snackBarAPIKey.show();
        }

        snackbarConn.dismiss();
        refreshLayout.setRefreshing(false);
    }

    private void saveAPIKey(String key) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.shared_pref_api), key);
        editor.apply();
    }

    private String loadAPIKey() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        //default value is null !!
        return sharedPref.getString(getString(R.string.shared_pref_api), null);
    }

}
