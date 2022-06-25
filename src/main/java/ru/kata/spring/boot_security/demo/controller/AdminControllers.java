package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;


@Controller
@RequestMapping("/admin")
public class AdminControllers {
    private UserService userService;
    private RoleService roleService;
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String adminPage(Model model, Principal principal) {
        User user = userService.findByName(principal.getName());
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("user", user);
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("newUser")@Valid User user, BindingResult bindingResult,
                         @ModelAttribute("userRoles") Long [] userRoles) {

        if (bindingResult.hasErrors()) {
            return "admin";
        }
        if (userRoles != null) {
            for(Long role: userRoles) {
                user.addRoleToCollection(roleService.findRoleById(role));
            }
        } else {
            user.addRoleToCollection(roleService.findRoleById(2L));
        }

        userService.saveUser(user);
        return "redirect:/admin";
    }


    @PatchMapping("/{id}")
    public String update(@ModelAttribute("newUser") @Valid User user, BindingResult bindingResult,
                         @ModelAttribute("userRoles") Long[] userRoles) {
        if (bindingResult.hasErrors()) {
            return "/admin";
        }
        if (userRoles != null) {
            for(Long role: userRoles) {
                user.addRoleToCollection(roleService.findRoleById(role));
            }
        }else {
            user.addRoleToCollection(roleService.findRoleById(2L));
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id")  Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
