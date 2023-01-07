package com.example.simplebitmap

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class NavigationDrawerActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toogle : ActionBarDrawerToggle
    lateinit var navView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        toogle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId){

//                R.id.bitmap -> replaceFragment(,it.title.toString())
                R.id.bitmap ->{
                    val intent = Intent(this,Bitmap::class.java)
                    startActivity(intent)
                }

                R.id.mainactivity -> {
                    val intent = Intent(this,GUIActivity::class.java)
                    startActivity(intent)
                }
                R.id.satellite -> {
                    val intent = Intent(this,SatelliteMapping::class.java)
                    startActivity(intent)
                }

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment,title: String){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framelayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toogle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}