package MyApp.BE.entity.repository;

import MyApp.BE.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * Find all messages in a conversation ordered by timestamp
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId " +
           "AND ((m.msgSender.userId = :userId AND m.isDeletedBySender = false) " +
           "OR (m.msgRecipient.userId = :userId AND m.isDeletedByRecipient = false)) " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findByConversationIdAndUserId(@Param("conversationId") String conversationId, 
                                                     @Param("userId") Long userId);

    /**
     * Find all messages in a conversation ordered by timestamp
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.timeStamp ASC")
    List<MessageEntity> findByConversationId(@Param("conversationId") String conversationId);

    /**
     * Find all messages in a conversation ordered by timestamp descending (newest first)
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.timeStamp DESC")
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
    @Query("DELETE FROM MessageEntity m WHERE m.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * Find distinct conversation IDs for a user (as sender or recipient)
     */
    @Query("SELECT DISTINCT m.conversationId FROM MessageEntity m WHERE " +
           "(m.msgSender.userId = :userId AND m.isDeletedBySender = false) OR " +
           "(m.msgRecipient.userId = :userId AND m.isDeletedByRecipient = false)")
    List<String> findDistinctConversationIdsByUserId(@Param("userId") Long userId);

    /**
     * Find latest message in each conversation for a user
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId IN " +
           "(SELECT DISTINCT m2.conversationId FROM MessageEntity m2 WHERE " +
           "(m2.msgSender.userId = :userId AND m2.isDeletedBySender = false) OR " +
           "(m2.msgRecipient.userId = :userId AND m2.isDeletedByRecipient = false)) " +
           "AND m.timeStamp = (SELECT MAX(m3.timeStamp) FROM MessageEntity m3 WHERE m3.conversationId = m.conversationId) " +
           "ORDER BY m.timeStamp DESC")
    List<MessageEntity> findRecentMessagesForUser(@Param("userId") Long userId);

    /**
     * Find messages between two specific users
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.msgSender.userId = :user1Id AND m.msgRecipient.userId = :user2Id) OR " +
           "(m.msgSender.userId = :user2Id AND m.msgRecipient.userId = :user1Id) " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findMessagesBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find unread messages for a user
     */
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.msgRecipient.userId = :userId AND m.isRead = false " +
           "AND m.isDeletedByRecipient = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);

    /**
     * Get message count between two users
     */
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE " +
           "(m.msgSender.userId = :user1Id AND m.msgRecipient.userId = :user2Id) OR " +
           "(m.msgSender.userId = :user2Id AND m.msgRecipient.userId = :user1Id)")
    long countMessagesBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find new messages in conversation since timestamp
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId " +
           "AND m.timeStamp > :timestamp " +
           "AND ((m.msgSender.userId = :userId AND m.isDeletedBySender = false) " +
           "OR (m.msgRecipient.userId = :userId AND m.isDeletedByRecipient = false)) " +
           "ORDER BY m.timeStamp ASC")
    List<MessageEntity> findNewMessagesInConversation(@Param("conversationId") String conversationId,
                                                     @Param("timestamp") OffsetDateTime timestamp,
                                                     @Param("userId") Long userId);

    /**
     * Mark messages as read in conversation
     */
    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = true WHERE m.conversationId = :conversationId " +
           "AND m.msgRecipient.userId = :userId AND m.isRead = false")
    int markMessagesAsReadInConversation(@Param("conversationId") String conversationId, 
                                       @Param("userId") Long userId);


    /**
     * Find paginated messages for conversation
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId " +
           "AND ((m.msgSender.userId = :userId AND m.isDeletedBySender = false) " +
           "OR (m.msgRecipient.userId = :userId AND m.isDeletedByRecipient = false)) " +
           "ORDER BY m.timeStamp DESC")
    Page<MessageEntity> findByConversationIdAndUserIdPaged(@Param("conversationId") String conversationId,
                                                          @Param("userId") Long userId,
                                                          Pageable pageable);


    /**
     * Find latest message in each conversation for a user
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.messageId IN (" +
           "SELECT MAX(m2.messageId) FROM MessageEntity m2 WHERE " +
           "(m2.msgSender.userId = :userId OR m2.msgRecipient.userId = :userId) " +
           "GROUP BY m2.conversationId) " +
           "ORDER BY m.timeStamp DESC")
    List<MessageEntity> findLatestMessagesByUserId(@Param("userId") Long userId);

}