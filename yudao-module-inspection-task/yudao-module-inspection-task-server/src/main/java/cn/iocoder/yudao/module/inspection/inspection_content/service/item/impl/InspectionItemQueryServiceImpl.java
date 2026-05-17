package cn.iocoder.yudao.module.inspection.inspection_content.service.item.impl;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.item.InspectionItemRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.item.InspectionItemDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.item.InspectionItemMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.service.item.InspectionItemQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡检项查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionItemQueryServiceImpl implements InspectionItemQueryService {

    private final InspectionItemMapper itemMapper;

    @Override
    public InspectionItemRespVO getItem(Long id) {
        InspectionItemDO itemDO = itemMapper.selectById(id);
        return itemDO == null ? null : toRespVO(itemDO);
    }

    @Override
    public List<InspectionItemRespVO> getItemsBySourceTypeAndCategoryId(String sourceType, Long categoryId) {
        return itemMapper.selectListBySourceTypeAndCategoryId(sourceType, categoryId).stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InspectionItemRespVO> getItemsBySourceTypeAndCategoryIdAndObjectModel(String sourceType, Long categoryId, String objectModel) {
        return itemMapper.selectListBySourceTypeAndCategoryIdAndObjectModel(sourceType, categoryId, objectModel).stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InspectionItemRespVO> getItemsBySourceTypeAndObjectModel(String sourceType, String objectModel) {
        return itemMapper.selectListBySourceTypeAndObjectModel(sourceType, objectModel).stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InspectionItemRespVO> getItemsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return itemMapper.selectListByIds(ids).stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InspectionItemDO> getItemDOsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return itemMapper.selectListByIds(ids);
    }

    @Override
    public List<InspectionItemDO> getItemsForObject(String sourceType, Long categoryId, String objectModel) {
        // 1. 优先精确匹配：sourceType + categoryId + objectModel
        List<InspectionItemDO> items = itemMapper.selectListBySourceTypeAndCategoryIdAndObjectModel(sourceType, categoryId, objectModel);
        if (!items.isEmpty()) {
            return items;
        }

        // 2. 其次匹配：sourceType + objectModel（忽略分类）
        items = itemMapper.selectListBySourceTypeAndObjectModel(sourceType, objectModel);
        if (!items.isEmpty()) {
            return items;
        }

        // 3. 最后匹配：sourceType + categoryId（忽略型号）
        return itemMapper.selectListBySourceTypeAndCategoryId(sourceType, categoryId);
    }

    private InspectionItemRespVO toRespVO(InspectionItemDO itemDO) {
        return BeanUtils.toBean(itemDO, InspectionItemRespVO.class);
    }
}
