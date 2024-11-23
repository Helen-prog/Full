package org.springsecurity.full.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springsecurity.full.entity.Category;
import org.springsecurity.full.entity.Post;
import org.springsecurity.full.entity.User;
import org.springsecurity.full.repository.UserRepo;
import org.springsecurity.full.service.*;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepo userRepo;

    @ModelAttribute
    public void commonUser(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userRepo.findByEmail(email);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/")
    public String index(Model m, @RequestParam(value = "category", defaultValue = "") String category, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {

        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("paramValue", category);  // перенести снизу
        m.addAttribute("categories", categories);
//        List<Post> posts = postService.getAllSelectPosts(category);
//        m.addAttribute("posts", posts);
        Page<Post> page = postService.getAllPostPagination(pageNo, pageSize, category);
        List<Post> posts = page.getContent();
        m.addAttribute("posts", posts);
        m.addAttribute("postsSize", posts.size());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("TotalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/user/home")
    public String home() {
        return "home";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, HttpSession session) {
        User u = userService.saveUser(user);
        if (u != null) {
            session.setAttribute("msg", "Register successfully");
        } else {
            session.setAttribute("msg", "Something wrong server");
        }
        return "redirect:/register";
    }

    @GetMapping("/item/{id}")
    public String item(@PathVariable int id, Model m) {
        Post postById = postService.getPostById(id);
        m.addAttribute("post", postById);
        return "view_item";
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam String ch, Model m){
        List<Post> searchPosts = postService.searchPost(ch);
        m.addAttribute("posts", searchPosts);
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "index";
    }
}
