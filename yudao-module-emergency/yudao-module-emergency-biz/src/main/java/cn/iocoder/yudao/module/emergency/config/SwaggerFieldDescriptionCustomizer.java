package cn.iocoder.yudao.module.emergency.config;

import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Swagger 字段描述自定义器
 *
 * 自动读取 Java 类的字段注释并应用到 Swagger 文档中
 * 支持从 DO 类的字段注释同步到 VO 类的 Schema 描述
 *
 * @author 系统生成
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SwaggerFieldDescriptionCustomizer {

    private final JavaDocCommentReader commentReader;

    @Bean
    public GlobalOpenApiCustomizer fieldDescriptionCustomizer() {
        return openApi -> {
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                // 注意：OpenAPI 库的 getSchemas() 返回原始类型 Map<String, Schema>，需要类型转换
                @SuppressWarnings("unchecked")
                Map<String, Schema<?>> schemas = (Map<String, Schema<?>>) (Map<?, ?>) openApi.getComponents().getSchemas();

                for (Map.Entry<String, Schema<?>> entry : schemas.entrySet()) {
                    String className = entry.getKey();
                    Schema<?> schema = entry.getValue();

                    // 尝试从对应的 DO 类读取字段注释
                    enhanceFieldDescriptions(schema, className);
                }
            }
        };
    }

    /**
     * 增强字段描述
     *
     * @param schema    Swagger Schema 对象
     * @param className 类名
     */
    @SuppressWarnings("unchecked")
    private void enhanceFieldDescriptions(Schema<?> schema, String className) {
        try {
            // 尝试找到对应的 DO 类
            Class<?> doClass = findCorrespondingDoClass(className);
            if (doClass != null) {
                // 注意：OpenAPI 库的 getProperties() 返回原始类型 Map<String, Schema>，需要类型转换
                Map<String, Schema<?>> properties = (Map<String, Schema<?>>) (Map<?, ?>) schema.getProperties();
                if (properties != null) {
                    for (Map.Entry<String, Schema<?>> propertyEntry : properties.entrySet()) {
                        String fieldName = propertyEntry.getKey();
                        Schema<?> fieldSchema = propertyEntry.getValue();

                        // 如果字段还没有描述，尝试从 DO 类读取
                        if (fieldSchema.getDescription() == null || fieldSchema.getDescription().trim().isEmpty()) {
                            String fieldComment = getFieldComment(doClass, fieldName);
                            if (fieldComment != null && !fieldComment.trim().isEmpty()) {
                                fieldSchema.setDescription(fieldComment);
                                log.debug("为字段 {}.{} 设置描述: {}", className, fieldName, fieldComment);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("处理类 {} 的字段描述时发生异常: {}", className, e.getMessage());
        }
    }

    /**
     * 查找对应的 DO 类
     *
     * @param voClassName VO 类名
     * @return 对应的 DO 类，如果找不到返回 null
     */
    private Class<?> findCorrespondingDoClass(String voClassName) {
        try {
            // VO 类名通常以 RespVO、ReqVO 等结尾
            String doClassName = voClassName;
            if (doClassName.endsWith("RespVO")) {
                doClassName = doClassName.substring(0, doClassName.length() - 6) + "DO";
            } else if (doClassName.endsWith("ReqVO")) {
                doClassName = doClassName.substring(0, doClassName.length() - 5) + "DO";
            } else if (doClassName.endsWith("VO")) {
                doClassName = doClassName.substring(0, doClassName.length() - 2) + "DO";
            }

            // 尝试在 dataobject 包下查找
            String fullClassName = "cn.iocoder.yudao.module.emergency.dal.dataobject." + doClassName;
            return Class.forName(fullClassName);

        } catch (ClassNotFoundException e) {
            // 尝试其他可能的包路径
            String[] possiblePackages = {
                "cn.iocoder.yudao.module.emergency.dal.dataobject.plan",
                "cn.iocoder.yudao.module.emergency.dal.dataobject.event",
                "cn.iocoder.yudao.module.emergency.dal.dataobject.response"
            };

            for (String packageName : possiblePackages) {
                try {
                    String doClassName = extractSimpleClassName(voClassName);
                    String fullClassName = packageName + "." + doClassName + "DO";
                    return Class.forName(fullClassName);
                } catch (ClassNotFoundException ignored) {
                    // 继续尝试下一个包
                }
            }
        } catch (Exception e) {
            log.debug("查找 DO 类失败: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 提取简单类名
     */
    private String extractSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        if (lastDot > 0) {
            return fullClassName.substring(lastDot + 1);
        }
        return fullClassName;
    }

    /**
     * 获取字段注释
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段注释，如果没有找到返回 null
     */
    private String getFieldComment(Class<?> clazz, String fieldName) {
        // 首先尝试使用注释读取器从源代码中读取
        String comment = commentReader.getFieldComment(clazz, fieldName);
        if (comment != null && !comment.trim().isEmpty()) {
            return comment;
        }

        // 如果读取器没有找到，尝试父类
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                // 还可以在这里添加其他注释获取逻辑
                return null;
            }
        } catch (NoSuchFieldException e) {
            // 字段不存在，尝试父类
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                return getFieldComment(superClass, fieldName);
            }
        } catch (Exception e) {
            log.debug("获取字段注释失败: {}.{}", clazz.getSimpleName(), fieldName, e);
        }

        return null;
    }
}
