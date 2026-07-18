package cn.cheers.x.inspection.task.dal.mysql.task;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplatePageReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检任务模板 Mapper。
 *
 * <p>模板的CRUD及多维度查询。</p>
 */
@Mapper
public interface InspectionTaskTemplateMapper extends BaseMapperX<InspectionTaskTemplateDO> {

    // ==================== 基础查询 ====================

    /**
     * 根据模板编码查询。
     */
    default InspectionTaskTemplateDO selectByTemplateCode(@Param("templateCode") String templateCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getTemplateCode, templateCode));
    }

    /**
     * 根据模板编码查询（排除指定ID）。
     */
    default InspectionTaskTemplateDO selectByTemplateCodeAndIdNot(@Param("templateCode") String templateCode, @Param("id") Long id) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getTemplateCode, templateCode)
                .ne(InspectionTaskTemplateDO::getId, id));
    }

    /**
     * 根据模板名称查询。
     */
    default InspectionTaskTemplateDO selectByTemplateName(@Param("templateName") String templateName) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getTemplateName, templateName));
    }

    // ==================== 条件查询 ====================

    /**
     * 条件分页查询。
     */
    default PageResult<InspectionTaskTemplateDO> selectPage(InspectionTaskTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eqIfPresent(InspectionTaskTemplateDO::getParentId, reqVO.getParentId())
                .eqIfPresent(InspectionTaskTemplateDO::getCategoryId, reqVO.getCategoryId())
                .likeIfPresent(InspectionTaskTemplateDO::getTemplateName, reqVO.getTemplateName())
                .likeIfPresent(InspectionTaskTemplateDO::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(InspectionTaskTemplateDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(InspectionTaskTemplateDO::getId));
    }

    /**
     * 条件列表查询（不分页）。
     */
    default List<InspectionTaskTemplateDO> selectListByCondition(InspectionTaskTemplatePageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eqIfPresent(InspectionTaskTemplateDO::getParentId, reqVO.getParentId())
                .eqIfPresent(InspectionTaskTemplateDO::getCategoryId, reqVO.getCategoryId())
                .likeIfPresent(InspectionTaskTemplateDO::getTemplateName, reqVO.getTemplateName())
                .likeIfPresent(InspectionTaskTemplateDO::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(InspectionTaskTemplateDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(InspectionTaskTemplateDO::getId));
    }

    // ==================== 状态查询 ====================

    /**
     * 根据启用状态查询。
     */
    default List<InspectionTaskTemplateDO> selectByEnabled(@Param("enabled") Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getEnabled, enabled)
                .orderByDesc(InspectionTaskTemplateDO::getCreateTime));
    }

    /**
     * 查询所有启用的模板。
     */
    default List<InspectionTaskTemplateDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getEnabled, true)
                .orderByDesc(InspectionTaskTemplateDO::getCreateTime));
    }

    // ==================== 分类查询 ====================

    /**
     * 根据分类ID查询。
     */
    default List<InspectionTaskTemplateDO> selectByCategoryId(@Param("categoryId") Long categoryId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getCategoryId, categoryId)
                .orderByDesc(InspectionTaskTemplateDO::getCreateTime));
    }

    // ==================== 树形结构查询 ====================

    /**
     * 查询子模板。
     */
    default List<InspectionTaskTemplateDO> selectByParentId(@Param("parentId") Long parentId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getParentId, parentId)
                .orderByAsc(InspectionTaskTemplateDO::getCreateTime));
    }

    /**
     * 查询顶层模板（父模板为空）。
     */
    default List<InspectionTaskTemplateDO> selectRootTemplates() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .isNull(InspectionTaskTemplateDO::getParentId)
                .orderByDesc(InspectionTaskTemplateDO::getCreateTime));
    }

    // ==================== 批量操作 ====================

    /**
     * 批量根据ID查询。
     */
    default List<InspectionTaskTemplateDO> selectByIds(@Param("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .in(InspectionTaskTemplateDO::getId, ids));
    }

    /**
     * 批量根据编码查询。
     */
    default List<InspectionTaskTemplateDO> selectByCodes(@Param("codes") List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .in(InspectionTaskTemplateDO::getTemplateCode, codes));
    }

    // ==================== 统计查询 ====================

    /**
     * 统计指定分类的模板数量。
     */
    default long countByCategoryId(@Param("categoryId") Long categoryId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getCategoryId, categoryId));
    }

    /**
     * 统计启用的模板数量。
     */
    default long countEnabled() {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getEnabled, true));
    }

    /**
     * 统计子模板数量。
     */
    default long countByParentId(@Param("parentId") Long parentId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskTemplateDO>()
                .eq(InspectionTaskTemplateDO::getParentId, parentId));
    }
}
