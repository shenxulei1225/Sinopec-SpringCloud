package cn.iocoder.yudao.framework.trees.service;

import cn.iocoder.yudao.framework.trees.core.DragOperation;
import cn.iocoder.yudao.framework.trees.core.TreeEntity;

import java.util.List;

/**
 * 通用树形结构服务接口
 * 提供基础的树形操作功能
 *
 * @param <T> 树形实体类型
 */
public interface TreeService<T extends TreeEntity<Long>> {

    /**
     * 创建节点
     *
     * @param node 节点信息
     * @return 节点ID
     */
    Long createNode(T node);

    /**
     * 更新节点
     *
     * @param node 节点信息
     */
    void updateNode(T node);

    /**
     * 删除节点
     *
     * @param id 节点ID
     */
    void deleteNode(Long id);

    /**
     * 获取节点信息
     *
     * @param id 节点ID
     * @return 节点信息
     */
    T getNode(Long id);

    /**
     * 获取子节点列表
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<T> getChildren(Long parentId);

    /**
     * 获取完整树形结构
     *
     * @return 完整树形结构
     */
    List<T> getTree();

    /**
     * 获取指定节点下的子树
     *
     * @param parentId 父节点ID
     * @return 子树列表
     */
    List<T> getSubTree(Long parentId);

    /**
     * 拖拽调整树结构
     *
     * @param request 拖拽请求
     * @return 是否成功
     */
    boolean dragNode(DragOperation.DragRequest<Long> request);

    /**
     * 移动节点
     *
     * @param id 节点ID
     * @param targetParentId 目标父节点ID
     * @return 是否成功
     */
    boolean moveNode(Long id, Long targetParentId);

    /**
     * 获取节点路径（从根节点到当前节点）
     *
     * @param id 节点ID
     * @return 节点路径
     */
    List<T> getNodePath(Long id);

    /**
     * 校验节点编码是否唯一
     *
     * @param code 节点编码
     * @param excludeId 排除的节点ID
     * @return 是否唯一
     */
    boolean isCodeUnique(String code, Long excludeId);

    /**
     * 校验父节点是否合法
     *
     * @param parentId 父节点ID
     * @param id 当前节点ID
     * @return 是否合法
     */
    boolean isValidParent(Long parentId, Long id);
}




