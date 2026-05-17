package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 场景图层响应 VO
 */
@Data
public class SceneLayerRespVO implements Serializable {
    
    private Long id;
    private String layerCode;
    private String layerName;
    private String layerDisplayName;
    private String layerType;
    private String layerConfigJson;
    private Boolean visibleFlag;
    private Boolean interactiveFlag;
    private Integer sortNo;
    private List<Long> instanceIds;
    private List<SceneLayerFilterRuleVO> filterRules;
}