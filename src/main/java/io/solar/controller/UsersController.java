package io.solar.controller;

import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.utils.QueryUtils;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import io.solar.utils.server.controller.Controller;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("users")
public class UsersController {


    @RequestMapping
    public List<User> getList(Pageable pageable, Transaction transaction) {

        Query query = transaction.query("select * from users limit :skip, :pageSize");
        QueryUtils.applyPagination(query, pageable);
        return mapUsers(query.executeQuery(new UserMapper()));
    }

    @RequestMapping(method = "post")
    public User updateUser(@AuthData User user, @RequestBody User payload, Transaction transaction) {
        if(!((user.getId() != null && user.getId().equals(payload.getId())) || AuthController.userCan(user, "edit-user"))) {
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

        return mapUsers(query.executeQuery(new UserMapper())).get(0);
    }

    private List<User> mapUsers(List<User> users) {
        for(User user : users) {
            if(user.getTitle() == null || user.getTitle().equals("")) {
                String login = user.getLogin();
                int index = login.indexOf("@");
                user.setTitle(index > -1 ? login.substring(0, user.getLogin().indexOf("@")) : login);
            }
            user.setPassword("");
            user.setLogin("");
        }
        return users;
    }

}
