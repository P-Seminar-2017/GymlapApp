package de.gymnasium_lappersdorf.gymlapapp.homeworkList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.gymnasium_lappersdorf.gymlapapp.R;


/**
 * Created by JOnas on 1/15/2018.
 */

public class HomeworkListActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_list);
        recyclerView = findViewById(R.id.homeworkRecycler);

        //todo set the adapter with data
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }



    class HomeworkListAdapter extends RecyclerView.Adapter<HomeworkViewHolder>{

        private List<HomeworkModel> data;

        public HomeworkListAdapter(List<HomeworkModel> data) {
            this.data = data;
        }


        @Override
        public HomeworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HomeworkViewHolder(LayoutInflater.from(getApplicationContext()).inflate( R.layout.homework_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(HomeworkViewHolder holder, int position) {
            if(position != 0)
            holder.bind(data.get(position), data.get(position).getDeadline().compareTo(data.get(position-1).getDeadline()) != 0);
        else

                holder.bind(data.get(position),true);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class HomeworkViewHolder extends RecyclerView.ViewHolder{


        private TextView doneText, courseText, messageText;
        private FrameLayout headContanier;
        public HomeworkViewHolder(View itemView) {
            super(itemView);
            doneText = itemView.findViewById(R.id.doneText);
            courseText = itemView.findViewById(R.id.corseText);
            messageText= itemView.findViewById(R.id.messageText);
            headContanier = itemView.findViewById(R.id.headContainer);

        }


        void bind(HomeworkModel model, boolean includeHead){
            doneText.setVisibility(model.isDone()? View.GONE: View.VISIBLE);
            courseText.setText(model.getCourseName());
            messageText.setText(model.getMessage());
            if(includeHead){
                headContanier.addView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.homework_list_head,  headContanier, false));
                headContanier.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ((TextView)headContanier.findViewById(R.id.dateText)).setText(dateConverter(model.getDeadline().getTimestamp()));
            }
        }

         String dateConverter(long timestamp){
             DateFormat format = new SimpleDateFormat("dd.MM.yy");
             try {
                 Date date = format.parse(timestamp+"");
                 return date.toString();
             } catch (ParseException e) {
                 e.printStackTrace();
             }
             return "";
         }
    }



}
