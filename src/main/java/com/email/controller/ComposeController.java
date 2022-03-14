package com.email.controller;

import com.email.inbox.email.EmailService;
import com.email.inbox.folders.FolderService;
import com.email.inbox.folders.folder.Folder;
import com.email.inbox.folders.repository.EmailRepository;
import com.email.inbox.folders.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    EmailService emailService;


    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to,
            @AuthenticationPrincipal OAuth2User principal, Model model){
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");

            List<Folder> userFolders = folderRepository.findAllById(userId);
            model.addAttribute("userFolders", userFolders);

            List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
            model.addAttribute("defaultFolders", defaultFolders);

            List<String> uniqueIds = splitIds(to);
            model.addAttribute("toIds",String.join(",",uniqueIds));
            model.addAttribute("stats",folderService.getLabelUnreadCount(userId));

            return "compose-page";
        }
    }

    private List<String> splitIds(String to) {
        if(!StringUtils.hasText(to)) {
            return new ArrayList<String>();
        }
          String[] toIds =  to.split(",");
           List<String> uniqueIds = Arrays.asList(toIds).stream().map(id->StringUtils.trimWhitespace(id))
                    .filter(id->StringUtils.hasText(id))
                    .distinct().collect(Collectors.toList());
           return uniqueIds;

    }

    @PostMapping(value = "/sendEmail")
    public ModelAndView sendEmail(
            @RequestBody MultiValueMap<String,String> formData,
            @AuthenticationPrincipal OAuth2User principal){

        String from = principal.getAttribute("login");
        List<String> toIds = splitIds(formData.getFirst("toIds"));
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");

        emailService.sendEmail(from,toIds,subject,body);

        return new ModelAndView("redirect:/");
    }

}
