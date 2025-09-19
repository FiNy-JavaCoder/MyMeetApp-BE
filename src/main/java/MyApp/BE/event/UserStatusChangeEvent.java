package MyApp.BE.event;

import MyApp.BE.dto.UserStatusDTO;
import org.springframework.context.ApplicationEvent;

public class UserStatusChangeEvent extends ApplicationEvent {
    private final UserStatusDTO status;

    public UserStatusChangeEvent(Object source, UserStatusDTO status) {
        super(source);
        this.status = status;
    }

    public UserStatusDTO getStatus() {
        return status;
    }
}