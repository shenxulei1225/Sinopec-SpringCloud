package cn.cheers.x.module.dynamicbusiness.controller.admin.group;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo.*;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupService;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupTreeNodeWithTargets;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 通用分组：字段库分组、单位分组、数据目录侧栏分组。
 * 这些都是平台配置，不是某本数据目录的业务数据，所以只看菜单权限（system:group:*），不按目录授权核对。
 */
@Tag(name = "管理后台 - 通用分组")
@RestController
@RequestMapping("/dynamicbusiness/group")
public class GroupController {

    @Resource
    private GroupService groupService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('system:group:create')")
    @Operation(summary = "创建分组")
    public CommonResult<Long> create(@Valid @RequestBody GroupCreateReqVO reqVO) {
        return success(groupService.createGroup(reqVO.getGroupType(), reqVO.getName(), reqVO.getDescription(), reqVO.getParentId(), reqVO.getSort(), reqVO.getStatus(), reqVO.getCodePrefix()));
    }

    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "更新分组")
    public CommonResult<Boolean> update(@Valid @RequestBody GroupUpdateReqVO reqVO) {
        groupService.updateGroup(reqVO.getGroupType(), reqVO.getId(), reqVO.getName(), reqVO.getDescription(), reqVO.getParentId(), reqVO.getSort(), reqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('system:group:delete')")
    @Operation(summary = "删除分组")
    public CommonResult<Boolean> delete(@RequestParam String groupType, @RequestParam Long id) {
        groupService.deleteGroup(groupType, id);
        return success(true);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:group:query')")
    @Operation(summary = "获取分组详情")
    public CommonResult<GroupRespVO> get(@RequestParam String groupType, @RequestParam Long id) {
        return success(toResp(groupService.getGroup(groupType, id)));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:group:query')")
    @Operation(summary = "分页查询分组")
    public CommonResult<PageResult<GroupRespVO>> page(@Valid GroupPageReqVO reqVO) {
        PageResult<GroupDO> page = groupService.pageGroups(reqVO.getGroupType(), reqVO, reqVO.getName(), reqVO.getStatus());
        return success(new PageResult<>(page.getList().stream().map(this::toResp).collect(Collectors.toList()), page.getTotal()));
    }

    @GetMapping("/tree")
    @PreAuthorize("@ss.hasPermission('system:group:query')")
    @Operation(summary = "分组树")
    public CommonResult<List<GroupRespVO>> tree(@RequestParam String groupType) {
        return success(groupService.treeGroups(groupType).stream().map(this::toResp).collect(Collectors.toList()));
    }

    @GetMapping("/tree-with-targets")
    @PreAuthorize("@ss.hasPermission('system:group:query')")
    @Operation(summary = "分组树（含目标列表）")
    public CommonResult<List<GroupRespVO>> treeWithTargets(@RequestParam String groupType,
                                                            @RequestParam(defaultValue = "FIELD") String targetType) {
        return success(groupService.treeGroupsWithTargets(groupType, targetType)
                .stream()
                .map(this::toRespWithTargets)
                .collect(Collectors.toList()));
    }

    @PostMapping("/bind")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "绑定目标")
    public CommonResult<Boolean> bind(@Valid @RequestBody GroupBindReqVO reqVO) {
        groupService.bindTarget(reqVO.getGroupType(), reqVO.getTargetId(), reqVO.getGroupId());
        return success(true);
    }

    @PostMapping("/unbind")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "解绑目标")
    public CommonResult<Boolean> unbind(@Valid @RequestBody GroupBindReqVO reqVO) {
        groupService.unbindTarget(reqVO.getGroupType(), reqVO.getTargetId(), reqVO.getGroupId());
        return success(true);
    }

    @GetMapping("/get-target-ids")
    @PreAuthorize("@ss.hasPermission('system:group:query')")
    @Operation(summary = "获取分组下目标ID")
    public CommonResult<List<Long>> getTargetIds(@RequestParam String groupType, @RequestParam Long groupId) {
        return success(groupService.getTargetIdsByGroup(groupType, groupId));
    }

    @PostMapping("/batch-bind")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "批量绑定目标")
    public CommonResult<Boolean> batchBind(@Valid @RequestBody GroupBatchBindReqVO reqVO) {
        groupService.batchBindTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getTargetIds());
        return success(true);
    }

    @PostMapping("/batch-unbind")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "批量解绑目标")
    public CommonResult<Boolean> batchUnbind(@Valid @RequestBody GroupBatchBindReqVO reqVO) {
        groupService.batchUnbindTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getTargetIds());
        return success(true);
    }

    @PostMapping("/reorder")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "组内排序")
    public CommonResult<Boolean> reorder(@Valid @RequestBody GroupReorderReqVO reqVO) {
        groupService.reorderTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getOrderedTargetIds());
        return success(true);
    }

    @PostMapping("/reorder-group")
    @PreAuthorize("@ss.hasPermission('system:group:update')")
    @Operation(summary = "分组重排")
    public CommonResult<Boolean> reorderGroup(@Valid @RequestBody GroupReorderGroupReqVO reqVO) {
        groupService.reorderGroup(reqVO.getGroupType(), reqVO.getMovedGroupId(), reqVO.getPrevGroupId(), reqVO.getNextGroupId());
        return success(true);
    }

    private GroupRespVO toResp(GroupDO d) {
        GroupRespVO vo = new GroupRespVO();
        vo.setId(d.getId());
        vo.setGroupType(d.getGroupType());
        vo.setCode(d.getCode());
        vo.setName(d.getName());
        vo.setDescription(d.getDescription());
        vo.setParentId(d.getParentId());
        vo.setPath(d.getPath());
        vo.setLevel(d.getLevel());
        vo.setSort(d.getSort());
        vo.setStatus(d.getStatus());
        vo.setCreateTime(d.getCreateTime());
        return vo;
    }

    private GroupRespVO toResp(GroupTreeNode d) {
        GroupRespVO vo = new GroupRespVO();
        vo.setId(d.getId());
        vo.setCode(d.getCode());
        vo.setName(d.getName());
        vo.setDescription(d.getDescription());
        vo.setParentId(d.getParentId());
        vo.setPath(d.getPath());
        vo.setLevel(d.getLevel());
        vo.setSort(d.getSort());
        vo.setStatus(d.getStatus());
        vo.setCreateTime(d.getCreateTime());
        if (d.getChildren() != null) {
            vo.setChildren(d.getChildren().stream().map(this::toResp).collect(Collectors.toList()));
        }
        return vo;
    }

    private GroupRespVO toRespWithTargets(GroupTreeNodeWithTargets d) {
        GroupRespVO vo = new GroupRespVO();
        vo.setId(d.getId());
        vo.setCode(d.getCode());
        vo.setName(d.getName());
        vo.setDescription(d.getDescription());
        vo.setParentId(d.getParentId());
        vo.setPath(d.getPath());
        vo.setLevel(d.getLevel());
        vo.setSort(d.getSort());
        vo.setStatus(d.getStatus());
        vo.setCreateTime(d.getCreateTime());
        if (d.getTargets() != null) {
            vo.setTargets(d.getTargets().stream()
                    .map(target -> {
                        if (target instanceof cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO field) {
                            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                            map.put("id", field.getId());
                            map.put("code", field.getCode());
                            map.put("name", field.getName());
                            map.put("type", field.getType());
                            map.put("unit", field.getUnit());
                            map.put("description", field.getDescription());
                            map.put("source", field.getSource());
                            map.put("status", field.getStatus());
                            map.put("indexStrategy", field.getIndexStrategy());
                            map.put("options", field.getOptions());
                            return map;
                        }
                        return null;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        if (d.getChildren() != null) {
            vo.setChildren(d.getChildren().stream().map(this::toRespWithTargets).collect(Collectors.toList()));
        }
        return vo;
    }
}
