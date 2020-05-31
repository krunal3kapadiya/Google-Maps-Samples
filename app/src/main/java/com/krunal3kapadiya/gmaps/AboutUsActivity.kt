package com.krunal3kapadiya.gmaps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {
    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, AboutUsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }
}
