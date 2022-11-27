package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ChatRoom implements Serializable {
    private String chatRoomId;
    private String chatroomName;
    private ArrayList<User> users = new ArrayList<>();

    public ChatRoom(String chatRoomId, String chatroomName, User user1, User user2) {
        this.chatRoomId = chatRoomId;
        this.chatroomName = chatroomName;
        users.add(user1);
        users.add(user2);
    }

    public ChatRoom(String chatroomName, User user1, User user2) {
        this.chatRoomId = UUID.randomUUID().toString();
        this.chatroomName = chatroomName;
        users.add(user1);
        users.add(user2);
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public void addUser(User user) {
        if (users.size() < 2) {
            users.add(user);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s,%s,%s,%s",
                chatRoomId, chatroomName, users.get(0).getUserName(), users.get(1).getUserName()
        );
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        } else {
            ChatRoom temp = (ChatRoom) target;
            return this.users.containsAll(temp.users);
        }
    }
}
