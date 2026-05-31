package cn.cheers.x.module.dynamicbusiness.service.entity.query.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

/**
 * 查询条件超限异常
 *
 * <p>当查询条件数量超过配置的最大限制时抛出此异常。</p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-QRY-003: 查询条件最多 10 个</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * // 检查条件数量
 * if (conditions.size() > maxConditions) {
 *     throw new TooManyConditionsException(conditions.size(), maxConditions);
 * }
 * </pre>
 *
 * @author 扩展字段查询服务
 */
public class TooManyConditionsException extends EntityQueryException {

    /**
     * 当前条件数量
     */
    private final int currentCount;

    /**
     * 最大允许条件数量
     */
    private final int maxCount;

    /**
     * 构造查询条件超限异常
     *
     * @param currentCount 当前条件数量
     * @param maxCount 最大允许条件数量
     */
    public TooManyConditionsException(int currentCount, int maxCount) {
        super(ExtendFieldQueryErrorCodeConstants.TOO_MANY_CONDITIONS, currentCount, maxCount);
        this.currentCount = currentCount;
        this.maxCount = maxCount;
    }

    /**
     * 获取当前条件数量
     */
    public int getCurrentCount() {
        return currentCount;
    }

    /**
     * 获取最大允许条件数量
     */
    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public String toString() {
        return String.format("TooManyConditionsException{currentCount=%d, maxCount=%d, code=%d, message='%s'}", 
                currentCount, maxCount, getCode(), getMessage());
    }
}
