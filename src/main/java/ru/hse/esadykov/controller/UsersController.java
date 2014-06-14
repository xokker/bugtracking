package ru.hse.esadykov.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.hse.esadykov.dao.UserDao;
import ru.hse.esadykov.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Ernest Sadykov
 * @since 31.05.2014
 */
@Controller
public class UsersController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    protected String doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;

        int userId = Integer.parseInt(req.getParameter("user_id"));
        try {
            userDao.deleteUser(userId);
            message = "User #" + userId + " successfully deleted";
        } catch (SQLException e) {
            message = "Error deleting user #" + userId;
            e.printStackTrace();
        }
        req.setAttribute("message", message);

        return "redirect:/users";
    }

    @RequestMapping(value = "/users/update/{id}", method = RequestMethod.POST)
    protected ModelAndView updateUser(
                                      @RequestParam(value = "fullName", required = false) String fullName,
                                      @RequestParam(value = "email", required = false) String email,
                                      @RequestParam(value = "password") String password,
                                      @PathVariable("id") String id, HttpServletResponse resp) throws ServletException, IOException {
        int userId;
        ModelMap mm = new ModelMap();
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        String encodedPassword = StringUtils.isNotBlank(password) ? passwordEncoder.encode(password) : null;
        User user = new User(userId, null, fullName, email, encodedPassword);

        try {
            userDao.updateUser(user);
            user = userDao.getUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mm.addAttribute("user", user);
        return new ModelAndView("user", mm);
    }

    @RequestMapping(value = "/users/update/{id}", method = RequestMethod.GET)
    protected ModelAndView showUser(HttpServletResponse resp, @PathVariable("id") String id) throws ServletException, IOException {
        int userId;
        ModelMap mm = new ModelMap();
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        try {
            User user = userDao.getUser(userId);
            mm.addAttribute("user", user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ModelAndView("user", mm);
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    protected String doPut(@RequestParam(value = "username") String username,
                           @RequestParam(value = "full_name", required = false) String fullName,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "password") String password,
                           Model model) throws ServletException, IOException {
        String message;

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, fullName, email, encodedPassword);

        try {
            userDao.saveUser(user);
            message = "User " + username + " successfully created";
        } catch (SQLException e) {
            message = "Error creating user " + username;
            e.printStackTrace();
        }
        model.addAttribute("message", message);

        return "redirect:/users";
    }

    @RequestMapping(value = "/users")
    protected String doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = null;
        try {
            users = userDao.getUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("users", users);

        return "users";
    }
}
