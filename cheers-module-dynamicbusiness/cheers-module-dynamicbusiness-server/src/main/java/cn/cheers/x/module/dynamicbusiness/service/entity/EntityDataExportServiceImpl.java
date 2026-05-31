package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityExportExcelVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityExportReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityImportExcelVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityImportRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务实体数据导入导出服务实现
 */
@Service
@Validated
@Slf4j
public class EntityDataExportServiceImpl implements EntityDataExportService {

    @Resource
    private EntityRepository entityRepository;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityImportRespVO importEntityList(List<EntityImportExcelVO> importList, Boolean updateSupport) {
        if (CollUtil.isEmpty(importList)) {
            throw new ServiceException(400, "导入数据不能为空");
        }

        List<String> createEntityNames = new ArrayList<>();
        List<String> updateEntityNames = new ArrayList<>();
        Map<String, String> failureEntityNames = new LinkedHashMap<>();

        for (int i = 0; i < importList.size(); i++) {
            EntityImportExcelVO importVO = importList.get(i);
            int rowNum = i + 2; // Excel 行号从2开始（第1行是表头）

            try {
                // 数据校验
                validateImportData(importVO, rowNum);

                // 检查是否已存在同名实体
                EntityDO existEntity = findExistingEntity(importVO);

                if (existEntity != null) {
                    if (Boolean.TRUE.equals(updateSupport)) {
                        // 更新已存在的实体
                        updateExistingEntity(existEntity, importVO);
                        updateEntityNames.add(importVO.getName());
                    } else {
                        failureEntityNames.put(importVO.getName(), 
                            String.format("第%d行：实体名称已存在，如需更新请勾选'更新已存在数据'", rowNum));
                    }
                } else {
                    // 创建新实体
                    createNewEntity(importVO);
                    createEntityNames.add(importVO.getName());
                }
            } catch (ServiceException e) {
                failureEntityNames.put(importVO.getName() != null ? importVO.getName() : "第" + rowNum + "行", 
                    e.getMessage());
            } catch (Exception e) {
                log.error("导入实体失败，行号：{}，数据：{}", rowNum, importVO, e);
                failureEntityNames.put(importVO.getName() != null ? importVO.getName() : "第" + rowNum + "行", 
                    "系统错误：" + e.getMessage());
            }
        }

        return EntityImportRespVO.builder()
                .createEntityNames(createEntityNames)
                .updateEntityNames(updateEntityNames)
                .failureEntityNames(failureEntityNames)
                .totalCount(importList.size())
                .successCount(createEntityNames.size() + updateEntityNames.size())
                .failureCount(failureEntityNames.size())
                .build();
    }

