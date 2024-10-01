package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.DTOs.ContactDTO;
import com.bezkoder.springjwt.DTOs.IncomeDTO;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.jwt.AuthTokenFilter;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessengerService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    PostRepository postRepository;


    @Autowired
    private AuthTokenFilter authTokenFilter;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    WebSocketService webSocketService;


    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void createPost(Post post) throws Exception {
        post.setTime(LocalDateTime.now().format(formatter));

        postRepository.save(post);
    }

    public List<Post> getPosts() {
        return this.postRepository.findAll();
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + String.valueOf(postId)));
    }

    public Long extractIdFromRequest(HttpServletRequest rq) {
        String jwt = authTokenFilter.parseJwt(rq);
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        return getUserIdByUsername(username);
    }

    public String getUsernameById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(User::getUsername).orElse(null);
    }

    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return user.getId();
    }

    public ContactDTO findUserByUsername(Long sender, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return new ContactDTO(user.getId(), user.getUsername(), areFriends(user.getId(), sender));
    }

    public boolean areFriends(Long user1, Long user2) {
        return contactRepository.existsBySenderAndReceiver(user1, user2);
    };


    public void createRequest(Long receiver, Long sender) throws Exception {
        boolean requestExists = requestRepository.existsBySenderAndReceiver(sender, receiver);
        webSocketService.sendSpecific(String.valueOf(receiver), "requests");



        if (!requestExists) {
            requestRepository.save(new Request(receiver, sender));
        }
    }

    public List<ContactDTO> findRequestsByReceiver(Long receiver) {
        List<Request> requests = requestRepository.findAllByReceiver(receiver);

        List<ContactDTO> contactDTOs = requests.stream().map(request -> {
            User sender = userRepository.findById(request.getSender())
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + request.getSender()));

            // Determine if the sender and receiver are friends
            boolean isFriend = areFriends(request.getSender(), receiver);

            // Create and return a new ContactDTO
            return new ContactDTO(sender.getId(), sender.getUsername(), isFriend);
        }).collect(Collectors.toList());
        return contactDTOs;
    }

    public void deleteRequest(Long user1, Long user2) {
        boolean requestExists = requestRepository.existsBySenderAndReceiver(user2, user1);
        if (requestExists) {
            requestRepository.delete(requestRepository.findBySenderAndReceiver(user2, user1));
        }
    }


    public Long createMessage(Message message) throws Exception {
        message.setTime(LocalDateTime.now().format(formatter));

        webSocketService.sendSpecific(String.valueOf(message.getReceiver()), "messages");
        return messageRepository.save(message).getId();

    }

        public List<Message> getMessages(Long user1, Long user2) {
        List<Message> messagesBySender = messageRepository.findAllBySenderAndReceiver(user1, user2);
        List<Message> messagesByReceiver = messageRepository.findAllBySenderAndReceiver(user2, user1);

        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messagesBySender);
        allMessages.addAll(messagesByReceiver);


        return allMessages;
    }


    public void updateChat(Message message) throws Exception {
        boolean chatExists1 = chatRepository.existsBySenderAndReceiver(message.getSender(), message.getReceiver());
        boolean chatExists2 = chatRepository.existsBySenderAndReceiver(message.getReceiver(), message.getSender());


        if ((!chatExists1) && (!chatExists2)) {
            chatRepository.save(new Chat(message.getId(), message.getSender(), message.getReceiver()));

        } else {

            Optional<Chat> chat1 = chatRepository.findBySenderAndReceiver(message.getSender(), message.getReceiver());
            Optional<Chat> chat2 = chatRepository.findBySenderAndReceiver(message.getReceiver(), message.getSender());
            if (chat1.isPresent()) {
                Chat ch  = chat1.get();
                ch.setLastMessage(message.getId());
                chatRepository.save(ch);
            }
            if (chat2.isPresent()) {
                Chat ch  = chat2.get();
                ch.setLastMessage(message.getId());
                chatRepository.save(ch);
            }
        }
        webSocketService.sendSpecific(String.valueOf(message.getReceiver()), "chats");
        webSocketService.sendSpecific(String.valueOf(message.getSender()), "chats");


    }

    public List<IncomeDTO> getChats(Long user) {
        List<Chat> chatsBySender = chatRepository.findBySender(user);
        List<Chat> chatsByReceiver = chatRepository.findByReceiver(user);

        List<Chat> allChats = new ArrayList<>();
        allChats.addAll(chatsBySender);
        allChats.addAll(chatsByReceiver);


        List<IncomeDTO> incomeDTOS = allChats.stream().map(chat -> {

            Message msg = messageRepository.findById(chat.getLastMessage()).get();
            
            Long friendId;
            if (Objects.equals(user, chat.getSender())) {
                friendId = chat.getReceiver();
            } else  {
                friendId = chat.getSender();
            }

            User friend = userRepository.findById(friendId)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + user));

            return new IncomeDTO(friend.getId(), msg.getText(), msg.getTime(), friend.getUsername());
        }).collect(Collectors.toList());

        for (IncomeDTO x  : incomeDTOS) {
            System.out.println(x);

        }

        return incomeDTOS;
    }

//    handle if already exist
    public void createContact(Long user1, Long user2) throws Exception {
        webSocketService.sendSpecific(String.valueOf(user2), "contacts");
        webSocketService.sendSpecific(String.valueOf(user1), "contacts");
        if (!contactRepository.existsBySenderAndReceiver(user1, user2) && !contactRepository.existsBySenderAndReceiver(user2, user1))
        contactRepository.save(new Contact(user1, user2));
    }

    public List<ContactDTO> getContacts(Long user) {
        List<Contact> contactsByReceiver = contactRepository.findByReceiver(user);
        List<Contact> contactsBySender = contactRepository.findBySender(user);

        List<Contact> allContacts = new ArrayList<>();
        allContacts.addAll(contactsByReceiver);
        allContacts.addAll(contactsBySender);


        List<ContactDTO> contactDTOs = allContacts.stream().map(contact -> {
            Long friendId;
            if (user == contact.getSender()) {
                friendId = contact.getReceiver();
            } else  {
                friendId = contact.getSender();
            }

            User sender = userRepository.findById(friendId)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + contact.getSender()));

            return new ContactDTO(sender.getId(), sender.getUsername(), true);
        }).collect(Collectors.toList());

        return contactDTOs;
    }
}