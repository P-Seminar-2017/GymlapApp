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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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

        setRecyclerView((RecyclerView) getV().findViewById(R.id.homework_rv));

        setHomeworkRvAdapter(new HomeworkRvAdapter(new Hausaufgabe[0], getActivity(), new HomeworkRvAdapter.DatasetChangeListener() {
            @Override
            public void onNotificationIdChanged(Hausaufgabe h) {
                int index = getHomeworks().indexOf(h);
                getHomeworks().get(index).setNotificationId(h.getNotificationId());
                getDbh().updateHomework(getHomeworks().get(index));
            }
        }));
        getRecyclerView().setAdapter(getHomeworkRvAdapter());

        setLinearLayoutManager(new LinearLayoutManager(getActivity()));
        getRecyclerView().setLayoutManager(getLinearLayoutManager());

        setCountLabel((TextView) getV().findViewById(R.id.homework_count_label));

        setFab((FloatingActionButton) getV().findViewById(R.id.add_homework_fab));
        getFab().setVisibility(View.INVISIBLE);
        setFabContainer((CoordinatorLayout) getV().findViewById(R.id.fab_container));

        snackbarConn = Snackbar.make(getFabContainer(), "Keine Verbindung", Snackbar.LENGTH_INDEFINITE);

        snackBarAPIKey = Snackbar.make(getFabContainer(), "Offline Modus", Snackbar.LENGTH_INDEFINITE);
        snackBarAPIKey.setAction("Online", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAPIKeyDialog();
            }
        });

        setStufe(5);
        setKlasse(getKlassenArray()[0]);

        updateDataset();
        updateLabel();
    }

    @Override
    public void updateDataset() {
        setDbh(new HausaufgabenDatabaseHandler(getActivity()));
        setHomeworks(new ArrayList<Hausaufgabe>());

        if (getDbh().getHomeworkCount() > 0) {
            //load content of db
            Hausaufgabe[] hw_db = getDbh().getAllHomeworks();

            Collections.addAll(getHomeworks(), hw_db);

            //Removing all local homeworks
            Iterator it = getHomeworks().iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (!temp.isFromInternet()) {
                    it.remove();
                }
            }
        }

        if (getHomeworks().size() == 0) initDownload();

        filter(getStufe(), getKlasse());
    }

    //creating dialog once for better performance
    private void createAPIKeyDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_api_key_dialog, null);

        keyInput = dialogView.findViewById(R.id.homework_input_api_key);

        AlertDialog.Builder builder = new AlertDialog.Builder(getV().getContext());
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

                if (getHomeworks().indexOf(neu) != -1) {
                    //Homework alredy exists -> update it
                    int index = getHomeworks().indexOf(neu);
                    neu.setDatabaseId(getHomeworks().get(index).getDatabaseId());
                    neu.setInternetId(getHomeworks().get(index).getInternetId());
                    neu.setDone(getHomeworks().get(index).isDone());

                    getHomeworks().set(index, neu);
                    //Updating database
                    getDbh().updateHomework(getHomeworks().get(index));
                } else {
                    getHomeworks().add(neu);

                    //Add new homework to database
                    long id = getDbh().addHomework(getHomeworks().get(i));
                    getHomeworks().get(i).setDatabaseId(id);
                }

            }

            //Removing all internet homeworks that aren't mentioned in the new data
            Iterator it = getHomeworks().iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (!jsonHandler.contains(temp) && temp.isFromInternet()) {
                    it.remove();
                    getDbh().deleteHomework(temp);
                }
            }

            snackBarAPIKey.dismiss();
            filter(getStufe(), getKlasse());
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
