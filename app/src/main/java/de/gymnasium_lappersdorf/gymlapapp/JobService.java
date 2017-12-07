package de.gymnasium_lappersdorf.gymlapapp;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

/**
 * Created by jonas on 12/3/2017.
 */

public class JobService extends android.app.job.JobService {


    //region constants

    private static final String EXTRA_TEXT = "extra_text";
    //endregion


    @Override
    public boolean onStartJob(JobParameters params) {

        String message = params.getExtras().getString(EXTRA_TEXT);
        //todo create Notification


        return false;

        
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    //region helper methods

    //creates a new job that will execute at the given timestamp(in milli seconds) and show the given notification
    public static void createScedule(Context context, long timestamp, String notification){
        ComponentName componentName = new ComponentName(context, JobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, componentName);
        builder.setMinimumLatency(timestamp-System.currentTimeMillis());
        PersistableBundle extra = new PersistableBundle();
        extra.putString(EXTRA_TEXT, notification);
        builder.setExtras(extra);
        JobScheduler jobScheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }



    //endregion
}
