package cn.iocoder.yudao.module.emergency.convert.report;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.report.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.report.InformationReportDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 信息报送流程 Convert
 *
 * @author 芋道源码
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InformationReportConvert {

    InformationReportConvert INSTANCE = Mappers.getMapper(InformationReportConvert.class);

    InformationReportDO convert(InformationReportCreateReqVO bean);

    InformationReportDO convert(InformationReportUpdateReqVO bean);

    InformationReportRespVO convert(InformationReportDO bean);

    PageResult<InformationReportRespVO> convertPage(PageResult<InformationReportDO> page);
}







