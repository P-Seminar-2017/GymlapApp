package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import de.gymnasium_lappersdorf.gymlapapp.App
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.stundenplanview.view.*
import javax.inject.Inject

/*
* view for displaying a single lesson
* */
class LessonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var databaseHandler: DatabaseHandler
    var expanded = false
    var position = 0
    private lateinit var lesson: Lesson
    lateinit var onDelete: () -> Unit
    lateinit var onExpand: (LessonView) -> Unit
    //
    lateinit var onHomework: () -> Unit

    init {
        App.appComponent.inject(this)
        View.inflate(context, R.layout.stundenplanview, this)
        cardView.setOnClickListener { toggleExpansion() }
        del_lesson.setOnClickListener { delete() }
        edit_lesson.setOnClickListener { edit() }
    }

    fun toggleExpansion() {
        if (!expanded) {
            onExpand(this)
        }
        expansion.visibility = if (expanded) View.GONE else View.VISIBLE
        arrow.setImageDrawable(context.getDrawable(if (expanded) R.drawable.arrowdown else R.drawable.arrowup))
        cardView.elevation = if (expanded) 0F else 6F
        expanded = !expanded
    }

    fun setLesson(lesson: Lesson) {
        this.lesson = lesson
        number.text = lesson.number
        val timeString = lesson.start + "-" + lesson.end
        time.text = timeString
        val sub = lesson.subject.target
        subject.text = sub.name
        course.text = "Kurs: ${sub.course}"
        teacher.text = "Lehrer: ${sub.teacher}"
        room.text = "Raum: ${sub.room}"
    }

    fun delete() {
        val d = lesson.day.target!!.day
        val day = databaseHandler.getDay(d)!!
        day.lessons.remove(lesson)
        databaseHandler.setDay(day)
        onDelete()
    }

    fun edit() {
        val intent = Intent(context, LessonActivity::class.java)
        intent.putExtra("lessonid", lesson.id)
        intent.putExtra("day", this.lesson.day.target!!.day)
        intent.putExtra("editing", true)
        context.startActivity(intent)
    }

    fun setHomework() {
        homework_indicator.visibility = View.VISIBLE
        homework_indicator.setOnClickListener { this.onHomework() }
    }
}