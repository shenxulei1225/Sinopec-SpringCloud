package cn.cheers.x.module.dynamicbusiness.dal.mysql.template;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字段模板 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface TemplateMapper extends BaseMapperX<TemplateDO> {

    /**
     * 根据模板编码查询
     */
    default TemplateDO selectByCode(String code) {
        return selectOne(TemplateDO::getCode, code);
    }

    /**
     * 根据模板名称查询
     */
    default TemplateDO selectByName(String name) {
        return selectOne(TemplateDO::getName, name);
    }

    /**
     * 根据业务类型编码查询模板列表
     */
    default List<TemplateDO> selectByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<TemplateDO>()
                .eqIfPresent(TemplateDO::getEntityTypeCode, entityTypeCode)
                .orderByAsc(TemplateDO::getId));
    }

    /**
     * 分页查询模板
     */
    default PageResult<TemplateDO> selectPage(TemplatePageReqVO reqVO) {
        LambdaQueryWrapperX<TemplateDO> wrapper = new LambdaQueryWrapperX<TemplateDO>()
                .eqIfPresent(TemplateDO::getEntityTypeCode, reqVO.getEntityTypeCode())
                .eqIfPresent(TemplateDO::getStatus, reqVO.getStatus())
                .orderByDesc(TemplateDO::getId);
        // 关键字搜索：名称或描述
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                    .like(TemplateDO::getName, reqVO.getKeyword())
                    .or()
                    .like(TemplateDO::getDescription, reqVO.getKeyword()));
        }
        return selectPage(reqVO, wrapper);
    }

    /**
     * 搜索模板（按名称和描述模糊搜索）
     */
    default List<TemplateDO> search(String keyword, String entityTypeCode) {
        LambdaQueryWrapperX<TemplateDO> wrapper = new LambdaQueryWrapperX<TemplateDO>()
                .eqIfPresent(TemplateDO::getEntityTypeCode, entityTypeCode)
                .orderByDesc(TemplateDO::getId);
        // 关键字搜索：名称或描述
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(TemplateDO::getName, keyword)
                    .or()
                    .like(TemplateDO::getDescription, keyword));
        }
        return selectList(wrapper);
    }
}
