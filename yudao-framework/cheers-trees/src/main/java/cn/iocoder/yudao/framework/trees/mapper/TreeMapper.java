package cn.iocoder.yudao.framework.trees.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.trees.core.TreeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用树形Mapper接口
 * 提供基础的数据库操作功能
 *
 * @param <T> 树形实体类型
 */
@Mapper
public interface TreeMapper<T extends TreeEntity<Long>> extends BaseMapperX<T> {

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<T> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据树路径查询节点
     *
     * @param treePath 树路径
     * @return 节点列表
     */
    List<T> selectByTreePath(@Param("treePath") String treePath);

    /**
     * 根据树路径前缀查询节点
     *
     * @param treePathPrefix 树路径前缀
     * @return 节点列表
     */
    List<T> selectByTreePathPrefix(@Param("treePathPrefix") String treePathPrefix);

    /**
     * 根据编码查询节点
     *
     * @param code 节点编码
     * @return 节点
     */
    T selectByCode(@Param("code") String code);

    /**
     * 根据编码查询节点（排除指定ID）
     *
     * @param code 节点编码
     * @param excludeId 排除的节点ID
     * @return 节点
     */
    T selectByCodeExcludeId(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * 更新节点的树路径
     *
     * @param id 节点ID
     * @param treePath 新的树路径
     * @return 更新行数
     */
    int updateTreePath(@Param("id") Long id, @Param("treePath") String treePath);

    /**
     * 批量更新节点的树路径
     *
     * @param ids 节点ID列表
     * @param treePathPrefix 树路径前缀
     * @return 更新行数
     */
    int batchUpdateTreePath(@Param("ids") List<Long> ids, @Param("treePathPrefix") String treePathPrefix);

    /**
     * 根据排序范围查询同级节点
     *
     * @param parentId 父节点ID
     * @param minSort 最小排序值
     * @param maxSort 最大排序值
     * @return 节点列表
     */
    List<T> selectBySortRange(@Param("parentId") Long parentId, 
                             @Param("minSort") Integer minSort, 
                             @Param("maxSort") Integer maxSort);

    /**
     * 获取同级节点的最大排序值
     *
     * @param parentId 父节点ID
     * @return 最大排序值
     */
    Integer selectMaxSortByParentId(@Param("parentId") Long parentId);

    /**
     * 根据层级查询节点
     *
     * @param level 层级
     * @return 节点列表
     */
    List<T> selectByLevel(@Param("level") Integer level);

    /**
     * 根据状态查询节点
     *
     * @param status 状态
     * @return 节点列表
     */
    List<T> selectByStatus(@Param("status") Integer status);
}



