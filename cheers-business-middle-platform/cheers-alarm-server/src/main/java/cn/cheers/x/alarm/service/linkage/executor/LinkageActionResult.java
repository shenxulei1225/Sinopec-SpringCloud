package cn.cheers.x.alarm.service.linkage.executor;

import lombok.Builder;
import lombok.Data;

/**
 * 联动动作执行结果
 *
 * @author 告警管理模块
 */
@Data
@Builder
public class LinkageActionResult {

    /**
     * 是否执行成功
     */
    private Boolean success;

    /**
     * 执行结果描述
     */
    private String resultMessage;

    /**
     * 错误信息（失败时）
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 是否需要人工介入
     * 
     * <p>当重试次数达到上限仍失败时，标记需要人工介入</p>
     */
    private Boolean manualIntervention;

    /**
     * 是否可以重试
     * 
     * <p>某些错误（如配置错误）不应该重试</p>
     */
    private Boolean canRetry;

    /**
     * 目标设备ID
     */
    private Long targetDeviceId;

    /**
     * 目标设备名称
     */
    private String targetDeviceName;

    /**
     * 扩展数据（JSON格式）
     */
    private String extData;

    /**
     * 创建成功结果
     *
     * @param resultMessage 结果描述
     * @return 成功结果
     */
    public static LinkageActionResult success(String resultMessage) {
        return LinkageActionResult.builder()
                .success(true)
                .resultMessage(resultMessage)
                .manualIntervention(false)
                .canRetry(false)
                .build();
    }

    /**
     * 创建成功结果（带设备信息）
     *
     * @param resultMessage    结果描述
     * @param targetDeviceId   目标设备ID
     * @param targetDeviceName 目标设备名称
     * @return 成功结果
     */
    public static LinkageActionResult success(String resultMessage, Long targetDeviceId, String targetDeviceName) {
        return LinkageActionResult.builder()
                .success(true)
                .resultMessage(resultMessage)
                .targetDeviceId(targetDeviceId)
                .targetDeviceName(targetDeviceName)
                .manualIntervention(false)
                .canRetry(false)
                .build();
    }

    /**
     * 创建失败结果（可重试）
     *
     * @param errorMessage 错误信息
     * @return 失败结果
     */
    public static LinkageActionResult failed(String errorMessage) {
        return LinkageActionResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .manualIntervention(false)
                .canRetry(true)
                .build();
    }

    /**
     * 创建失败结果（指定是否可重试）
     *
     * @param errorMessage 错误信息
     * @param canRetry     是否可重试
     * @return 失败结果
     */
    public static LinkageActionResult failed(String errorMessage, boolean canRetry) {
        return LinkageActionResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .manualIntervention(false)
                .canRetry(canRetry)
                .build();
    }

    /**
     * 创建失败结果（需要人工介入）
     *
     * @param errorMessage       错误信息
     * @param manualIntervention 是否需要人工介入
     * @return 失败结果
     */
    public static LinkageActionResult failedWithManualIntervention(String errorMessage, boolean manualIntervention) {
        return LinkageActionResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .manualIntervention(manualIntervention)
                .canRetry(!manualIntervention)
                .build();
    }

    /**
     * 创建模拟执行成功结果
     *
     * @param resultMessage 结果描述
     * @return 模拟执行成功结果
     */
    public static LinkageActionResult dryRunSuccess(String resultMessage) {
        return LinkageActionResult.builder()
                .success(true)
                .resultMessage("[模拟执行] " + resultMessage)
                .manualIntervention(false)
                .canRetry(false)
                .build();
    }

    /**
     * 判断是否执行成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }

    /**
     * 判断是否需要人工介入
     *
     * @return 是否需要人工介入
     */
    public boolean needManualIntervention() {
        return Boolean.TRUE.equals(manualIntervention);
    }

    /**
     * 判断是否可以重试
     *
     * @return 是否可以重试
     */
    public boolean isCanRetry() {
        return Boolean.TRUE.equals(canRetry);
    }

}
