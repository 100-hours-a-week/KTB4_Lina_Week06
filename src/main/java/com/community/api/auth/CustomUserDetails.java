package com.community.api.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.community.api.user.User;

import java.util.List;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User{

    private final Long userId;

    public CustomUserDetails(User user){
        super(user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = user.getUserId();
    }

    public Long getUserId(){
        return userId;
    }
}
