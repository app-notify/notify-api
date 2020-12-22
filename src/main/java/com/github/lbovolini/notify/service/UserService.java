package com.github.lbovolini.notify.service;

import com.github.lbovolini.notify.model.User;
import com.github.lbovolini.notify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService implements CrudService<User> {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<?> delete(String id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> find(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> update(User user) {
        return userRepository.save(user);
    }
}
