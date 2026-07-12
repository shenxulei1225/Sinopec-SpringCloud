package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体 VO → 跨模块 RPC DTO 转换。
 */
public final class EntityDtoConvert {

    private EntityDtoConvert() {
    }

    public static EntityRespDTO toDto(EntityRespVO vo) {
        if (vo == null) {
            return null;
        }
        EntityRespDTO dto = new EntityRespDTO();
        dto.setId(vo.getId());
        dto.setSort(vo.getSort());
        dto.setBaseFields(vo.getBaseFields());
        dto.setCustomFields(vo.getCustomFields());
        dto.setModelName(vo.getModelName());
        dto.setCreateTime(vo.getCreateTime());
        dto.setUpdateTime(vo.getUpdateTime());
        return dto;
    }

    public static List<EntityRespDTO> toDtoList(List<EntityRespVO> vos) {
        if (vos == null || vos.isEmpty()) {
            return List.of();
        }
        List<EntityRespDTO> result = new ArrayList<>(vos.size());
        for (EntityRespVO vo : vos) {
            EntityRespDTO dto = toDto(vo);
            if (dto != null) {
                result.add(dto);
            }
        }
        return result;
    }

}
