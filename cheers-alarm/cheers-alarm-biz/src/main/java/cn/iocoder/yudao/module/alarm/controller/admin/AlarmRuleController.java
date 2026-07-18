package cn.iocoder.yudao.module.alarm.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.rule.*;
import cn.iocoder.yudao.module.alarm.convert.AlarmRuleConvert;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmRuleDO;
import cn.iocoder.yudao.module.alarm.service.rule.AlarmRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 告警规则管理 Controller
 * 
 * <p>提供告警规则的 CRUD、启用/禁用、测试等功能。</p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>告警规则 CRUD 操作（FR-008）</li>
 *   <li>规则启用/禁用（FR-008）</li>
 *   <li>规则测试（FR-008）</li>
 * </ul>
 * </p>
 * 
 * <p>支持的规则类型：
 * <ul>
 *   <li>THRESHOLD - 阈值告警：数据值超过预设阈值</li>
 *   <li>RATE - 变化率告警：数据变化率超过预设范围</li>
 *   <li>PATTERN - 异常模式告警：数据符合预设的异常模式</li>
 *   <li>COMBINATION - 组合条件告警：多个条件组合满足</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Tag(name = "管理后台 - 告警规则管理")
@RestController
@RequestMapping("/alarm/rules")
@Validated
public class AlarmRuleController {

    @Resource
    private AlarmRuleService alarmRuleService;

    // ========== 告警规则 CRUD ==========

