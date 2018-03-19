package de.gymnasium_lappersdorf.gymlapapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Leon on 03.01.2018.
 */

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvAdapter.ViewHolder> {

    private ArrayList<newsitem> dataset = new ArrayList<>();
    Context c;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public NewsItemView nv;

        public ViewHolder(NewsItemView n) {
            super(n);
            nv = n;
        }
    }

    public HomeRvAdapter(Context c){
        this.c = c;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NewsItemView nv = new NewsItemView(parent.getContext());
        ViewHolder vh = new ViewHolder(nv);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nv.setParams(dataset.get(position));
        holder.nv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(c.getResources().getColor(R.color.colorPrimary))
                        .enableUrlBarHiding()
                        .setShowTitle(true)
                        .setCloseButtonIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.back));

                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(c, Uri.parse(holder.nv.getHref()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setDataset(ArrayList<newsitem> e) {
        dataset = e;
    }
}
