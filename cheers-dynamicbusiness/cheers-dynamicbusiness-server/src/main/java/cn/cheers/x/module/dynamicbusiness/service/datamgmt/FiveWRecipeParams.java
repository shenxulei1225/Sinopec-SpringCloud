package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import lombok.Builder;
import lombok.Value;

/**
 * 场景配方参数：按 registry / 分类种类等业务字段填充，禁止按业务编码 switch。
 */
@Value
@Builder
public class FiveWRecipeParams {

    /** 目录注册编码（dm_five_w_orchestration 的 entity_type_code） */
    String registryCode;

    /** 存储类型编码；与 registry 相同时可省略，由模板默认 */
    String storageEntityTypeCode;

    /** 分类种类编码（布局种子用；编排头不再写槽） */
    String categoryTypeCode;

    /** 展示标签（布局种子用；编排头不再写槽） */
    String typeName;

    /** 分类即对象时布局分类栏编号（布局种子用；编排头不再写槽） */
    String categorySlotRef;
}
