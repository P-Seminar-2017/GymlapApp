package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 01.06.2018 | created by Lukas S
 */

public class HausaufgabenFragment extends Fragment {
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

    private ArrayList<Hausaufgabe> homeworks; //aus dem inet
    private ArrayList<Hausaufgabe> homeworksLocal; //vom user erstellt //TODO extra filter oder tab für vom user erstellte hausis

    private Hausaufgabe lastItem = null;
    private int lastItemPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_homework, container, false);
        return v;
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
                }
            }
        });

        fab = v.findViewById(R.id.add_homework_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        homeworks = new ArrayList<>();
        homeworksLocal = new ArrayList<>();

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
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        initDownload();
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
            Hausaufgabe[] temp = homeworks.toArray(new Hausaufgabe[homeworks.size()]);
            ArrayList<Hausaufgabe> temp_new_items = new ArrayList<>();

            //Alle items aus dem inet zuerst enfernen
            homeworks.clear();

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

                neu = new Hausaufgabe(
                        jsonHandler.getID(i),
                        jsonHandler.getFach(i),
                        jsonHandler.getText(i),
                        jsonHandler.getDate(i),
                        Integer.parseInt(jsonHandler.getKlasse(i)),
                        jsonHandler.getStufe(i),
                        hw_type,
                        true);

                //Gab es das item schon? -> "done" übernehmen
                for (Hausaufgabe aTemp : temp) {
                    if (aTemp.isFromInternet() && aTemp.getId() == jsonHandler.getID(i)) {
                        neu.setDone(aTemp.isDone()); //"done" übernehmen
                    }
                }

                homeworks.add(neu);
                if (!neu.isDone()) temp_new_items.add(neu);
            }

            homeworkRvAdapter.setDataset(temp_new_items.toArray(new Hausaufgabe[temp_new_items.size()]));
            homeworkRvAdapter.notifyDataSetChanged();
            updateLabel();
        } else {
            //TODO No success
        }

        refreshLayout.setRefreshing(false);
        snackbarConn.dismiss();
    }

}
