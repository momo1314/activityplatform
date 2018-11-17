package org.redrock.activityplatform.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by momo on 2018/4/28
 */
@Configuration
@ComponentScan(basePackages = {
        "org.redrock.activityplatform.data.dao",
        "org.redrock.activityplatform.data.service"
})
public class MybatisConfig {
    @Bean
    public DataSource dataSource() throws IOException, PropertyVetoException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("classpath:jdbc.properties");
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(properties.getProperty("jdbc.driver"));
        dataSource.setUser(properties.getProperty("jdbc.user"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        dataSource.setJdbcUrl(properties.getProperty("jdbc.url"));
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxIdleTime(120);
        dataSource.setMaxPoolSize(60);
        dataSource.setMinPoolSize(5);
        dataSource.setInitialPoolSize(10);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(this.dataSource());
        String packageSearchPath = "classpath:mybatis/**/*.xml";
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
        sqlSessionFactory.setMapperLocations(resources);
        sqlSessionFactory.setTypeAliasesPackage("org.redrock.activityplatform.data.domain");
        return sqlSessionFactory;
    }

    @Bean
    public MapperScannerConfigurer configurer() throws Exception {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("org.redrock.activityplatform.data..dao");
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws IOException, PropertyVetoException {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(this.dataSource());
        return transactionManager;
    }
}
