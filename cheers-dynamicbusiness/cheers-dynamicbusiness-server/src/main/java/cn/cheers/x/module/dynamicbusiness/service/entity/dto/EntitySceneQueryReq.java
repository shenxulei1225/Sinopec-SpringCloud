package cn.cheers.x.module.dynamicbusiness.service.entity.dto;

import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import lombok.Data;

/**
 * 统一实体查询请求参数
 *
 * <p>通过 {@link EntityQueryScene} 明确表达查询意图，避免 Service 方法参数过多导致的可读性和维护性问题。</p>
 */
@Data
public class EntitySceneQueryReq {

    /** 查询场景（必填） */
    private EntityQueryScene scene;

    /** 业务类型编码（部分场景必填） */
    private String entityTypeCode;

    /** 模型ID（部分场景必填） */
    private Long modelId;

    /** 分类ID（部分场景必填） */
    private Long categoryId;

    /** 实体ID（部分场景必填） */
    private Long entityId;

    /** 根实体ID（部分场景必填） */
    private Long rootEntityId;

    /** 实体来源业务类型编码（模式C必填） */
    private String entitySourceEntityType;

    /** 页码（LIST形态时使用，默认1） */
    private Integer pageNo;

    /** 每页条数（LIST形态时使用，默认20） */
    private Integer pageSize;

    /** 搜索关键词（可选） */
    private String keyword;
}

