package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDtoConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Resource
    private ModelMapper modelMapper;

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

    /**
     * 按型号编码创建实体。缺型号或底座类型 → 报缺口，不猜默认型号。
     */
    public Long create(EntityWriteReqDTO req) {
        ModelDO model = requireModel(req);
        String entityTypeCode = requireEntityType(req, model);
        EntityCreateReqVO vo = new EntityCreateReqVO();
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", entityTypeCode);
        baseFields.put("modelId", model.getId());
        if (StrUtil.isNotBlank(req.getName())) {
            baseFields.put("name", req.getName().trim());
        }
        baseFields.put("status", 1);
        vo.setBaseFields(baseFields);
        vo.setCustomFields(copyFields(req.getFields()));
        return entityService.create(vo);
    }

    /**
     * 按字段编码覆盖更新。先读旧值再合并，避免只带部分字段时把未传列抹掉。
     * 本次请求里的键若已在旧详情的基础列里（如步骤图默认空数组），必须用本次值盖掉，不能让旧空值再写回去。
     */
    public void updateFields(EntityWriteReqDTO req) {
        if (req == null || req.getId() == null) {
            throw new ServiceException(400, "实体 id 不能为空");
        }
        String entityTypeCode = StrUtil.trim(req.getEntityTypeCode());
        if (StrUtil.isBlank(entityTypeCode)) {
            throw new ServiceException(400, "底座类型编码不能为空");
        }
        EntityRespVO old = entityService.get(req.getId(), entityTypeCode);
        if (old == null) {
            throw new ServiceException(404, "实体不存在");
        }
        Map<String, Object> baseFields = old.getBaseFields() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(old.getBaseFields());
        if (StrUtil.isNotBlank(req.getName())) {
            baseFields.put("name", req.getName().trim());
        }
        baseFields.put("entityTypeCode", entityTypeCode);
        Map<String, Object> customFields = old.getCustomFields() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(old.getCustomFields());
        Map<String, Object> incoming = copyFields(req.getFields());
        customFields.putAll(incoming);
        for (Map.Entry<String, Object> entry : incoming.entrySet()) {
            if (entry.getKey() != null && baseFields.containsKey(entry.getKey())) {
                baseFields.put(entry.getKey(), entry.getValue());
            }
        }
        EntityUpdateReqVO vo = new EntityUpdateReqVO();
        vo.setId(req.getId());
        vo.setBaseFields(baseFields);
        vo.setCustomFields(customFields);
        entityService.update(vo);
    }

    public void delete(Long id, String entityTypeCode) {
        if (id == null) {
            throw new ServiceException(400, "实体 id 不能为空");
        }
        if (StrUtil.isBlank(entityTypeCode)) {
            throw new ServiceException(400, "底座类型编码不能为空");
        }
        EntityDeleteReqVO req = new EntityDeleteReqVO();
        req.setId(id);
        req.setEntityTypeCode(entityTypeCode.trim());
        entityService.delete(req);
    }

    public Long getModelIdByCode(String modelCode) {
        if (StrUtil.isBlank(modelCode)) {
            throw new ServiceException(400, "型号编码不能为空");
        }
        ModelDO model = modelMapper.selectByCode(modelCode.trim());
        if (model == null || model.getId() == null) {
            throw new ServiceException(400, "型号不存在：" + modelCode);
        }
        return model.getId();
    }

    private ModelDO requireModel(EntityWriteReqDTO req) {
        if (req == null || StrUtil.isBlank(req.getModelCode())) {
            throw new ServiceException(400, "型号编码不能为空");
        }
        ModelDO model = modelMapper.selectByCode(req.getModelCode().trim());
        if (model == null || model.getId() == null) {
            throw new ServiceException(400, "型号不存在：" + req.getModelCode());
        }
        return model;
    }

    private static String requireEntityType(EntityWriteReqDTO req, ModelDO model) {
        String requested = req.getEntityTypeCode() == null ? "" : req.getEntityTypeCode().trim();
        String fromModel = model.getEntityTypeCode() == null ? "" : model.getEntityTypeCode().trim();
        if (StrUtil.isBlank(fromModel)) {
            throw new ServiceException(400, "型号没有底座类型");
        }
        if (StrUtil.isNotBlank(requested) && !requested.equals(fromModel)) {
            throw new ServiceException(400, "底座类型与型号不一致");
        }
        return fromModel;
    }

    private static Map<String, Object> copyFields(Map<String, Object> fields) {
        return fields == null ? new LinkedHashMap<>() : new LinkedHashMap<>(fields);
    }

}
