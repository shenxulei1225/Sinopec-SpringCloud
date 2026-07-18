package cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * Entity 验证异常基类
 *
 * <p>所有 Entity 数据验证相关的异常都继承此类。</p>
 *
 * <h3>异常层次结构</h3>
 * <pre>
 * EntityValidationException (基类)
 * ├── InvalidParentRefException     - parent_id 引用无效
 * ├── CircularReferenceException    - 循环引用检测
 * └── InvalidEntityRefException     - ENTITY_REF 字段引用无效
 * </pre>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-VAL-006: 所有 Entity 引用验证在保存时执行，不依赖前端验证</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
public class EntityValidationException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 被验证的 Entity ID
     */
    private final Long entityId;

    /**
     * 被验证的 Model ID
     */
    private final Long modelId;

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码
     */
    public EntityValidationException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
        this.entityId = null;
        this.modelId = null;
    }

    /**
     * 使用错误码和格式化参数构造异常
     *
     * @param errorCode 错误码
     * @param args 格式化参数
     */
    public EntityValidationException(ErrorCode errorCode, Object... args) {
        super(formatMessage(errorCode.getMsg(), args));
        this.code = errorCode.getCode();
        this.message = formatMessage(errorCode.getMsg(), args);
        this.entityId = null;
        this.modelId = null;
    }

    /**
     * 使用错误码、Entity 信息和格式化参数构造异常
     *
     * @param errorCode 错误码
     * @param entityId Entity ID
     * @param modelId Model ID
     * @param args 格式化参数
     */
    public EntityValidationException(ErrorCode errorCode, Long entityId, Long modelId, Object... args) {
        super(formatMessage(errorCode.getMsg(), args));
        this.code = errorCode.getCode();
        this.message = formatMessage(errorCode.getMsg(), args);
        this.entityId = entityId;
        this.modelId = modelId;
    }

    /**
     * 使用自定义错误码和消息构造异常
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public EntityValidationException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.entityId = null;
        this.modelId = null;
    }

    /**
     * 使用自定义错误码、消息和 Entity 信息构造异常
     *
     * @param code 错误码
     * @param message 错误消息
     * @param entityId Entity ID
     * @param modelId Model ID
     */
    public EntityValidationException(Integer code, String message, Long entityId, Long modelId) {
        super(message);
        this.code = code;
        this.message = message;
        this.entityId = entityId;
        this.modelId = modelId;
    }

    /**
     * 获取错误码
     */
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取被验证的 Entity ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * 获取被验证的 Model ID
     */
    public Long getModelId() {
        return modelId;
    }

    /**
     * 格式化消息
     * 
     * <p>将消息中的 {} 占位符替换为参数值。</p>
     * 
     * @param template 消息模板
     * @param args 参数
     * @return 格式化后的消息
     */
    protected static String formatMessage(String template, Object... args) {
        if (template == null || args == null || args.length == 0) {
            return template;
        }
        
        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;
        
        while (i < template.length()) {
            if (i < template.length() - 1 && template.charAt(i) == '{' && template.charAt(i + 1) == '}') {
                if (argIndex < args.length) {
                    result.append(args[argIndex] != null ? args[argIndex].toString() : "null");
                    argIndex++;
                } else {
                    result.append("{}");
                }
                i += 2;
            } else {
                result.append(template.charAt(i));
                i++;
            }
        }
        
        return result.toString();
    }

    @Override
    public String toString() {
        return String.format("%s{entityId=%d, modelId=%d, code=%d, message='%s'}", 
                getClass().getSimpleName(), entityId, modelId, code, message);
    }
}
