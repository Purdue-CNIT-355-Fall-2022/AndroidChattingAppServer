package db

import data.Chat
import data.ChatRoom
import data.User
import util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class LocalDatabase {

    companion object {
        private var instance: LocalDatabase? = null
        fun getInstance(): LocalDatabase {
            if (instance == null) {
                instance = LocalDatabase()
                instance!!.initialize()
            }
            return instance!!
        }
    }

    private var userTable: ArrayList<User> = ArrayList<User>()
    private var chatRoomTable: ArrayList<ChatRoom> =ArrayList<ChatRoom>()
    private var chatTable: ArrayList<Chat> = ArrayList<Chat>()

    var initialized: Boolean = false


    fun initialize() {
        var bw: BufferedReader

        try {
            bw = BufferedReader(FileReader("USER.txt"))
            val tempIterator = bw.lines().iterator()

            while (tempIterator.hasNext()) {
                try {
                    val tempUser = User(tempIterator.next())
                    userTable.add(tempUser)
                } catch (e: NullPointerException) {
                    continue;
                }
            }
            bw.close()
        } catch (e2: IOException) {
            Log.d("Database", "Unable to load USER table.")
        }

        try {
            bw = BufferedReader(FileReader("CHAT_ROOM.txt"))
            val tempIterator = bw.lines().iterator()

            while (tempIterator.hasNext()) {
                try {
                    val temp: List<String> = tempIterator.next().split(",")
                    val tempChatroom = ChatRoom(temp[0], temp[1], User(temp[2]), User(temp[3]))
                    chatRoomTable.add(tempChatroom)
                } catch (e: NullPointerException) {
                    continue
                }
            }
            bw.close()
        } catch (e: IOException) {
            Log.d("Database", "Unable to load CHAT_ROOM table.")
        }

        try {
            bw = BufferedReader(FileReader("CHAT.txt"))
            val tempIterator = bw.lines().iterator()

            while (tempIterator.hasNext()) {
                try {
                    val temp: List<String> = tempIterator.next().split(",")
                    val tempChat = Chat(temp[0], temp[1], User(temp[2]), User(temp[3]), temp[4].toLong(), temp[5])
                    chatTable.add(tempChat)
                } catch (e: NullPointerException) {
                    continue
                }
            }
            bw.close()
        } catch (e: IOException) {
            Log.d("Database", "Unable to load CHAT table.")
        }

        initialized = true
    }

    fun findUser(name: String): User? {
        var temp: User? = null
        for (user in userTable) {
            if (user.userName == name) {
                temp = user
            }
        }
        return temp
    }

    fun getAllUsers(): ArrayList<User> {
        return userTable
    }

    fun getAllChatRoomByUser(user: User): ArrayList<ChatRoom> {
        val temp: ArrayList<ChatRoom> = ArrayList<ChatRoom>()
        for (chatRoom in chatRoomTable) {
            if (chatRoom.users.contains(user)) {
                temp.add(chatRoom)
            }
        }
        return temp
    }

    fun getAllChatByChatRoom(chatRoomId: String): ArrayList<Chat> {
        val temp: ArrayList<Chat> = ArrayList<Chat>()
        for (chat in chatTable) {
            if (chat.chatRoomId == chatRoomId) {
                temp.add(chat)
            }
        }
        return temp
    }

    fun addUser(user: User) {
        userTable.add(user)
    }

    fun addChatRoom(chatRoom: ChatRoom) {
        if (!chatRoomTable.contains(chatRoom)) {
            chatRoomTable.add(chatRoom)
        }
    }

    fun addChat(chat: Chat) {
        chatTable.add(chat)
    }

    fun notifyUserTableChanged() : Boolean {
        return try {
            val bw = BufferedWriter(FileWriter("USER.txt", false))
            val sb = StringBuilder()

            for (user in userTable) {
                sb.append(user.toString()).append("\n")
            }
            bw.write(sb.toString())
            bw.close()
            true
        } catch (e: IOException) {
            Log.d("Database", "Unable to notify User Table")
            false
        }
    }

    fun notifyChatRoomTableChanged() : Boolean {
        return try {
            val bw = BufferedWriter(FileWriter("CHAT_ROOM.txt", false))
            val sb = StringBuilder()

            for (chatRoom in chatRoomTable) {
                sb.append(chatRoom.toString()).append("\n")
            }
            bw.write(sb.toString())
            bw.close()
            true
        } catch (e: IOException) {
            Log.d("Database", "Unable to notify ChatRoom Table")
            false
        }
    }

    fun notifyChatTableChanged() : Boolean {
        return try {
            val bw = BufferedWriter(FileWriter("CHAT.txt", false))
            val sb = StringBuilder()

            for (chat in chatTable) {
                sb.append(chat.toString()).append("\n")
            }
            bw.write(sb.toString())
            bw.close()
            true
        } catch (e: IOException) {
            Log.d("Database", "Unable to notify Chat Table")
            false
        }
    }

    fun notifyAllChanges() : Boolean {
        if (!notifyUserTableChanged()) {
            return false
        }
        if (!notifyChatRoomTableChanged()) {
            return false
        }
        if (!notifyChatTableChanged()) {
            return false
        }
        return true
    }

}