package com.neosoft.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Primary
@EnableJpaRepositories("com.neosoft.app.infrastructure.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {}
