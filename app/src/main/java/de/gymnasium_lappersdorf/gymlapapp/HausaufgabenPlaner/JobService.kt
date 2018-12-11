package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle

import de.gymnasium_lappersdorf.gymlapapp.MainActivity
import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * Created by jonas on 12/3/2017.
 */

class JobService : android.app.job.JobService() {

    companion object {

        private const val EXTRA_TEXT = "extra_text"

        //creates a new job that will execute at the given timestamp(in milli seconds) and show the given notification
        fun createSchedule(context: Context, timestamp: Long, notification: String) : Int {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_notification_id_key), Context.MODE_PRIVATE)
            var id = sharedPref.getInt(context.getString(R.string.shared_pref_notification_id), 0)
            id++
            val componentName = ComponentName(context, JobService::class.java)
            val builder = JobInfo.Builder(id, componentName)
            builder.setMinimumLatency(timestamp - System.currentTimeMillis())
            val extra = PersistableBundle()
            extra.putString(EXTRA_TEXT, notification)
            builder.setExtras(extra)
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())

            with (sharedPref.edit()) {
                putInt(context.getString(R.string.shared_pref_notification_id), id)
                apply()
            }

            return id
        }
    }

    override fun onStartJob(params: JobParameters): Boolean {
        val message = params.extras.getString(EXTRA_TEXT)

        GymlapNotification(baseContext, message!!, MainActivity::class.java, params.jobId).show()
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }
}
