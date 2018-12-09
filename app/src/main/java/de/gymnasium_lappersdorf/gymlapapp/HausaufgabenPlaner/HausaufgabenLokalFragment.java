package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

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


        recyclerView = v.findViewById(R.id.homework_rv);

        homeworkRvAdapter = new HomeworkRvAdapter(new Hausaufgabe[0], getActivity(), new HomeworkRvAdapter.DatasetChangeListener() {
            @Override
            public void onNotificationIdChanged(Hausaufgabe h) {
                int index = homeworks.indexOf(h);
                homeworks.get(index).setNotificationId(h.getNotificationId());
                dbh.updateHomework(homeworks.get(index));
            }
        });
        recyclerView.setAdapter(homeworkRvAdapter);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        countLabel = v.findViewById(R.id.homework_count_label);


        fab = v.findViewById(R.id.add_homework_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddHomeworkActivity.class);
                i.putExtra("STUFE", stufe);
                i.putExtra("KLASSE", klasse == KLASSEN_ARRAY[0] ? "a" : klasse);
                startActivityForResult(i, REQUEST_ID);
            }
        });
        fabContainer = v.findViewById(R.id.fab_container);

        snackbarRevert = Snackbar.make(fabContainer, "Hausaufgabe erledigt", Snackbar.LENGTH_LONG);
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

        updateDataset();
        updateLabel();
    }

    //Adding new homework to "homeworks"
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ID) {

            if (resultCode == RESULT_OK) {
                long id = data.getExtras().getLong("HW_ID");

                Hausaufgabe temp = dbh.getHomework(id);

                homeworks.add(temp);

                filter(temp.getStufe(), temp.getKurs());
            }

        }
    }

    @Override
    public void updateDataset() {
        dbh = new HausaufgabenDatabaseHandler(getActivity());
        homeworks = new ArrayList<>();

        if (dbh.getHomeworkCount() > 0) {
            //load content of db
            Hausaufgabe[] hw_db = dbh.getAllHomeworks();

            Collections.addAll(homeworks, hw_db);

            //Removing all local homeworks that are marked as done and all online homeworks
            Iterator it = homeworks.iterator();
            Hausaufgabe temp;
            while (it.hasNext()) {
                temp = (Hausaufgabe) it.next();
                if (temp.isDone() && !temp.isFromInternet()) {
                    it.remove();
                    dbh.deleteHomework(temp);
                } else if (temp.isFromInternet()) {
                    it.remove();
                }
            }
        } else {
            homeworks = new ArrayList<>(); //TODO: Remove
        }

        filter(stufe, klasse);
    }

}
