package com.email.controller;

import com.email.inbox.email.Email;
import com.email.inbox.folders.FolderService;
import com.email.inbox.folders.folder.EmailListItem;
import com.email.inbox.folders.folder.EmailListItemKey;
import com.email.inbox.folders.folder.Folder;
import com.email.inbox.folders.repository.EmailListItemRepository;
import com.email.inbox.folders.repository.EmailRepository;
import com.email.inbox.folders.repository.FolderRepository;
import com.email.inbox.folders.repository.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    EmailListItemRepository emailListItemRepository;

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/emails/{id}")
    public String homePage(@RequestParam String folder,
            @PathVariable UUID id,
            @AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");

            List<Folder> userFolders = folderRepository.findAllById(userId);
            model.addAttribute("userFolders", userFolders);

            List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
            model.addAttribute("defaultFolders", defaultFolders);

            Optional<Email> optionalEmail = emailRepository.findById(id);
            if(!optionalEmail.isPresent()){
                return "index-page";
            }
            Email email = optionalEmail.get();
            String toList = String.join(",",email.getTo());
            model.addAttribute("email", email);
            model.addAttribute("toList", toList);

            EmailListItemKey emailListItemKey = new EmailListItemKey();
            emailListItemKey.setId(userId);
            emailListItemKey.setLabel(folder);
            emailListItemKey.setTimeUUID(email.getId());

            Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(emailListItemKey);
            if(optionalEmailListItem.isPresent()){
                 EmailListItem item = optionalEmailListItem.get();
                 item.setRead(true);
                 emailListItemRepository.save(item);
                 unreadEmailStatsRepository.decrementCounter(userId,folder);
            }
            model.addAttribute("stats",folderService.getLabelUnreadCount(userId));
            return "email-page";
        }
    }
}
