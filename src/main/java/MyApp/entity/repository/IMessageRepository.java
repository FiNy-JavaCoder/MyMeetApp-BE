package MyApp.entity.repository;

import MyApp.entity.MessageEntity;
import MyApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByConversationId(String conversationId);
}
