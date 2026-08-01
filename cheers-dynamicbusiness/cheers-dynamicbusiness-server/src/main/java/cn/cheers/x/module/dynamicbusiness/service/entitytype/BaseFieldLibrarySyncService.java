package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;

/**
 * 固定列字段与字段库、模型分配的同步能力。
 */
public interface BaseFieldLibrarySyncService {

    /**
     * 将字段库中的固定列字段同步到指定业务类型下的全部模型（field_source=BASE）。
     */
    void assignLibraryFieldToAllModels(
            String entityTypeCode,
            FieldDO libraryField,
            EntityTypeBaseFieldDO baseField);

    /**
     * 将指定业务类型已登记的全部固定列写入单个型号的分配表（仅补缺，不删自定义字段）。
     * 用于新建型号：读路径不再虚合并 BASE，必须在写路径物化。
     *
     * @return 新插入的分配行数
     */
    int assignAllBaseFieldsToModel(Long modelId);

    /**
     * 同步某业务类型下全部型号的基础字段分配：清理指向已删字段库的僵尸 BASE 行，再按当前基础字段补缺。
     *
     * <p>日常增删基础字段 / 新建型号不走此方法（走 {@link #assignLibraryFieldToAllModels} /
     * {@link #assignAllBaseFieldsToModel}）。本方法预留给后续「数据清理工具」整类型对齐，暂不挂产品入口。</p>
     *
     * @return 新插入的分配行总数
     */
    int syncAllBaseFieldsForEntityType(String entityTypeCode);

    /**
     * 从指定业务类型下的全部模型移除字段库固定列分配，并清理分组引用。
     */
    void removeLibraryFieldFromAllModels(String entityTypeCode, Long libraryFieldId);
}
