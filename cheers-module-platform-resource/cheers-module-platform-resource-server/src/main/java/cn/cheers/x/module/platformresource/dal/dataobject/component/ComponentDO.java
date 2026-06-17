package cn.cheers.x.module.platformresource.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 组件库目录 DO：登记可复用 UI 单元。
 * 用户偏好 props 见 {@link ComponentPropsDO}；接口契约见业务能力模块 component_interface。
 * 前端实现路径由前端组件注册表维护，不在此表。
 */
@Data
@TableName("pr_component")
public class ComponentDO extends BaseDO {

    @TableId
    private Long id;

    /** 组件唯一编码，与 {@link ComponentPropsDO#getComponentCode()} 一致 */
    private String componentCode;

    /** 组件分类：list / tree / primitive / layout 等 */
    private String type;

    /** 展示名称 */
    private String name;

    /** 图标 */
    private String icon;

    /** 状态 (0-禁用, 1-启用) */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 描述 */
    private String description;
}
