package cn.cheers.x.scene.platform.controller.admin.scene.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 场景图层筛选规则 VO
 */
@Data
public class SceneLayerFilterRuleVO implements Serializable {
    
    /** 字段名 */
    private String field;
    
    /** 操作符：eq/ne/like/in/between */
    private String op;
    
    /** 值 */
    private Object value;
    
    /** 值列表 */
    private List<Object> values;
}