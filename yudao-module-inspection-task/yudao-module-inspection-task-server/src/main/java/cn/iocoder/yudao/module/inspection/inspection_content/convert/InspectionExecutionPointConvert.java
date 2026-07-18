package cn.iocoder.yudao.module.inspection.inspection_content.convert;

import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.model.InspectionExecutionPointView;

public class InspectionExecutionPointConvert {

    public static InspectionExecutionPointRespVO convertRespVO(InspectionExecutionPointView bean) {
        return BeanUtils.toBean(bean, InspectionExecutionPointRespVO.class);
    }
}
