package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0
    private var downloading = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val queryProgressRunnable: Runnable = Runnable {
        queryProgress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
        if (downloadID != 0L) {
            startQueryProgress()
        }
    }

    private fun startQueryProgress() {
        downloading = true
        handler.post(queryProgressRunnable)
    }

    private fun stopQueryProgress() {
        downloading = false
        handler.removeCallbacks(queryProgressRunnable)
    }

    private fun queryProgress() {
        val query = DownloadManager.Query().setFilterById(downloadID)
        val cursor = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_RUNNING -> {
                    custom_button.setLoadingButtonState(ButtonState.Loading)
                    val downloadedSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (totalSize != -1L) {
                        // TODO: Update the download progress of the LoadingButton
                        Log.d(TAG, "Progress: ${(downloadedSize.toFloat() / totalSize) * 100}")
                    }
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    custom_button.setLoadingButtonState(ButtonState.Completed)
                    stopQueryProgress()
                }
                DownloadManager.STATUS_FAILED -> {
                    custom_button.setLoadingButtonState(ButtonState.Completed)
                    stopQueryProgress()
                }
            }
        } else {
            stopQueryProgress()
        }
        cursor.close()
        if(downloading) {
            handler.post(queryProgressRunnable)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
