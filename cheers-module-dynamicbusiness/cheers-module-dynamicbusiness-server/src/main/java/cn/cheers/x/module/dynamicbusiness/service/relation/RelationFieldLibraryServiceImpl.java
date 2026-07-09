package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryWithStatusVO;
import cn.cheers.x.module.dynamicbusiness.convert.relation.RelationFieldLibraryConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

/**
 * 关联字段库 Service 实现类
 * 
 * 业务规则：
 * - BR-BDA-010：松散引用，库存 {@code refEntityType}（业务类型编码）；模型选用后字段编码为 {@link RelationFieldCodes#toModelFieldCode(String)}，与实体 JSON 键、关系表 {@code field_code} 一致
 * - BR-BDA-011：状态自动更新,关联目标创建后,字段状态自动从"待建"变为"可用"
 * - BR-BDA-012：使用时验证,Model 选用关联字段时,验证目标是否存在
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class RelationFieldLibraryServiceImpl implements RelationFieldLibraryService {

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private RefConstraintLibraryService refConstraintLibraryService;

    // ========== CRUD 方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRelationField(RelationFieldLibraryCreateReqVO reqVO) {
        // 1. 验证字段编码唯一性
        validateFieldCodeUnique(reqVO.getFieldCode(), null);

        // 2. 规范化并校验约束器配置
        normalizeConstraintConfig(reqVO);
        validateConstraintConfig(reqVO.getConstraintEnabled(), reqVO.getConstraintType(), reqVO.getRefEntityType());

        // 3. 转换并创建
        RelationFieldLibraryDO field = RelationFieldLibraryConvert.INSTANCE.convert(reqVO);
        field.setUsageCount(0);
        field.setIsSystem(false);
        relationFieldLibraryMapper.insert(field);

        log.info("[createRelationField][创建关联字段成功,id={}, fieldCode={}, refEntityType={}]",
                field.getId(), field.getFieldCode(), field.getRefEntityType());
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRelationField(RelationFieldLibraryUpdateReqVO reqVO) {
        // 1. 验证字段存在
        RelationFieldLibraryDO existField = validateRelationFieldExistsAndGet(reqVO.getId());

        // 2. 验证字段编码唯一性（排除自身）
        validateFieldCodeUnique(reqVO.getFieldCode(), reqVO.getId());

        // 3. 规范化并校验约束器配置
        normalizeConstraintConfig(reqVO);
        validateConstraintConfig(reqVO.getConstraintEnabled(), reqVO.getConstraintType(), reqVO.getRefEntityType());

        // 4. 更新
        RelationFieldLibraryDO updateField = RelationFieldLibraryConvert.INSTANCE.convert(reqVO);
        // 保留不可修改的字段
        updateField.setUsageCount(existField.getUsageCount());
        updateField.setIsSystem(existField.getIsSystem());
        relationFieldLibraryMapper.updateById(updateField);

        log.info("[updateRelationField][更新关联字段成功,id={}, fieldCode={}]",
                reqVO.getId(), reqVO.getFieldCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRelationField(Long id) {
        // 1. 验证字段存在
        RelationFieldLibraryDO field = validateRelationFieldExistsAndGet(id);

        // 2. 验证是否为系统预置字段
        if (Boolean.TRUE.equals(field.getIsSystem())) {
            throw exception(RELATION_FIELD_SYSTEM_CANNOT_DELETE);
        }

        // 3. 验证是否正在使用中
        if (field.getUsageCount() != null && field.getUsageCount() > 0) {
            throw exception(RELATION_FIELD_IN_USE);
        }

        // 4. 删除
        relationFieldLibraryMapper.deleteById(id);

        log.info("[deleteRelationField][删除关联字段成功,id={}, fieldCode={}]",
                id, field.getFieldCode());
    }

    @Override
    public RelationFieldLibraryRespVO getRelationField(Long id) {
        RelationFieldLibraryDO field = relationFieldLibraryMapper.selectById(id);
        return RelationFieldLibraryConvert.INSTANCE.convert(field);
    }

    @Override
    public RelationFieldLibraryRespVO getRelationFieldByCode(String fieldCode) {
        RelationFieldLibraryDO field = relationFieldLibraryMapper.selectByFieldCode(fieldCode);
        return RelationFieldLibraryConvert.INSTANCE.convert(field);
    }

    // ========== 列表查询方法 ==========

    @Override
    public List<RelationFieldLibraryWithStatusVO> getRelationFieldListWithStatus() {
        List<RelationFieldLibraryDO> fields = relationFieldLibraryMapper.selectAll();
        return enrichWithStatus(fields);
    }

    @Override
    public List<RelationFieldLibraryRespVO> getAvailableRelationFields(String refEntityType) {
        List<RelationFieldLibraryDO> fields = relationFieldLibraryMapper.selectByRefEntityType(refEntityType);

        // 过滤出业务类型存在的字段
        List<RelationFieldLibraryRespVO> result = new ArrayList<>();
        for (RelationFieldLibraryDO field : fields) {
            if (checkTargetExists(field.getRefEntityType(), null)) {
                result.add(RelationFieldLibraryConvert.INSTANCE.convert(field));
            }
        }
        return result;
    }

    @Override
    public PageResult<RelationFieldLibraryWithStatusVO> getRelationFieldPage(RelationFieldLibraryPageReqVO reqVO) {
        PageResult<RelationFieldLibraryDO> pageResult = relationFieldLibraryMapper.selectPage(
                reqVO.getRefEntityType(),
                reqVO.getKeyword(),
                reqVO.getPageNo(),
                reqVO.getPageSize()
        );

        // 转换并填充状态
        List<RelationFieldLibraryWithStatusVO> voList = enrichWithStatus(pageResult.getList());
        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public List<RelationFieldLibraryRespVO> getSystemRelationFields() {
        List<RelationFieldLibraryDO> fields = relationFieldLibraryMapper.selectSystemFields();
        return RelationFieldLibraryConvert.INSTANCE.convertList(fields);
    }


    // ========== 状态检查方法 ==========

    @Override
    public boolean checkTargetExists(String entityType, String modelCode) {
        // 1. 检查业务类型是否存在
        EntityTypeDO entityTypeDO = entityTypeMapper.selectByCode(entityType);
        if (entityTypeDO == null) {
            return false;
        }

        // 2. 业务级引用不再校验模型
        if (modelCode == null || modelCode.isBlank()) {
            return true;
        }

        // 3. 若传入模型编码，则尽力兼容旧逻辑
        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model == null) {
            return false;
        }

        return entityType.equals(model.getEntityTypeCode());
    }

    @Override
    public String[] getTargetNames(String entityType, String modelCode) {
        String[] names = new String[2];

        // 1. 获取实体类型名称
        EntityTypeDO entityTypeDO = entityTypeMapper.selectByCode(entityType);
        if (entityTypeDO != null) {
            names[0] = entityTypeDO.getName();
        }

        // 2. 业务级引用默认不展示模型名称
        if (modelCode == null || modelCode.isBlank()) {
            return names;
        }

        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model != null && entityType.equals(model.getEntityTypeCode())) {
            names[1] = model.getName();
        }

        return names;
    }

    // ========== 使用次数管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementUsageCount(Long id) {
        relationFieldLibraryMapper.incrementUsageCount(id);
        log.debug("[incrementUsageCount][增加关联字段使用次数,id={}]", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementUsageCount(Long id) {
        relationFieldLibraryMapper.decrementUsageCount(id);
        log.debug("[decrementUsageCount][减少关联字段使用次数,id={}]", id);
    }

    // ========== 验证方法 ==========

    @Override
    public void validateRelationFieldExists(Long id) {
        validateRelationFieldExistsAndGet(id);
    }

    @Override
    public void validateTargetAvailable(String entityType, String modelCode) {
        if (!checkTargetExists(entityType, modelCode)) {
            throw exception(RELATION_FIELD_TARGET_NOT_EXISTS);
        }
    }

    // ========== 私有方法 ==========

    private static final String CONSTRAINT_NONE = "NONE";

    private void normalizeConstraintConfig(RelationFieldLibraryCreateReqVO reqVO) {
        if (reqVO.getConstraintEnabled() == null) {
            reqVO.setConstraintEnabled(false);
        }
        if (!Boolean.TRUE.equals(reqVO.getConstraintEnabled()) && (reqVO.getConstraintType() == null || reqVO.getConstraintType().isBlank())) {
            reqVO.setConstraintType(CONSTRAINT_NONE);
        }
    }

    private void normalizeConstraintConfig(RelationFieldLibraryUpdateReqVO reqVO) {
        if (reqVO.getConstraintEnabled() == null) {
            reqVO.setConstraintEnabled(false);
        }
        if (!Boolean.TRUE.equals(reqVO.getConstraintEnabled()) && (reqVO.getConstraintType() == null || reqVO.getConstraintType().isBlank())) {
            reqVO.setConstraintType(CONSTRAINT_NONE);
        }
    }

    private void validateConstraintConfig(Boolean constraintEnabled, String constraintType, String targetEntityType) {
        if (Boolean.TRUE.equals(constraintEnabled)) {
            if (constraintType == null || constraintType.isBlank()) {
                throw exception(RELATION_FIELD_CONSTRAINT_TYPE_REQUIRED);
            }
            refConstraintLibraryService.validateConstraintType(targetEntityType, null, constraintType);
        } else {
            if (constraintType != null && !constraintType.isBlank() && !CONSTRAINT_NONE.equals(constraintType)) {
                throw exception(RELATION_FIELD_CONSTRAINT_TYPE_NOT_ALLOWED);
            }
        }
    }


    /**
     * 验证关联字段存在并返回
     */
    private RelationFieldLibraryDO validateRelationFieldExistsAndGet(Long id) {
        RelationFieldLibraryDO field = relationFieldLibraryMapper.selectById(id);
        if (field == null) {
            throw exception(RELATION_FIELD_NOT_EXISTS);
        }
        return field;
    }

    /**
     * 验证字段编码唯一性
     */
    private void validateFieldCodeUnique(String fieldCode, Long excludeId) {
        if (relationFieldLibraryMapper.existsByFieldCode(fieldCode, excludeId)) {
            throw exception(RELATION_FIELD_CODE_DUPLICATE);
        }
    }

    /**
     * 为字段列表填充状态信息
     * 
     * 状态计算规则（BR-BDA-011）：
     * - AVAILABLE: 关联目标存在
     * - PENDING: 关联目标不存在
     */
    private List<RelationFieldLibraryWithStatusVO> enrichWithStatus(List<RelationFieldLibraryDO> fields) {
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }

        List<RelationFieldLibraryWithStatusVO> result = new ArrayList<>(fields.size());
        for (RelationFieldLibraryDO field : fields) {
            RelationFieldLibraryWithStatusVO vo = RelationFieldLibraryConvert.INSTANCE.convertWithStatus(field);

            // 检查目标是否存在
            boolean targetExists = checkTargetExists(field.getRefEntityType(), null);
            vo.setTargetExists(targetExists);
            vo.setStatus(targetExists ? RelationFieldLibraryWithStatusVO.STATUS_AVAILABLE
                                      : RelationFieldLibraryWithStatusVO.STATUS_PENDING);

            // 获取目标名称
            String[] names = getTargetNames(field.getRefEntityType(), null);
            vo.setTargetEntityTypeName(names[0]);
            vo.setTargetModelName(names[1]);

            result.add(vo);
        }
        return result;
    }
}
