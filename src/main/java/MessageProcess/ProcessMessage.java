package MessageProcess;

public class ProcessMessage {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    User user;

    String message;

    public ProcessMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
