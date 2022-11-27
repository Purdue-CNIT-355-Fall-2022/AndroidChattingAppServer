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

        while (true) {
            var socket = mServerSocket.accept();
            createSession(socket)
        }

    }

    fun createSession(socket: Socket) {
        Thread {
            val database: LocalDatabase = LocalDatabase.getInstance()
            var sessionUser: User? = null
            try {
                // Open Input Output Stream
                val mWriter = ObjectOutputStream(socket.getOutputStream())
                val br = BufferedReader(InputStreamReader(socket.getInputStream()))

                while (true) {
                    var req: List<String> = br.readLine().split(",")
                    if (req[0] == "REQ_LOGIN") {
                        val tempUser = User(req[1])
                        val currentUsers = database.getAllUsers()
                        if (!currentUsers.contains(tempUser)) {
                            sessionUser = tempUser
                            // Send session info to client.
                            sendObject(sessionUser, mWriter)
                            break
                        } else {
                            // Send session info to client.
                            sendObject(sessionUser, mWriter)
                        }
                    } else if (req[0] == "ADD_U") {
                        // Add User
                        // Expected request sequence:
                        // ADD_U,userName
                        val newName = req[1]
                        val temp = database.findUser(newName)
                        if (temp == null) {
                            val newUser = User(newName)
                            database.addUser(newUser)
                            database.notifyAllChanges()
                        }
                    } else {
                        continue
                    }
                }

                while (sessionUser != null) {
                    if (!socket.isClosed) {
                        // Send session info to client.
                        sendObject(sessionUser, mWriter)
                    }

                    try {
                        // Handle user request.
                        val userRequest: Iterator<String> = br.lines().iterator()

                        while (userRequest.hasNext()) {
                            var currentReq: List<String> = userRequest.next().split(",")

                            try {
                                when (currentReq[0]) {
                                    // REQUEST USER; REQ_U
                                    "REQ_U" -> {
                                        val temp = database.findUser(currentReq[1])
                                        sendObject(temp, mWriter)
                                    }
                                    // Request all users.
                                    "REQ_AU" -> {
                                        val temp = database.getAllUsers()
                                        sendObject(temp, mWriter)
                                    }
                                    "REQ_CR" -> {
                                        val targetUser = database.findUser(currentReq[1])
                                        var result = targetUser?.let {
                                            database.getAllChatRoomByUser(it)
                                        }
                                        // If there is no associate chatroom, null will be returned.
                                        sendObject(result, mWriter)
                                    }
                                    "REQ_C" ->  {
                                        val result = database.getAllChatByChatRoom(currentReq[1])
                                        sendObject(result, mWriter)
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
                                    "EXIT" -> {
                                        database.notifyAllChanges()
                                        sessionUser = null
                                        socket.close()
                                    }
                                }
                            } catch (e: NullPointerException) {
                                Log.d("Session", "Unable to process request. Request will be consumed")
                            }
                        }

                    } catch (e: UncheckedIOException) {
                        Log.d("session", "Close Connection")
                        sessionUser = null
                        socket.close()
                        break
                    }
                }

            } catch (e: IOException) {
                Log.d("Session", e.message ?: "IOException thrown")
            }
        }
    }

    fun sendObject(target: Any?, writer: ObjectOutputStream): Boolean {
        return try {
            Log.d("sendObject", "Send Object")
            writer.reset()
            writer.writeObject(target)
            writer.flush()

            true
        } catch (e: IOException) {
            Log.d("sendObject", "Unable to process request.")
            false
        }
    }
}