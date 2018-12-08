package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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

import java.util.Arrays;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 01.06.2018 | created by Lukas S
 */

public class HausaufgabenFragment extends Fragment implements NumberPicker.OnValueChangeListener, Spinner.OnItemSelectedListener {

    private static final String[] KLASSEN_ARRAY = new String[]{"Alle", "a", "b", "c", "d", "e"};

    private View v;
    private Toolbar tb;
    private SwipeRefreshLayout refreshLayout;

    //Tab
    private ViewPager vp;
    private TabLayout tl;
    private HomeworkTabAdapter adapter;

    //Dialog
    private AlertDialog filterDialog;
    private NumberPicker stufenPicker;
    private int stufe;
    private String klasse;
    private Spinner klasseSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
        getActivity().setTitle("Hausaufgaben");

        tb = getActivity().findViewById(R.id.toolbar_main);
        refreshLayout = v.findViewById(R.id.swiperefresh_homework);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int index = 0;
                TabLayout.Tab tab = tl.getTabAt(index);
                tab.select();
                ((HausaufgabenOnlineFragment) adapter.getItem(index)).initDownload();
            }
        });

        vp = view.findViewById(R.id.pager_homework);
        adapter = new HomeworkTabAdapter(getChildFragmentManager());
        ((HausaufgabenOnlineFragment) adapter.getItem(0)).setRefreshLayout(refreshLayout);
        vp.setAdapter(adapter);

        tl = view.findViewById(R.id.tab_layout_homework);
        tl.addTab(tl.newTab().setText("Online"));
        tl.addTab(tl.newTab().setText("Lokal"));
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        resetFilterAttributes();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            tb.setElevation(0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            tb.setElevation(4);
        } catch (NullPointerException e) {
            e.printStackTrace();
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

    //creating dialog once for better performance
    private void createFilterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_filter_dialog, null);
        builder.setTitle("Deine Klasse");
        builder.setView(dialogView);

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

        builder.setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stufe = stufenPicker.getValue();
                klasse = KLASSEN_ARRAY[klasseSpinner.getSelectedItemPosition()];
                filter(stufe, klasse);
            }
        });
        builder.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetFilterAttributes();
                filter(stufe, klasse);
            }
        });
        builder.setNeutralButton("Alles", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                showAll();
            }
        });

        filterDialog = builder.create();
    }

    private void showFilterDialog() {
        //Default values
        stufenPicker.setValue(stufe);
        klasseSpinner.setSelection(Arrays.asList(KLASSEN_ARRAY).indexOf(klasse));

        filterDialog.show();
    }

    private void filter(int stufe, String klasse) {
        ((HausaufgabenTabFragment) adapter.getItem(0)).filter(stufe, klasse);
        ((HausaufgabenTabFragment) adapter.getItem(1)).filter(stufe, klasse);
    }

    private void showAll() {
        ((HausaufgabenTabFragment) adapter.getItem(0)).showAll();
        ((HausaufgabenTabFragment) adapter.getItem(1)).showAll();
    }

    private void resetFilterAttributes() {
        stufe = 5;
        klasse = KLASSEN_ARRAY[0];
    }

}
