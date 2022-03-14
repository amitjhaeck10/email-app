package com.email.inbox.folders;

import com.email.inbox.folders.folder.Folder;
import com.email.inbox.folders.folder.UnreadEmailStats;
import com.email.inbox.folders.repository.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> getDefaultFolders(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent", "green"),
                new Folder(userId, "Draft", "red"),
                new Folder(userId, "Spam", "red"),
                new Folder(userId, "Trash", "red"));
    }

    public Map<String,Integer> getLabelUnreadCount(String userId){
        List<UnreadEmailStats> unreadEmailStats = unreadEmailStatsRepository.findAllById(userId);
        return unreadEmailStats.stream().
                collect(Collectors.toMap(UnreadEmailStats::getLabel,UnreadEmailStats::getUnreadCount));
    }
}
