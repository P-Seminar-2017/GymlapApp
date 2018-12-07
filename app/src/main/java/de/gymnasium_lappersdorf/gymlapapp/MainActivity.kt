package de.gymnasium_lappersdorf.gymlapapp

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import de.gymnasium_lappersdorf.gymlapapp.Navigation.NavDrawerOnClickHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting Toolbar
        setSupportActionBar(toolbar_main)

        //setting up Navigation-Drawer
        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout_main,
                toolbar_main,
                R.string.drawer_close,
                R.string.drawer_open
        )
        drawer_layout_main.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toggle.syncState()
        val handler = NavDrawerOnClickHandler(this)
        navigation.setNavigationItemSelectedListener(handler)
    }
}