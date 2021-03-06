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
* !only one instance should exist at runtime,
* so leave initialisation to dagger!
* */
@Singleton
class DatabaseHandler {

    @Inject
    lateinit var store: BoxStore
    private var dayBox: Box<Day>
    private var subBox: Box<Subject>
    private var lessonBox: Box<Lesson>

    /*
    * initializes the boxes for the different objects
    * */
    init {
        App.appComponent.inject(this)
        dayBox = store.boxFor()
        subBox = store.boxFor()
        lessonBox = store.boxFor()
        createDB()
    }

    /*
    * initializes the database by adding 5 days
    * and 3 default subjects on first access
    * */
    private fun createDB() {
        val days: List<Day> = getDays()
        if (days.isEmpty()) {
            for (i in 0 until 5) {
                setDay(Day(0, i.toLong()))
            }
        }
        val subjects: List<Subject> = getSubjects()
        if (subjects.isEmpty()) {
            val defaultSubjects = listOf("Deutsch", "Mathe", "Sport")
            for (i in defaultSubjects) {
                setSubject(Subject(0, i, "", "", ""))
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
    * removes a [subject] from the database
    * */
    fun rmSubject(subject: Subject) = subBox.remove(subject)

    /*
    * @return all the Subject objects in the database
    * */
    fun getSubjects(): List<Subject> {
        val query = subBox.query().build()
        return query.find()
    }

    /*
    * @returns a lesson based on its [id]
    * */
    fun getLesson(id: Long): Lesson? {
        val query = lessonBox.query().equal(Lesson_.id, id).build()
        try {
            return query.findUnique()
        } catch (e: NonUniqueResultException) {
            e.printStackTrace()
        }
        return null
    }

    /*
    * removes a [lesson] from the database
    * */
    fun rmLesson(lesson: Lesson) = lessonBox.remove(lesson)

    /*
    * @returns a list of lessons for a [subject]
    * */
    fun getLessons(subject: Subject): List<Lesson> {
        val query = lessonBox.query().equal(Lesson_.subjectId, subject.id).build()
        return query.find()
    }

    /*
    * @returns a list of numbers at which days a given [subject] takes place
    * */
    fun getDaysForSubject(subject: String): List<Int> {
        val sub = getSubject(subject)!!
        val lessons = getLessons(sub)
        val days = emptyList<Int>().toMutableList()
        for (l in lessons) {
            val num = l.day.target!!.day.toInt()
            if (num !in days) {
                days.add(num)
            }
        }
        return days
    }
}