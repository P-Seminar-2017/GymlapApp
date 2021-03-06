package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    private Fragment f;
    private DatasetChangeListener listener;

    public interface DatasetChangeListener {
        void onNotificationIdChanged(Hausaufgabe h);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public HausaufgabenItemView hv;

        public ViewHolder(HausaufgabenItemView h) {
            super(h);
            hv = h;
        }
    }

    public HomeworkRvAdapter(Hausaufgabe[] dataset, Context c, Fragment f, DatasetChangeListener listener) {
        this.dataset = new ArrayList<>(Arrays.asList(dataset));
        this.c = c;
        this.f = f;
        this.listener = listener;
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

                if (dataset.get(holder.getAdapterPosition()).isSetAsNotification(c)) {
                    int id = dataset.get(holder.getAdapterPosition()).getNotificationId();
                    JobScheduler jobScheduler = (JobScheduler) c.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    jobScheduler.cancel(id);
                    dataset.get(holder.getAdapterPosition()).setNotificationId(-1);
                    Toast.makeText(c, "Erinnerung gestoppt", Toast.LENGTH_SHORT).show();
                } else {
                    int scheduleId = JobService.Companion.createSchedule(c, dataset.get(holder.getAdapterPosition()).getTimestamp(), dataset.get(holder.getAdapterPosition()).getText());
                    dataset.get(holder.getAdapterPosition()).setNotificationId(scheduleId);
                    long hours = (dataset.get(holder.getAdapterPosition()).getTimestamp() - System.currentTimeMillis()) / 3600000L;
                    Toast.makeText(c, "Erinnerung gesetzt (" + (hours >= 24 ? Math.round(hours / 24f) + " Tage" : hours + " Stunden") + " verbleibend)",
                            Toast.LENGTH_SHORT).show();
                }

                holder.hv.updateButton();
                listener.onNotificationIdChanged(dataset.get(holder.getAdapterPosition()));
            }
        });

        if (!dataset.get(position).isFromInternet()) {
            holder.hv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(c, AddHomeworkActivity.class);
                    intent.putExtra("EDIT_ID", dataset.get(holder.getAdapterPosition()).getDatabaseId());
                    f.startActivityForResult(intent, ((HausaufgabenLokalFragment) f).getEditId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setDataset(Hausaufgabe[] dataset) {
        this.dataset = new ArrayList<>(Arrays.asList(dataset));
    }

    public Hausaufgabe[] getDataset() {
        return dataset.toArray(new Hausaufgabe[0]);
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
