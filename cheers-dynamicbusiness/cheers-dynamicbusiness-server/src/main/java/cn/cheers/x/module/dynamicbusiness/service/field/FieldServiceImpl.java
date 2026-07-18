package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.field.FieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.event.FieldDefinitionChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Validated
@Slf4j
public class FieldServiceImpl implements FieldService {

    /** ENTITY_REF 关联目标 entityTypeCode 持久化在 provider_code */
    private static final String DYNAMIC_ENTITY_PROVIDER_PREFIX = "dynamic-entity:";

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private SmartSearchableService smartSearchableService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Long createField(FieldCreateReqVO reqVO) {
        validateUnit(reqVO.getType(), reqVO.getUnit());
        validateEnumOptions(reqVO.getType(), reqVO.getOptions());
        Long tenantId = getTenantId();
        FieldDO field = FieldConvert.INSTANCE.convert(reqVO);
        applyEntityRefProvider(field, reqVO.getTargetEntityType());
        // 自动生成 code（code 由系统生成，不通过前端传入）
        String code = generateCode();
        // 确保生成的 code 唯一（虽然概率极低，但为了安全起见）
        while (fieldMapper.selectByCode(code) != null) {
            code = generateCode();
        }
        field.setCode(code);
        field.setTenantId(tenantId);
        
        // 智能默认可查询：根据字段类型自动设置 is_searchable 和 is_sortable
        // 需求：FR-081, FR-082, FR-083, FR-084
        applySmartSearchableDefaults(field);
        
        fieldMapper.insert(field);
        // 清除字段列表缓存
        evictFieldListCache(field.getType());
        
        // 发布字段创建事件
        // 需求：缓存一致性、FR-BDA-090~093
        // TODO: 事件驱动机制（任务 30-32）- 暂时注释
        // publishFieldChangedEvent(FieldDefinitionChangedEvent.ChangeType.CREATED, field);
        
        return field.getId();
    }

    @Override
    public void updateField(FieldUpdateReqVO reqVO) {
        FieldDO db = getFieldDO(reqVO.getId());
        if (isSystemField(db)) {
            throw new ServiceException(400, "系统字段不允许修改");
        }
        validateUnit(reqVO.getType(), reqVO.getUnit());
        validateEnumOptions(reqVO.getType(), reqVO.getOptions());
        Long tenantId = getTenantId();
        String oldType = db.getType();
        FieldDO update = FieldConvert.INSTANCE.convert(reqVO);
        applyEntityRefProvider(update, reqVO.getTargetEntityType());
        update.setTenantId(tenantId);
        fieldMapper.updateById(update);
        // 清除缓存
        evictFieldCache(reqVO.getId());
        evictFieldListCache(oldType);
        if (!Objects.equals(oldType, reqVO.getType())) {
            evictFieldListCache(reqVO.getType());
        }
        
        // 发布字段更新事件
        // 需求：缓存一致性、FR-BDA-090~093
        // TODO: 事件驱动机制（任务 30-32）- 暂时注释
        // FieldDO updatedField = getFieldDO(reqVO.getId());
        // publishFieldChangedEvent(FieldDefinitionChangedEvent.ChangeType.UPDATED, updatedField);
    }

    @Override
    public void deleteField(Long id) {
        FieldDO db = getFieldDO(id);
        if (isSystemField(db)) {
            throw new ServiceException(400, "系统字段不允许删除");
        }
        fieldMapper.deleteById(id);
        // 清除缓存
        evictFieldCache(id);
        evictFieldListCache(db.getType());
        
        // 发布字段删除事件
        // 需求：缓存一致性、FR-BDA-090~093
        // TODO: 事件驱动机制（任务 30-32）- 暂时注释
        // publishFieldChangedEvent(FieldDefinitionChangedEvent.ChangeType.DELETED, db);
    }

    @Override
    public FieldRespVO getField(Long id) {
        // 先尝试从缓存获取
        FieldDO cached = FieldCacheHelper.getCachedField(stringRedisTemplate, id);
        if (cached != null) {
            FieldRespVO vo = FieldConvert.INSTANCE.convert(cached);
            enrichEntityRefTarget(vo, cached);
            return vo;
        }
        // 缓存未命中，从数据库获取
        FieldDO db = getFieldDO(id);
        // 写入缓存
        FieldCacheHelper.cacheField(stringRedisTemplate, db);
        FieldRespVO vo = FieldConvert.INSTANCE.convert(db);
        enrichEntityRefTarget(vo, db);
        return vo;
    }

