package de.gymnasium_lappersdorf.gymlapapp;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Leon on 27.11.2017.
 */

public class StundenplanRvAdapter extends RecyclerView.Adapter<StundenplanRvAdapter.ViewHolder> {

    private Stunde[] dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Stundenplanview sv;

        public ViewHolder(Stundenplanview s) {
            super(s);
            sv = s;
        }
    }

    public StundenplanRvAdapter(Stunde[] dataset) {
        this.dataset = dataset;
    }

    @Override
    public StundenplanRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Stundenplanview v = new Stundenplanview(parent.getContext());
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sv.setParams(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    public void setDataset(Stunde[] dataset){
        this.dataset = dataset;
    }
}
