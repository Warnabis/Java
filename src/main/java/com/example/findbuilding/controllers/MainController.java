package com.example.findbuilding.controllers;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.Role;
import com.example.findbuilding.models.User;
import com.example.findbuilding.services.BuildingService;
import com.example.findbuilding.services.UserService;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {

    private final BuildingService buildingService;
    private final UserService userService;
    private static final String ADMIN_PASSWORD = "admin";

    public MainController(BuildingService buildingService, UserService userService) {
        this.buildingService = buildingService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
            && !auth.getName().equals("anonymousUser");
        String username = isAuthenticated ? auth.getName() : null;
        boolean isAdmin = false;
        List<Building> buildings;

        if (isAuthenticated) {
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                isAdmin = user.getRole() == Role.ADMIN;
                buildings = buildingService.getNearestBuildings(user.getId());
            } else {
                buildings = buildingService.getAllBuildings();
            }
        } else {
            buildings = buildingService.getAllBuildings();
        }

        model.addAttribute("buildings", buildings);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("username", username);
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "main";
    }

    @GetMapping("/registerMain")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/registerMain")
    public String register(@ModelAttribute User user,
                           @ModelAttribute("adminPassword") String adminPassword) {

        if (ADMIN_PASSWORD.equals(adminPassword)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        userService.registerUser(user);
        return "redirect:/";
    }

    @GetMapping("/top")
    public String getTopBuildings(@RequestParam(name = "native", required = false, defaultValue = "false") boolean useNativeQuery, Model model) {
        long startTime = System.nanoTime();

        List<Building> topBuildings = buildingService.getTopBuildings(useNativeQuery);
        model.addAttribute("buildings", topBuildings);
        model.addAttribute("useNative", useNativeQuery);

        long duration = (System.nanoTime() - startTime) / 1_000_000;
        log.info("Запрос топ-зданий выполнен за {} мс", duration);

        return "topBuildings";
    }


}
