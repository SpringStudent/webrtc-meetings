package io.springstudent.meeting.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig {

    private String type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DruidConfig getDruidConfig() {
        return new DruidConfig();
    }

    @Data
    public static class DruidConfig {
        private String filters;
        private String connectionProperties;
    }

    @Bean
    @Primary
    public DataSource druidDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        DruidConfig druidConfig;
        dataSource.setDbType(type);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        druidConfig = getDruidConfig();
        dataSource.setFilters(druidConfig.filters);
        dataSource.setConnectionProperties(druidConfig.connectionProperties);
        return dataSource;

    }

}
