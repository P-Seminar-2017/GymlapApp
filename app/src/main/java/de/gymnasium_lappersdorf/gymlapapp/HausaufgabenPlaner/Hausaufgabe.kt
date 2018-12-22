package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.app.job.JobScheduler
import android.content.Context
import java.util.*

/**
 * Created by lmatn on 01.03.2018.
 */

class Hausaufgabe(var fach: String?, var text: String?, var timestamp: Long, var stufe: Int, var kurs: String?, val type: Types) {
    var databaseId: Long = 0 //-1 if not in database already
    var internetId: Long = 0 //-1 if not from internet
    var notificationId: Int = 0
    var isDone: Boolean = false

    init {
        this.internetId = -1
        this.databaseId = -1
        this.notificationId = -1
        this.isDone = false
    }

    enum class Types {
        DATE, NEXT, NEXT2
    }

    val dateFormatted: String
        get() {
            val c = Calendar.getInstance()
            c.time = Date(timestamp)
            return String.format("%s.%s.%s",
                    c.get(Calendar.DATE),
                    c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.YEAR))
        }

    val dayOfWeek: String
        get() {
            val tage = arrayOf("Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag") //Calendar.DAY_OF_WEEK starts at 1 (sunday)
            val c = Calendar.getInstance()
            c.time = Date(timestamp)
            return tage[c.get(Calendar.DAY_OF_WEEK) - 1]
        }

    val isFromInternet: Boolean
        get() = internetId.compareTo(-1) != 0

    val isOutdated = timestamp - System.currentTimeMillis() < 0

    fun isSetAsNotification(c: Context): Boolean {
        val jobScheduler = c.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobs = jobScheduler.allPendingJobs
        var alreadySet = false

        for (i in jobs.indices) {
            if (jobs[i].id == notificationId) {
                alreadySet = true
                break
            }
        }

        return alreadySet
    }

    override fun equals(other: Any?): Boolean {

        if (this.javaClass == other!!.javaClass) {
            val obj = other as Hausaufgabe?

            if (this.databaseId == obj!!.databaseId && !isFromInternet) {
                return true
            } else if (this.internetId == obj.internetId && isFromInternet) {
                return true
            }
        }

        return false
    }

    override fun hashCode(): Int {
        var result = databaseId.hashCode()
        result = 31 * result + internetId.hashCode()
        return result
    }
}

