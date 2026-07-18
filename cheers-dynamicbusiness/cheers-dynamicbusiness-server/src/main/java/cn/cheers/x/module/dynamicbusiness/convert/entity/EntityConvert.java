package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体 DO/VO 转换（手写，避免 MapStruct 与 Lombok 在 IDE 中对 {@code Map customFields} 类型不同步）。
 */
public final class EntityConvert {

    public static final EntityConvert INSTANCE = new EntityConvert();

    private EntityConvert() {
    }

    public EntityDO convert(EntityCreateReqVO bean) {
        if (bean == null) {
            return null;
        }
        EntityDO entity = new EntityDO();
        EntityFieldMapsSupport.applyWriteMapsToEntityDO(entity, bean.getBaseFields(), bean.getCustomFields());
        return entity;
    }

    public EntityDO convert(EntityUpdateReqVO bean) {
        if (bean == null) {
            return null;
        }
        EntityDO entity = new EntityDO();
        entity.setId(bean.getId());
        EntityFieldMapsSupport.applyWriteMapsToEntityDO(entity, bean.getBaseFields(), bean.getCustomFields());
        return entity;
    }

    public EntityRespVO convert(EntityDO bean) {
        if (bean == null) {
            return null;
        }
        EntityRespVO respVO = new EntityRespVO();
        respVO.setId(bean.getId());
        respVO.setSort(bean.getSort());
        respVO.setBaseFields(EntityFieldMapsSupport.buildBaseFieldsFromEntityDO(bean));
        respVO.setCustomFields(copyMap(bean.getCustomFields()));
        respVO.setCreateTime(bean.getCreateTime());
        respVO.setUpdateTime(bean.getUpdateTime());
        return respVO;
    }

    public List<EntityRespVO> convertList(List<EntityDO> list) {
        if (list == null) {
            return null;
        }
        List<EntityRespVO> result = new ArrayList<>(list.size());
        for (EntityDO entity : list) {
            result.add(convert(entity));
        }
        return result;
    }

    private static Map<String, Object> copyMap(Map<String, Object> source) {
        return source == null ? null : new LinkedHashMap<>(source);
    }
}
