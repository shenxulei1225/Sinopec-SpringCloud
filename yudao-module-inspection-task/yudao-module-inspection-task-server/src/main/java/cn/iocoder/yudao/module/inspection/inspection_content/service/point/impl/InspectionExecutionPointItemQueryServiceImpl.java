package cn.iocoder.yudao.module.inspection.inspection_content.service.point.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.point.InspectionExecutionPointItemDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.point.InspectionExecutionPointItemMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.InspectionExecutionPointItemQueryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.model.InspectionExecutionPointItemView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 点位检查项查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionExecutionPointItemQueryServiceImpl implements InspectionExecutionPointItemQueryService {

    private final InspectionExecutionPointItemMapper inspectionExecutionPointItemMapper;

    @Override
    public InspectionExecutionPointItemView getPointItemView(Long id) {
        InspectionExecutionPointItemDO itemDO = validatePointItemExists(id);
        return toPointItemView(itemDO);
    }

    @Override
    public List<InspectionExecutionPointItemView> getPointItemViewList(InspectionExecutionPointItemPageReqVO reqVO) {
        return inspectionExecutionPointItemMapper.selectListByCondition(reqVO).stream()
                .map(this::toPointItemView)
                .toList();
    }

    private InspectionExecutionPointItemDO validatePointItemExists(Long id) {
        InspectionExecutionPointItemDO itemDO = inspectionExecutionPointItemMapper.selectById(id);
        if (itemDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "点位检查项不存在");
        }
        return itemDO;
    }

    private InspectionExecutionPointItemView toPointItemView(InspectionExecutionPointItemDO itemDO) {
        return BeanUtils.toBean(itemDO, InspectionExecutionPointItemView.class);
    }
}
