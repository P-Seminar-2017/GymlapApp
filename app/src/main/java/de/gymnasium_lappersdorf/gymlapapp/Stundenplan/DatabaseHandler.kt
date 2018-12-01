package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import de.gymnasium_lappersdorf.gymlapapp.App
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.exception.NonUniqueResultException
import io.objectbox.kotlin.boxFor
import javax.inject.Inject
import javax.inject.Singleton

/*
* class that handles all CRUD operations
* for Day and Subject in the objectbox DB
* !only one instance of BoxStore should exist
* at runtime, so leave initialisation to dagger!
* */
@Singleton
class DatabaseHandler {

    @Inject
    lateinit var store: BoxStore
    private var dayBox: Box<Day>
    private var subBox: Box<Subject>

    /*
    * initializes the boxes for the different objects
    * */
    init {
        App.appComponent.inject(this)
        dayBox = store.boxFor()
        subBox = store.boxFor()
        createDB()
    }

    /*
    * initializes the database by adding 5 days on first access
    * */
    private fun createDB() {
        val days: List<Day> = getDays()
        if (days.isEmpty()) {
            for (i in 0 until 5) {
                setDay(Day(0, i.toLong()))
            }
        }
    }

    /*
    * add one [day] object to the database
    * */
    fun setDay(day: Day) {
        dayBox.put(day)
    }

    /*
    * @returns just one day based on a [day] number
    * */
    fun getDay(day: Long): Day? {
        val query = dayBox.query().equal(Day_.day, day).build()
        try {
            return query.findUnique()
        } catch (e: NonUniqueResultException) {
            e.printStackTrace()
        }
        return null
    }

    /*
    * @return all the Day objects in the database
    * */
    private fun getDays(): List<Day> {
        val query = dayBox.query().build()
        return query.find()
    }

    /*
    * add one [subject] object to the database
    * */
    fun setSubject(subject: Subject) {
        subBox.put(subject)
    }

    /*
    * @returns just one subject based on a [name] String
    * */
    fun getSubject(name: String): Subject? {
        val query = subBox.query().equal(Subject_.name, name).build()
        try {
            return query.findUnique()
        } catch (e: NonUniqueResultException) {
            e.printStackTrace()
        }
        return null
    }

    /*
    * @return all the Subject objects in the database
    * */
    fun getSubjects(): List<Subject> {
        val query = subBox.query().build()
        return query.find()
    }
}