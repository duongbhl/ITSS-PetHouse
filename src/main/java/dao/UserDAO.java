package dao;

import model.User;

public class UserDAO extends BaseDAO<User, Long> {
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
}
