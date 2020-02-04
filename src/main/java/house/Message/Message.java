package house.Message;

import house.TimeManager;

public class Message {
    MessageType type;
    Object content;
    long timestamp;


    public Message(MessageType type, Object content) {
        this.type = type;
        this.content = content;
        this.timestamp= TimeManager.getTime();
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content=" + content +
                '}';
    }
}
