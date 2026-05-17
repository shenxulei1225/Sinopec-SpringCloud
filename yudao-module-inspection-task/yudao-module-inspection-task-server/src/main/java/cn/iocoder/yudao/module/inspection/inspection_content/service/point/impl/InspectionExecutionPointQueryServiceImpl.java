package cn.iocoder.yudao.module.inspection.inspection_content.service.point.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.point.InspectionExecutionPointDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.point.InspectionExecutionPointItemMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.point.InspectionExecutionPointMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.InspectionExecutionPointQueryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.model.InspectionExecutionPointView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 执行点位查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionExecutionPointQueryServiceImpl implements InspectionExecutionPointQueryService {

    private final InspectionExecutionPointMapper inspectionExecutionPointMapper;
    @SuppressWarnings("unused")
    private final InspectionExecutionPointItemMapper inspectionExecutionPointItemMapper;

    @Override
    public InspectionExecutionPointView getPointView(Long pointId) {
        InspectionExecutionPointDO pointDO = validatePointExists(pointId);
        return toPointView(pointDO);
    }

    @Override
    public List<InspectionExecutionPointView> getPointViewList(InspectionExecutionPointPageReqVO reqVO) {
        return inspectionExecutionPointMapper.selectListByCondition(reqVO).stream()
                .map(this::toPointView)
                .toList();
    }

    private InspectionExecutionPointDO validatePointExists(Long pointId) {
        InspectionExecutionPointDO pointDO = inspectionExecutionPointMapper.selectById(pointId);
        if (pointDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "执行点位不存在");
        }
        return pointDO;
    }

    private InspectionExecutionPointView toPointView(InspectionExecutionPointDO pointDO) {
        return BeanUtils.toBean(pointDO, InspectionExecutionPointView.class);
    }
}
