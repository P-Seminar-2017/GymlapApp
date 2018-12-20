package de.gymnasium_lappersdorf.gymlapapp.Home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var rvAdapter: ArticlesRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "GymlapApp"
        //rv initialisation
        rvAdapter = ArticlesRvAdapter(emptyList(), context!!)
        home_rv.layoutManager = LinearLayoutManager(context)
        home_rv.adapter = rvAdapter

        /*
        * sets the animation of the swipeToRefreshLayout to [refreshing]
        * */
        fun setRefreshingIndicator(refreshing: Boolean) {
            try {
                swiperefresh_home.isRefreshing = refreshing
            } catch (e: IllegalStateException) {
                //activity rebuilt during refresh animation e.g screen rotation
                //view can't be accessed anymore, but will be rebuilt anyway
            }
        }

        swiperefresh_home.setOnRefreshListener {
            //pulled to refresh
            GlobalScope.launch(Dispatchers.Main) {
                val newArticles = Articles.getDownload(Articles.URL, context!!)
                rvAdapter.setDataset(newArticles)
                rvAdapter.notifyDataSetChanged()
                setRefreshingIndicator(false)
            }
        }
        val con = context!!
        GlobalScope.launch(Dispatchers.Main) {
            setRefreshingIndicator(true)
            //receive Articles from cache and/or download
            Articles.initialize(con) { articles ->
                rvAdapter.setDataset(articles)
                rvAdapter.notifyDataSetChanged()
            }
            setRefreshingIndicator(false)
        }
    }
}
