package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner.Hausaufgabe

/*
* recyclerviewAdapter for displaying lessons in a day
* */
class RvAdapter(
        var dataset: MutableList<Lesson>,
        private val hw: List<Hausaufgabe>,
        private val onHomework: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    override fun getItemCount(): Int = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LessonView(parent.context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        //set lessonView properties
        holder.lessonView.setLesson(dataset[position])
        holder.lessonView.position = position
        for (n in hw) {
            if (n.fach!! == dataset[position].subject.target.name) {
                holder.lessonView.setHomework()
            }
        }
        //called on deleting a lesson
        holder.lessonView.onDelete = {
            //update rv
            dataset.removeAt(holder.adapterPosition)
            if (dataset.size == 0) {
                recyclerView.visibility = View.GONE
            } else {
                notifyItemRemoved(holder.adapterPosition)
            }
        }
        //called on expanding a lesson
        holder.lessonView.onExpand = { lessonView: LessonView ->
            for (i in 0 until recyclerView.childCount) {
                val item = recyclerView.getChildAt(i)!!
                item as LessonView
                if (item.position != lessonView.position && item.expanded) {
                    item.toggleExpansion()
                }
            }
        }
        //passing onHomework through to lessonView
        holder.lessonView.onHomework = onHomework
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    class ViewHolder(v: LessonView) : RecyclerView.ViewHolder(v) {
        val lessonView: LessonView = v
    }
}