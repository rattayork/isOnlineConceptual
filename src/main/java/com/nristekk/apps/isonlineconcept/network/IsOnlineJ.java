package com.nristekk.apps.isonlineconcept.network;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.operators.observable.ObservableFromFuture;
import io.reactivex.rxjava3.schedulers.Schedulers;

/*
This class contains elements of checking Online state using Java
*/
public class IsOnlineJ {

    //Default UDP socket timeout, 3 seconds is much long enough these day.
    protected static int UDP_SO_TIMEOUT = 3000;

    //Default Http timeout after connected, we set it for 1.5 Seconds
    protected static int HTTP_TIMEOUT = 1500;


    /*
    This is worker method being used for trigger UDP connection
    the code design to connection in general UDP, for us to specific the application protocol
    as our own will (etc. DNS, NTP, TFTP: using particular port number)
    we separate the worker method out of wrapper method to make it modular
    in case it may being improved or modified in the future.
    */
    public static Boolean checkUdpWorkerJ(String hostServer, int portNumber){
        //Open UDP socket using try-with-resource to be less verbose
        //and close the resource itself automatically
        try(DatagramSocket socket = new DatagramSocket()){
            byte[] requestBytes = new byte[128];
            byte[] receiveBytes = new byte[1024];

            /*
            Set socket timeout is important for UDP protocol it made the socket no longer waiting for the answer of the server.
            If the timeout pass by it will throw socket timeout exception or IO exception.We will simply assume that
            we can't connect to this specific socket right now
            */
            socket.setSoTimeout(UDP_SO_TIMEOUT);
            /*
            Set up request and receive packet, then send and receive data, here is when the socket
            establish a connection over UDP.
             */
            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length);
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            SocketAddress socketAddress = new InetSocketAddress(hostServer, portNumber);
            socket.connect(socketAddress);
            socket.send(requestPacket);
            socket.receive(receivePacket);

            //Everything seems alright
            return true;

        }
        //For simplicity and won't waste any time for NullException to be debugged
        //Every Exception simply be thrown 'False' as a result
        catch (Throwable ex){
            return false;
        }

    }


    /*
    This is wrapper method of the above "checkUdpWorker" method, it transform the below one
    into Asynchronous result. In our case it will modify the output to be Observable from Future
    */
    public static Observable<Boolean> checkUdpJ(String hostServer, int portNumber){

        //Create Observable from Future of above method
        return Observable.fromFuture(Executors.newSingleThreadExecutor().submit(()->checkUdpWorkerJ(hostServer, portNumber)));
    }


    /*
    This method will be used as worker method for check Online state
    through Http request
     */
    public static Boolean checkHttpWorkerJ(String hostServer){
        HttpURLConnection urlc = null;
        try {
            urlc = (HttpURLConnection) (new URL(hostServer).openConnection());
            urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(HTTP_TIMEOUT);
            urlc.connect();

            int responseCode = urlc.getResponseCode();
            if(responseCode == 200){

                return true;
            }
            else{
                //Avoid Leaked, even we've set Response Timeout
                urlc.disconnect();

                return false;
            }

        } catch (Throwable ex) {
            urlc.disconnect();
            return false;
        }finally {
            if(urlc != null){
                //Always close -> Avoid Leaked
                urlc.disconnect();
            }
        }
    }


    /*
    Wrapper method of above. For simplicity, It create Observable from Future that is
    executed from Executors
     */
    public static Observable<Boolean> checkHttpJ(String hostServer){
        //Create Observable from Future of above method
        return Observable.fromFuture(Executors.newSingleThreadExecutor().submit(()->checkHttpWorkerJ(hostServer)));
    }



}
