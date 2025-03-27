package com.google.mediapipe.examples.poselandmarker

import android.util.Log
import okhttp3.*

class WebSocketManager(private val onConnected: (() -> Unit)? = null) {

    private val serverUrl = "ws://172.30.1.29:8080/ws/pose"  // 너의 서버 IP
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ 연결됨")
                onConnected?.invoke()  // 👈 연결 후 콜백 실행
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "📩 메시지 받음: $text")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "❌ 연결 종료: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "⚠️ 에러: ${t.message}", t)
            }
        })
    }

    fun sendMessage(message: String) {
        if (::webSocket.isInitialized) {
            val isSuccess = webSocket.send(message)
            Log.d("WebSocket", "📤 메시지 전송 성공 여부: $isSuccess")
        } else {
            Log.w("WebSocket", "WebSocket이 아직 초기화되지 않음")
        }
    }

    fun disconnect() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "연결 종료")
        }
    }
}
