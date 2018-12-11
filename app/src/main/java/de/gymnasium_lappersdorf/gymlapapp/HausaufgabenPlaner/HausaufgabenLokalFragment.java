package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import de.gymnasium_lappersdorf.gymlapapp.R;

import static android.app.Activity.RESULT_OK;

/**
 * 07.12.2018 | created by Lukas S
 */
public class HausaufgabenLokalFragment extends HausaufgabenTabFragment {

    private static final int REQUEST_ID = 1;

    private ItemTouchHelper itemTouchHelper;

    private Snackbar snackbarRevert;

    //Recycler swipe removing
    private Hausaufgabe lastItem = null;
    private int lastItemPosition = -1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
        getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddHomeworkActivity.class);
                i.putExtra("STUFE", getStufe());
                i.putExtra("KLASSE", getKlasse() == getKlassenArray()[0] ? "a" : getKlasse());
                startActivityForResult(i, REQUEST_ID);
            }
        });
        setFabContainer((CoordinatorLayout) getV().findViewById(R.id.fab_container));

        snackbarRevert = Snackbar.make(getFabContainer(), "Hausaufgabe erledigt", Snackbar.LENGTH_LONG);
        snackbarRevert.setAction("Rückgängig", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastItem != null) {
                    int pos = getHomeworks().indexOf(lastItem);
                    getHomeworks().get(pos).setDone(false);
                    getHomeworkRvAdapter().restoreItem(getHomeworks().get(pos), lastItemPosition);
                    updateLabel();
                    getRecyclerView().smoothScrollToPosition(lastItemPosition);
                    lastItem = null;
                    //Updating database
                    getDbh().updateHomework(getHomeworks().get(pos));
                }
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
                int pos = getHomeworks().indexOf(getHomeworkRvAdapter().getDataset()[viewHolder.getAdapterPosition()]);
                getHomeworks().get(pos).setDone(true);
                lastItem = getHomeworks().get(pos);
                lastItemPosition = viewHolder.getAdapterPosition();
                getHomeworkRvAdapter().removeItem(viewHolder.getAdapterPosition());
                updateLabel();
                snackbarRevert.show();
                //Updating database
                getDbh().updateHomework(getHomeworks().get(pos));
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(getRecyclerView());

        setStufe(5);
        setKlasse(getKlassenArray()[0]);

        updateDataset();
        updateLabel();
    }

    //Adding new homework to "homeworks"
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ID) {

            if (resultCode == RESULT_OK) {
                long id = data.getExtras().getLong("HW_ID");

                Hausaufgabe temp = getDbh().getHomework(id);

                getHomeworks().add(temp);

                filter(temp.getStufe(), temp.getKurs());
            }

        }
    }

    @Override
    public void updateDataset() {
        setDbh(new HausaufgabenDatabaseHandler(getActivity()));
        setHomeworks(new ArrayList<Hausaufgabe>());

        if (getDbh().getHomeworkCount() > 0) {
            //load content of db
            Hausaufgabe[] hw_db = getDbh().getAllHomeworks();

            Collections.addAll(getHomeworks(), hw_db);

            //Removing all local homeworks that are marked as done and all online homeworks
            Iterator it = getHomeworks().iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (temp.isDone() && !temp.isFromInternet()) {
                    it.remove();
                    getDbh().deleteHomework(temp);
                } else if (temp.isFromInternet()) {
                    it.remove();
                }
            }
        } else {
            setHomeworks(new ArrayList<Hausaufgabe>()); //TODO: Remove
        }

        filter(getStufe(), getKlasse());
    }

}
