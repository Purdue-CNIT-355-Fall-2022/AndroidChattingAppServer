import data.Chat
import data.ChatRoom
import data.User
import db.LocalDatabase
import util.Log
import java.io.*
import java.lang.NullPointerException
import java.net.ServerSocket
import java.net.Socket

/**
 * Handles Server Connection.
 */
class ServerConnection {

    fun openServer(portNo: Int) {
        // Open ServerSocket for incoming connection
        val mServerSocket: ServerSocket = ServerSocket(portNo)
        Log.d("ServerConnection", "Open server on port: $portNo")

        while (true) {
            Log.d("ServerConnection", "Accepting port on: $portNo")
            var socket = mServerSocket.accept();
            createSession(socket)
        }

    }

    fun createSession(socket: Socket) {
        Log.d("Session", "Session Catched");
        Thread {
            val database: LocalDatabase = LocalDatabase.getInstance()
            var sessionUser: User? = null
            try {
                // Open Input Output Stream
                val mWriter = ObjectOutputStream(socket.getOutputStream())
                val br = BufferedReader(InputStreamReader(socket.getInputStream()))

                Log.d("Session", "Closed? ${socket.isConnected}")
                try {
                    // Handle user request.
                    // val userRequest: Iterator<String> = br.lines().iterator()
                    val userReq = br.readLine()

                    if (userReq != null) {
                        var currentReq: List<String> = userReq.split(",")
                        Log.d("Request", "Req: ${currentReq ?: "NULL"}")

                        try {
                            when (currentReq[0]) {
                                "REQ_LOGIN" -> {
                                    val tempUser = User(currentReq[1])
                                    val currentUsers = database.getAllUsers()
                                    if (currentUsers.contains(tempUser)) {
                                        println("Not in user pool")
                                        sessionUser = tempUser
                                        // Send session info to client.
                                        mWriter.reset()
                                        mWriter.writeObject(sessionUser)
                                        mWriter.flush()
                                    }
                                }
                                "ADD_U" -> {
                                    // Add User
                                    // Expected request sequence:
                                    // ADD_U,userName
                                    val newName = currentReq[1]
                                    val temp = database.findUser(newName)
                                    if (temp == null) {
                                        val newUser = User(newName)
                                        mWriter.reset()
                                        mWriter.writeObject(newUser)
                                        mWriter.flush()
                                        database.notifyAllChanges()
                                    }
                                }
                                // REQUEST USER; REQ_U
                                "REQ_U" -> {
                                    val temp = database.findUser(currentReq[1])
                                    mWriter.reset()
                                    mWriter.writeObject(temp)
                                    mWriter.flush()
                                }
                                // Request all users.
                                "REQ_AU" -> {
                                    val temp = database.getAllUsers()
                                    mWriter.reset()
                                    mWriter.writeObject(temp)
                                    mWriter.flush()
                                }
                                "REQ_CR" -> {
                                    val targetUser = database.findUser(currentReq[1])
                                    var result = targetUser?.let {
                                        database.getAllChatRoomByUser(it)
                                    }
                                    // If there is no associate chatroom, null will be returned.
                                    mWriter.reset()
                                    mWriter.writeObject(result)
                                    mWriter.flush()
                                }
                                "REQ_C" ->  {
                                    val result = database.getAllChatByChatRoom(currentReq[1])
                                    mWriter.reset()
                                    mWriter.writeObject(result)
                                    mWriter.flush()
                                }
                                "ADD_CR" -> {
                                    // Add new chatRoom
                                    // Expected Request Sequence
                                    // ADD_CR,chatRoomName,userName1,userName2
                                    // userName1 would be the session user.
                                    val user1 = database.findUser(currentReq[2])
                                    val user2 = database.findUser(currentReq[3])
                                    val newChatRoom = ChatRoom(currentReq[1], user1, user2)
                                    database.notifyAllChanges()
                                }
                                "ADD_C" -> {
                                    // Add new chat
                                    // ADD_C,chatRoomId,userName1,username2,meg
                                    val sender = database.findUser(currentReq[2])
                                    val receiver = database.findUser(currentReq[3])
                                    val newChat = Chat(currentReq[1], sender, receiver, currentReq[4])
                                    database.notifyAllChanges()
                                }
                            }
                        } catch (e: NullPointerException) {
                            Log.d("Session", "Unable to process request. Request will be consumed")
                        }
                        database.notifyAllChanges()
                        sessionUser = null

                        mWriter.close()
                        br.close()

                        socket.close()
                    }

                } catch (e: UncheckedIOException) {
                    Log.d("session", "Close Connection")
                    sessionUser = null
                    socket.close()
                }

            } catch (e: IOException) {
                Log.d("Session", e.message ?: "IOException thrown")
            }

        }.start();
        Log.d("Session", "Session End")

    }
}