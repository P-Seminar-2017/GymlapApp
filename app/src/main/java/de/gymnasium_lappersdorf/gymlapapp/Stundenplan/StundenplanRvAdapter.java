package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * Created by Leon on 27.11.2017.
 */

public class StundenplanRvAdapter extends RecyclerView.Adapter<StundenplanRvAdapter.ViewHolder> {

    private List<Lesson> dataset;
    long day;
    Context context;

    public StundenplanRvAdapter(List<Lesson> dataset, long day, Context context) {
        this.dataset = dataset;
        this.day = day;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Stundenplanview sv;

        public ViewHolder(Stundenplanview s) {
            super(s);
            sv = s;
        }
    }

    @Override
    public StundenplanRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Stundenplanview v = new Stundenplanview(parent.getContext());
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.sv.setLesson(dataset.get(position));
        holder.sv.setClickResponse(new Stundenplanview.ClickResponse() {
            @Override
            public void singleClickResponse() {
                holder.sv.toggleExpansion();
            }

            @Override
            public void longClickResponse() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                Button button = new Button(context, null, R.attr.borderlessButtonStyle);
                button.setText("Stunde löschen");
                builder.setView(button);
                final AlertDialog dialog = builder.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //delete from database
                        Day dayToRmFrom = DatabaseHandler.INSTANCE.getDay(day);
                        dayToRmFrom.lessons.remove(dataset.get(holder.getAdapterPosition()));
                        DatabaseHandler.INSTANCE.setDay(dayToRmFrom);
                        //update rv
                        dataset.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setDataset(List<Lesson> dataset) {
        this.dataset = dataset;
    }
}
