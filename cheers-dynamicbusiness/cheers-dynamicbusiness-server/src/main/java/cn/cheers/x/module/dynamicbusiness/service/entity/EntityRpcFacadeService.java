package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDtoConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 通用实体读 RPC 门面：委托 {@link EntityService} / {@link EntityCoreService}，投影为 {@link EntityRespDTO}。
 */
@Service
public class EntityRpcFacadeService {

    @Resource
    private EntityService entityService;
    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private CustomFieldValidationService customFieldValidationService;

    public EntityRespDTO get(Long id, String entityTypeCode) {
        EntityRespVO vo = entityService.get(id, entityTypeCode);
        return EntityDtoConvert.toDto(vo);
    }

    public List<EntityRespDTO> listByIds(List<Long> ids, String entityTypeCode) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<EntityDO> entities = entityCoreService.listByIds(ids, entityTypeCode);
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(entities, customFieldValidationService);
        return EntityDtoConvert.toDtoList(vos);
    }

    public boolean exists(Long id, String entityTypeCode) {
        return entityCoreService.existsById(id, entityTypeCode);
    }

    public List<EntityRespDTO> list(String entityTypeCode, Long modelId) {
        List<EntityDO> entities = entityCoreService.listEntities(entityTypeCode, modelId, null);
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(entities, customFieldValidationService);
        return EntityDtoConvert.toDtoList(vos);
    }

    public EntityRespDTO getByCode(String code, String entityTypeCode) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        return list(entityTypeCode, null).stream()
                .filter(dto -> Objects.equals(code, EntityRpcDtoSupport.readCode(dto)))
                .findFirst()
                .orElse(null);
    }

    public List<EntityRespDTO> listByCodes(List<String> codes, String entityTypeCode) {
        if (CollUtil.isEmpty(codes)) {
            return Collections.emptyList();
        }
        Set<String> codeSet = new HashSet<>(codes);
        return list(entityTypeCode, null).stream()
                .filter(dto -> codeSet.contains(EntityRpcDtoSupport.readCode(dto)))
                .toList();
    }

}
