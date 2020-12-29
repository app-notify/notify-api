package com.github.lbovolini.notify.router;

import com.github.lbovolini.notify.model.User;
import com.github.lbovolini.notify.service.UserService;
import com.github.lbovolini.notify.validation.AbstractValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class UserHandler extends AbstractValidationHandler<User, Validator> {

    private final UserService userService;

    @Autowired
    public UserHandler(Validator validator, UserService userService) {
        super(User.class, validator);
        this.userService = userService;
    }

    /**
     *
     * @param request
     * @return
     * @throws IllegalArgumentException - if there is no path variable with the given name
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        return Mono.just(request.pathVariable("id"))
                .flatMap(userService::delete)
                .flatMap(voidz -> ServerResponse.noContent().build());
    }

    /**
     *
     * @param request
     * @return
     * @throws IllegalArgumentException - if there is no path variable with the given name
     */
    public Mono<ServerResponse> find(ServerRequest request) {

        return Mono.just(request.pathVariable("id"))
                .flatMap(userService::find)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {

        Flux<User> allUsersFlux = userService.findAll();

        return ServerResponse.ok().body(allUsersFlux, User.class);
    }

    // !todo 409 conflict
    public Mono<ServerResponse> save(ServerRequest request) {

        return request.bodyToMono(User.class)
                .doOnNext(this::validate)
                .flatMap(userService::save)
                .flatMap(savedUser -> ServerResponse.created(getLocation(request, savedUser)).build());
    }

    // !todo will flatMap calls be skipped if Mono is empty?
    public Mono<ServerResponse> update(ServerRequest request) {

        return request.bodyToMono(User.class)
                .doOnNext(this::validate)
                .flatMap(user -> userService.find(user.getId()))
                .flatMap(userService::update)
                .flatMap(updatedUser -> ServerResponse.ok().body(fromValue(updatedUser)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private URI getLocation(ServerRequest request, User savedUser) {
        return URI.create(request.path() + "/" + savedUser.getId());
    }

}
