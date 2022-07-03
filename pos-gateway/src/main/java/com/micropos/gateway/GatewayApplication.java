package com.micropos.gateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.webflux.dsl.WebFlux;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    private final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public DirectChannel deliveryChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inGate() {
        logger.info("inGate");
        return IntegrationFlows.from(WebFlux.inboundGateway("/delivery"))
                .headerFilter("accept-encoding", false)
                .channel("deliveryChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outGate() {
        logger.info("outGate");
        return IntegrationFlows.from("deliveryChannel")
                .handle(Http.outboundGateway("http://localhost:8087/api/delivery")
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(List.class))
                .get();
//        return IntegrationFlows.from("deliveryChannel")
//                .handle(Http.outboundGateway("https://api.chucknorris.io/jokes/random")
//                        .httpMethod(HttpMethod.GET)
//                        .expectedResponseType(Joke.class))
//                .get();
    }


//    public class Joke {
//        private String icon_url;
//        private String id;
//        private String url;
//        private String value;
//    }

}