    @Override
    public List<FieldRespVO> search(String keyword, String type, String source, Integer status) {
        List<FieldDO> list = fieldMapper.search(keyword, type, source, status);
        return enrichEntityRefTargetList(FieldConvert.INSTANCE.convertList(list), list);
    }

    @Override
    public List<FieldRespVO> listAll(String type, String source, Integer status) {
        return search(null, type, source, status);
    }

    @Override
    public PageResult<FieldRespVO> page(FieldPageReqVO reqVO) {
        PageResult<FieldDO> page = fieldMapper.selectPage(reqVO, reqVO.getKeyword(), reqVO.getType(), reqVO.getSource(), reqVO.getStatus());
        List<FieldRespVO> list = enrichEntityRefTargetList(FieldConvert.INSTANCE.convertList(page.getList()), page.getList());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public void enable(Long id) {
        toggleStatus(id, 1);
    }

    @Override
    public void disable(Long id) {
        toggleStatus(id, 0);
    }

    // ================= helper =================

    private String generateCode() {
        return "F-" + IdUtil.fastSimpleUUID();
    }

    private void validateUnit(String type, String unit) {
        if ("NUMBER".equalsIgnoreCase(type) && StringUtils.isBlank(unit)) {
            throw new ServiceException(400, "数值类字段必须填写单位");
        }
    }

    /**
     * 验证枚举类型字段的选项列表
     * 
     * 需求：FR-015-A, BR-VAL-015
     * - 枚举类型字段必须提供选项列表
     * - 选项格式为 JSON 数组，每个选项必须包含 label 和 value
     * - value 在选项列表中必须唯一
     * 
     * @param type 字段类型
     * @param options 选项列表（JSON 格式）
     */
    private void validateEnumOptions(String type, String options) {
        if (!"ENUM".equalsIgnoreCase(type)) {
            return;
        }
        
        // 枚举类型必须提供选项列表
        if (StringUtils.isBlank(options)) {
            throw new ServiceException(400, "枚举类型字段必须提供选项列表");
        }
        
        // 验证 JSON 格式
        if (!JSONUtil.isTypeJSONArray(options)) {
            throw new ServiceException(400, "选项列表格式错误，必须是 JSON 数组");
        }
        
        JSONArray optionsArray = JSONUtil.parseArray(options);
        if (optionsArray.isEmpty()) {
            throw new ServiceException(400, "选项列表不能为空");
        }
        
        // 验证每个选项的格式，并检查 value 唯一性
        Set<String> values = new HashSet<>();
        for (int i = 0; i < optionsArray.size(); i++) {
            Object item = optionsArray.get(i);
            if (!(item instanceof JSONObject)) {
                throw new ServiceException(400, "选项格式错误，每个选项必须是对象");
            }
            
            JSONObject option = (JSONObject) item;
            String label = option.getStr("label");
            String value = option.getStr("value");
            
            if (StringUtils.isBlank(label)) {
                throw new ServiceException(400, "选项的 label 不能为空");
            }
            if (StringUtils.isBlank(value)) {
                throw new ServiceException(400, "选项的 value 不能为空");
            }
            
            // 检查 value 唯一性
            if (values.contains(value)) {
                throw new ServiceException(400, "选项的 value 必须唯一，重复值: " + value);
            }
            values.add(value);
        }
    }

    /**
     * 检查 code 唯一性（code 全局唯一，不需要 tenantId）
     */
    private void checkCodeUnique(String code, Long id) {
        FieldDO exist = fieldMapper.selectByCode(code);
        if (exist != null && !Objects.equals(exist.getId(), id)) {
            throw new ServiceException(400, "字段编码已存在");
        }
    }

    private FieldDO getFieldDO(Long id) {
        FieldDO db = fieldMapper.selectById(id);
        if (db == null) {
            throw new ServiceException(404, "字段不存在");
        }
        return db;
    }

    private boolean isSystemField(FieldDO db) {
        return "SYSTEM".equalsIgnoreCase(db.getSource());
    }

    private void toggleStatus(Long id, Integer status) {
        FieldDO db = getFieldDO(id);
        if (isSystemField(db)) {
            throw new ServiceException(400, "系统字段不允许变更状态");
        }
        db.setStatus(status);
        fieldMapper.updateById(db);
    }

    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }

    // ================= 智能默认可查询 =================

