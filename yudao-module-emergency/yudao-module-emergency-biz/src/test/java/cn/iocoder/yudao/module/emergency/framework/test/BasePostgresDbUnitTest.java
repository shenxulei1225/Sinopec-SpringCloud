package cn.iocoder.yudao.module.emergency.framework.test;

import cn.hutool.extra.spring.SpringUtil;
import cn.cheers.x.framework.datasource.config.CheersDataSourceAutoConfiguration;
import cn.cheers.x.framework.mybatis.config.CheersMybatisAutoConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 依赖 PostgreSQL 的单元测试基类
 *
 * 使用真实的 PostgreSQL 数据库进行测试，支持完整的 PostgreSQL 特性（如 JSONB）
 * 数据库连接配置通过 application-test.yaml 提供，通常使用 Testcontainers
 *
 * **设计说明**：完全参考 yudao-framework 官方测试基类 `BaseDbUnitTest` 的设计
 * - 不使用 `@ComponentScan`，避免触发 Spring Boot 的自动配置机制
 * - 使用 `@Import` 显式导入配置类，精确控制加载内容
 * - 测试类通过 `@Import(ServiceClass.class)` 显式导入需要测试的 Service
 *
 * **参考资源**：
 * - yudao-framework 官方测试基类：`yudao-framework/yudao-spring-boot-starter-test/src/main/java/cn/iocoder/yudao/framework/test/core/ut/BaseDbUnitTest.java`
 * - system 模块测试示例：`yudao-module-system/yudao-module-system-server/src/test/java/cn/iocoder/yudao/module/system/service/user/AdminUserServiceImplTest.java`
 * - yudao 官方单元测试教程：https://www.iocoder.cn/Spring-Boot/Unit-Test/?yudao
 *
 * @author 芋道源码
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = BasePostgresDbUnitTest.Application.class,
        // 按照 yudao 官方方式，在 @SpringBootTest 的 properties 中排除 Security 自动配置
        // 这样可以确保在自动配置加载之前就排除，避免加载顺序问题
        properties = {
                "spring.autoconfigure.exclude=cn.cheers.x.framework.security.config.CheersWebSecurityConfigurerAdapter,cn.cheers.x.framework.security.config.CheersSecurityAutoConfiguration"
        }
)
@ActiveProfiles("test") // 设置使用 application-test 配置文件
// 注意：@Testcontainers 注解已移除，改为在代码中动态处理
public class BasePostgresDbUnitTest {

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        // 检查是否禁用 Testcontainers（通过系统属性或环境变量）
        String disableTestcontainers = System.getProperty("testcontainers.disable") != null 
                ? System.getProperty("testcontainers.disable")
                : System.getenv("TESTCONTAINERS_DISABLE");
        
        if ("true".equalsIgnoreCase(disableTestcontainers)) {
            // 如果禁用了 Testcontainers，使用 application-test.yaml 中的配置
            // 不设置任何动态属性，让 Spring Boot 使用配置文件中的数据库连接
            // 注意：需要确保 application-test.yaml 中配置了正确的数据库连接和初始化脚本
            System.out.println("Testcontainers disabled, using database configuration from application-test.yaml");
            return;
        }
        
