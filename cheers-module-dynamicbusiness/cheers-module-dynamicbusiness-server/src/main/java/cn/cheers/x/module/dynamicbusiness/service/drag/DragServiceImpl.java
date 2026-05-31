package cn.cheers.x.module.dynamicbusiness.service.drag;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDragReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DragServiceImpl implements DragService {

    @Resource
    private ModelCategoryRelationService modelCategoryRelationService;

    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;

    @Resource
    private CategoryService categoryService;

    private final Map<String, DragActionHandler> actionHandlers = new HashMap<>();

    @PostConstruct
    public void initHandlers() {
        register("MODEL", "MODEL", "BEFORE", this::handleModelToModel);
        register("MODEL", "MODEL", "AFTER", this::handleModelToModel);
        register("MODEL", "CATEGORY", "INNER", this::handleModelToCategory);
        register("ENTITY", "CATEGORY", "INNER", this::handleEntityToCategory);
        register("CATEGORY", "CATEGORY", "BEFORE", this::handleCategoryToCategory);
        register("CATEGORY", "CATEGORY", "AFTER", this::handleCategoryToCategory);
        register("CATEGORY", "CATEGORY", "INNER", this::handleCategoryToCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DragExecuteRespVO execute(DragExecuteReqVO reqVO) {
        String sourceType = safeUpper(reqVO.getSourceNodeType());
        String targetType = safeUpper(reqVO.getTargetNodeType());
        String position = safeUpper(reqVO.getPosition());

        DragActionHandler handler = actionHandlers.get(actionKey(sourceType, targetType, position));
        if (handler == null) {
            throw new ServiceException(400, "暂不支持该拖拽类型");
        }
        return handler.handle(reqVO);
    }

    private DragExecuteRespVO handleModelToModel(DragExecuteReqVO reqVO) {
        Long categoryId = reqVO.getTargetContainerId() != null ? reqVO.getTargetContainerId() : reqVO.getSourceContainerId();
        if (categoryId == null) {
            throw new ServiceException(400, "模型排序缺少分类上下文 targetContainerId/sourceContainerId");
        }
        modelCategoryRelationService.reorderModelInCategory(
                reqVO.getSourceId(),
                reqVO.getTargetId(),
                categoryId,
                safeUpper(reqVO.getPosition())
        );
        return DragExecuteRespVO.builder().success(true).action("MODEL_REORDER").message("排序成功").build();
    }

    private DragExecuteRespVO handleModelToCategory(DragExecuteReqVO reqVO) {
        modelCategoryRelationService.moveOrBindModelToCategory(
                reqVO.getSourceId(),
                reqVO.getSourceContainerId(),
                reqVO.getTargetId()
        );
        return DragExecuteRespVO.builder().success(true).action("MODEL_MOVE_OR_BIND").message("绑定成功").build();
    }

    private DragExecuteRespVO handleEntityToCategory(DragExecuteReqVO reqVO) {
        CategoryRespVO category = categoryService.getCategoryVO(reqVO.getTargetId());
        if (category == null) {
            throw new ServiceException(404, "目标分类不存在");
        }
        String businessTypeCode = reqVO.getBusinessTypeCode();
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
            throw new ServiceException(400, "businessTypeCode 不能为空");
        }
        entityCategoryRelationService.associate(reqVO.getSourceId(), reqVO.getTargetId(), businessTypeCode);
        return DragExecuteRespVO.builder().success(true).action("ENTITY_BIND_CATEGORY").message("关联成功").build();
    }

    private DragExecuteRespVO handleCategoryToCategory(DragExecuteReqVO reqVO) {
        CategoryRespVO target = categoryService.getCategoryVO(reqVO.getTargetId());
        if (target == null) {
            throw new ServiceException(404, "目标分类不存在");
        }

        CategoryDragReqVO body = new CategoryDragReqVO();
        body.setDragId(reqVO.getSourceId());
        body.setTargetId(reqVO.getTargetId());
        body.setCategoryTypeCode(target.getCategoryTypeCode());

        String pos = safeUpper(reqVO.getPosition());
        if ("INNER".equals(pos)) {
            body.setPosition(CategoryDragReqVO.Position.INNER);
            body.setTargetParentId(reqVO.getTargetId());
        } else if ("BEFORE".equals(pos)) {
            body.setPosition(CategoryDragReqVO.Position.BEFORE);
        } else if ("AFTER".equals(pos)) {
            body.setPosition(CategoryDragReqVO.Position.AFTER);
        } else {
            throw new ServiceException(400, "CATEGORY->CATEGORY 仅支持 BEFORE/AFTER/INNER");
        }

        categoryService.dragCategory(body);
        return DragExecuteRespVO.builder().success(true).action("CATEGORY_DRAG").message("分类调整成功").build();
    }

    private void register(String sourceType, String targetType, String position, DragActionHandler handler) {
        actionHandlers.put(actionKey(sourceType, targetType, position), handler);
    }

    private String actionKey(String sourceType, String targetType, String position) {
        return sourceType + "->" + targetType + "@" + position;
    }

    private String safeUpper(String v) {
        return v == null ? "" : v.trim().toUpperCase();
    }

    @FunctionalInterface
    private interface DragActionHandler {
        DragExecuteRespVO handle(DragExecuteReqVO reqVO);
    }
}
