package cn.iocoder.yudao.module.inspection.inspection_content.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.model.InspectionExecutionPointItemView;

public class InspectionExecutionPointItemConvert {

    public static InspectionExecutionPointItemRespVO convertRespVO(InspectionExecutionPointItemView bean) {
        return BeanUtils.toBean(bean, InspectionExecutionPointItemRespVO.class);
    }
}
