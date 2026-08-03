package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import lombok.Builder;
import lombok.Value;

/**
 * 场景配方参数：按 registry / 分类种类等业务字段填充，禁止按业务编码 switch。
 */
@Value
@Builder
public class FiveWRecipeParams {

    /** 目录注册编码（dm_five_w_* 的 entity_type_code） */
    String registryCode;

    /** 存储类型编码；与 registry 相同时可省略，由模板默认 */
    String storageEntityTypeCode;

    /** 分类槽绑定的 categoryTypeCode */
    String categoryTypeCode;

    /** 分类槽 / 实体槽展示标签 */
    String typeName;

    /** 分类即实体配方：Who 分类槽 slotRef */
    String categorySlotRef;
}
