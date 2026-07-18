package cn.cheers.x.system.controller.admin.view.vo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 保存用户布局 Request VO
 */
@Data
public class ViewSaveLayoutReqVO {

    /**
     * 布局 ID
     */
    private String layoutId;

    /**
     * 布局名称（用户自定义时使用）
     */
    private String name;

    /**
     * 布局 schema
     */
    @NotNull(message = "布局 schema 不能为空")
    private Object schema;

    /**
     * 覆盖内容（如果基于模板修改）
     */
    private Object override;
}
