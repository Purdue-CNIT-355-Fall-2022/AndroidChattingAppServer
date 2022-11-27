package data;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    // Getter & Setter
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            User temp = (User) obj;
            return this.userName.equals(temp.userName);
        }
    }
}
