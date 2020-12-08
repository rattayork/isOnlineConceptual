package com.nristekk.apps.isonlineconcept.network

import android.util.Log
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import java.lang.Exception
import java.net.*
import java.util.concurrent.Callable


//Default UDP socket timeout, 3 seconds is much long enough these day.
private val UDP_SO_TIMEOUT = 3000;

//Default Http timeout after connected, we set it for 1.5 Seconds
private val HTTP_TIMEOUT = 1500;



/*
* This is the worker function, it describe element and process to connect
* the UDP socket in low-level
*/
fun checkUdpWorkerKt(hostServer:String, portNumber:Int): Boolean{
    val socket = DatagramSocket()

    try{

        val requestBytes = ByteArray(128)
        val receiveBytes = ByteArray(1024)

        val requestPacket = DatagramPacket(requestBytes, requestBytes.size)
        val receivePacket = DatagramPacket(receiveBytes, receiveBytes.size)
        val socketAddress = InetSocketAddress(hostServer, portNumber)

        //Set socket timeout for UDP protocol is mandate
        //Because UDP is connection less oriented protocol
        socket.soTimeout = UDP_SO_TIMEOUT
        socket.connect(socketAddress)

        socket.send(requestPacket)
        socket.receive(receivePacket)

        return true

    }catch (ex:Exception){
        return false

    }finally {
        socket?.let {
            //Close the socket if it was not closed yet
            if(socket.isConnected and !socket.isClosed)  it.close()
        }
    }



}


/*
* Wrapper function of above "checkUdpWorkerKt" function
* This function crate Result to be Observable from Callable for simplicity
*/
fun checkUdpKt(hostServer:String, portNumber:Int) : @NonNull Observable<Boolean> {

    //Create instance of Callable conventionally using
    //instance of Callable, Kotlin fashion means instance of class that implement Callable interface
    val called = Callable<Boolean>{ checkUdpWorkerKt(hostServer, portNumber) }

    return Observable.fromCallable{called.call()}

}



/*
* Worker function that do Http request in low-level
*/
fun checkHttpWorkerKt(hostServer:String):Boolean{
    var urlc:HttpURLConnection?=null

    try {
        urlc= URL(hostServer).openConnection() as HttpURLConnection
        urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36");
        urlc.setRequestProperty("Connection", "close");
        urlc.setConnectTimeout(HTTP_TIMEOUT);
        urlc.connect();

        val responseCode = urlc.getResponseCode();
        if(responseCode == 200){
            return true;
        }
        else{
            //Avoid Leaked, even we've set Response Timeout
            urlc?.let {
                urlc.disconnect()
            }
            return false;
        }

    }catch (ex:Throwable){
        return false
    }finally {
        urlc?.let {
            urlc.disconnect()
        }
    }

}


/*
* Wrapper function of above 'checkHttpWorkerKt' function
* For simplicity, it will create Observable from Callable of Above function
*/
fun checkHttpKt(hostServer:String) : @NonNull Observable<Boolean>{

    //in Kotlin we can create class instance implementing Callable interface easily
    val called = Callable<Boolean>{ checkHttpWorkerKt(hostServer) }

    return Observable.fromCallable { called.call() }
}








