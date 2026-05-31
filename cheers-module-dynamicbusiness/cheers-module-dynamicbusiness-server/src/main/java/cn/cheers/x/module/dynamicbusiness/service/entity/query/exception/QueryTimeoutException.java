package cn.cheers.x.module.dynamicbusiness.service.entity.query.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

/**
 * 查询超时异常
 *
 * <p>当查询执行时间超过配置的超时时间时抛出此异常。</p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-QRY-004: 统计查询超时时间 30 秒</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * try {
 *     // 执行查询
 *     result = queryEngine.query(request);
 * } catch (TimeoutException e) {
 *     throw new QueryTimeoutException(timeoutSeconds, e);
 * }
 * </pre>
 *
 * @author 扩展字段查询服务
 */
public class QueryTimeoutException extends EntityQueryException {

    /**
     * 超时时间（秒）
     */
    private final Integer timeoutSeconds;

    /**
     * 查询类型（query/aggregate/count）
     */
    private final String queryType;

    /**
     * 构造查询超时异常
     */
    public QueryTimeoutException() {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT);
        this.timeoutSeconds = null;
        this.queryType = null;
    }

    /**
     * 构造查询超时异常（带原因）
     *
     * @param cause 原因
     */
    public QueryTimeoutException(Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT, cause);
        this.timeoutSeconds = null;
        this.queryType = null;
    }

    /**
     * 构造查询超时异常（带超时时间）
     *
     * @param timeoutSeconds 超时时间（秒）
     */
    public QueryTimeoutException(Integer timeoutSeconds) {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT);
        this.timeoutSeconds = timeoutSeconds;
        this.queryType = null;
    }

    /**
     * 构造查询超时异常（带超时时间和原因）
     *
     * @param timeoutSeconds 超时时间（秒）
     * @param cause 原因
     */
    public QueryTimeoutException(Integer timeoutSeconds, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT, cause);
        this.timeoutSeconds = timeoutSeconds;
        this.queryType = null;
    }

    /**
     * 构造查询超时异常（带查询类型和超时时间）
     *
     * @param queryType 查询类型
     * @param timeoutSeconds 超时时间（秒）
     */
    public QueryTimeoutException(String queryType, Integer timeoutSeconds) {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT);
        this.queryType = queryType;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * 构造查询超时异常（带查询类型、超时时间和原因）
     *
     * @param queryType 查询类型
     * @param timeoutSeconds 超时时间（秒）
     * @param cause 原因
     */
    public QueryTimeoutException(String queryType, Integer timeoutSeconds, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.QUERY_TIMEOUT, cause);
        this.queryType = queryType;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * 获取超时时间（秒）
     */
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * 获取查询类型
     */
    public String getQueryType() {
        return queryType;
    }

    @Override
    public String toString() {
        return String.format("QueryTimeoutException{queryType='%s', timeoutSeconds=%d, code=%d, message='%s'}", 
                queryType, timeoutSeconds, getCode(), getMessage());
    }
}
