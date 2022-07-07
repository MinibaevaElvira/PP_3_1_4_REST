package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;


@Controller
public class UserControllers {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public String userPage(Principal principal, Model model) {
        User user = userService.findByName(principal.getName());
        model.addAttribute("user",user);
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage(Principal principal, Model model) {
        User user = userService.findByName(principal.getName());
        model.addAttribute("user",user);
        return "admin";
    }
}
