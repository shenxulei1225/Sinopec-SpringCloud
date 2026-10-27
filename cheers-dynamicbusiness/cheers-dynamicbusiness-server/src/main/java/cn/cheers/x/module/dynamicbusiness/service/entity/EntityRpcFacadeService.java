package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityPageReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDtoConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;

    public EntityRespDTO get(Long id, String entityTypeCode) {
        EntityRespVO vo = entityService.get(id, entityTypeCode);
        return EntityDtoConvert.toDto(vo);
    }

    public List<EntityRespDTO> listByIds(List<Long> ids, String entityTypeCode) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<EntityDO> entities =
                entityCoreService.listByIdsWithDedicatedBaseFields(ids, entityTypeCode);
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(
                entities, customFieldValidationService, entityDedicatedColumnService);
        return EntityDtoConvert.toDtoList(vos);
    }

    public boolean exists(Long id, String entityTypeCode) {
        return entityCoreService.existsById(id, entityTypeCode);
    }

    /**
     * 列表带回类型基础字段。协议指令的方向和协议版本写在专用列上，不在扩展字段里。
     */
    public List<EntityRespDTO> list(String entityTypeCode, Long modelId) {
        List<EntityDO> entities = entityCoreService.listEntities(entityTypeCode, modelId, null);
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (EntityDO entity : entities) {
            if (entity != null && entity.getId() != null) {
                ids.add(entity.getId());
            }
        }
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(ids, entityTypeCode);
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(
                loaded, customFieldValidationService, entityDedicatedColumnService);
        return EntityDtoConvert.toDtoList(vos);
    }

    /**
     * 数据库先完成过滤和分页，再一次加载本页基础字段。
     * 基础字段编码必须能由当前底座元数据解析为安全物理列。
     */
    public PageResult<EntityRespDTO> page(EntityPageReqDTO req) {
        if (req == null || StrUtil.isBlank(req.getEntityTypeCode())) {
            throw new ServiceException(400, "底座类型编码不能为空");
        }
        String entityTypeCode = req.getEntityTypeCode().trim();
        List<PhysicalColumnFilter> filters = new ArrayList<>();
        Map<String, Object> baseFieldEquals = req.getBaseFieldEquals() == null
                ? Map.of()
                : req.getBaseFieldEquals();
        for (Map.Entry<String, Object> entry : baseFieldEquals.entrySet()) {
            String fieldCode = StrUtil.trim(entry.getKey());
            String column = entityDedicatedColumnService.resolvePhysicalColumn(entityTypeCode, fieldCode);
            if (column == null) {
                throw new ServiceException(400, "基础字段不能用于数据库筛选：" + fieldCode);
            }
            filters.add(new PhysicalColumnFilter(column, "EQ", entry.getValue()));
        }
        PageResult<EntityDO> page = entityCoreService.pageEntities(
                entityTypeCode, req.getModelId(), req.getStatus(), req.getKeyword(),
                req.getPageNo(), req.getPageSize(), filters);
        List<Long> ids = page.getList().stream()
                .map(EntityDO::getId)
                .filter(java.util.Objects::nonNull)
                .toList();
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(ids, entityTypeCode);
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(
                loaded, customFieldValidationService, entityDedicatedColumnService);
        return new PageResult<>(EntityDtoConvert.toDtoList(vos), page.getTotal());
    }

    /**
     * 按业务编码读这一条的完整档案（含参数清单、步骤图）。
     * 编码只定位这一行，返回和按编号打开详情同一套装配。
     * 不扫该类型的列表。列表行没有这些固定列。
     */
    /**
     * 这个编码是否只剩已删除行。还有未删除的同编码时为 false。
     * 生成任务用来跳过已删除动作，不把编码不存在当成已删除。
     */
    public boolean isDeletedByCode(String code, String entityTypeCode) {
        return entityCoreService.isDeletedByCode(entityTypeCode, code);
    }

    public EntityRespDTO getByCode(String code, String entityTypeCode) {
        if (StrUtil.isBlank(code) || StrUtil.isBlank(entityTypeCode)) {
            return null;
        }
        EntityDO row = entityCoreService.findByExactCode(entityTypeCode, code.trim());
        if (row == null || row.getId() == null) {
            return null;
        }
        return get(row.getId(), entityTypeCode);
    }

    /**
     * 按编码一次定位实体，再一次加载完整基础字段。
     * 列表核对共享该批量结果，禁止逐编码调用详情接口。
     */
    public List<EntityRespDTO> listByCodes(List<String> codes, String entityTypeCode) {
        if (CollUtil.isEmpty(codes) || StrUtil.isBlank(entityTypeCode)) {
            return Collections.emptyList();
        }
        Set<String> seen = new HashSet<>();
        for (String code : codes) {
            if (StrUtil.isNotBlank(code)) {
                seen.add(code.trim());
            }
        }
        List<EntityDO> matched = entityCoreService.listByCodes(new ArrayList<>(seen), entityTypeCode.trim());
        List<Long> ids = matched.stream().map(EntityDO::getId).filter(java.util.Objects::nonNull).toList();
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(ids, entityTypeCode.trim());
        List<EntityRespVO> vos = EntityDoVoHelper.toRespVOList(
                loaded, customFieldValidationService, entityDedicatedColumnService);
        return EntityDtoConvert.toDtoList(vos);
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

    /**
     * 读这个型号已经写下的字段默认值。没有分配或默认值为空则空着，不编一个数。
     */
    public String getModelFieldDefault(Long modelId, String fieldCode) {
        if (modelId == null || modelId <= 0 || StrUtil.isBlank(fieldCode)) {
            return null;
        }
        List<ModelFieldAssignmentDO> rows = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        String code = fieldCode.trim();
        for (ModelFieldAssignmentDO row : rows) {
            if (row == null || !code.equals(row.getFieldCode())) {
                continue;
            }
            return StrUtil.trimToNull(row.getDefaultValue());
        }
        return null;
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
