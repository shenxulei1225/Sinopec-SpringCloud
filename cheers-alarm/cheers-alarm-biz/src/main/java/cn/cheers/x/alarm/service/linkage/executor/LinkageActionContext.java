package cn.cheers.x.alarm.service.linkage.executor;

import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 联动动作执行上下文
 * 
 * <p>包含执行联动动作所需的所有上下文信息</p>
 *
 * @author 告警管理模块
 */
@Data
@Builder
public class LinkageActionContext {

    /**
     * 告警信息
     */
    private AlarmDO alarm;

    /**
     * 联动规则
     */
    private LinkageRuleDO linkageRule;

    /**
     * 动作类型
     */
    private String actionType;

    /**
     * 动作配置（JSON字符串）
     */
    private String actionConfig;

    /**
     * 动作配置（解析后的Map）
     */
    private Map<String, Object> actionConfigMap;

    /**
     * 目标设备ID
     */
    private Long targetDeviceId;

    /**
     * 目标设备名称
     */
    private String targetDeviceName;

    /**
     * 是否为模拟执行（干运行）
     * 
     * <p>模拟执行时不实际控制设备，仅验证配置有效性</p>
     */
    private Boolean dryRun;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行记录ID（重试时使用）
     */
    private Long executionId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 扩展参数
     */
    private Map<String, Object> extParams;

    /**
     * 获取动作配置中的字符串值
     *
     * @param key 配置键
     * @return 配置值
     */
    public String getConfigString(String key) {
        if (actionConfigMap == null) {
            return null;
        }
        Object value = actionConfigMap.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取动作配置中的Long值
     *
     * @param key 配置键
     * @return 配置值
     */
    public Long getConfigLong(String key) {
        if (actionConfigMap == null) {
            return null;
        }
        Object value = actionConfigMap.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取动作配置中的Integer值
     *
     * @param key 配置键
     * @return 配置值
     */
    public Integer getConfigInteger(String key) {
        if (actionConfigMap == null) {
            return null;
        }
        Object value = actionConfigMap.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取动作配置中的Boolean值
     *
     * @param key 配置键
     * @return 配置值
     */
    public Boolean getConfigBoolean(String key) {
        if (actionConfigMap == null) {
            return null;
        }
        Object value = actionConfigMap.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 判断是否为模拟执行
     *
     * @return 是否模拟执行
     */
    public boolean isDryRun() {
        return Boolean.TRUE.equals(dryRun);
    }

}
