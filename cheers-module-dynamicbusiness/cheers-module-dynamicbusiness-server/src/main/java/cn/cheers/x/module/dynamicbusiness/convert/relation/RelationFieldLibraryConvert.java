package cn.cheers.x.module.dynamicbusiness.convert.relation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryWithStatusVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 关联字段库 Convert
 * 
 * @author yudao
 */
@Mapper
public interface RelationFieldLibraryConvert {

    RelationFieldLibraryConvert INSTANCE = Mappers.getMapper(RelationFieldLibraryConvert.class);

    /**
     * 创建请求 VO 转 DO
     */
    RelationFieldLibraryDO convert(RelationFieldLibraryCreateReqVO reqVO);

    /**
     * 更新请求 VO 转 DO
     */
    RelationFieldLibraryDO convert(RelationFieldLibraryUpdateReqVO reqVO);

    /**
     * DO 转响应 VO
     */
    RelationFieldLibraryRespVO convert(RelationFieldLibraryDO field);

    /**
     * DO 列表转响应 VO 列表
     */
    List<RelationFieldLibraryRespVO> convertList(List<RelationFieldLibraryDO> list);

    /**
     * 分页结果转换
     */
    default PageResult<RelationFieldLibraryRespVO> convertPage(PageResult<RelationFieldLibraryDO> page) {
        if (page == null) {
            return null;
        }
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    /**
     * DO 转带状态的响应 VO
     * 
     * 注意：状态相关字段（status, targetEntityTypeName, targetModelName, targetExists）
     * 需要在 Service 层设置
     */
    default RelationFieldLibraryWithStatusVO convertWithStatus(RelationFieldLibraryDO field) {
        if (field == null) {
            return null;
        }
        RelationFieldLibraryWithStatusVO vo = new RelationFieldLibraryWithStatusVO();
        vo.setId(field.getId());
        vo.setFieldName(field.getFieldName());
        vo.setFieldCode(field.getFieldCode());
        vo.setRefEntityType(field.getRefEntityType());
        vo.setDisplayFieldCode(field.getDisplayFieldCode());
        vo.setConstraintEnabled(field.getConstraintEnabled());
        vo.setConstraintType(field.getConstraintType());
        vo.setDescription(field.getDescription());
        vo.setUsageCount(field.getUsageCount());
        vo.setIsSystem(field.getIsSystem());
        vo.setCreateTime(field.getCreateTime());
        vo.setUpdateTime(field.getUpdateTime());
        // 状态相关字段需要在 Service 层设置
        return vo;
    }

    /**
     * DO 列表转带状态的响应 VO 列表
     */
    default List<RelationFieldLibraryWithStatusVO> convertListWithStatus(List<RelationFieldLibraryDO> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::convertWithStatus).toList();
    }
}
