package cn.iocoder.yudao.module.inspection.inspection_content.service.item;

import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.item.InspectionItemRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.item.InspectionItemDO;

import java.util.List;

/**
 * 巡检项查询服务。
 */
public interface InspectionItemQueryService {

    /**
     * 获取巡检项详情。
     */
    InspectionItemRespVO getItem(Long id);

    /**
     * 根据业务实体类型和分类ID获取巡检项列表。
     */
    List<InspectionItemRespVO> getItemsBySourceTypeAndCategoryId(String sourceType, Long categoryId);

    /**
     * 根据业务实体类型、分类ID和型号获取巡检项列表（精确匹配）。
     */
    List<InspectionItemRespVO> getItemsBySourceTypeAndCategoryIdAndObjectModel(String sourceType, Long categoryId, String objectModel);

    /**
     * 根据业务实体类型和型号获取巡检项列表（忽略分类ID）。
     */
    List<InspectionItemRespVO> getItemsBySourceTypeAndObjectModel(String sourceType, String objectModel);

    /**
     * 根据 ID 列表获取巡检项列表。
     */
    List<InspectionItemRespVO> getItemsByIds(List<Long> ids);

    /**
     * 根据 ID 列表获取巡检项 DO 列表。
     */
    List<InspectionItemDO> getItemDOsByIds(List<Long> ids);

    /**
     * 获取对象关联的巡检项。
     *
     * @param sourceType 业务实体类型
     * @param categoryId 分类ID
     * @param objectModel 设备型号
     * @return 巡检项列表
     */
    List<InspectionItemDO> getItemsForObject(String sourceType, Long categoryId, String objectModel);
}
