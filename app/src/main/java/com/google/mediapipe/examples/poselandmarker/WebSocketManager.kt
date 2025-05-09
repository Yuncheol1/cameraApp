package com.google.mediapipe.examples.poselandmarker

import android.util.Log
import okhttp3.*
class WebSocketManager(private val onConnected: (() -> Unit)? = null) {
    private val serverUrl =
        //"wss://driven-goldfish-needlessly.ngrok-free.app/ws/pose" //차명진
        //"wss://harmless-lacewing-freely.ngrok-free.app/ws/pose"  //나
        //"wss://whole-starfish-mutually.ngrok-free.app/ws/pose" //탁경헌
        "wss://fowl-one-definitely.ngrok-free.app/ws/pose" //고승현

        private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ 연결됨")
                onConnected?.invoke()

                // 🔥 추가된 부분: 최초 연결 시 서버에 등록 메시지 전송
                val registerMessage = """
                    {
                        "type": "register",
                        "userId": "user123",
                        "role": "mobile"
                    }
                """.trimIndent()

                webSocket.send(registerMessage)
                Log.d("WebSocket", "📤 등록 메시지 전송됨: $registerMessage")
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
