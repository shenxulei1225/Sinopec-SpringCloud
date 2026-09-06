package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;

/**
 * 固定列字段与字段库、模型分配的同步能力。
 *
 * <p>权威：类型「启用中」的基础字段（status=1）。停用字段不得挂在型号分配上。
 */
public interface BaseFieldLibrarySyncService {

    /**
     * 将字段库中的固定列字段同步到指定业务类型下的全部模型（field_source=BASE）。
     * 若基础字段已停用（status≠1），改为从各型号卸下，不写入。
     */
    void assignLibraryFieldToAllModels(
            String entityTypeCode,
            FieldDO libraryField,
            EntityTypeBaseFieldDO baseField);

    /**
     * 将指定业务类型「启用中」的固定列写入单个型号的分配表（仅补缺），
     * 并卸下已不在启用集中的 BASE 分配（含停用残留）。
     * 用于新建型号：读路径不再虚合并 BASE，必须在写路径物化。
     *
     * @return 新插入的分配行数
     */
    int assignAllBaseFieldsToModel(Long modelId);

    /**
     * 同步某业务类型下全部型号的基础字段分配：按启用集补缺，并清理停用/已删字段的僵尸 BASE 行。
     *
     * <p>日常增删基础字段 / 新建型号不走此方法（走 {@link #assignLibraryFieldToAllModels} /
     * {@link #assignAllBaseFieldsToModel}）。停用时走 {@link #removeLibraryFieldFromAllModels}。
     * 本方法亦用于存量清理（如本地对齐幽灵字段）。</p>
     *
     * @return 新插入的分配行总数
     */
    int syncAllBaseFieldsForEntityType(String entityTypeCode);

    /**
     * 从指定业务类型下的全部模型移除字段库固定列分配，并清理分组引用。
     */
    void removeLibraryFieldFromAllModels(String entityTypeCode, Long libraryFieldId);
}
