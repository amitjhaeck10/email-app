package com.email.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.email.inbox.email.EmailService;
import com.email.inbox.folders.FolderService;
import com.email.inbox.folders.folder.EmailListItem;
import com.email.inbox.folders.folder.Folder;
import com.email.inbox.folders.folder.UnreadEmailStats;
import com.email.inbox.folders.repository.EmailListItemRepository;
import com.email.inbox.folders.repository.FolderRepository;
import com.email.inbox.folders.repository.UnreadEmailStatsRepository;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    EmailListItemRepository emailListItemRepository;

    @Autowired
    EmailService emailService;

    @GetMapping(value = "/")
    public String homePage(@RequestParam(required = false) String folder,
            @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");

            List<Folder> userFolders = folderRepository.findAllById(userId);
            model.addAttribute("userFolders", userFolders);

            List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
            model.addAttribute("defaultFolders", defaultFolders);

            if(!StringUtils.hasText(folder)){
                folder="Inbox";
            }
            List<EmailListItem> emailListItems = emailListItemRepository.findAllByKey_IdAndKey_Label(userId,folder);
            PrettyTime prettyTime = new PrettyTime();
            emailListItems.stream().forEach(emailItem->{
                   UUID timeUuid = emailItem.getKey().getTimeUUID();
                   Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
                   emailItem.setAgoTimeEmail(prettyTime.format(emailDateTime));
            });


            model.addAttribute("emailListItems",emailListItems);
            model.addAttribute("folderName",folder);
            Map<String,Integer> mapCountToLabel = folderService.getLabelUnreadCount(userId);
            System.out.println("mapCountToLabel = " + mapCountToLabel.toString());
            model.addAttribute("stats",mapCountToLabel);

            return "index-page";
        }
    }

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("amitjhaeck10", "Important", "blue"));
        folderRepository.save(new Folder("amitjhaeck10", "Personal", "blue"));
        folderRepository.save(new Folder("amitjhaeck10", "Work", "blue"));
        folderRepository.save(new Folder("amitjhaeck10", "Promotion", "blue"));

        for (int i = 0; i < 10; i++) {
            emailService.sendEmail("amitjhaeck10",Arrays.asList("amitjhaeck10","abc"),"Subject "+i,"Hello "+i);
        }
    }
}
