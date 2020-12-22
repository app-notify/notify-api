package com.github.lbovolini.notify.controller;

import com.github.lbovolini.notify.model.User;
import com.github.lbovolini.notify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class UserController implements CrudController<User> {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @DeleteMapping("/users/{id}")
    public Mono<ResponseEntity<?>> delete(@PathVariable(name = "id") String id) {
        userService.delete(id);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Mono<User>> find(@PathVariable(name = "id") String id) {
        Mono<User> userMono = userService.find(id);
        return ResponseEntity.ok(userMono);
    }

    @GetMapping("/users")
    public ResponseEntity<Flux<User>> findAll() {
        Flux<User> userFlux = userService.findAll();
        return ResponseEntity.ok(userFlux);
    }

    @Override
    @PostMapping("/users")
    public Mono<ResponseEntity<String>> save(@Valid @RequestBody User user) {
        return userService.save(user)
                .map(userSaved -> new ResponseEntity<>(userSaved.getId(), HttpStatus.CREATED))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Override
    public Mono<ResponseEntity<String>> update(User user) {
        return userService.update(user)
                .map(userUpdated -> new ResponseEntity<String>(HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
