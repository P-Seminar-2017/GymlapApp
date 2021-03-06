package de.gymnasium_lappersdorf.gymlapapp.Info

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import de.gymnasium_lappersdorf.gymlapapp.BuildConfig
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.fragment_info.*

/*
* Fragment for showing about section and licensing
* */
class InfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "Über diese App"
        val version = "Version: ${BuildConfig.VERSION_NAME}"
        about_version.text = version
        about_licenses.setOnClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
        }
        about_cvs.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .setCloseButtonIcon(BitmapFactory.decodeResource(context!!.resources, R.drawable.back))
            builder.build().launchUrl(context!!, Uri.parse("https://github.com/P-Seminar-2017/GymlapApp"))
        }
    }
}