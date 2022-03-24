package uz.gxteam.webSocet

import android.util.Log
import okhttp3.*
import okio.ByteString
import uz.gxteam.variantapp.iterceptor.MySharedPreference
import java.util.concurrent.TimeUnit


class WebSocketEchoApp (): WebSocketListener() {
    private var mWebSocketEcho: WebSocketEchoApp? = null
    private var mWebSocketInteractor: WebSocketInteractor? = null
    var mySharedPreference: MySharedPreference?=null

    fun getInstance(webSocketInteractor: WebSocketInteractor): WebSocketEchoApp? {
        if (mWebSocketEcho == null) {
            mWebSocketInteractor = webSocketInteractor
            mWebSocketEcho = WebSocketEchoApp()
            mWebSocketEcho!!.run()
        }
        return mWebSocketEcho
    }


    private fun run() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(50000, TimeUnit.MILLISECONDS)
            .build()

        val request: Request = Request.Builder()
            .url("ws://web.variantgroup.uz:6001/app/mykey?protocol=7&client=js&version=7.0.6&flash=false")
            .build()

        request.header("Authorization ${mySharedPreference?.tokenType} ${mySharedPreference?.accessToken}")
        request.header("Accept application/json")

        client.newWebSocket(request, this)

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.

        client.dispatcher.executorService.shutdown()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        mWebSocketInteractor?.onOpen(webSocket)
        Log.e("Socket Open", "onOpen: ", )
        /*webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(1000, "Goodbye, World!");*/
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        println("MESSAGE: $text")
        mWebSocketInteractor?.onGetMessage(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        println("MESSAGE: " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(1000, null)
        println("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        t.printStackTrace()
    }

    interface WebSocketInteractor {
        fun onOpen(webSocket: WebSocket?)
        fun onGetMessage(message: String?)
    }
}