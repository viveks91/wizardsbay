package com.example.helloworld.auth;

import com.example.helloworld.core.User;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import com.example.helloworld.db.UserDAO;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {

    private final UserDAO userDao;
    
    public ExampleAuthenticator(UserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        User user = userDao.retrieve(credentials.getUsername());
        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            return Optional.of(user);
        }
        return Optional.absent();
    }
}
