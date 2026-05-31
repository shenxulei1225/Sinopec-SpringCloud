package cn.cheers.x.module.dynamicbusiness.controller.admin.group;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo.*;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupService;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupTreeNodeWithTargets;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 通用分组")
@RestController
@RequestMapping("/dynamicbusiness/group")
public class GroupController {

    @Resource
    private GroupService groupService;

    @PostMapping("/create")
    @Operation(summary = "创建分组")
    public CommonResult<Long> create(@Valid @RequestBody GroupCreateReqVO reqVO) {
        return success(groupService.createGroup(reqVO.getGroupType(), reqVO.getName(), reqVO.getDescription(), reqVO.getParentId(), reqVO.getSort(), reqVO.getStatus(), reqVO.getCodePrefix()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分组")
    public CommonResult<Boolean> update(@Valid @RequestBody GroupUpdateReqVO reqVO) {
        groupService.updateGroup(reqVO.getGroupType(), reqVO.getId(), reqVO.getName(), reqVO.getDescription(), reqVO.getParentId(), reqVO.getSort(), reqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分组")
    public CommonResult<Boolean> delete(@RequestParam String groupType, @RequestParam Long id) {
        groupService.deleteGroup(groupType, id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取分组详情")
    public CommonResult<GroupRespVO> get(@RequestParam String groupType, @RequestParam Long id) {
        return success(toResp(groupService.getGroup(groupType, id)));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询分组")
    public CommonResult<PageResult<GroupRespVO>> page(@Valid GroupPageReqVO reqVO) {
        PageResult<GroupDO> page = groupService.pageGroups(reqVO.getGroupType(), reqVO, reqVO.getName(), reqVO.getStatus());
        return success(new PageResult<>(page.getList().stream().map(this::toResp).collect(Collectors.toList()), page.getTotal()));
    }

    @GetMapping("/tree")
    @Operation(summary = "分组树")
    public CommonResult<List<GroupRespVO>> tree(@RequestParam String groupType) {
        return success(groupService.treeGroups(groupType).stream().map(this::toResp).collect(Collectors.toList()));
    }

    @GetMapping("/tree-with-targets")
    @Operation(summary = "分组树（含目标列表）")
    public CommonResult<List<GroupRespVO>> treeWithTargets(@RequestParam String groupType,
                                                            @RequestParam(defaultValue = "FIELD") String targetType) {
        return success(groupService.treeGroupsWithTargets(groupType, targetType)
                .stream()
                .map(this::toRespWithTargets)
                .collect(Collectors.toList()));
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定目标")
    public CommonResult<Boolean> bind(@Valid @RequestBody GroupBindReqVO reqVO) {
        groupService.bindTarget(reqVO.getGroupType(), reqVO.getTargetId(), reqVO.getGroupId());
        return success(true);
    }

    @PostMapping("/unbind")
    @Operation(summary = "解绑目标")
    public CommonResult<Boolean> unbind(@Valid @RequestBody GroupBindReqVO reqVO) {
        groupService.unbindTarget(reqVO.getGroupType(), reqVO.getTargetId(), reqVO.getGroupId());
        return success(true);
    }

    @GetMapping("/get-target-ids")
    @Operation(summary = "获取分组下目标ID")
    public CommonResult<List<Long>> getTargetIds(@RequestParam String groupType, @RequestParam Long groupId) {
        return success(groupService.getTargetIdsByGroup(groupType, groupId));
    }

    @PostMapping("/batch-bind")
    @Operation(summary = "批量绑定目标")
    public CommonResult<Boolean> batchBind(@Valid @RequestBody GroupBatchBindReqVO reqVO) {
        groupService.batchBindTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getTargetIds());
        return success(true);
    }

    @PostMapping("/batch-unbind")
    @Operation(summary = "批量解绑目标")
    public CommonResult<Boolean> batchUnbind(@Valid @RequestBody GroupBatchBindReqVO reqVO) {
        groupService.batchUnbindTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getTargetIds());
        return success(true);
    }

    @PostMapping("/reorder")
    @Operation(summary = "组内排序")
    public CommonResult<Boolean> reorder(@Valid @RequestBody GroupReorderReqVO reqVO) {
        groupService.reorderTargets(reqVO.getGroupType(), reqVO.getGroupId(), reqVO.getOrderedTargetIds());
        return success(true);
    }

    @PostMapping("/reorder-group")
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
