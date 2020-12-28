package com.github.lbovolini.notify.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    private final UserHandler userHandler;

    @Autowired
    public RouterConfig(UserHandler userHandler) {
        this.userHandler = userHandler;
    }


    @Bean
    public RouterFunction<ServerResponse> userRouter() {
        return route(POST("/users"), userHandler::save)
                .andRoute(GET("/users"), userHandler::findAll);
    }
}
