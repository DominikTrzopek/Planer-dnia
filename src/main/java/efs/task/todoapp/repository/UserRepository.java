package efs.task.todoapp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserRepository implements Repository<String, UserEntity> {

    Map<String,UserEntity> users = new HashMap<>();
    @Override
    public String save(UserEntity userEntity) {
        UserEntity previous =  users.putIfAbsent(userEntity.username,userEntity);
        if(previous != null)
        {
            return null;
        }
        return userEntity.username;
    }

    @Override
    public UserEntity query(String id) {
        return users.get(id);
    }

    @Override
    public List<UserEntity> query(Predicate<UserEntity> condition) {
        return users.values()
                .stream()
                .filter(condition)
                .collect(Collectors.toList());
    }


    @Override
    public UserEntity update(String id, UserEntity userEntity) {
        try {
            UserEntity previous = users.replace(id, userEntity);
        }
        catch (NullPointerException err) {
            return null;
        }
        return userEntity;
    }

    @Override
    public boolean delete(String id) {
        UserEntity removed = users.remove(id);
        return removed != null;
    }
}
