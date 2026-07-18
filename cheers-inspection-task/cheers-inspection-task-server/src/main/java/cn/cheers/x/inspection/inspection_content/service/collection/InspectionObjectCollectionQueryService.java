package cn.cheers.x.inspection.inspection_content.service.collection;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionPageReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionRespVO;

import java.util.List;

/**
 * 巡检对象集合查询服务。
 */
public interface InspectionObjectCollectionQueryService {

    /**
     * 获取集合详情。
     */
    InspectionObjectCollectionRespVO getCollection(Long id);

    /**
     * 分页查询集合列表。
     */
    List<InspectionObjectCollectionRespVO> getCollectionPage(InspectionObjectCollectionPageReqVO reqVO);

    /**
     * 获取集合列表（简单信息）。
     */
    List<InspectionObjectCollectionRespVO> getSimpleCollectionList();
}
