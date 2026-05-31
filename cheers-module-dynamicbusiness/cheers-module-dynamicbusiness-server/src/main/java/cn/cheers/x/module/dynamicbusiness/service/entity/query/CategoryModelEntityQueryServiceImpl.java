package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityConvert;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class CategoryModelEntityQueryServiceImpl implements CategoryModelEntityQueryService {

    @Resource
    private ModelCategoryRelationService modelCategoryRelationService;
    @Resource
    private ModelService modelService;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Override
    public List<EntityRespVO> getEntityTreeByCategory(Long categoryId, String businessTypeCode) {
        final long start = System.currentTimeMillis();
        final long warnThresholdMs = 1000L;

        if (categoryId == null || businessTypeCode == null || businessTypeCode.isEmpty()) {
            return new ArrayList<>();
        }

        long t1 = System.currentTimeMillis();
        // 1. 获取该分类下的所有模型（包含子分类），再按业务类型过滤
        List<Long> categoryModelIds = modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(categoryId, businessTypeCode);
        List<ModelRespVO> models = categoryModelIds.isEmpty() ? new ArrayList<>() : modelService.getModelsByIds(categoryModelIds);

        if (models.isEmpty()) {
            return new ArrayList<>();
        }

        long t2 = System.currentTimeMillis();
        // 2. 批量查询这些模型下的实体（一次 IN 查询）
        List<Long> modelIds = models.stream().map(ModelRespVO::getId).collect(Collectors.toList());
        List<EntityDO> entityDOs = entityRepository.findByModelIds(modelIds, businessTypeCode);
        if (entityDOs.isEmpty()) {
            return new ArrayList<>();
        }

        long t3 = System.currentTimeMillis();
        // 3. 转换为 RespVO
        List<EntityRespVO> allEntities = entityDOs.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        // 4. O(n) 构建树（避免流式递归 O(n²)）
        List<EntityRespVO> tree = buildTreeLinear(allEntities);
        long end = System.currentTimeMillis();

        long total = end - start;
        long modelQueryCost = t2 - t1;
        long entityQueryCost = t3 - t2;
        long convertAndBuildCost = end - t3;

        if (total >= warnThresholdMs) {
            log.warn("[SLOW_QUERY][PatternBCategory] categoryId={}, businessType={}, total={}ms, modelQuery={}ms, entityQuery={}ms, convertAndBuild={}ms, modelCount={}, entityCount={}, rootCount={}",
                    categoryId, businessTypeCode, total, modelQueryCost, entityQueryCost, convertAndBuildCost,
                    modelIds.size(), allEntities.size(), tree.size());
        }

        return tree;
    }

    @Override
    public PageResult<EntityRespVO> pageEntityByCategory(Long categoryId, String businessTypeCode,
                                                            String keyword, Integer pageNo, Integer pageSize) {
        if (categoryId == null || businessTypeCode == null || businessTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }

        List<Long> modelIds = modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(categoryId, businessTypeCode);
        if (modelIds == null || modelIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }

        PageResult<EntityDO> pageResult = entityCoreService.pageEntitiesByModelIds(
                businessTypeCode,
                modelIds,
                null,
                keyword,
                pageNo,
                pageSize
        );

        List<EntityRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .toList();
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<EntityRespVO> buildTreeLinear(List<EntityRespVO> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, EntityRespVO> byId = new HashMap<>(entities.size() * 2);
        for (EntityRespVO e : entities) {
            if (e.getId() != null) {
                byId.put(e.getId(), e);
            }
            e.setChildren(null);
        }

        List<EntityRespVO> roots = new ArrayList<>();
        for (EntityRespVO e : entities) {
            Long parentId = e.getParentId();
            if (parentId == null) {
                roots.add(e);
                continue;
            }
            EntityRespVO parent = byId.get(parentId);
            if (parent == null) {
                roots.add(e);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(e);
        }

        Comparator<EntityRespVO> cmp = Comparator.comparing(v -> v.getId() == null ? Long.MAX_VALUE : v.getId());
        roots.sort(cmp);
        sortChildrenRecursively(roots, cmp);
        return roots;
    }

    private void sortChildrenRecursively(List<EntityRespVO> nodes, Comparator<EntityRespVO> cmp) {
        if (nodes == null || nodes.isEmpty()) return;
        for (EntityRespVO n : nodes) {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                n.getChildren().sort(cmp);
                sortChildrenRecursively(n.getChildren(), cmp);
            }
        }
    }

    private EntityRespVO convertToRespVO(EntityDO entity) {
        if (entity == null) {
            return null;
        }
        EntityRespVO respVO = EntityConvert.INSTANCE.convert(entity);
        if (respVO.getCustomFields() != null) {
            respVO.setCustomFields(customFieldValidationService.decryptCustomFields(
                    respVO.getCustomFields(), entity.getModelId()));
        }
        return respVO;
    }
}

