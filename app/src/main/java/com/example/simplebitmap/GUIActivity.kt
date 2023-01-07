package com.example.simplebitmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.apogee.customgui.MainActivity

class GUIActivity : AppCompatActivity() {

    lateinit var imageview : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView = findViewById<ImageView>(R.id.imageView)
        val clear = findViewById<Button>(R.id.clear)
        val draw = findViewById<Button>(R.id.draw)

        val mainActivity : com.apogee.customgui.MainActivity= MainActivity()
        mainActivity.intiliaze(imageView = imageView,this)

        clear.setOnClickListener {
            mainActivity.clearpoints()
        }

        draw.setOnClickListener {
            mainActivity.addPoint(125F,250F,"A")
        }

    }

}