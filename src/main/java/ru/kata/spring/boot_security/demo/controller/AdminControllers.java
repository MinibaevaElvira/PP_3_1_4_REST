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
    public String adminPage(){return "admin";}


    @GetMapping("/{id}")
    public String userPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        return "/user";
    }

    @GetMapping("/users")
    public String showAllUsers (Model model) {
        model.addAttribute("users", userService.getAll());
        return "/adminPage/users";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("roles", roleService.getRoles());
        return "adminPage/new";
    }

    @PostMapping("/users")
    public String create(@ModelAttribute("user")@Valid User user, BindingResult bindingResult,
                         @ModelAttribute("userRoles") String[] userRoles) {

        if (bindingResult.hasErrors()) {
            return "/adminPage/new";
        }
        if (userRoles != null) {
            for(String role: userRoles) {
                user.addRoleToCollection(roleService.findByName(role));
            }
        }
        user.addRoleToCollection(roleService.findByName("ROLE_USER"));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("user", userService.findUserById(id));
        return "/adminPage/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                         @PathVariable("id") Long id,
                         @ModelAttribute("userRoles") String[] userRoles) {
        if (bindingResult.hasErrors()) {
            return "/adminPage/edit";
        }
        if (userRoles != null) {
            for(String role: userRoles) {
                user.addRoleToCollection(roleService.findByName(role));
            }
        }
        user.addRoleToCollection(roleService.findByName("ROLE_USER"));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id")  Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
