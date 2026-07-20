package cn.iocoder.yudao.module.emergency.dal.mysql.report;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.report.vo.InformationReportPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.report.InformationReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 信息报送流程 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface InformationReportMapper extends BaseMapperX<InformationReportDO> {

    default PageResult<InformationReportDO> selectPage(InformationReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InformationReportDO>()
                .eqIfPresent(InformationReportDO::getEventId, reqVO.getEventId())
                .eqIfPresent(InformationReportDO::getReportType, reqVO.getReportType())
                .eqIfPresent(InformationReportDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(InformationReportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InformationReportDO::getId));
    }
}








