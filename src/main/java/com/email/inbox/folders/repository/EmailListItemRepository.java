package com.email.inbox.folders.repository;

import java.util.List;

import com.email.inbox.folders.folder.EmailListItem;
import com.email.inbox.folders.folder.EmailListItemKey;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailListItemRepository extends CassandraRepository<EmailListItem, EmailListItemKey> {

    public List<EmailListItem> findAllByKey_IdAndKey_Label(String id, String label);

}
