package cn.cheers.x.scene.platform.controller.admin.actor;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ActorRespVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ActorSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ComponentTreeRespVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ComponentTreeNodeReqVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ComponentTreeNodeRespVO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import cn.cheers.x.scene.platform.model.ComponentTree;
import cn.cheers.x.scene.platform.model.ComponentTreeNode;
import cn.cheers.x.scene.platform.service.actor.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Actor")
@RestController
@RequestMapping("/scene-3d/actors")
@Validated
public class ActorController {

    @Resource
    private ActorService actorService;

    // ========== Actor CRUD ==========

    @GetMapping
    @Operation(summary = "获得 Actor 列表")
    public CommonResult<List<ActorRespVO>> getActorList() {
        return success(actorService.getActorList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Actor 详情")
    public CommonResult<ActorRespVO> getActor(@PathVariable Long id) {
        return success(convertToRespVO(actorService.getActor(id)));
    }

    @PostMapping
    @Operation(summary = "创建 Actor")
    public CommonResult<Long> createActor(@Valid @RequestBody ActorSaveReqVO reqVO) {
        return success(actorService.createActor(reqVO.toDO()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Actor")
    public CommonResult<Boolean> updateActor(@PathVariable Long id,
                                              @Valid @RequestBody ActorSaveReqVO reqVO) {
        actorService.updateActor(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Actor 默认值")
    public CommonResult<Boolean> updateActorDefaults(@PathVariable Long id,
                                                     @Valid @RequestBody ActorSaveReqVO reqVO) {
        actorService.updateActorDefaults(id, reqVO.toDO());
        return success(true);
    }

    // ========== 组件树管理 ==========

    @GetMapping("/{actorCode}/component-tree")
    @Operation(summary = "获取 Actor 组件树")
    public CommonResult<ComponentTreeRespVO> getComponentTree(@PathVariable String actorCode) {
        ComponentTree tree = actorService.getComponentTree(actorCode);
        return success(convertToComponentTreeRespVO(tree));
    }

    @PostMapping("/{actorCode}/components")
    @Operation(summary = "添加组件节点")
    public CommonResult<Boolean> addComponent(
            @PathVariable String actorCode,
            @RequestParam(defaultValue = "root") String parentCode,
            @Valid @RequestBody ComponentTreeNodeReqVO reqVO) {
        ComponentTreeNode newNode = BeanUtils.toBean(reqVO, ComponentTreeNode.class);
        actorService.addComponent(actorCode, parentCode, newNode);
        return success(true);
    }

    @DeleteMapping("/{actorCode}/components/{componentCode}")
    @Operation(summary = "删除组件节点")
    public CommonResult<Boolean> removeComponent(
            @PathVariable String actorCode,
            @PathVariable String componentCode) {
        actorService.removeComponent(actorCode, componentCode);
        return success(true);
    }

    @PutMapping("/{actorCode}/components/{componentCode}")
    @Operation(summary = "更新组件节点")
    public CommonResult<Boolean> updateComponent(
            @PathVariable String actorCode,
            @PathVariable String componentCode,
            @Valid @RequestBody ComponentTreeNodeReqVO reqVO) {
        ComponentTreeNode updates = BeanUtils.toBean(reqVO, ComponentTreeNode.class);
        actorService.updateComponent(actorCode, componentCode, updates);
        return success(true);
    }

    // ========== 转换方法 ==========

    private ActorRespVO convertToRespVO(ActorDO item) {
        ActorRespVO vo = BeanUtils.toBean(item, ActorRespVO.class);
        if (item.getComponentTree() != null) {
            vo.setComponentTree(convertToComponentTreeRespVO(item.getComponentTree()));
        }
        return vo;
    }

    private ComponentTreeRespVO convertToComponentTreeRespVO(ComponentTree tree) {
        if (tree == null || tree.getRoot() == null) {
            return new ComponentTreeRespVO();
        }
        ComponentTreeRespVO respVO = new ComponentTreeRespVO();
        respVO.setRoot(convertToTreeNodeRespVO(tree.getRoot()));
        return respVO;
    }

    private ComponentTreeNodeRespVO convertToTreeNodeRespVO(ComponentTreeNode node) {
        ComponentTreeNodeRespVO respVO = BeanUtils.toBean(node, ComponentTreeNodeRespVO.class);
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            respVO.setChildren(node.getChildren().stream()
                    .map(this::convertToTreeNodeRespVO)
                    .collect(Collectors.toList()));
        }
        return respVO;
    }
}
