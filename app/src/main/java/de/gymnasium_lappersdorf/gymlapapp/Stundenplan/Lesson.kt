package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne


@Entity
data class Day(
        @Id var id: Long = 0,
        val day: Long = 0
) {
    @Backlink(to = "day")
    lateinit var lessons: ToMany<Lesson>
}

@Entity
data class Lesson(
        @Id var id: Long = 0,
        val number: String = "",
        val start: String = "",
        val end: String = ""
) {
    lateinit var day: ToOne<Day>
    lateinit var subject: ToOne<Subject>
}

@Entity
data class Subject(
        @Id var id: Long = 0,
        val name: String,
        val course: String,
        val teacher: String,
        val room: String
) {
    @Backlink(to = "subject")
    lateinit var lessons: ToMany<Lesson>
}