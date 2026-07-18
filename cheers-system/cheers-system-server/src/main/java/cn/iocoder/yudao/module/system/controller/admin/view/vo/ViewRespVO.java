package cn.iocoder.yudao.module.system.controller.admin.view.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * View DO 返回值
 *
 * 说明：
 * - View 是给用户直接使用的“现成页面/模块”
 * - View 自身只描述页面结构和槽位，不再承载单一组件语义
 */
@Data
public class ViewRespVO {

    /** 视图唯一标识 */
    private String key;

    /** 视图展示名 */
    private String label;

    /** 图标 */
    private String icon;

    /**
     * 视图内容定义（JSON）
     *
     * 结构建议：
     * {
     *   layoutSchema: {...},
     *   items: [
     *     { id, kind: "component", componentId, overrides },
     *     { id, kind: "view", viewKey, overrides }
     *   ]
     * }
     */
    private Object composition;

    /** 是否作为模板展示 */
    private Boolean isTemplate;

    /** 视图扩展元数据（可选） */
    private Object uiConfig;

    /** 布局配置（可选，保留给外部使用） */
    private LayoutConfig layoutConfig;

    /** 可用布局列表 */
    private List<LayoutTemplate> layouts;

    /** 状态 */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 描述 */
    private String description;

    @Data
    public static class LayoutConfig {
        private EndpointConfig endpoint;
        private Object schema;
    }

    @Data
    public static class EndpointConfig {
        private String url;
        private String method;
        private Map<String, Object> params;
    }

    @Data
    public static class LayoutTemplate {
        private String id;
        private String name;
        private String thumbnail;
        private Object schema;
        private Boolean isSystem;
        private Object override;
    }
}