    @PostMapping("/create")
    @Operation(summary = "创建告警规则", description = "创建新的告警规则，支持阈值、变化率、模式、组合四种规则类型")
    @PreAuthorize("@ss.hasPermission('alarm:rule:create')")
    public CommonResult<Long> createAlarmRule(@Valid @RequestBody AlarmRuleCreateReqVO createReqVO) {
        Long ruleId = alarmRuleService.createAlarmRule(createReqVO);
        return success(ruleId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新告警规则", description = "更新已有的告警规则，规则编码不可修改")
    @PreAuthorize("@ss.hasPermission('alarm:rule:update')")
    public CommonResult<Boolean> updateAlarmRule(@Valid @RequestBody AlarmRuleUpdateReqVO updateReqVO) {
        alarmRuleService.updateAlarmRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除告警规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:rule:delete')")
    public CommonResult<Boolean> deleteAlarmRule(@RequestParam("id") Long id) {
        alarmRuleService.deleteAlarmRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取告警规则详情")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<AlarmRuleRespVO> getAlarmRule(@RequestParam("id") Long id) {
        AlarmRuleDO rule = alarmRuleService.getAlarmRule(id);
        return success(AlarmRuleConvert.INSTANCE.convert(rule));
    }

    @GetMapping("/page")
    @Operation(summary = "获取告警规则分页列表", description = "支持按规则名称、编码、类型、告警类型、级别、启用状态等条件筛选")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<PageResult<AlarmRuleRespVO>> getAlarmRulePage(@Valid AlarmRulePageReqVO pageReqVO) {
        PageResult<AlarmRuleDO> pageResult = alarmRuleService.getAlarmRulePage(pageReqVO);
        return success(AlarmRuleConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有启用的告警规则列表", description = "获取所有启用状态的告警规则，用于规则匹配")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<List<AlarmRuleRespVO>> getEnabledAlarmRuleList() {
        List<AlarmRuleDO> list = alarmRuleService.getEnabledAlarmRuleList();
        return success(AlarmRuleConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-by-device-type")
    @Operation(summary = "根据设备类型获取告警规则列表", description = "获取指定设备类型的所有启用规则")
    @Parameter(name = "deviceType", description = "设备类型", required = true, example = "WATER_LEVEL_SENSOR")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<List<AlarmRuleRespVO>> getAlarmRuleListByDeviceType(@RequestParam("deviceType") String deviceType) {
        List<AlarmRuleDO> list = alarmRuleService.getEnabledAlarmRuleListByDeviceType(deviceType);
        return success(AlarmRuleConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-by-alarm-type")
    @Operation(summary = "根据告警类型获取告警规则列表", description = "获取指定告警类型的所有启用规则")
    @Parameter(name = "alarmTypeId", description = "告警类型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<List<AlarmRuleRespVO>> getAlarmRuleListByAlarmType(@RequestParam("alarmTypeId") Long alarmTypeId) {
        List<AlarmRuleDO> list = alarmRuleService.getEnabledAlarmRuleListByAlarmTypeId(alarmTypeId);
        return success(AlarmRuleConvert.INSTANCE.convertList(list));
    }

    // ========== 规则状态管理 ==========

    @PutMapping("/update-status")
    @Operation(summary = "启用/禁用告警规则", description = "更新告警规则的启用状态")
    @PreAuthorize("@ss.hasPermission('alarm:rule:update')")
    public CommonResult<Boolean> updateAlarmRuleStatus(@RequestParam("id") Long id,
                                                        @RequestParam("enabled") Boolean enabled) {
        alarmRuleService.updateAlarmRuleStatus(id, enabled);
        return success(true);
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用告警规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:rule:update')")
    public CommonResult<Boolean> enableAlarmRule(@PathVariable("id") Long id) {
        alarmRuleService.updateAlarmRuleStatus(id, true);
        return success(true);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用告警规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:rule:update')")
    public CommonResult<Boolean> disableAlarmRule(@PathVariable("id") Long id) {
        alarmRuleService.updateAlarmRuleStatus(id, false);
        return success(true);
    }

    // ========== 规则测试 ==========

    @PostMapping("/test")
    @Operation(summary = "测试告警规则", description = "使用提供的测试数据验证规则的有效性和匹配结果")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<AlarmRuleTestRespVO> testAlarmRule(@Valid @RequestBody AlarmRuleTestReqVO testReqVO) {
        AlarmRuleTestRespVO result = alarmRuleService.testAlarmRule(testReqVO);
        return success(result);
    }

    // ========== 监测字段 ==========

    @GetMapping("/monitor-fields")
    @Operation(summary = "获取监测字段列表", description = "获取可用于告警规则配置的监测字段列表")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<List<MonitorFieldRespVO>> getMonitorFieldList() {
        // TODO: 后续从 IoT 模块动态获取物模型属性
        // 目前返回预定义的常用监测字段
        List<MonitorFieldRespVO> fields = buildDefaultMonitorFields();
        return success(fields);
    }

    @GetMapping("/monitor-fields/by-device-type")
    @Operation(summary = "根据设备类型获取监测字段列表", description = "获取指定设备类型的监测字段列表")
    @Parameter(name = "deviceType", description = "设备类型", example = "WATER_LEVEL_SENSOR")
    @PreAuthorize("@ss.hasPermission('alarm:rule:query')")
    public CommonResult<List<MonitorFieldRespVO>> getMonitorFieldListByDeviceType(
            @RequestParam(value = "deviceType", required = false) String deviceType) {
        // TODO: 后续根据设备类型从 IoT 模块获取对应的物模型属性
        // 目前返回预定义的常用监测字段
        List<MonitorFieldRespVO> fields = buildDefaultMonitorFields();
        return success(fields);
    }

    /**
     * 构建默认的监测字段列表
     * 
     * <p>后续将从 IoT 模块的物模型中动态获取。</p>
     */
    private List<MonitorFieldRespVO> buildDefaultMonitorFields() {
        List<MonitorFieldRespVO> fields = new ArrayList<>();
        
        // 常用字段
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("waterLevel").fieldName("水位")
                .unit("cm").unitName("厘米").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("temperature").fieldName("温度")
                .unit("℃").unitName("摄氏度").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("humidity").fieldName("湿度")
                .unit("%").unitName("百分比").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("pressure").fieldName("压力")
                .unit("kPa").unitName("千帕").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("flowRate").fieldName("流量")
                .unit("m³/h").unitName("立方米/小时").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("voltage").fieldName("电压")
                .unit("V").unitName("伏特").dataType("double").groupName("常用字段").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("current").fieldName("电流")
                .unit("A").unitName("安培").dataType("double").groupName("常用字段").build());
        
        // 环境监测
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("pm25").fieldName("PM2.5")
                .unit("μg/m³").unitName("微克/立方米").dataType("double").groupName("环境监测").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("coLevel").fieldName("CO浓度")
                .unit("ppm").unitName("百万分之一").dataType("double").groupName("环境监测").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("smokeLevel").fieldName("烟雾浓度")
                .unit("%").unitName("百分比").dataType("double").groupName("环境监测").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("visibility").fieldName("能见度")
                .unit("m").unitName("米").dataType("double").groupName("环境监测").build());
        
        // 设备状态
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("status").fieldName("设备状态")
                .unit("").unitName("").dataType("int").groupName("设备状态").build());
        fields.add(MonitorFieldRespVO.builder()
                .fieldCode("online").fieldName("在线状态")
                .unit("").unitName("").dataType("bool").groupName("设备状态").build());
        
        return fields;
    }

}