        try {
            // Ensure container is started before any property access
            TestContainersConfiguration.postgres.start();

            // Use the shared PostgreSQL container from TestContainersConfiguration
            registry.add("spring.datasource.url", () -> TestContainersConfiguration.postgres.getJdbcUrl());
            registry.add("spring.datasource.username", () -> TestContainersConfiguration.postgres.getUsername());
            registry.add("spring.datasource.password", () -> TestContainersConfiguration.postgres.getPassword());
            registry.add("spring.datasource.driver-class-name", () -> TestContainersConfiguration.postgres.getDriverClassName());
            registry.add("spring.sql.init.mode", () -> "never");
            // Dynamic datasource configuration for multi-datasource support
            registry.add("spring.datasource.dynamic.primary", () -> "master");
            registry.add("spring.datasource.dynamic.datasource.master.url", () -> TestContainersConfiguration.postgres.getJdbcUrl());
            registry.add("spring.datasource.dynamic.datasource.master.username", () -> TestContainersConfiguration.postgres.getUsername());
            registry.add("spring.datasource.dynamic.datasource.master.password", () -> TestContainersConfiguration.postgres.getPassword());
            registry.add("spring.datasource.dynamic.datasource.master.driver-class-name", () -> TestContainersConfiguration.postgres.getDriverClassName());

            // Configure HikariCP for dynamic datasource (critical for test stability)
            registry.add("spring.datasource.dynamic.hikari.connection-timeout", () -> "20000");
            registry.add("spring.datasource.dynamic.hikari.maximum-pool-size", () -> "3");
            registry.add("spring.datasource.dynamic.hikari.minimum-idle", () -> "1");
            registry.add("spring.datasource.dynamic.hikari.idle-timeout", () -> "60000"); // 1 minute
            registry.add("spring.datasource.dynamic.hikari.max-lifetime", () -> "120000"); // 2 minutes (much shorter for tests)
            registry.add("spring.datasource.dynamic.hikari.keepalive-time", () -> "30000"); // 30 seconds
            registry.add("spring.datasource.dynamic.hikari.validation-timeout", () -> "3000");
            registry.add("spring.datasource.dynamic.hikari.connection-test-query", () -> "SELECT 1");
            registry.add("spring.datasource.dynamic.hikari.leak-detection-threshold", () -> "30000");
        } catch (Exception e) {
            // 如果 Testcontainers 启动失败（例如 Docker 不可用），回退到使用配置文件
            System.err.println("Warning: Testcontainers failed to start, falling back to application-test.yaml configuration: " + e.getMessage());
            // 不设置任何动态属性，让 Spring Boot 使用配置文件中的数据库连接
        }
    }

    /**
     * 测试配置类
     * 
     * **重要设计说明**：参考 yudao-framework 官方测试基类 `BaseDbUnitTest` 的设计
     * 
     * **核心设计原则**：
     * 1. **不使用 `@ComponentScan`**：避免触发 Spring Boot 的自动配置机制
     * 2. **使用 `@Import` 显式导入**：精确控制加载哪些配置类
     * 3. **测试类通过 `@Import(ServiceClass.class)` 显式导入需要测试的 Service**
     * 
     * **为什么这样设计**：
     * - `@ComponentScan` 会触发 Spring Boot 的自动配置机制
     * - 自动配置机制会加载 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中列出的所有自动配置类
     * - `@ComponentScan` 的 `excludeFilters` 无法排除通过自动配置机制加载的类
     * - 使用 `@Import` 显式导入可以完全避免自动配置问题
     * 
     * **参考资源**：
     * - yudao-framework 官方测试基类：`yudao-framework/yudao-spring-boot-starter-test/src/main/java/cn/iocoder/yudao/framework/test/core/ut/BaseDbUnitTest.java`
     * - system 模块测试示例：`yudao-module-system/yudao-module-system-server/src/test/java/cn/iocoder/yudao/module/system/service/user/AdminUserServiceImplTest.java`
     * 
     * **测试类使用方式**：
     * ```java
     * @Import({
     *     EmergencyEventServiceImpl.class,
     *     EventStateMachine.class,
     *     EventCategoryValidationService.class,
     *     TimelineCacheService.class,
     *     MockApisTestConfiguration.class
     * })
     * public class EmergencyEventServiceImplTest extends BasePostgresDbUnitTest {
     *     @Resource
     *     private EmergencyEventServiceImpl eventService;
     *     // ...
     * }
     * ```
     */
    @Import({
            // DB 配置类
            CheersDataSourceAutoConfiguration.class, // 自己的 DB 配置类
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            // 注意：不导入 SqlInitializationTestConfiguration，因为数据库初始化由 Testcontainers 处理
            
            // MyBatis 配置类
            CheersMybatisAutoConfiguration.class, // 自己的 MyBatis 配置类
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类
            MybatisPlusJoinAutoConfiguration.class, // MyBatis 的Join配置类

            // Testcontainers 配置类（提供数据库连接和初始化）
            // 注意：如果 Docker 不可用，TestContainersConfiguration 会失败，但不影响使用配置文件中的数据库连接
            TestContainersConfiguration.class,

            // 其它配置类
            SpringUtil.class
            
            // 注意：不导入 Security 相关配置类，因为测试环境不需要完整的 Security 功能
            // 测试类如果需要 Security 相关的 Bean，可以在 MockApisTestConfiguration 中提供 Mock Bean
    })
    @SpringBootApplication(
            // 按照 yudao 官方方式，在 @SpringBootApplication 的 exclude 属性中排除 Security、Redis、Tenant 和 RPC 自动配置
            // 这样可以确保在自动配置加载之前就排除，避免加载顺序问题
            // 测试环境使用 Mock Bean 替代这些服务，所以排除相关自动配置类
            exclude = {
                    cn.cheers.x.framework.security.config.CheersWebSecurityConfigurerAdapter.class,
                    cn.cheers.x.framework.security.config.CheersSecurityAutoConfiguration.class,
                    cn.cheers.x.framework.security.config.CheersSecurityRpcAutoConfiguration.class,
                    cn.cheers.x.framework.redis.config.CheersRedisAutoConfiguration.class,
                    cn.cheers.x.framework.redis.config.CheersCacheAutoConfiguration.class,
                    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class,
                    cn.cheers.x.framework.tenant.config.CheersTenantAutoConfiguration.class,
                    cn.cheers.x.framework.tenant.config.CheersTenantRpcAutoConfiguration.class,
                    cn.cheers.x.framework.dict.config.CheersDictAutoConfiguration.class,
                    cn.cheers.x.framework.xss.config.CheersXssAutoConfiguration.class
            }
    )
    public static class Application {
    }

}

