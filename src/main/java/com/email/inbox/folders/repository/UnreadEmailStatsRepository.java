package com.email.inbox.folders.repository;

import com.email.inbox.folders.folder.EmailListItem;
import com.email.inbox.folders.folder.UnreadEmailStats;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats,String> {

    List<UnreadEmailStats> findAllById(String userId);

    @Query("update unread_email_stats set unreadCount=unreadCount+1 where id= ?0  and label= ?1")
    public void incrementCounter(String userId,String folder);

    @Query("update unread_email_stats set unreadCount=unreadCount-1 where id= ?0  and label= ?1")
    public void decrementCounter(String userId,String folder);
}
