package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler reviewHandler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/v1/reviews"), builder ->
                        builder
                                .POST("", req -> reviewHandler.addReview(req))
                                .GET("", req -> reviewHandler.getAllReviews(req)))
                .GET("/v1/helloworld", req -> ServerResponse.ok().bodyValue("helloworld"))
                .build();
    }
}
