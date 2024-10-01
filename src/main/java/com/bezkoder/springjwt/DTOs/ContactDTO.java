package com.bezkoder.springjwt.DTOs;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContactDTO {

    @Getter
    private Long id;
    @Getter
    private String username;
    @Getter
    private boolean isFriend;

    public ContactDTO(Long id, String username, boolean isFriend) {
        this.id = id;
        this.username = username;
        this.isFriend = isFriend;
    }

    public boolean isFriend() {
        return isFriend;
    }

}
