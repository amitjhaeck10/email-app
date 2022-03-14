package com.email.inbox.email;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.email.inbox.folders.folder.EmailListItem;
import com.email.inbox.folders.folder.EmailListItemKey;
import com.email.inbox.folders.repository.EmailListItemRepository;
import com.email.inbox.folders.repository.EmailRepository;
import com.email.inbox.folders.repository.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    EmailListItemRepository emailListItemRepository;

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> toIds,String subject,String body){

        Email email = new Email();
        email.setTo(toIds);
        email.setFrom(from);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());

        emailRepository.save(email);

        toIds.forEach(to->{
            EmailListItem emailListItem = getEmailListItem(toIds, subject, email, to,"Inbox");
            emailListItemRepository.save(emailListItem);
            unreadEmailStatsRepository.incrementCounter(to,"Inbox");
        });

        EmailListItem emailListItem = getEmailListItem(toIds, subject, email, from,"Sent");
        emailListItem.setRead(true);
        emailListItemRepository.save(emailListItem);
    }

    private EmailListItem getEmailListItem(List<String> toIds, String subject, Email email, String itemOwner,String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner);
        key.setTimeUUID(email.getId());
        key.setLabel(folder);

        EmailListItem emailListItem = new EmailListItem();
        emailListItem.setKey(key);

        emailListItem.setSubject(subject);
        emailListItem.setTo(toIds);
        emailListItem.setRead(false);

        return emailListItem;
    }


}
