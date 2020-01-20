package house.Message;

public class Message {
    MessageType type;
    Object content;

    public Message(MessageType type, Object content) {
        this.type = type;
        this.content = content;
    }
}
