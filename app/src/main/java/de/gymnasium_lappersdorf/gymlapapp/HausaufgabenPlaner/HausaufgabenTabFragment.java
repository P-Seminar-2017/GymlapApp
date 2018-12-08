package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 07.12.2018 | created by Lukas S
 */
public abstract class HausaufgabenTabFragment extends Fragment {

    protected static final String[] KLASSEN_ARRAY = new String[]{"Alle", "a", "b", "c", "d", "e"};

    protected ArrayList<Hausaufgabe> homeworks;

    protected View v;
    protected TextView countLabel;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager linearLayoutManager;
    protected HomeworkRvAdapter homeworkRvAdapter;

    protected FloatingActionButton fab;
    protected CoordinatorLayout fabContainer;

    protected int stufe;
    protected String klasse;


    //Database
    protected HausaufgabenDatabaseHandler dbh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_homework_tab, container, false);
        return v;
    }

    protected void showAll() {
        homeworkRvAdapter.setDataset(homeworks.toArray(new Hausaufgabe[]{}));
        homeworkRvAdapter.notifyDataSetChanged();
        updateLabel();
    }

    protected void updateLabel() {
        int count = homeworkRvAdapter.getItemCount();

        if (klasse == KLASSEN_ARRAY[0])
            countLabel.setText((count == 0 ? "Kein" : homeworkRvAdapter.getItemCount()) + (homeworkRvAdapter.getItemCount() > 1 ? " Einträge" : " Eintrag") + " für die " + stufe + ". Klasse");
        else
            countLabel.setText((count == 0 ? "Kein" : homeworkRvAdapter.getItemCount()) + (homeworkRvAdapter.getItemCount() > 1 ? " Einträge" : " Eintrag") + " für " + stufe + " " + klasse);
    }

    protected void filter(int stufe, String klasse) {
        ArrayList<Hausaufgabe> temp_new_items = new ArrayList<>();
        this.stufe = stufe;
        this.klasse = klasse;

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

        homeworkRvAdapter.setDataset(temp_new_items.toArray(new Hausaufgabe[]{}));
        homeworkRvAdapter.notifyDataSetChanged();
        updateLabel();
    }

    public abstract void updateDataset();

}
