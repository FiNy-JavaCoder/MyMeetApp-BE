package MyApp.BE.entity.repository;

import MyApp.BE.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * Find all messages in a conversation ordered by timestamp
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId ORDER BY m.timeStamp ASC")
    List<MessageEntity> findByConversationId(@Param("conversationId") String conversationId);

    /**
     * Find all messages in a conversation ordered by timestamp descending (newest first)
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId ORDER BY m.timeStamp DESC")
    List<MessageEntity> findByConversationIdOrderByTimeStampDesc(@Param("conversationId") String conversationId);

    /**
     * Check if conversation exists
     */
    boolean existsByConversationId(String conversationId);

    /**
     * Count messages in conversation
     */
    long countByConversationId(String conversationId);

    /**
     * Delete all messages in conversation
     */
    @Modifying
    @Query("DELETE FROM Message m WHERE m.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * Find distinct conversation IDs for a user (as sender or recipient)
     */
    @Query("SELECT DISTINCT m.conversationId FROM Message m WHERE m.msgSender.userId = :userId OR m.msgRecipient.userId = :userId")
    List<String> findDistinctConversationIdsByUserId(@Param("userId") Long userId);

    /**
     * Find latest message in each conversation for a user
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId IN " +
           "(SELECT DISTINCT m2.conversationId FROM Message m2 WHERE m2.msgSender.userId = :userId OR m2.msgRecipient.userId = :userId) " +
           "AND m.timeStamp = (SELECT MAX(m3.timeStamp) FROM Message m3 WHERE m3.conversationId = m.conversationId)")
    List<MessageEntity> findLatestMessagesByUserId(@Param("userId") Long userId);

    /**
     * Find messages between two specific users
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.msgSender.userId = :user1Id AND m.msgRecipient.userId = :user2Id) OR " +
           "(m.msgSender.userId = :user2Id AND m.msgRecipient.userId = :user1Id) " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findMessagesBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find unread messages for a user (this would require additional field in MessageEntity)
     */
    @Query("SELECT m FROM Message m WHERE m.msgRecipient.userId = :userId ORDER BY m.timeStamp DESC")
    List<MessageEntity> findUnreadMessagesByRecipient(@Param("userId") Long userId);

    /**
     * Get message count between two users
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
           "(m.msgSender.userId = :user1Id AND m.msgRecipient.userId = :user2Id) OR " +
           "(m.msgSender.userId = :user2Id AND m.msgRecipient.userId = :user1Id)")
    long countMessagesBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}