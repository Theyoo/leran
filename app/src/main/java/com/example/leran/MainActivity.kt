package com.example.leran

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    /*---------------------------------------------------------------------*/
    private val NOTIFICATION_ID = 0

    /*---------------------------------------------------------------------*/
    /*---------------------------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton: Button = findViewById(R.id.button2)

        rollButton.setOnClickListener {


             

            rollDice()

        }
        retrofit()





    }

    /*---------------------------------------------------------------------*/
    /*---------------------------------------------------------------------*/
    private fun rollDice() {
        val ttd = AnimationUtils.loadAnimation(this,R.anim.ttd)
        val dice = Dice(6)
        val diceRoll = dice.roll()
        val diceImage: ImageView = findViewById(R.id.imageView)
        diceImage.setImageResource(R.drawable.dice_2)
        diceImage.startAnimation(ttd)
        when (diceRoll) {
            1 -> diceImage.setImageResource(R.drawable.dice_1)

            2 -> diceImage.setImageResource(R.drawable.dice_2)

            3 -> diceImage.setImageResource(R.drawable.dice_3)

            4 -> diceImage.setImageResource(R.drawable.dice_4)

            5 -> diceImage.setImageResource(R.drawable.dice_5)

            6 -> resRun(diceImage, diceRoll)

        }


    }

    /*---------------------------------------------------------------------*/
    /*---------------------------------------------------------------------*/
    private fun resRun(diceImage: ImageView, diceRoll: Int) {
        Log.d("tog", "i work  ${diceRoll}")
        diceImage.setImageResource(R.drawable.dice_6)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val broadcastIntent: Intent = Intent(this, MainActivity::class.java)
        val actionIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.egg_notification_channel_id)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("com.example.leran", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            var builder = NotificationCompat.Builder(this, "com.example.leran")
                .setSmallIcon(R.drawable.dice_1)
                .setContentTitle("Выпало: 6")
                .setContentText("Вам повезло")
                .setColor(Color.RED)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Вам повезло")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.dice_6, "OPEN", actionIntent)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(NOTIFICATION_ID, builder.build())

            }
        } else {

            var builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.dice_1)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line...")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(NOTIFICATION_ID, builder.build())

            }

        }


    }

    /*---------------------------------------------------------------------*/
    /*---------------------------------------------------------------------*/
    private fun retrofit() {


        val retrofit = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val api = retrofit.create(ApiService::class.java)
        api.fittAllUsers().enqueue(object : Callback<List<User>> {

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                showDate(response.body()!!)

            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {

                Log.d("tog", "Problem :(  ${t.message}")

            }


        })


    }

    private fun showDate(users: List<User>?) {
        my_recycler_view.apply {

            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = users?.let { UsersAdapter(it) }

        }
    }

    /*---------------------------------------------------------------------*/
    /*---------------------------------------------------------------------*/
        class Dice(val numSides: Int) {

            fun roll(): Int {

                return (1..numSides).random()
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main,menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchManager

        



        return true
    }
    /*---------------------------------------------------------------------*/
    }
