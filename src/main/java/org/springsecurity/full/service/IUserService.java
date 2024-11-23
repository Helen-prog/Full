package org.springsecurity.full.service;

import org.springsecurity.full.entity.Post;
import org.springsecurity.full.entity.User;

import java.util.List;

public interface IUserService {
    public User saveUser(User user);

    public void removeSessionMessage();

    public List<User> getUsers(String role);


}
