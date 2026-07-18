package cn.cheers.x.inspection.inspection_content.service.library;

import cn.cheers.x.inspection.inspection_content.controller.admin.library.vo.*;

import java.util.List;

/**
 * 巡检对象库服务。
 *
 * <p>统一入口，内部通过适配器调用各个业务系统。</p>
 *
 * <p>提供三种数据获取方式：</p>
 * <ul>
 *     <li>方式一（推荐）：分步获取 - 先获取型号+检查项，再按需获取对象</li>
 *     <li>方式二：一次性获取 - 一次性返回完整树形结构</li>
 *     <li>方式三：直接查询检查项</li>
 * </ul>
 */
public interface InspectionLibraryService {

    // ==================== 来源管理 ====================

    /**
     * 获取可用的巡检对象来源列表。
     */
    List<LibrarySourceVO> getSources();

    // ==================== 方式一：分步获取（推荐） ====================

    /**
     * 【方式一】获取分类下的型号列表（含检查项）。
     */
    List<ModelWithItemsVO> getModelsWithItems(String sourceCode, Long categoryId);

    /**
     * 【方式一】查询巡检对象列表（可按型号筛选）。
     */
    List<LibraryObjectVO> getObjects(String sourceCode, Long categoryId, String objectModel);

    // ==================== 方式二：一次性获取 ====================

    /**
     * 【方式二】一次性获取分类下的型号-对象-检查项完整树。
     */
    FullTreeVO getFullTree(String sourceCode, Long categoryId);

    // ==================== 方式三：直接查询检查项 ====================

    /**
     * 【方式三】直接查询指定型号的检查项。
     */
    List<LibraryItemVO> getItems(String sourceCode, Long categoryId, String objectModel);
}
