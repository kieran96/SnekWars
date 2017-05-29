package util;

/**
 * Created by Mitchell on 26-May-17.
 */
public class LoggingPacket {

    private String message;

    public LoggingPacket() {

    }
    public LoggingPacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public int hashCode() {
        return message.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof LoggingPacket) && (message.hashCode() == obj.hashCode()));
    }
}
