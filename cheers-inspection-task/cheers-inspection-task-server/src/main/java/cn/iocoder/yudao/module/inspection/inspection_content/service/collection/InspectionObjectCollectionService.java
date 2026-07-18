package cn.iocoder.yudao.module.inspection.inspection_content.service.collection;

import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionCreateReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import jakarta.validation.Valid;

/**
 * 巡检对象集合服务。
 *
 * <p>用于管理巡检配置模板，方便快速添加到巡检任务中。</p>
 */
public interface InspectionObjectCollectionService {

    /**
     * 创建巡检对象集合（模板）。
     */
    Long createCollection(@Valid InspectionObjectCollectionCreateReqVO createReqVO);

    /**
     * 更新巡检对象集合（模板）。
     */
    void updateCollection(@Valid InspectionObjectCollectionUpdateReqVO updateReqVO);

    /**
     * 删除巡检对象集合（模板）。
     */
    void deleteCollection(Long id);

    /**
     * 更新集合内容。
     *
     * @param id 集合ID
     * @param content 巡检内容（复用 InspectionContent 结构）
     */
    void updateCollectionContent(Long id, InspectionContent content);
}
