package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import lombok.Data;
import java.util.Map;

/**
 * 组件 DO 返回值
 *
 * 说明：
 * - 组件是可复用 UI 单元的“默认定义”
 * - 只描述组件本身，不带 module / layout / 嵌套视图信息
 */
@Data
public class ComponentRespVO {

    /** 表主键，对应 componentId */
    private Long id;
    /** 语义化组件编码（与 key 相同，供前端统一使用 componentCode） */
    private String componentCode;
    /** 历史字段，与 componentCode 一致 */
    private String key;
    private String type;
    private String name;
    private String icon;
    /** 默认 props */
    private Object props;
    private EndpointConfig dataConfig;
    private Map<String, EndpointConfig> apiConfig;
    private Object uiConfig;
    private Integer status;
    private Integer sort;
    private String description;

    @Data
    public static class EndpointConfig {
        private String url;
        private String method;
        private Map<String, Object> params;
    }
}