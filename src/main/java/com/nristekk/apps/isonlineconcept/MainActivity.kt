package com.nristekk.apps.isonlineconcept

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {


    /*
    Initial Textviews those will be used to start Specific language activity
    */
    lateinit var linkKotlinText:TextView;
    lateinit var linkJavaText:TextView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set (inflate) textview object from layout Ids
        linkKotlinText = findViewById(R.id.kotlinLink)
        linkJavaText = findViewById(R.id.javaLink)

        //When Textviews click then start specific Activity according to the language (Java/Kotlin)
        linkKotlinText.setOnClickListener{
                view-> intent = Intent(this, ActivityKotlin::class.java)
                        startActivity(intent)
        }

        linkJavaText.setOnClickListener{
                view-> intent = Intent(this, ActivityJava::class.java)
                        startActivity(intent)
        }

    }





}