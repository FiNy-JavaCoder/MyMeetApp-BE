package MyApp.entity.repository;

import MyApp.entity.MessageEntity;
import MyApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId")
    List<MessageEntity> findByConversationId(@Param("conversationId") String conversationId);

}
