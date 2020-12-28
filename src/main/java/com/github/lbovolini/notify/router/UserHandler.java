package com.github.lbovolini.notify.router;

import com.github.lbovolini.notify.model.User;
import com.github.lbovolini.notify.service.UserService;
import com.github.lbovolini.notify.validation.AbstractValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

@Component
public class UserHandler extends AbstractValidationHandler<User, Validator> {

    private final UserService userService;

    @Autowired
    public UserHandler(Validator validator, UserService userService) {
        super(User.class, validator);
        this.userService = userService;
    }

    public Mono<ServerResponse> save(ServerRequest request) {

        Mono<User> userMono = request.bodyToMono(User.class).doOnNext(this::validate);

        return userMono.flatMap(user -> {
            Mono<User> savedUserMono = userService.save(user);
            return ServerResponse.ok().build();
        });
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().body(fromPublisher(userService.findAll(), User.class));
    }

}
