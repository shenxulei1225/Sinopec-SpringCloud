package cn.cheers.x.alarm.controller.admin.vo.alarm;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 告警 Response VO
 */
@Schema(description = "管理后台 - 告警 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AlarmRespVO {

    @Schema(description = "告警ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("告警ID")
    private Long id;

    @Schema(description = "告警编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALM-20260104-00001")
    @ExcelProperty("告警编码")
    private String alarmCode;

    @Schema(description = "告警类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmTypeId;

    @Schema(description = "告警分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmCategoryId;

    @Schema(description = "告警模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmModelId;

    @Schema(description = "告警类型路径", example = "环境告警 > 水位告警 > 水位超标")
    @ExcelProperty("告警类型")
    private String alarmTypePath;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    @ExcelProperty("告警级别")
    private String alarmLevel;

    @Schema(description = "告警状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty("告警状态")
    private String alarmStatus;

    @Schema(description = "告警来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM")
    @ExcelProperty("告警来源")
    private String alarmSource;

    @Schema(description = "告警内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标，当前水位55cm，阈值50cm")
    @ExcelProperty("告警内容")
    private String alarmContent;

    @Schema(description = "关联设备ID", example = "1")
    private Long deviceId;

    @Schema(description = "设备名称", example = "水位传感器-B区2号")
    @ExcelProperty("设备名称")
    private String deviceName;

    @Schema(description = "位置ID", example = "1")
    private Long locationId;

    @Schema(description = "位置名称", example = "B区 > 2号防火分区 > 集水坑")
    @ExcelProperty("位置")
    private String locationName;

    @Schema(description = "触发值", example = "55cm")
    @ExcelProperty("触发值")
    private String triggerValue;

    @Schema(description = "阈值", example = "50cm")
    @ExcelProperty("阈值")
    private String thresholdValue;

    @Schema(description = "触发次数", example = "1")
    @ExcelProperty("触发次数")
    private Integer triggerCount;

    @Schema(description = "升级级别", example = "0")
    private Integer escalationLevel;

    @Schema(description = "持续时长（秒）", example = "300")
    @ExcelProperty("持续时长(秒)")
    private Long durationSeconds;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
