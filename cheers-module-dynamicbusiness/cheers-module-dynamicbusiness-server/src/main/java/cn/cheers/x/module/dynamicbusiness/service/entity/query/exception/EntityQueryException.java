package cn.cheers.x.module.dynamicbusiness.service.entity.query.exception;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * Entity 查询异常基类
 *
 * <p>所有扩展字段查询相关的异常都继承此类。</p>
 *
 * <h3>异常层次结构</h3>
 * <pre>
 * EntityQueryException (基类)
 * ├── FieldNotSearchableException   - 字段不可查询
 * ├── TooManyConditionsException    - 查询条件超限
 * ├── QueryTimeoutException         - 查询超时
 * └── SyncException                 - 数据同步异常
 * </pre>
 *
 * @author 扩展字段查询服务
 */
public class EntityQueryException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码
     */
    public EntityQueryException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 使用错误码和格式化参数构造异常
     *
     * @param errorCode 错误码
     * @param args 格式化参数
     */
    public EntityQueryException(ErrorCode errorCode, Object... args) {
        super(formatMessage(errorCode.getMsg(), args));
        this.code = errorCode.getCode();
        this.message = formatMessage(errorCode.getMsg(), args);
    }

    /**
     * 使用错误码和原因构造异常
     *
     * @param errorCode 错误码
     * @param cause 原因
     */
    public EntityQueryException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 使用错误码、格式化参数和原因构造异常
     *
     * @param errorCode 错误码
     * @param cause 原因
     * @param args 格式化参数
     */
    public EntityQueryException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(formatMessage(errorCode.getMsg(), args), cause);
        this.code = errorCode.getCode();
        this.message = formatMessage(errorCode.getMsg(), args);
    }

    /**
     * 使用自定义错误码和消息构造异常
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public EntityQueryException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用自定义错误码、消息和原因构造异常
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因
     */
    public EntityQueryException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
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
        return String.format("%s{code=%d, message='%s'}", 
                getClass().getSimpleName(), code, message);
    }
}
