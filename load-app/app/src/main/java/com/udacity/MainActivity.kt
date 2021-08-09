package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0
    private var downloading = false

    private var downloadURL: String? = null
    private var downloadOptionName: String? = null
    private var estimatedTotalSize = Long.MAX_VALUE

    private lateinit var notificationManager: NotificationManager

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
            if(downloadURL == null) {
                Toast.makeText(
                    this,
                    getString(R.string.select_file_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            download()
        }

        radio_group.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.glide_radio_button -> {
                    downloadURL = GLIDE_URL
                    downloadOptionName = getString(R.string.glide_download_option)
                }
                R.id.loadapp_radio_button -> {
                    downloadURL = LOAD_APP_URL
                    downloadOptionName = getString(R.string.loadapp_download_option)
                }
                R.id.retrofit_radio_button -> {
                    downloadURL = RETROFIT_URL
                    downloadOptionName = getString(R.string.retrofit_download_option)
                }
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        if(downloading) {
            cancelDownload()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(downloadID == id) {
                displayNotification()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                // Disable badges for this channel
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun displayNotification() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(applicationContext, downloadOptionName)
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadURL))
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

    private fun cancelDownload() {
        downloadManager.remove(downloadID)
    }

    private fun startQueryProgress() {
        estimatedTotalSize = Long.MAX_VALUE
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
                DownloadManager.STATUS_PENDING -> {
                    custom_button.setLoadingButtonState(ButtonState.Loading)
                }
                DownloadManager.STATUS_RUNNING -> {
                    val downloadedSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    // if totalSize is -1L (unknown) and the downloaded size is not 0L, we give an estimated total size for the progress value.
                    // Once it's given, the estimated total size should be fixed until totalSize is not -1L
                    if (downloadedSize != 0L && estimatedTotalSize == Long.MAX_VALUE) {
                        estimatedTotalSize = downloadedSize * 100
                    } else if (totalSize != -1L) {
                        estimatedTotalSize = totalSize
                    }
                    val progress = (downloadedSize.toFloat() / estimatedTotalSize) * 100
                    custom_button.setLoadingProgress(progress)
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    custom_button.setLoadingButtonState(ButtonState.Completed)
                    stopQueryProgress()
                }
                DownloadManager.STATUS_FAILED -> {
                    custom_button.setLoadingButtonState(ButtonState.Failed)
                    stopQueryProgress()
                }
            }
        } else {
            custom_button.setLoadingButtonState(ButtonState.Failed)
            stopQueryProgress()
        }
        cursor.close()
        if(downloading) {
            handler.post(queryProgressRunnable)
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://search.maven.org/remote_content?g=com.squareup.retrofit2&a=retrofit&v=LATEST"
    }

}
