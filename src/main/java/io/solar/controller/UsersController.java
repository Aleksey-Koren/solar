package io.solar.controller;

import io.solar.entity.User;
import io.solar.mapper.TotalMapper;
import io.solar.mapper.UserMapper;
import io.solar.utils.Page;
import io.solar.utils.QueryUtils;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import io.solar.utils.server.controller.RequestParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Controller
@RequestMapping("users")
public class UsersController {


    @RequestMapping
    public Page<User> getList(
            Pageable pageable,
            Transaction transaction,
            @AuthData User user,
            @RequestParam("login") String login,
            @RequestParam("title") String title
    ) {
        boolean canEdit = AuthController.userCan(user, "edit-user", transaction);
        if(!canEdit) {
            login = null;
        } else if("".equals(login)) {
            login = null;
        }
        if("".equals(title)) {
            title = null;
        }

        Query dataQuery = transaction.query("select * from users where 1 = 1 "
                + (login != null ? " and login like :login" : "")
                + (title != null ? " and title like :title" : "") + " limit :skip, :pageSize");

        Query countQuery = transaction.query("select count(1) from users where 1 = 1 "
                       + (login != null ? " and login like :login" : "")
                       + (title != null ? " and title like :title" : ""));
        if(login != null) {
            dataQuery.setString("login", login + "%");
            countQuery.setString("login", login + "%");
        }
        if(title != null) {
            dataQuery.setString("title", title + "%");
            countQuery.setString("title", title + "%");
        }
        QueryUtils.applyPagination(dataQuery, pageable);
        List<User> data = mapUsers(dataQuery.executeQuery(new UserMapper()), canEdit);

        long count = countQuery.executeQuery(new TotalMapper()).get(0);
        return new Page<>(data, count);
    }

    @RequestMapping("{id}")
    public User getOne(@PathVariable("id") Long id, Transaction transaction, @AuthData User user) {
        Query query = transaction.query("select * from users where id = :id");
        query.setLong("id", id);
        boolean canEdit = AuthController.userCan(user, "edit-user", transaction);
        List<User> users = mapUsers(query.executeQuery(new UserMapper()), canEdit);
        return users.size() == 1 ? users.get(0) : null;
    }

    @RequestMapping(method = "post")
    public User updateUser(@AuthData User user, @RequestBody User payload, Transaction transaction) {
        if(!((user.getId() != null && user.getId().equals(payload.getId())) || AuthController.userCan(user, "edit-user", transaction))) {
            throw new RuntimeException("no permissions");
        }
        if(payload.getId() == null) {
            throw new RuntimeException("bad request, id should be defined");
        }
        Query query = transaction.query("update users set title = :title where id = :id");
        query.setString("title", payload.getTitle());
        query.setLong("id", payload.getId());
        query.execute();

        query = transaction.query("select * from users where id = :id");
        query.setLong("id", payload.getId());

        return mapUsers(query.executeQuery(new UserMapper()), true).get(0);
    }

    private List<User> mapUsers(List<User> users, boolean canEdit) {
        for(User user : users) {
            if(user.getTitle() == null || user.getTitle().equals("")) {
                String login = user.getLogin();
                int index = login.indexOf("@");
                user.setTitle(index > -1 ? login.substring(0, user.getLogin().indexOf("@")) : login);
            }
            user.setPassword("");
            if(!canEdit) {
                user.setLogin("");
            }
        }
        return users;
    }

}
