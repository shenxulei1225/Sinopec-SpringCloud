package cn.cheers.x.infra.framework.file.config;

import cn.cheers.x.infra.framework.file.core.client.FileClientFactory;
import cn.cheers.x.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * 
 */
@Configuration(proxyBeanMethods = false)
public class CheersFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
