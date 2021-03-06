package com.searchservice.app.config;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "capacity-plan")
@Getter
@Setter
public class CapacityPlanProperties {

    private List<Plan> plans;

    @Getter
    @Setter
    public static class Plan {
        private String sku;
        private String name;
        private int replicas;
        private int shards;
    }

}
