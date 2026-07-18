package cn.cheers.x.inspection.inspection_content.service.object;

import cn.cheers.x.inspection.inspection_content.service.source.ObjectSourceAdapter;

/**
 * 巡检对象查询服务。
 *
 * <p>提供独立的巡检对象详情查询能力，用于：</p>
 * <ul>
 *     <li>在任务中选择巡检对象前，预览对象详情</li>
 *     <li>单独查看某个对象的扩展信息（位置、负责人、状态等）</li>
 *     <li>校验对象编码是否存在</li>
 * </ul>
 *
 * <p>与 ContentEnhancementService 的区别：</p>
 * <ul>
 *     <li>ContentEnhancementService：批量增强任务中的对象详情</li>
 *     <li>ObjectSourceQueryService：独立查询单个或多个对象详情</li>
 * </ul>
 */
public interface ObjectSourceQueryService {

    /**
     * 查询对象详情。
     *
     * @param sourceType 来源类型（如 facility）
     * @param objectCode 对象编码
     * @return 对象详情，不存在时返回 null
     */
    ObjectSourceAdapter.ObjectDetail getObjectDetail(String sourceType, String objectCode);

    /**
     * 批量查询对象详情。
     *
     * @param sourceType 来源类型
     * @param objectCodes 对象编码列表
     * @return 对象详情映射，key 为 objectCode
     */
    java.util.Map<String, ObjectSourceAdapter.ObjectDetail> listObjectDetails(String sourceType, java.util.List<String> objectCodes);

    /**
     * 校验对象是否存在。
     *
     * @param sourceType 来源类型
     * @param objectCode 对象编码
     * @return 是否存在
     */
    boolean exists(String sourceType, String objectCode);

    /**
     * 查询对象列表（用于选择对象时的搜索）。
     *
     * @param sourceType 来源类型
     * @param categoryId 分类ID（可选）
     * @param keyword 关键字（可选）
     * @return 对象列表
     */
    java.util.List<ObjectSourceAdapter.InspectionObject> listObjects(String sourceType, Long categoryId, String keyword);

    /**
     * 查询来源类型列表。
     *
     * @return 所有支持的来源类型
     */
    java.util.List<String> listSourceTypes();
}
