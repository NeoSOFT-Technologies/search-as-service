package com.solr.clientwrapper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
        private int storage;
    }

}