    /**
     * 应用智能默认可查询设置
     * 
     * 根据字段类型自动设置 is_searchable、is_sortable 和 index_strategy 的默认值。
     * 如果用户已经显式设置了值，则保留用户的设置（用户可覆盖默认值）。
     * 
     * 智能默认规则（需求：FR-BDA-080~083）：
     * - 常用类型（TEXT、NUMBER、INTEGER、DATE、DATETIME、BOOLEAN、ENUM、ENTITY_REF）默认可查询
     * - 大文本类型（LONG_TEXT）默认不可查询，用户可手动开启
     * - JSON 类型默认不可查询
     * - 根据字段类型自动选择合适的索引策略（GIN/BTREE/NONE）
     * 
     * @param field 字段对象
     */
    private void applySmartSearchableDefaults(FieldDO field) {
        // 使用 SmartSearchableService 应用智能默认设置
        // 该服务会自动设置 isSearchable 和 isSortable（如果为 null）
        smartSearchableService.applySmartDefaults(field);
        
        // 设置索引策略（如果用户未显式设置）
        if (field.getIndexStrategy() == null || field.getIndexStrategy().isEmpty()) {
            SmartSearchableService.IndexStrategy indexStrategy = 
                    smartSearchableService.getDefaultIndexStrategy(field.getType());
            field.setIndexStrategy(indexStrategy.getCode());
            log.debug("字段 [{}] 类型 [{}] 应用智能默认索引策略: {}", 
                    field.getName(), field.getType(), indexStrategy.getCode());
        }
    }

    // ================= 缓存方法 =================

    /**
     * 清除字段缓存
     */
    private void evictFieldCache(Long id) {
        FieldCacheHelper.evictField(stringRedisTemplate, id);
    }

    /**
     * 清除字段列表缓存
     */
    private void evictFieldListCache(String type) {
        FieldCacheHelper.evictFieldList(stringRedisTemplate, type);
        // 同时清除 "all" 类型的缓存
        FieldCacheHelper.evictFieldList(stringRedisTemplate, null);
    }

    // ================= 事件发布方法 =================

    /**
     * 发布字段定义变更事件
     * 
     * 用于触发：
     * 1. 清除关联发现缓存
     * 2. 更新依赖该字段的计算字段
     * 
     * 需求：缓存一致性、FR-BDA-090~093
     */
    private void publishFieldChangedEvent(FieldDefinitionChangedEvent.ChangeType changeType, FieldDO field) {
        try {
            FieldDefinitionChangedEvent event;
            switch (changeType) {
                case CREATED:
                    event = FieldDefinitionChangedEvent.created(
                            this, field.getId(), field.getCode(), 
                            field.getName(), field.getType(), field.getTenantId());
                    break;
                case UPDATED:
                    event = FieldDefinitionChangedEvent.updated(
                            this, field.getId(), field.getCode(), 
                            field.getName(), field.getType(), field.getTenantId());
                    break;
                case DELETED:
                    event = FieldDefinitionChangedEvent.deleted(
                            this, field.getId(), field.getCode(), 
                            field.getName(), field.getType(), field.getTenantId());
                    break;
                default:
                    log.warn("[publishFieldChangedEvent][未知的变更类型: {}]", changeType);
                    return;
            }
            eventPublisher.publishEvent(event);
            log.debug("[publishFieldChangedEvent][发布字段定义变更事件: {}]", event);
        } catch (Exception e) {
            // 事件发布失败不应影响主流程
            log.warn("[publishFieldChangedEvent][发布字段定义变更事件失败: fieldId={}, changeType={}, error={}]", 
                    field.getId(), changeType, e.getMessage());
        }
    }

    // ================= ENTITY_REF 关联目标 =================

    private void applyEntityRefProvider(FieldDO field, String targetEntityType) {
        if (field == null || !FieldTypeEnum.isEntityRef(field.getType())) {
            return;
        }
        if (StringUtils.isNotBlank(targetEntityType)) {
            field.setProviderCode(DYNAMIC_ENTITY_PROVIDER_PREFIX + targetEntityType.trim());
        }
    }

    private void enrichEntityRefTarget(FieldRespVO vo, FieldDO field) {
        if (vo == null || field == null || StringUtils.isNotBlank(vo.getTargetEntityType())) {
            return;
        }
        String providerCode = field.getProviderCode();
        if (providerCode != null && providerCode.startsWith(DYNAMIC_ENTITY_PROVIDER_PREFIX)) {
            String code = providerCode.substring(DYNAMIC_ENTITY_PROVIDER_PREFIX.length()).trim();
            if (!code.isEmpty()) {
                vo.setTargetEntityType(code);
            }
        }
    }

    private List<FieldRespVO> enrichEntityRefTargetList(List<FieldRespVO> vos, List<FieldDO> fields) {
        if (vos == null || fields == null || vos.size() != fields.size()) {
            return vos;
        }
        for (int i = 0; i < vos.size(); i++) {
            enrichEntityRefTarget(vos.get(i), fields.get(i));
        }
        return vos;
    }
}

