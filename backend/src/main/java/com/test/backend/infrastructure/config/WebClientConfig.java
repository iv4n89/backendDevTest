package com.test.backend.infrastructure.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Value("${webclient.connection-pool.max-connections}")
    private int maxConnections;

    @Value("${webclient.connection-pool.max-idle-time}")
    private Duration maxIdleTime;

    @Value("${webclient.connection-pool.max-life-time}")
    private Duration maxLifeTime;

    @Value("${webclient.connection-pool.pending-acquire-timeout}")
    private Duration pendingAcquireTimeout;

    @Value("${webclient.connection-pool.evict-in-background}")
    private Duration evictInBackground;

    @Value("${webclient.timeouts.connect-timeout}")
    private int connectTimeout;

    @Value("${webclient.timeouts.response-timeout}")
    private Duration responseTimeout;

    @Value("${webclient.timeouts.read-timeout}")
    private Duration readTimeout;

    @Value("${webclient.timeouts.write-timeout}")
    private Duration writeTimeout;

    @Bean
    public WebClient webClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .maxIdleTime(maxIdleTime)
                .maxLifeTime(maxLifeTime)
                .pendingAcquireTimeout(pendingAcquireTimeout)
                .evictInBackground(evictInBackground)
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(responseTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout.getSeconds(), TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout.getSeconds(), TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
