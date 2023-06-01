package com.concurrency.seckill.utils;

import com.concurrency.seckill.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private final ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
