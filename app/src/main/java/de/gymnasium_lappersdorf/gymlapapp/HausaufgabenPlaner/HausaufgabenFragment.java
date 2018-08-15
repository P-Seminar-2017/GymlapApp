package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import de.gymnasium_lappersdorf.gymlapapp.R;

import static android.app.Activity.RESULT_OK;

/**
 * 01.06.2018 | created by Lukas S
 */

public class HausaufgabenFragment extends Fragment implements NumberPicker.OnValueChangeListener, Spinner.OnItemSelectedListener {
    private static final int REQUEST_ID = 1;
    private static final String[] KLASSEN_ARRAY = new String[]{"Alle", "a", "b", "c", "d", "e"};

    private View v;
    private RecyclerView recyclerView;
    private HomeworkRvAdapter homeworkRvAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private TextView countLabel;
    private Snackbar snackbarConn, snackbarRevert;
    private FloatingActionButton fab;
    private ItemTouchHelper itemTouchHelper;

    private JsonHandler jsonHandler;
    private OnlineSQLHandler onlineSQLHandler;

    private ArrayList<Hausaufgabe> homeworks; //enthält zurzeit alle hausaufgaben, auch aus der database und abgeschlossene //TODO abgeschlossene hausaufgaben löschen

    //Dialog
    private AlertDialog filterDialog;
    private NumberPicker stufenPicker;
    private int stufe;
    private String klasse;
    private Spinner klasseSpinner;

    //Recycler swipe removing
    private Hausaufgabe lastItem = null;
    private int lastItemPosition = -1;

