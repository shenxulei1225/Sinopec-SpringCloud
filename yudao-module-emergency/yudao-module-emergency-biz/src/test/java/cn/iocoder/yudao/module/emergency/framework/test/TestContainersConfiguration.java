package cn.iocoder.yudao.module.emergency.framework.test;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 共享的 Testcontainers 配置类
 *
 * 为所有测试提供统一的 PostgreSQL 容器实例，避免并发测试时的连接冲突
 *
 * @author 芋道源码
 */
@Configuration
@Testcontainers
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class TestContainersConfiguration {

    /**
     * PostgreSQL 测试容器
     * 注意：使用 @Container 注解的静态字段会被 JUnit 5 的 @Testcontainers 扩展自动管理生命周期
     * 容器会在所有测试完成后自动关闭，无需手动关闭
     */
    @Container
    @SuppressWarnings("resource") // Testcontainers 会自动管理容器生命周期，无需手动关闭
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("emergency_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema-postgres.sql") // 使用init script初始化数据库
            .withReuse(false); // 禁用容器复用，确保每次测试都是干净的环境

    // 确保容器在Spring上下文初始化时已经启动
    // 使用 destroyMethod = "close" 确保 Spring 在销毁 Bean 时关闭容器，避免资源泄漏
    @Bean(destroyMethod = "close")
    public PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }

}
