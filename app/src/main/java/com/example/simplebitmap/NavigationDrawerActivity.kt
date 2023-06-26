package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.simplebitmap.TiltedAnimation.TiltedActivity
import com.google.android.material.navigation.NavigationView

class NavigationDrawerActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toogle : ActionBarDrawerToggle
    lateinit var navView : NavigationView
    private val PERMISSION_REQUEST_STORAGE = 1000
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    var currentNightMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        requestpermission()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Get the current night mode from the system settings
        // Get the current night mode from the system settings
        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK


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
                R.id.csvreaderwriter -> {
                    val intent = Intent(this,CSVActivity::class.java)
                    startActivity(intent)
                }
                R.id.dxf -> {
                    val intent = Intent(this,DXFActivity::class.java)
                    startActivity(intent)
                }
                R.id.animation -> {
                    val intent = Intent(this,TiltedActivity::class.java)
                    startActivity(intent)
                }
                R.id.rasterimage -> {
                    val intent = Intent(this,RasterImageActivity::class.java)
                    startActivity(intent)
                }

                R.id.osm -> {
                    val intent = Intent(this,OSMActivity::class.java)
                    startActivity(intent)
                }

                R.id.osm2 -> {
                    val intent = Intent(this,OSMActivity2::class.java)
                    startActivity(intent)
                }

                R.id.themecolor -> {
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    recreate();
                }

            }
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
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

    /// Runtime RequestPermission
    fun requestpermission() {
        //request permission for Read
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
        //request permission for Write
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }
}