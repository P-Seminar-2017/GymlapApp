package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 01.06.2018 | created by Lukas S
 */

public class HomeworkRvAdapter extends RecyclerView.Adapter<HomeworkRvAdapter.ViewHolder> {
    private ArrayList<Hausaufgabe> dataset;
    private Context c;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public HausaufgabenItemView hv;

        public ViewHolder(HausaufgabenItemView h) {
            super(h);
            hv = h;
        }
    }

    public HomeworkRvAdapter(Hausaufgabe[] dataset, Context c) {
        this.dataset = new ArrayList<>(Arrays.asList(dataset));
        this.c = c;
    }

    @Override
    public HomeworkRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HausaufgabenItemView v = new HausaufgabenItemView(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.hv.setHomework(dataset.get(position));
        holder.hv.setRemindButtonCallback(new HausaufgabenItemView.Callback() {
            @Override
            public void onClick() {
                JobService.createSchedule(c, dataset.get(holder.getAdapterPosition()).getTimestamp(), dataset.get(holder.getAdapterPosition()).getText());
                Toast.makeText(c, "Erinnerung gesetzt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setDataset(Hausaufgabe[] dataset){
        this.dataset = new ArrayList<>(Arrays.asList(dataset));
    }

    public Hausaufgabe[] getDataset() {
        return dataset.toArray(new Hausaufgabe[dataset.size()]);
    }

    public void removeItem(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Hausaufgabe hw, int position) {
        dataset.add(position, hw);
        notifyItemInserted(position);
    }
}
