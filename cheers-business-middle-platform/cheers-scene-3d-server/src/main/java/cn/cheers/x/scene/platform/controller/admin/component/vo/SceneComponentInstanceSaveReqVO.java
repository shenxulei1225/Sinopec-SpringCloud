package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.SceneComponentInstanceDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 场景组件挂载保存请求 VO")
@Data
public class SceneComponentInstanceSaveReqVO {

    private Long sceneId;

    private String sceneCode;

    private String instanceCode;

    private String componentCode;

    private Boolean enabledFlag;

    private String configJson;

    private String metadataJson;

    private Integer sortNo;

    public SceneComponentInstanceDO toDO() {
        SceneComponentInstanceDO item = new SceneComponentInstanceDO();
        item.setSceneId(sceneId);
        item.setSceneCode(sceneCode);
        item.setInstanceCode(instanceCode);
        item.setComponentCode(componentCode);
        item.setEnabledFlag(enabledFlag);
        item.setConfigJson(configJson);
        item.setMetadataJson(metadataJson);
        item.setSortNo(sortNo);
        return item;
    }
}
