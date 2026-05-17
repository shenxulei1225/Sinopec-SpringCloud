package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 场景图层保存请求 VO
 */
@Data
public class SceneLayerSaveReqVO implements Serializable {
    
    @NotBlank(message = "图层编码不能为空")
    private String layerCode;
    
    @NotBlank(message = "图层名称不能为空")
    private String layerName;
    
    private String layerDisplayName;
    private String layerType;
    private String layerConfigJson;
    private Boolean visibleFlag;
    private Boolean interactiveFlag;
    private Integer sortNo;
    
    /** 图层实例ID列表 */
    private List<Long> instanceIds;
    
    /** 图层筛选规则 */
    private List<SceneLayerFilterRuleVO> filterRules;
}