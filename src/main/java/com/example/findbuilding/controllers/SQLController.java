package com.example.findbuilding.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

    @Controller
    @RequestMapping("/sql")
    public class SQLController {
        private final JdbcTemplate jdbcTemplate;

        public SQLController(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @GetMapping
        public String showQueryPage() {
            return "sql_query";
        }

        @PostMapping
        public String executeSQL(@RequestParam String query, Model model) {
            try {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
                model.addAttribute("result", result.toString());
            } catch (Exception e) {
                model.addAttribute("result", "Ошибка: " + e.getMessage());
            }
            return "sql_query";
        }
    }
