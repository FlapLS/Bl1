package com.example.bl_lab1.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.bl_lab1.repositories", entityManagerFactoryRef = "entityManagerFactory",
                       transactionManagerRef = "transactionManager")
@EntityScan("com.example.bl_lab1.*")
public class EntityManagerConfig {
    
    @Autowired
    private Environment environment;
    
    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean dataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("postgres");
        dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
        Properties xaProperties = new Properties();
        xaProperties.put("databaseName", "postgres");
        xaProperties.setProperty("user", environment.getProperty("spring.datasource.username"));
        xaProperties.setProperty("password", environment.getProperty("spring.datasource.password"));
        xaProperties.setProperty("serverName", "localhost");
        xaProperties.setProperty("portNumber", "5432");
        dataSource.setXaProperties(xaProperties);
        dataSource.setPoolSize(10);
        return dataSource;
    }
    
    
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.bl_lab1.model");
        factory.setDataSource(dataSource());
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.show_sql", "false");
        jpaProperties.put("hibernate.ddl-auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        jpaProperties.put("hibernate.current_session_context_class", "jta");
        jpaProperties.put("javax.persistence.transactionType", "jta");
        jpaProperties.put("hibernate.transaction.manager_lookup_class", "com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup");
//        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    @Bean
    public UserTransactionImp myTransactionImp() {
        return new UserTransactionImp();
    }
}
