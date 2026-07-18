package cn.cheers.x.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 * 
 */
@ConfigurationProperties(prefix = "cheers.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "cheers.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
