package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryBatchDeleteRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCloneReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeWithModelsRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDragReqVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CategoryService {

    // ==================== 存在性检查方法 ====================

    /**
     * 检查分类是否存在
     * 
     * @param categoryId 分类ID
     * @return 是否存在
     */
    boolean existsById(Long categoryId);

    /**
     * 批量检查分类是否存在
     * 
     * <p>使用 IN 查询优化性能，避免 N+1 问题。
     * 返回存在的分类ID集合，不存在的ID不会出现在结果中。</p>
     * 
     * @param categoryIds 分类ID列表
     * @return 存在的分类ID集合
     */
    Set<Long> filterExistingCategoryIds(List<Long> categoryIds);

    // ==================== 基础 CRUD 操作 ====================

    Long createCategory(CategoryCreateReqVO reqVO);

    /**
     * 复制分类：默认挂到源节点同一父下（同级），不复制子树。
     * 高级分类会带上源关联实体的字段值再走创建管线。
     */
    Long cloneCategory(CategoryCloneReqVO reqVO);

    void updateCategory(CategoryUpdateReqVO reqVO);

    /**
     * 删除分类
     * 
     * <p>删除规则：</p>
     * <ul>
     *   <li>cascade=false：仅删除当前分类，如果有子分类则禁止删除（抛出异常）</li>
     *   <li>cascade=true：级联删除当前分类及其所有子分类</li>
     * </ul>
     * 
     * @param reqVO 删除请求VO
     */
    void deleteCategory(CategoryDeleteReqVO reqVO);

    /**
     * 实体侧已删除绑定实体并断开 link 后，仅删除高级分类业务节点（不再删实体）。
     * 简单分类若仍有/曾有异常绑定由调用方先验；此处再校验种类为 ADVANCED。
     */
    void deleteCategoryNodeAfterEntityRemoved(Long categoryId, String categoryTypeCode);

    CategoryRespVO getCategoryVO(Long id);

    /**
     * 获取指定分类维度（categoryTypeCode）的分类树
     *
     * @param categoryTypeCode 分类类型编码（维度）
     * @param status 状态（null 表示不过滤）
     */
    List<CategoryTreeRespVO> getCategoryTreeByType(String categoryTypeCode, Integer status);

    /**
     * 获取分类树（含模型列表）。
     *
     * <p>用于 Pattern B 左侧树展示：每个分类节点附带 models 列表（按关联 sort,id 排序）。</p>
     *
     * @param categoryTypeCode 分类类型编码
     * @param status 状态过滤（null 表示不过滤）
     */
    List<CategoryTreeWithModelsRespVO> getCategoryTreeWithModels(String categoryTypeCode, Integer status);

    /**
     * 获取指定分类为根节点的子树（包含该分类自身 + 所有后代）。
     *
     * @param id 分类编号（根节点）
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @param status 状态过滤（null 表示不过滤）
     * @return 子树根节点（包含 children）
     */
    CategoryTreeRespVO getCategorySubtreeWithRoot(Long id, String categoryTypeCode, Integer status);

    void moveCategory(Long id, Long targetParentId, String categoryTypeCode);

    /**
     * 对分类进行排序
     * 
     * <p>对指定父分类下的子分类进行排序，根据 orderedIds 的顺序更新 sort 字段。</p>
     * 
     * @param parentId 父分类ID（null 表示根分类）
     * @param orderedIds 子分类ID列表（按排序顺序）
     * @param categoryTypeCode 分类类型编码
     */
    void sortCategories(Long parentId, List<Long> orderedIds, String categoryTypeCode);

    /**
     * 拖拽排序/改层级
     *
     * @param reqVO 拖拽请求
     */
    void dragCategory(CategoryDragReqVO reqVO);

    /**
     * 按关键词在指定分类体系（categoryTypeCode）下搜索分类列表。
     *
     * <p>说明：</p>
     * <ul>
     *   <li>keyword 为模糊匹配（通常匹配分类名称）</li>
     *   <li>仅在指定的 categoryTypeCode（分类维度/体系）范围内搜索</li>
     * </ul>
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>分类选择器：在下拉/弹窗中按关键词快速检索分类</li>
     *   <li>分类管理：在分类管理页面按名称模糊搜索分类</li>
     *   <li>关联配置：为 Model/Entity 选择分类时提供搜索候选列表</li>
     * </ul>
     *
     * @param keyword 搜索关键词
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @return 匹配的分类列表
     */
    List<CategoryRespVO> searchCategoryList(String keyword, String categoryTypeCode);

    /**
     * 按关键词在指定分类体系（categoryTypeCode）下搜索分类树。
     *
     * <p>返回结构为树形（{@link CategoryTreeRespVO}），并且会包含：</p>
     * <ul>
     *   <li>命中节点（name like keyword）</li>
     *   <li>命中节点的所有祖先节点（直到根节点），用于保证树可完整展示路径</li>
     *   <li>命中节点的全部子孙节点（整棵子树），用于命中后直接展开查看</li>
     * </ul>
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>树组件搜索：搜索后直接以树形结构展示结果（无需前端重新拼树）</li>
     *   <li>搜索定位：展示命中节点并保留祖先链，方便用户理解上下文</li>
     * </ul>
     *
     * @param keyword 搜索关键词（按分类名称模糊匹配）
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @return 搜索结果树
     */
    List<CategoryTreeRespVO> searchCategoryTree(String keyword, String categoryTypeCode);

    /**
     * 启用/禁用分类。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>分类管理：对分类进行启用/禁用操作</li>
     *   <li>下拉/树查询：仅展示启用状态分类时，需要更新分类状态</li>
     * </ul>
     *
     * @param id 分类编号
     * @param status 状态（通常：0-禁用，1-启用）
     * @param categoryTypeCode 分类类型编码（维度/体系）
     */
    void updateStatus(Long id, Integer status, String categoryTypeCode);

    /**
     * 获取指定父分类下的直接子分类列表（不递归）。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>懒加载树：前端展开某个节点时，只拉取其一层子节点</li>
     *   <li>级联选择：按 parentId 查询下一层候选</li>
     * </ul>
     *
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @param parentId 父分类ID（null 表示根节点）
     * @param status 状态过滤（null 表示不过滤）
     * @return 子分类列表
     */
    List<CategoryRespVO> listByParent(String categoryTypeCode, Long parentId, Integer status);

    /**
     * 获取指定父分类下的所有子分类列表（递归）。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>批量操作：获取某个分类下的所有子分类ID，用于批量查询关联的Entity</li>
     *   <li>级联查询：需要获取某个分类下的所有后代分类</li>
     * </ul>
     *
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @param parentId 父分类ID（null 表示根节点）
     * @param status 状态过滤（null 表示不过滤）
     * @return 所有子分类列表（递归，扁平结构）
     */
    List<CategoryRespVO> listByParentRecursive(String categoryTypeCode, Long parentId, Integer status);

    /**
     * 获取从根分类到指定分类的路径（祖先链）。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>面包屑导航：展示当前分类所在层级路径</li>
     *   <li>树定位：根据路径自动展开树到目标节点</li>
     * </ul>
     *
     * @param id 分类编号
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @return 路径节点列表（从根到当前）
     */
    List<CategoryRespVO> getPath(Long id, String categoryTypeCode);

    /**
     * 批量创建分类。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>初始化导入：一次性创建多条分类数据</li>
     *   <li>批量配置：管理端批量新增分类节点</li>
     * </ul>
     *
     * @param categories 分类创建请求列表
     * @return 创建成功的分类列表
     */
    List<CategoryRespVO> batchCreateCategory(List<CategoryCreateReqVO> categories);

    /**
     * 批量更新分类。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>批量维护：一次性修改多个分类的名称/描述/状态等</li>
     * </ul>
     *
     * @param categories 分类更新请求列表
     * @return 更新后的分类列表
     */
    List<CategoryRespVO> batchUpdateCategory(List<CategoryUpdateReqVO> categories);

    /**
     * 批量删除分类。
     *
     * <p>应用场景：</p>
     * <ul>
     *   <li>分类清理：管理端批量删除多个分类节点</li>
     * </ul>
     *
     * @param ids 分类ID列表
     * @param cascade 是否级联删除子分类
     * @param categoryTypeCode 分类类型编码（维度/体系）
     * @return 批量删除结果（包含成功/失败信息）
     */
    CategoryBatchDeleteRespVO batchDeleteCategory(List<Long> ids, boolean cascade, String categoryTypeCode);

    /**
     * 获取分类及其所有子分类的ID列表（按 categoryTypeCode 过滤）
     * 
     * <p>该方法会递归获取指定分类及其所有子分类的ID，确保只包含同一分类体系（categoryTypeCode）下的分类。</p>
     * 
     * @param categoryId 分类ID
     * @param categoryTypeCode 分类类型编码（用于确定分类体系）
     * @return 分类ID列表（包含自身和所有子分类）
     */
    List<Long> getAllCategoryIdsIncludingChildren(Long categoryId, String categoryTypeCode);

    /**
     * 批量获取多个分类及其所有子分类 ID 列表。
     *
     * @param categoryIds 分类根节点 ID 列表
     * @param categoryTypeCode 分类类型编码（可空；与单条接口语义一致）
     * @return 根 ID 到该根子树全部分类 ID（含自身）的映射，不含 null 键
     */
    Map<Long, List<Long>> getAllCategoryIdsIncludingChildrenBatch(List<Long> categoryIds, String categoryTypeCode);
}

