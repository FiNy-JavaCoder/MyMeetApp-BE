package MyApp.BE.event;

import MyApp.BE.dto.MessageDTO;
import org.springframework.context.ApplicationEvent;

public class MessageSentEvent extends ApplicationEvent {
    private final MessageDTO message;

    public MessageSentEvent(Object source, MessageDTO message) {
        super(source);
        this.message = message;
    }

    public MessageDTO getMessage() {
        return message;
    }
}