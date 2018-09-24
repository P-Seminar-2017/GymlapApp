package de.gymnasium_lappersdorf.gymlapapp.Home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * Created by leon on 08.12.17.
 */

public class HomeFragment extends Fragment {

    RecyclerView rv;
    HomeRvAdapter homeRvAdapter;
    LinearLayoutManager linearLayoutManager;
    Snackbar snackbar;
    SwipeRefreshLayout refreshLayout;
    String content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rv = v.findViewById(R.id.home_rv);
        homeRvAdapter = new HomeRvAdapter(getActivity());
        rv.setAdapter(homeRvAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        snackbar = Snackbar.make(v, "Keine Verbindung", Snackbar.LENGTH_INDEFINITE);
        refreshLayout = v.findViewById(R.id.swiperefresh_home);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDownload();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState==null){
            initDownload();
            System.out.println("save NOT restored!");
        }
        else{
            processOutput(savedInstanceState.getString("HTML_OUTPUT"));
            System.out.println("save restored!");
        }

    }

    //checks for internet connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //downloads page information from the net
    private void initDownload() {
        if (isNetworkConnected(getActivity())) {
            refreshLayout.setRefreshing(true);
            TextInternetConnection con = new TextInternetConnection(new TextInternetConnection.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    content = output;
                    processOutput(output);
                }
            });
            con.execute("https://www.gymnasium-lappersdorf.de/home/");
        } else {
            snackbar.show();
            refreshLayout.setRefreshing(false);
        }
    }

    //parse Html and insert into recyclerview
    public void processOutput(String output){
        // temporary solution; will be replaced by proper HTML parsing
        output = output.replace("&nbsp;", "");
        String[] split = output.split("<!--\n\t=====================\n\t\tPartials/List/Item.html\n-->");
        split = Arrays.copyOfRange(split, 1, split.length - 1);

        ArrayList<newsitem> newsitems = new ArrayList<>();
        for (String i : split) {

            String[] itemsplit = i.split("<");

            newsitems.add(new newsitem(/*date*/itemsplit[7].replace("meta itemprop=\"datePublished\" content=\"", "").replace("\" />", "").replace("-", "."),
                            /*title*/ itemsplit[12].replace("span itemprop=\"headline\">", ""),
                            /*href*/ "https://www.gymnasium-lappersdorf.de" + itemsplit[11].split("href=\"")[1].replace("\">", ""),
                            /*img*/ "https://www.gymnasium-lappersdorf.de" + itemsplit[19].split("\" width")[0].replace("img src=\"", ""),
                            /*txt*/ itemsplit[25].replace("p>", "")));

        }

        homeRvAdapter.setDataset(newsitems);
        homeRvAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
        snackbar.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("HTML_OUTPUT", content);
        super.onSaveInstanceState(outState);
    }
}
