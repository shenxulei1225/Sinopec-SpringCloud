package cn.cheers.x.module.dynamicbusiness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * 模板配置属性
 * 
 * 用于配置模板文件的存储路径和相关参数
 * 
 * @author yudao
 */
@Component
@ConfigurationProperties(prefix = "cheers.template")
@Validated
@Data
public class TemplateProperties {

    /**
     * 页面模板路径
     * 
     * 支持相对路径（相对于项目根目录）和绝对路径
     * 默认值：.kiro/specs/动态业务管理/多视图模板系统/模板配置数据/页面模板/
     */
    @NotBlank(message = "页面模板路径不能为空")
    private String pageTemplatePath = ".kiro/specs/动态业务管理/多视图模板系统/模板配置数据/页面模板/";

    /**
     * 字段模板路径
     */
    @NotBlank(message = "字段模板路径不能为空")
    private String fieldTemplatePath = ".kiro/specs/动态业务管理/多视图模板系统/模板配置数据/字段模板/";

    /**
     * 模板文件扩展名
     */
    private String templateFileExtension = ".json";

    /**
     * 是否启用模板缓存
     */
    private boolean enableCache = true;

    /**
     * 缓存过期时间（秒）
     */
    private long cacheExpireSeconds = 3600;
}




















