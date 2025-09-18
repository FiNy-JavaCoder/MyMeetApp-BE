package MyApp.BE.entity.repository;

import MyApp.BE.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // Find all messages in a conversation
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.timeStamp ASC")
    List<MessageEntity> findByConversationId(@Param("conversationId") String conversationId);

    // Find messages between two users
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.msgSender.userId = :userId1 AND m.msgRecipient.userId = :userId2) OR " +
           "(m.msgSender.userId = :userId2 AND m.msgRecipient.userId = :userId1) " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                                  @Param("userId2") Long userId2);

    // Find recent messages for a user
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "m.msgSender.userId = :userId OR m.msgRecipient.userId = :userId " +
           "ORDER BY m.timeStamp DESC")
    List<MessageEntity> findRecentMessagesForUser(@Param("userId") Long userId);

    // Find unread messages for a user
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "m.msgRecipient.userId = :userId AND m.isRead = false " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findUnreadMessagesForUser(@Param("userId") Long userId);

    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE " +
           "m.msgRecipient.userId = :userId AND m.isRead = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);

    // Find messages in a conversation after a certain timestamp (for real-time updates)
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "m.conversationId = :conversationId AND m.timeStamp > :timestamp " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findNewMessagesInConversation(@Param("conversationId") String conversationId,
                                                       @Param("timestamp") OffsetDateTime timestamp);

    // Get last message in conversation
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId " +
           "ORDER BY m.timeStamp DESC LIMIT 1")
    Optional<MessageEntity> findLastMessageInConversation(@Param("conversationId") String conversationId);

    // Delete messages older than a certain date
    void deleteByTimeStampBefore(OffsetDateTime timestamp);
}