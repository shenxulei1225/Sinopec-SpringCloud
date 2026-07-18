package cn.cheers.x.scene.platform.controller.admin.scene.vo;

import cn.cheers.x.scene.platform.controller.admin.actor.vo.ActorInstanceRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景加载响应 VO
 * 合并 DB 定义 + Redis 运行时数据
 *
 * @author Sinopec
 */
@Schema(description = "管理后台 - 场景加载响应 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneLoadRespVO {

    @Schema(description = "场景详情")
    private SceneDetailRespVO scene;

    @Schema(description = "ActorInstance 实例列表（合并运行时数据）")
    private List<ActorInstanceRespVO> actorInstances;

    @Schema(description = "场景组件列表")
    private List<SceneComponentRespVO> sceneComponents;

    @Schema(description = "在线用户数")
    private Integer onlineCount;
}