    @Override
    public List<EntityExportExcelVO> exportEntityList(EntityExportReqVO reqVO) {
        // 查询实体列表（通过 Repository 层，自动处理动态表名）
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .businessTypeCode(reqVO.getBusinessTypeCode())
                .modelId(reqVO.getModelId())
                .status(reqVO.getStatus())
                .build();
        List<EntityDO> entityList = entityRepository.findAll(query);

        // 过滤关键词
        if (StrUtil.isNotBlank(reqVO.getKeyword())) {
            entityList = entityList.stream()
                    .filter(e -> e.getName() != null && e.getName().contains(reqVO.getKeyword()))
                    .collect(Collectors.toList());
        }

        // 获取模型信息用于填充模型名称
        Map<Long, String> modelNameMap = getModelNameMap(entityList);

        // 转换为导出 VO
        return entityList.stream()
                .map(entity -> convertToExportVO(entity, modelNameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public List<EntityImportExcelVO> getImportTemplate(Long modelId) {
        return getImportTemplate(null, modelId);
    }

    @Override
    public List<EntityImportExcelVO> getImportTemplate(String businessTypeCode, Long modelId) {
        List<EntityImportExcelVO> templateList = new ArrayList<>();

        // 创建示例数据
        EntityImportExcelVO example = EntityImportExcelVO.builder()
                .name("示例实体名称")
                .businessTypeCode(StrUtil.isNotBlank(businessTypeCode) ? businessTypeCode : "equipment")
                .modelId(modelId != null ? modelId : 1L)
                .customFields("{\"1\":\"字段值1\", \"2\":\"字段值2\"}")
                .status(1)
                .build();
        templateList.add(example);

        // 如果指定了模型ID，添加字段说明
        if (modelId != null) {
            String fieldDescription = buildFieldDescription(modelId);
            if (StrUtil.isNotBlank(fieldDescription)) {
                EntityImportExcelVO descRow = EntityImportExcelVO.builder()
                        .name("【字段说明】")
                        .businessTypeCode("")
                        .modelId(null)
                        .customFields(fieldDescription)
                        .status(null)
                        .build();
                templateList.add(descRow);
            }
        }

        return templateList;
    }

    @Override
    public String exportToCsv(EntityExportReqVO reqVO) {
        List<EntityExportExcelVO> exportList = exportEntityList(reqVO);

        StringBuilder csv = new StringBuilder();
        // CSV 表头
        csv.append("实体ID,实体名称,业务类型编码,模型ID,模型名称,自定义字段(JSON),状态,创建时间,更新时间\n");

        // CSV 数据行
        for (EntityExportExcelVO vo : exportList) {
            csv.append(escapeCsvField(vo.getId()))
                    .append(",").append(escapeCsvField(vo.getName()))
                    .append(",").append(escapeCsvField(vo.getBusinessTypeCode()))
                    .append(",").append(escapeCsvField(vo.getModelId()))
                    .append(",").append(escapeCsvField(vo.getModelName()))
                    .append(",").append(escapeCsvField(vo.getCustomFields()))
                    .append(",").append(escapeCsvField(vo.getStatus()))
                    .append(",").append(escapeCsvField(vo.getCreateTime()))
                    .append(",").append(escapeCsvField(vo.getUpdateTime()))
                    .append("\n");
        }

        return csv.toString();
    }

    @Override
    public String exportToJson(EntityExportReqVO reqVO) {
        List<EntityExportExcelVO> exportList = exportEntityList(reqVO);
        return JSON.toJSONString(exportList);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验导入数据
     */
    private void validateImportData(EntityImportExcelVO importVO, int rowNum) {
        if (StrUtil.isBlank(importVO.getName())) {
            throw new ServiceException(400, String.format("第%d行：实体名称不能为空", rowNum));
        }
        if (StrUtil.isBlank(importVO.getBusinessTypeCode())) {
            throw new ServiceException(400, String.format("第%d行：业务类型编码不能为空", rowNum));
        }
        if (importVO.getModelId() == null) {
            throw new ServiceException(400, String.format("第%d行：模型ID不能为空", rowNum));
        }
        if (importVO.getStatus() == null) {
            throw new ServiceException(400, String.format("第%d行：状态不能为空", rowNum));
        }

        // 校验模型是否存在
        ModelDO model = modelMapper.selectById(importVO.getModelId());
        if (model == null) {
            throw new ServiceException(400, String.format("第%d行：模型ID[%d]不存在", rowNum, importVO.getModelId()));
        }
        if (!Objects.equals(model.getBusinessTypeCode(), importVO.getBusinessTypeCode())) {
            throw new ServiceException(400, String.format("第%d行：模型与业务类型不匹配", rowNum));
        }

        // 校验自定义字段格式
        if (StrUtil.isNotBlank(importVO.getCustomFields())) {
            try {
                JSON.parseObject(importVO.getCustomFields());
            } catch (Exception e) {
                throw new ServiceException(400, String.format("第%d行：自定义字段JSON格式错误", rowNum));
            }

            // 校验自定义字段值
            validateCustomFields(importVO.getModelId(), importVO.getCustomFields(), rowNum);
        }
    }

    /**
     * 校验自定义字段值
     */
    private void validateCustomFields(Long modelId, String customFieldsJson, int rowNum) {
        if (StrUtil.isBlank(customFieldsJson)) {
            return;
        }

        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (CollUtil.isEmpty(assignments)) {
            return;
        }

        JSONObject jsonObj = JSON.parseObject(customFieldsJson);

        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null) {
                continue;
            }

            String idKey = String.valueOf(field.getId());
            Object val = jsonObj.get(idKey);

            // 必填校验
            if (Boolean.TRUE.equals(assignment.getRequired())) {
                if (val == null || (val instanceof String && ((String) val).isEmpty())) {
                    throw new ServiceException(400, 
                        String.format("第%d行：字段[%s]为必填项", rowNum, field.getName()));
                }
            }
        }
    }

    /**
     * 查找已存在的实体
     */
    private EntityDO findExistingEntity(EntityImportExcelVO importVO) {
        // 通过 Repository 层查询（自动处理动态表名）
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .businessTypeCode(importVO.getBusinessTypeCode())
                .modelId(importVO.getModelId())
                .build();
        List<EntityDO> existList = entityRepository.findAll(query);
        return existList.stream()
                .filter(e -> Objects.equals(e.getName(), importVO.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 创建新实体
     */
    private void createNewEntity(EntityImportExcelVO importVO) {
        EntityDO entity = EntityDO.builder()
                .name(importVO.getName())
                .businessTypeCode(importVO.getBusinessTypeCode())
                .modelId(importVO.getModelId())
                .customFields(importVO.getCustomFields())
                .status(importVO.getStatus())
                .build();
        entity.setTenantId(getTenantId());
        // 通过 Repository 层保存（自动处理动态表名）
        entityRepository.save(entity);
    }

    /**
     * 更新已存在的实体
     */
    private void updateExistingEntity(EntityDO existEntity, EntityImportExcelVO importVO) {
        EntityDO update = new EntityDO();
        update.setId(existEntity.getId());
        update.setBusinessTypeCode(existEntity.getBusinessTypeCode()); // 必须设置 businessTypeCode 用于表名路由
        update.setCustomFields(importVO.getCustomFields());
        update.setStatus(importVO.getStatus());
        // 通过 Repository 层更新（自动处理动态表名）
        entityRepository.update(update);
    }

    /**
     * 获取模型名称映射
     */
    private Map<Long, String> getModelNameMap(List<EntityDO> entityList) {
        Set<Long> modelIds = entityList.stream()
                .map(EntityDO::getModelId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (CollUtil.isEmpty(modelIds)) {
            return Collections.emptyMap();
        }

        Map<Long, String> modelNameMap = new HashMap<>();
        for (Long modelId : modelIds) {
            ModelDO model = modelMapper.selectById(modelId);
            if (model != null) {
                modelNameMap.put(modelId, model.getName());
            }
        }
        return modelNameMap;
    }

    /**
     * 转换为导出 VO
     */
    private EntityExportExcelVO convertToExportVO(EntityDO entity, Map<Long, String> modelNameMap) {
        return EntityExportExcelVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .businessTypeCode(entity.getBusinessTypeCode())
                .modelId(entity.getModelId())
                .modelName(modelNameMap.get(entity.getModelId()))
                .customFields(entity.getCustomFields())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    /**
     * 构建字段说明
     */
    private String buildFieldDescription(Long modelId) {
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (CollUtil.isEmpty(assignments)) {
            return null;
        }

        StringBuilder desc = new StringBuilder("字段说明：");
        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field != null) {
                desc.append(String.format(" [%d]%s(%s%s);",
                        field.getId(),
                        field.getName(),
                        field.getType(),
                        Boolean.TRUE.equals(assignment.getRequired()) ? ",必填" : ""));
            }
        }
        return desc.toString();
    }

    /**
     * CSV 字段转义
     */
    private String escapeCsvField(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * 获取当前租户ID
     */
    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
