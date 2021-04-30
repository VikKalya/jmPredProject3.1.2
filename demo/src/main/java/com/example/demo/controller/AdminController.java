package com.example.demo.controller;


import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "redirect:/login";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') or hasAuthority('ADMIN,USER')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String listUsers(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("listUser", this.userService.allUsers());
        return "users";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') or hasAuthority('ADMIN,USER')")
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") User user, @RequestParam("role") String[] role) {
        Set<Role> roles = new HashSet<>();

        for(String str: role){
            roles.add(roleService.getRoleByName(str));
        }
        user.setRoles(roles);
        userService.add(user);
        return "redirect:/admin";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') or hasAuthority('ADMIN,USER')")
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam("role") String[] role) {
        Set<Role> roles = new HashSet<>();
        for(String str: role){
            roles.add(roleService.getRoleByName(str));
        }
        user.setRoles(roles);
        userService.edit(user);
        return "redirect:/admin";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') or hasAuthority('ADMIN,USER')")
    @PostMapping("/delete")
    public String removeUser(@RequestParam(name = "id") int id) {
        User user = userService.getById(id);
        this.userService.delete(user);
        return "redirect:/admin";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') or hasAuthority('ADMIN,USER')")
    @RequestMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "redirect:/admin";
    }
}
