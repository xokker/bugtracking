package ru.hse.esadykov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.hse.esadykov.dao.BugDao;
import ru.hse.esadykov.dao.CommentDao;
import ru.hse.esadykov.dao.UserDao;
import ru.hse.esadykov.model.Bug;
import ru.hse.esadykov.model.BugStatus;
import ru.hse.esadykov.model.Comment;
import ru.hse.esadykov.model.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Ernest Sadykov
 * @since 31.05.2014
 */
@Controller
public class BugController {

    @Autowired
    private BugDao bugDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/bug/{id}", method = RequestMethod.POST)
    protected ModelAndView doPost(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "body") String body,
                                  ModelMap model,
                                  HttpServletResponse response,
                                  @PathVariable("id") String id) throws IOException {
        int bugId;
        try {
            bugId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        Comment comment = new Comment();
        User user = new User();
        user.setUsername(username);
        comment.setAuthor(user);
        comment.setBody(body);
        comment.setBugId(bugId);
        try {
            commentDao.saveComment(comment);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doGet(model, response, id);
    }

    @RequestMapping(value = "/bug/{id}", method = RequestMethod.GET)
    protected ModelAndView doGet(ModelMap model,
                           HttpServletResponse response,
                           @PathVariable("id") String id) throws IOException {
        int bugId;
        try {
            bugId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        try {
            Bug bug = bugDao.getBug(bugId);
            if (bug == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            User responsible = userDao.getUser(bug.getResponsibleId());
            bug.setResponsible(responsible);
            User creator = userDao.getUser(bug.getCreatorId());
            bug.setCreator(creator);
            model.addAttribute("bug", bug);
            List<Comment> comments = commentDao.getComments(bugId);
            model.addAttribute("comments", comments);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ModelAndView("bug", model);
    }

    @RequestMapping(value = "/bug/{id}/close", method = RequestMethod.POST)
    protected String doClose(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        int bugId;
        try {
            bugId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        bugDao.setStatus(bugId, BugStatus.CLOSED);

        return "redirect:/bugs";
    }
}
