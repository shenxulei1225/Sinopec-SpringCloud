package cn.cheers.x.alarm.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.linkage.*;
import cn.cheers.x.alarm.convert.LinkageRuleConvert;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import cn.cheers.x.alarm.service.linkage.LinkageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 联动规则管理 Controller
 * 
 * <p>提供联动规则的 CRUD、启用/禁用、测试等功能。</p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>联动规则 CRUD 操作（FR-010）</li>
 *   <li>规则启用/禁用（FR-010）</li>
 *   <li>规则测试（FR-010）</li>
 * </ul>
 * </p>
 * 
 * <p>支持的联动动作类型：
 * <ul>
 *   <li>NOTIFICATION - 发送通知</li>
 *   <li>WORK_ORDER - 创建工单</li>
 *   <li>DEVICE_CONTROL - 设备控制</li>
 *   <li>VIDEO_LINKAGE - 视频联动</li>
 *   <li>ACCESS_CONTROL - 门禁控制</li>
 *   <li>FIRE_CONTROL - 消防控制</li>
 *   <li>SCRIPT - 执行脚本</li>
 *   <li>API_CALL - 调用API</li>
 * </ul>
 * </p>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>BR-BIZ-004：消防设施控制必须优先于其他联动动作执行</li>
 *   <li>BR-PRM-001：联动规则配置需要管理员权限</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Tag(name = "管理后台 - 联动规则管理")
@RestController
@RequestMapping("/alarm/linkage-rules")
@Validated
public class LinkageRuleController {

    @Resource
    private LinkageService linkageService;

    // ========== 联动规则 CRUD ==========

    @PostMapping("/create")
    @Operation(summary = "创建联动规则", description = "创建新的联动规则，配置告警触发后的联动动作")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:create')")
    public CommonResult<Long> createLinkageRule(@Valid @RequestBody LinkageRuleCreateReqVO createReqVO) {
        Long ruleId = linkageService.createLinkageRule(createReqVO);
        return success(ruleId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新联动规则", description = "更新已有的联动规则，规则编码不可修改")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:update')")
    public CommonResult<Boolean> updateLinkageRule(@Valid @RequestBody LinkageRuleUpdateReqVO updateReqVO) {
        linkageService.updateLinkageRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除联动规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:delete')")
    public CommonResult<Boolean> deleteLinkageRule(@RequestParam("id") Long id) {
        linkageService.deleteLinkageRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取联动规则详情")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:query')")
    public CommonResult<LinkageRuleRespVO> getLinkageRule(@RequestParam("id") Long id) {
        LinkageRuleDO rule = linkageService.getLinkageRule(id);
        return success(LinkageRuleConvert.INSTANCE.convert(rule));
    }

    @GetMapping("/page")
    @Operation(summary = "获取联动规则分页列表", description = "支持按规则名称、编码、告警规则、告警类型、级别、启用状态等条件筛选")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:query')")
    public CommonResult<PageResult<LinkageRuleRespVO>> getLinkageRulePage(@Valid LinkageRulePageReqVO pageReqVO) {
        PageResult<LinkageRuleDO> pageResult = linkageService.getLinkageRulePage(pageReqVO);
        return success(LinkageRuleConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有启用的联动规则列表", description = "获取所有启用状态的联动规则")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:query')")
    public CommonResult<List<LinkageRuleRespVO>> getEnabledLinkageRuleList() {
        List<LinkageRuleDO> list = linkageService.getEnabledLinkageRules();
        return success(LinkageRuleConvert.INSTANCE.convertList(list));
    }

    // ========== 规则状态管理 ==========

    @PutMapping("/update-status")
    @Operation(summary = "启用/禁用联动规则", description = "更新联动规则的启用状态")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:update')")
    public CommonResult<Boolean> updateLinkageRuleStatus(@RequestParam("id") Long id,
                                                          @RequestParam("enabled") Boolean enabled) {
        linkageService.toggleLinkageRuleStatus(id, enabled);
        return success(true);
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用联动规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:update')")
    public CommonResult<Boolean> enableLinkageRule(@PathVariable("id") Long id) {
        linkageService.toggleLinkageRuleStatus(id, true);
        return success(true);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用联动规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:update')")
    public CommonResult<Boolean> disableLinkageRule(@PathVariable("id") Long id) {
        linkageService.toggleLinkageRuleStatus(id, false);
        return success(true);
    }

    // ========== 规则测试 ==========

    @PostMapping("/test")
    @Operation(summary = "测试联动规则", description = "测试联动规则配置的有效性，支持模拟执行（不实际控制设备）")
    @PreAuthorize("@ss.hasPermission('alarm:linkage-rule:query')")
    public CommonResult<LinkageRuleTestRespVO> testLinkageRule(@Valid @RequestBody LinkageRuleTestReqVO testReqVO) {
        LinkageRuleTestRespVO result = linkageService.testLinkageRule(testReqVO);
        return success(result);
    }

}
