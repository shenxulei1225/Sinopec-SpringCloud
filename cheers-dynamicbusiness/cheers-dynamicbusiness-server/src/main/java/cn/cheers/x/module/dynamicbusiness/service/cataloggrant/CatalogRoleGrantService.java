package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import java.util.List;

/**
 * 数据目录上的角色授权。
 * 角色分两种来源：系统里直接分配给人的角色，和有效任职的职务绑定的角色。受约束的目录只认任职带来的角色。
 * 负责：按角色整份替换分类、模型、实体、详情上的操作；判断当前用户能否在某个目录上对某个对象做某一项；
 * 删除数据目录时删掉各角色在这本目录上的授权。
 * 不负责：选实体表；不把存数编码当成目录；不判断记录是否属于目录（见 CatalogEntryAccessService）。
 * 超级管理员只管租户和业务开通，不读写租户业务数据，这里不给它放行。
 */
public interface CatalogRoleGrantService {

    List<CatalogRoleGrantItem> listByRoleId(Long roleId);

    void replaceRoleGrants(Long roleId, List<CatalogRoleGrantItem> items);

    /**
     * 删除数据目录时调用：删掉全部角色在这本目录上的授权。
     * 目录删了授权还留着，角色页会列出不存在的目录，可管目录也会因「数据目录不存在」保存失败。
     */
    void deleteGrantsOfDeletedCatalog(String registryCode);

    /**
     * 当前登录用户能否在这个数据目录上，对这个对象（分类、模型、实体、详情）做这一项。
     * 只认同一对象上的授权行，分类的勾选不放行实体，反过来也一样。
     * 目录注册编码为空、目录不在本租户、对象或操作不在约定内，都返回 false。
     */
    boolean allow(Long userId, String registryCode, String subject, String operation);

    /**
     * 当前登录用户在这本目录的实体上能做哪几项。数据管理据此显示新增、修改、删除按钮。
     * 只决定按钮，不含数据范围。范围在列表、详情和保存时由数据范围核对。
     */
    CatalogEntityOperations myEntityOperations(Long userId, String registryCode);

    /**
     * 当前登录用户在这个数据目录上有没有任意一个对象的查看授权。
     * 用于分类树：分类树是公共的筛选视角，能打开这一页的人就能看本页用到的分类。
     */
    boolean allowAnyQuery(Long userId, String registryCode);
}
