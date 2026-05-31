package cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 循环引用异常
 * 
 * <p>当检测到 Entity 的 parent_id 形成循环引用时抛出此异常。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-VAL-004: parent_id 不能形成循环引用（系统强制）</li>
 * </ul>
 * 
 * <h3>循环引用示例</h3>
 * <pre>
 * Entity A (parent_id = C)
 *    ↓
 * Entity B (parent_id = A)
 *    ↓
 * Entity C (parent_id = B)
 *    ↓
 * Entity A (循环！)
 * 
 * 循环路径：A → B → C → A
 * </pre>
 * 
 * @author 扩展字段查询服务
 */
public class CircularReferenceException extends EntityValidationException {

    /**
     * 循环引用路径（Entity ID 列表）
     */
    private final List<Long> cyclePath;

    /**
     * 循环引用路径（Entity 名称列表，用于显示）
     */
    private final List<String> cyclePathNames;

    /**
     * 构造循环引用异常
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param cyclePath 循环引用路径（Entity ID 列表）
     */
    public CircularReferenceException(Long entityId, Long modelId, List<Long> cyclePath) {
        super(ExtendFieldQueryErrorCodeConstants.CIRCULAR_REFERENCE_DETECTED, entityId, modelId,
                formatCyclePath(cyclePath));
        this.cyclePath = cyclePath;
        this.cyclePathNames = null;
    }

    /**
     * 构造循环引用异常（带名称）
     *
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param cyclePath 循环引用路径（Entity ID 列表）
     * @param cyclePathNames 循环引用路径（Entity 名称列表）
     */
    public CircularReferenceException(Long entityId, Long modelId, List<Long> cyclePath, List<String> cyclePathNames) {
        super(ExtendFieldQueryErrorCodeConstants.CIRCULAR_REFERENCE_DETECTED, entityId, modelId,
                formatCyclePathWithNames(cyclePath, cyclePathNames));
        this.cyclePath = cyclePath;
        this.cyclePathNames = cyclePathNames;
    }

    /**
     * 获取循环引用路径（Entity ID 列表）
     */
    public List<Long> getCyclePath() {
        return cyclePath;
    }

    /**
     * 获取循环引用路径（Entity 名称列表）
     */
    public List<String> getCyclePathNames() {
        return cyclePathNames;
    }

    /**
     * 获取循环引用路径长度
     */
    public int getCycleLength() {
        return cyclePath != null ? cyclePath.size() : 0;
    }

    /**
     * 格式化循环路径（仅 ID）
     */
    private static String formatCyclePath(List<Long> cyclePath) {
        if (cyclePath == null || cyclePath.isEmpty()) {
            return "未知循环路径";
        }
        return cyclePath.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" → "));
    }

    /**
     * 格式化循环路径（带名称）
     */
    private static String formatCyclePathWithNames(List<Long> cyclePath, List<String> cyclePathNames) {
        if (cyclePath == null || cyclePath.isEmpty()) {
            return "未知循环路径";
        }

        if (cyclePathNames != null && cyclePathNames.size() == cyclePath.size()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cyclePath.size(); i++) {
                if (i > 0) {
                    sb.append(" → ");
                }
                sb.append(cyclePathNames.get(i)).append("(").append(cyclePath.get(i)).append(")");
            }
            return sb.toString();
        }

        return formatCyclePath(cyclePath);
    }

    @Override
    public String toString() {
        return String.format("CircularReferenceException{entityId=%d, modelId=%d, cyclePath=%s, " +
                        "cyclePathNames=%s, code=%d, message='%s'}", 
                getEntityId(), getModelId(), cyclePath, cyclePathNames, getCode(), getMessage());
    }
}
