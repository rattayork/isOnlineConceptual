package com.nristekk.apps.isonlineconcept;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.nristekk.apps.isonlineconcept.network.IsOnlineJ;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

    }

    @Override
    public void onResume(){
        super.onResume();

        //function that check Online state of App(Device)
        checkOnline();

        //Uncomment below to launch check interval test
        //checkOnlineIntervalOnly();

        //Uncomment below to launch check only chain test
        //checkOnlyChain();

        //Uncomment below to launch check only UDP protocol
        //checkOnlyUDP();

        //Uncomment below to launch check only Http protocol
        //checkOnlyHttp();

    }



    //function of Observable checking IsOnline state

    private void checkOnline(){
        Observable.interval(10, TimeUnit.SECONDS)
                .flatMap( sequence-> { return
                        IsOnlineJ.checkUdpJ("208.67.222.222", 53)
                                .concatMap(isUdpUp -> {
                                    //UDP check passed, you're online!, tell the user!
                                    if (isUdpUp) return Observable.just(isUdpUp);
                                        //UDP check failed let's check with Http further
                                    else return IsOnlineJ.checkHttpJ("https://www.google.com/");
                                });
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull Boolean isUDPConnected) {
                        if(isUDPConnected) Toast.makeText(getApplicationContext(), getString(R.string.isonline_true), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() { }
                });
    }



    // method of Observable checking Online state through Interval

    private void checkOnlineIntervalOnly(){
        Observable.interval(10, TimeUnit.SECONDS)
                .flatMap( sequence-> { return
                    IsOnlineJ.checkUdpJ("208.67.222.222", 53)
                            .concatMap(isUdpUp -> {
                                //UDP check passed, you're online!, tell the user!
                                if (isUdpUp) return Observable.just(isUdpUp);
                                    //UDP check failed let's check with Http further
                                else return IsOnlineJ.checkHttpJ("https://www.google.com/");
                            });
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull Boolean isUDPConnected) {
                        if(isUDPConnected) Toast.makeText(getApplicationContext(), getString(R.string.isonline_true), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() { }
                });
    }



    // method of Observable checking Online state through chaining

    private void checkOnlyChain(){
        //Check UDP socket connection (DNS:// OpenDNS:53) first
        IsOnlineJ.checkUdpJ("208.67.222.222",53)
                .concatMap(
                        isUdp->{

                            //UDP check passed, you're online!, tell the user!
                            if(isUdp) return Observable.just(isUdp);

                            //UDP check failed let's check with Http further
                            else return IsOnlineJ.checkHttpJ("https://www.google.com/");
                        }
                ).subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull Boolean isUDPConnected) {
                        if(isUDPConnected) Toast.makeText(getApplicationContext(), getString(R.string.isonline_true), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() { }
                });
    }



    // method of Observable checking Online state through UDP socket only

    private void checkOnlyUDP(){
        //We'd like to use UDP of DNS (port Number 53)
        // from OpenDNS (IP address = 208.67.222.222)
        IsOnlineJ.checkUdpJ("208.67.222.222",53)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull Boolean isUDPConnected) {
                        if(isUDPConnected) Toast.makeText(getApplicationContext(), getString(R.string.isonline_true), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() { }
                });
    }



    // method of Observable checking Onlint state through Http request only

    private void checkOnlyHttp(){
        //We check the Http request to determine Online state using google.com
        IsOnlineJ.checkHttpJ("https://www.google.com/")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onNext(@NonNull Boolean isHttpConnected) {
                        if(isHttpConnected) Toast.makeText(getApplicationContext(), getString(R.string.isonline_true), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.isonline_false), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() { }
                });
    }



}