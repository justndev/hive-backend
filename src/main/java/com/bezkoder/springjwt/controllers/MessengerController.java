package com.bezkoder.springjwt.controllers;
import com.bezkoder.springjwt.DTOs.ContactDTO;
import com.bezkoder.springjwt.DTOs.IncomeDTO;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.services.MessengerService;
import com.bezkoder.springjwt.services.UniqueFilenameGenerator;
import com.bezkoder.springjwt.websocket.UserSessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
@RequestMapping("/api/messenger")
public class MessengerController {

    @Autowired
    private UserSessionRegistry userSessionRegistry;

    @Autowired
    MessengerService messengerService;

    UniqueFilenameGenerator uniqueFilenameGenerator;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/posts")
    public List<Post> getPosts() {
        return messengerService.getPosts();
    }

    @GetMapping("/post")
    public Post getPosts(@RequestParam("postId") Long postId) {
        return messengerService.getPost(postId);
    }

    @PostMapping("/post")
    public String uploadPost(HttpServletRequest rq, @RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("content") String content) {
        try {
            if (file.isEmpty()) {
                return "Please select a file to upload.";
            }

            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdir();
            }
            Long userId = messengerService.extractIdFromRequest(rq);
            String filename = UniqueFilenameGenerator.generateUniqueFilename(userId, file.getOriginalFilename() );

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + "/" + filename);
            Files.write(path, bytes);

            Post post = new Post();
            post.setCreatorId(userId);
            post.setCreatorUsername(messengerService.getUsernameById(userId));
            post.setTitle(title);
            post.setContent(content);
            post.setFilename(filename);
            messengerService.createPost(post);

            return "You successfully uploaded '" + file.getOriginalFilename() + "'";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload '" + file.getOriginalFilename() + "'";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/contact")
    public ContactDTO findContactByUsername(HttpServletRequest rq, @RequestParam String username) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        return messengerService.findUserByUsername(rqId, username);
    }

    @GetMapping("/contacts")
    public List<ContactDTO> getContacts(HttpServletRequest rq) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        return messengerService.getContacts(rqId);
    }

    @PostMapping("/request")
    public void sendFriendRequest(HttpServletRequest rq, @RequestParam Long requestedId) throws Exception {
        Long rqId = messengerService.extractIdFromRequest(rq);
        messengerService.createRequest(requestedId, rqId);
    }

    @GetMapping("/requests")
    public List<ContactDTO> getRequests(HttpServletRequest rq) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        return messengerService.findRequestsByReceiver(rqId);
    }

    @PostMapping("/request-accept")
    public void acceptRequest(HttpServletRequest rq, @RequestParam Long requestedId) throws Exception {
        Long rqId = messengerService.extractIdFromRequest(rq);

        messengerService.deleteRequest(rqId, requestedId);
        messengerService.createContact(rqId, requestedId);
    }

    @PostMapping("/request-deny")
    public void declineRequest(HttpServletRequest rq, @RequestParam Long requestedId) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        messengerService.deleteRequest(rqId, requestedId);
    }

    @PostMapping("/messages")
    public void sendMessage(HttpServletRequest rq, @Valid @RequestBody Message message) throws Exception {
        if (message.getReceiver() != 0) {
            Long rqId = messengerService.extractIdFromRequest(rq);
            message.setSender(rqId);
            Long id = messengerService.createMessage(message);
            message.setId(id);

            messengerService.updateChat(message);
        } else {
            System.out.println("Denied");
        }
    }

    @GetMapping("/messages")
    public List<Message> getMessages(HttpServletRequest rq, @RequestParam Long requestedId) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        return messengerService.getMessages(rqId, requestedId);
    }

    @GetMapping("/chats")
    public List<IncomeDTO> getChats(HttpServletRequest rq) {
        Long rqId = messengerService.extractIdFromRequest(rq);
        return messengerService.getChats(rqId);
    }
}