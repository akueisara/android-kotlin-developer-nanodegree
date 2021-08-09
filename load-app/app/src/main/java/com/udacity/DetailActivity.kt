package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val DOWNLOAD_OPTION_NAME = "download_option_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent.hasExtra(DOWNLOAD_OPTION_NAME)) {
            file_name_detail_text_view.text = intent.getStringExtra(DOWNLOAD_OPTION_NAME)
            status_name_detail_text_view.text = getString(R.string.success_status)
        }

        ok_button.setOnClickListener {
            finish()
        }
    }
}
