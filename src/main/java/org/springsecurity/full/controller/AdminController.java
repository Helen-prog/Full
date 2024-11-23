package org.springsecurity.full.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springsecurity.full.entity.Category;
import org.springsecurity.full.entity.Post;
import org.springsecurity.full.entity.User;
import org.springsecurity.full.repository.UserRepo;
import org.springsecurity.full.service.ICategoryService;
import org.springsecurity.full.service.IPostService;
import org.springsecurity.full.service.IUserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IPostService postService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private IUserService userService;

    @ModelAttribute
    public void commonUser(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userRepo.findByEmail(email);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepo.findByEmail(email);
        model.addAttribute("user", user);
        return "admin/profile";
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddItem")
    public String loadAddItem(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_item";
    }

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, HttpSession session) {
        Boolean existCategory = categoryService.existCategory(category.getName());
        if (existCategory) {
            session.setAttribute("errorMsg", "Category Name already exists");
        } else {
            Category saveCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory)) {  // выполняет проверку null, а также проверку пробелов
                session.setAttribute("errorMsg", "Category Save Failed");
            } else {
                session.setAttribute("succMsg", "Saved Successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("succMsg", "Category Delete Successfully");
        } else {
            session.setAttribute("errorMsg", "Category Delete Failed");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, HttpSession session) {
        Category oldCategory = categoryService.getCategoryById(category.getId());
        if (oldCategory != null) {
            oldCategory.setName(category.getName());
        }
        Category updateCategory = categoryService.saveCategory(oldCategory);
        if (!ObjectUtils.isEmpty(updateCategory)) {
            session.setAttribute("succMsg", "Category Update Successfully");
        } else {
            session.setAttribute("errorMsg", "Category Update Failed");
        }
        return "redirect:/admin/category";
    }


    @PostMapping("/savePost")
    public String savePost(@ModelAttribute Post post, HttpSession session, @RequestParam("file") MultipartFile image) throws IOException { //,

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        post.setImage(imageName);

        Post savePost = postService.savePost(post);

        if (!ObjectUtils.isEmpty(savePost)) {
            String saveFile = new File("src/main/resources/static/img").getAbsolutePath();
            System.out.println(saveFile);

            if (!image.isEmpty()) {
                Path path = Paths.get(saveFile + File.separator + "post_img" + File.separator + image.getOriginalFilename());
                System.out.println(path);

                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            //Метод Files.copy() принимает два объекта Path (исходный и целевой) и опцию копирования StandardCopyOption.REPLACE_EXISTING, которая указывает, что если целевой файл уже существует, он должен быть заменен.

            session.setAttribute("succMsg", "Post Save Successfully");
        } else {
            session.setAttribute("errorMsg", "Post Save Failed");
        }
        return "redirect:/admin/loadAddItem";
    }

    @GetMapping("/items")
    public String loadViewPost(Model m, @RequestParam(defaultValue = "") String ch) {
        List<Post> posts = null;
        if(ch != null && ch.length() > 0) {
            posts = postService.searchPost(ch);
        } else{
            posts = postService.getAllPosts();
        }
        m.addAttribute("posts", posts);
        return "admin/items";
    }

    @GetMapping("/deleteItem/{id}")
    public String deleteItem(@PathVariable int id, HttpSession session) {
        Boolean deletePost = postService.deletePost(id);
        if (deletePost) {
            session.setAttribute("succMsg", "Product delete success");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/editItem/{id}")
    public String editItem(@PathVariable int id, Model m) {
        m.addAttribute("post", postService.getPostById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_items";
    }

    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute Post post, @RequestParam("file") MultipartFile image, HttpSession session, Model m) {

        Post updateProduct = postService.updatePost(post, image);
        if (!ObjectUtils.isEmpty(updateProduct)) {
            session.setAttribute("succMsg", "Post Update Successfully");
        } else {
            session.setAttribute("errorMsg", "Post Update Failed");
        }

        return "redirect:/admin/editItem/" + post.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m, @RequestParam Integer type) {
        List<User> users = null;
        if (type == 1) {
            users = userService.getUsers("ROLE_USER");
        } else {
            users = userService.getUsers("ROLE_ADMIN");
        }
        m.addAttribute("userType",type);
        m.addAttribute("users", users);
        return "/admin/users";
    }
}