    //Database
    private HausaufgabenDatabaseHandler dbh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_homework, container, false);
        setHasOptionsMenu(true);
        createFilterDialog();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.homework_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        snackbarConn = Snackbar.make(v, "Keine Verbindung", Snackbar.LENGTH_INDEFINITE);
        snackbarRevert = Snackbar.make(v, "Hausaufgabe erledigt", Snackbar.LENGTH_LONG);
        snackbarRevert.setAction("Rückgängig", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastItem != null) {
                    int pos = homeworks.indexOf(lastItem);
                    homeworks.get(pos).setDone(false);
                    homeworkRvAdapter.restoreItem(homeworks.get(pos), lastItemPosition);
                    updateLabel();
                    recyclerView.smoothScrollToPosition(lastItemPosition);
                    lastItem = null;
                    //Updating database
                    dbh.updateHomework(homeworks.get(pos));
                }
            }
        });

        fab = v.findViewById(R.id.add_homework_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddHomeworkActivity.class);
                startActivityForResult(i, REQUEST_ID);
            }
        });

        homeworks = new ArrayList<>();

        refreshLayout = v.findViewById(R.id.swiperefresh_homework);
        recyclerView = v.findViewById(R.id.homework_rv);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        homeworkRvAdapter = new HomeworkRvAdapter(homeworks.toArray(new Hausaufgabe[homeworks.size()]), getContext());
        recyclerView.setAdapter(homeworkRvAdapter);

        countLabel = v.findViewById(R.id.homework_count_label);
        updateLabel();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDownload();
            }
        });

        //Swipe card to remove
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = homeworks.indexOf(homeworkRvAdapter.getDataset()[viewHolder.getAdapterPosition()]);
                homeworks.get(pos).setDone(true);
                lastItem = homeworks.get(pos);
                lastItemPosition = viewHolder.getAdapterPosition();
                homeworkRvAdapter.removeItem(viewHolder.getAdapterPosition());
                updateLabel();
                snackbarRevert.show();
                //Updating database
                dbh.updateHomework(homeworks.get(pos));
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        stufe = 5;
        klasse = KLASSEN_ARRAY[0];

        //Database
        dbh = new HausaufgabenDatabaseHandler(v.getContext());

        if (dbh.getHomeworkCount() > 0) {
            //load content of db //TODO async
            Hausaufgabe[] hw_db = dbh.getAllHomeworks();

            Collections.addAll(homeworks, hw_db);

            //Removing all local homeworks that are marked as done
            Iterator it = homeworks.iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (temp.isDone() && !temp.isFromInternet()) {
                    it.remove();
                    dbh.deleteHomework(temp);
                }
            }

            filter(stufe, klasse);
        } else {
            initDownload();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                break;
            case R.id.filter_item:
                showFilterDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Numberpicker
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

        if (oldVal != newVal) {
            if (newVal > 10) {
                klasseSpinner.setSelection(0);
                klasseSpinner.setEnabled(false);
            } else {
                klasseSpinner.setEnabled(true);
            }

            stufe = newVal;
            filter(stufe, klasse);
        }
    }

    //Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        klasse = KLASSEN_ARRAY[pos];
        filter(stufe, klasse);
    }

    //Spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //Adding new homework to "homeworks"
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ID) {

            if (resultCode == RESULT_OK) {
                long id = data.getExtras().getLong("HW_ID");

                Hausaufgabe temp = dbh.getHomework(id);

                homeworks.add(temp);

                stufe = temp.getStufe();
                klasse = temp.getKurs();

                filter(stufe, klasse);
            }

        }
    }

    //creating dialog once for better performance
    private void createFilterDialog() {

        AlertDialog.Builder d = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_filter_dialog, null);
        d.setTitle("Deine Klasse");
        d.setView(dialogView);

        stufenPicker = dialogView.findViewById(R.id.filter_numberpicker);
        stufenPicker.setMaxValue(13);
        stufenPicker.setMinValue(5);
        stufenPicker.setWrapSelectorWheel(false);
        stufenPicker.setOnValueChangedListener(this);

        klasseSpinner = dialogView.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(v.getContext(), android.R.layout.simple_spinner_item, KLASSEN_ARRAY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        klasseSpinner.setAdapter(adapter);
        klasseSpinner.setOnItemSelectedListener(this);

        d.setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stufe = stufenPicker.getValue();
                klasse = KLASSEN_ARRAY[klasseSpinner.getSelectedItemPosition()];
                filter(stufe, klasse);
            }
        });
        d.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetFilter();
                filter(stufe, klasse);
            }
        });

        //TODO Remove in release
        d.setNeutralButton("Dev", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                for (int i = 0; i < homeworks.size(); i++) {
                    if (homeworks.get(i).isDone()) {
                        homeworks.get(i).setDone(false);
                        dbh.updateHomework(homeworks.get(i));
                    }
                }
                resetFilter();
            }
        });

        filterDialog = d.create();
    }

    private void showFilterDialog() {
        //Default values
        stufenPicker.setValue(stufe);
        klasseSpinner.setSelection(Arrays.asList(KLASSEN_ARRAY).indexOf(klasse));

        filterDialog.show();
    }

    //checks for internet connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void updateLabel() {
        if (homeworkRvAdapter.getItemCount() == 0) {
            countLabel.setText("Keine Einträge");
        } else {
            countLabel.setText("Einträge: " + homeworkRvAdapter.getItemCount());
        }
    }

    private void initDownload() {
        if (isNetworkConnected(getActivity())) {
            refreshLayout.setRefreshing(true);

            onlineSQLHandler = new OnlineSQLHandler("917342346673", "http://api.lakinator.bplaced.net/request.php", OnlineSQLHandler.RequestTypes.ALL, new OnlineSQLHandler.SQLCallback() {
                @Override
                public void onDataReceived(JsonHandler jsonHandler) {
                    HausaufgabenFragment.this.jsonHandler = jsonHandler;
                    processData();
                }
            });

            onlineSQLHandler.execute();
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

                neu = new Hausaufgabe(
                        jsonHandler.getFach(i),
                        jsonHandler.getText(i),
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
                }
            }

            filter(stufe, klasse);
        } else {
            //TODO No success
        }

        refreshLayout.setRefreshing(false);
        snackbarConn.dismiss();
    }

    private void filter(int stufe, String klasse) {
        ArrayList<Hausaufgabe> temp_new_items = new ArrayList<>();

        for (int i = 0; i < homeworks.size(); i++) {

            if (klasse.equals(KLASSEN_ARRAY[0])) {
                //Alle sind ausgewählt

                if (stufe == homeworks.get(i).getStufe() && !homeworks.get(i).isDone()) {
                    temp_new_items.add(homeworks.get(i));
                }

            } else {
                //Es wird pro klasse unterschieden

                if (stufe == homeworks.get(i).getStufe() && klasse.equals(homeworks.get(i).getKurs()) && !homeworks.get(i).isDone()) {
                    temp_new_items.add(homeworks.get(i));
                }
            }

        }

        homeworkRvAdapter.setDataset(temp_new_items.toArray(new Hausaufgabe[temp_new_items.size()]));
        homeworkRvAdapter.notifyDataSetChanged();
        updateLabel();
    }

    private void resetFilter() {
        ArrayList<Hausaufgabe> temp_new_items = new ArrayList<>();

        stufe = 5;
        klasse = KLASSEN_ARRAY[0];

        //TODO currently displays all homeworks -> used for "dev" dialog -> on release change to filter(..., ...);
        for (int i = 0; i < homeworks.size(); i++) {

            if (!homeworks.get(i).isDone()) {
                temp_new_items.add(homeworks.get(i));
            }

        }

        homeworkRvAdapter.setDataset(temp_new_items.toArray(new Hausaufgabe[temp_new_items.size()]));
        homeworkRvAdapter.notifyDataSetChanged();
        updateLabel();
    }

}
