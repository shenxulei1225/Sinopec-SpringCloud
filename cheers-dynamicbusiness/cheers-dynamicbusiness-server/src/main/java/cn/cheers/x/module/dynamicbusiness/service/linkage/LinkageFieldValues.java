package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

/**
 * 从联动实体上按字段编码取值。
 *
 * <p>专用列优先，自定义字段其次。不按中文名找列。</p>
 */
final class LinkageFieldValues {

    private LinkageFieldValues() {
    }

    static Object value(EntityDO entity, String fieldCode) {
        if (entity == null || fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        Object value = entity.getDedicatedBaseFieldValues() == null
                ? null
                : entity.getDedicatedBaseFieldValues().get(fieldCode);
        if (value == null && entity.getCustomFields() != null) {
            value = entity.getCustomFields().get(fieldCode);
        }
        return value;
    }
}
