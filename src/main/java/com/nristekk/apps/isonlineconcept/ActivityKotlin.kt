package com.nristekk.apps.isonlineconcept

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nristekk.apps.isonlineconcept.network.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ActivityKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    }


    override fun onResume() {
        super.onResume()

        //function that check Online state of App(Device)
        checkOnline()

        //Uncomment below to launch check interval test
        //checkOnlineIntervalOnly()

        //Uncomment below to launch check only chain test
        //checkOnlyChain()

        //Uncomment below to launch check only UDP protocol
        //checkOnlyUDP()

        //Uncomment below to launch check only Http protocol
        //checkOnlyHttp();

    }



    //function of Observable checking IsOnline state

    private fun checkOnline(){
        Observable.interval(10, TimeUnit.SECONDS)
                .flatMap {  sequence->(
                        checkUdpKt( "208.67.222.222",53)
                                .concatMap {
                                    it->(

                                        return@concatMap
                                        //Be able to establish UDP socket to OpenDNS -> send 'true' result to be Observable
                                        if (it) Observable.just(it)
                                        //Tested with UDP socket failed -> let's get Observable result from checking Http to be Observed
                                        else checkHttpKt("https://www.google.com/")
                                        )
                                }
                        )
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribeBy(
                        onNext={
                            if (it) Toast.makeText(applicationContext, getString(R.string.isonline_true), Toast.LENGTH_SHORT).show()
                            else Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        })
    }



    //function of Observable checking Online state through Interval
    private  fun checkOnlineIntervalOnly(){
        //run interval check every 10 seconds
        Observable.interval(10, TimeUnit.SECONDS)
                .flatMap {  sequence->(
                            checkUdpKt( "208.67.222.222",53)
                                    .concatMap {
                                        it->(

                                            return@concatMap
                                            //Be able to establish UDP socket to OpenDNS -> send 'true' result to be Observable
                                            if (it) Observable.just(it)
                                            //Tested with UDP socket failed -> let's get Observable result from checking Http to be Observed
                                            else checkHttpKt("https://www.google.com/")
                                                )
                                    }
                        )
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext={
                            if (it) Toast.makeText(applicationContext, getString(R.string.isonline_true), Toast.LENGTH_SHORT).show()
                            else Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        })

    }



    //function of Observable checking Online state through chaining
    private fun checkOnlyChain(){
        //Checking with UDP on topped with DNS(port 53) by OpenDNS first
        checkUdpKt("208.67.222.222",53)
                .concatMap{
                    it->( return@concatMap

                        //Be able to establish UDP socket to OpenDNS -> send 'true' result to be Observable
                        if (it) Observable.just(it)

                        //Tested with UDP socket failed -> let's get Observable result from checking Http to be Observed
                        else checkHttpKt("https://www.google.com/")
                        )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                onNext={
                    if (it) Toast.makeText(applicationContext, getString(R.string.isonline_true), Toast.LENGTH_SHORT).show()
                    else Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                },
                onError = {
            Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
        }
        )
    }


    // function of Observable checking Online state through UDP socket only

    private fun checkOnlyUDP(){
        checkUdpKt("208.67.222.222",53)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //Let's use subscribeBy instead of subscribe
                .subscribeBy(
                        onNext={
                            if (it) Toast.makeText(applicationContext, getString(R.string.isonline_true), Toast.LENGTH_SHORT).show()
                            else Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        }
                )
    }



    // function of Observable checking Onlint state through Http request only

    private fun checkOnlyHttp(){
        checkHttpKt("https://www.google.com/")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //Let's use subscribeBy instead of subscribe
                .subscribeBy(
                        onNext={
                            if (it) Toast.makeText(applicationContext, getString(R.string.isonline_true), Toast.LENGTH_SHORT).show()
                            else Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(applicationContext, getString(R.string.isonline_false), Toast.LENGTH_SHORT).show()
                        }
                )
    }


}