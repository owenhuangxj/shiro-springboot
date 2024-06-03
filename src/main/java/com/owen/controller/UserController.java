package com.owen.controller;

import com.owen.entity.User;
import com.owen.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{name}")
    public User getUser(@PathVariable("name") String name) {
        return userService.findUserByName(name);
    }

    @PostMapping("/login")
    public String login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
        } catch (UnknownAccountException exception) {
            return "Username wrong!!!";
        } catch (IncorrectCredentialsException exception) {
            return "Password wrong!!!";
        } catch (AuthenticationException exception) {
            return "Don't know...!!!";
        }
        return "SUCCESS";
    }
}
