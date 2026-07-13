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
     * 从指定业务类型下的全部模型移除字段库固定列分配，并清理分组引用。
     */
    void removeLibraryFieldFromAllModels(String entityTypeCode, Long libraryFieldId);
}
