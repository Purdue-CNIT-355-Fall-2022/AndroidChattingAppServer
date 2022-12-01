package data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Chat implements Serializable {
    private String chatRoomId;
    private String chatId;

    private User sender, receiver;
    private Date sendDate;
    private String msg;

    public Chat(String chatRoomId, String chatId, User sender, User receiver, Long dateLong, String msg) {
        this.chatRoomId = chatRoomId;
        this.chatId = chatId;
        this.sender = sender;
        this.receiver = receiver;

        this.sendDate = new Date(dateLong);
        this.msg = msg;
    }

    public Chat(String chatRoomId, User sender, User receiver, String msg) {
        this.chatRoomId = chatRoomId;
        this.chatId = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.sendDate = Calendar.getInstance().getTime();
        this.msg = msg;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getChatId() {
        return chatId;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return String.format(
                "%s,%s,%s,%s,%d,%s",
                chatRoomId, chatId, sender.getUserName(), receiver.getUserName(), sendDate.getTime(),  msg
        );
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        } else {
            Chat temp = (Chat) target;
            return this.getChatId().equals(temp.chatId);
        }
    }
}
