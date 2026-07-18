package cn.cheers.x.inspection.inspection_content.service.collection.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionPageReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionRespVO;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.collection.InspectionObjectCollectionDO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.inspection_content.dal.mysql.collection.InspectionObjectCollectionMapper;
import cn.cheers.x.inspection.inspection_content.service.collection.InspectionObjectCollectionQueryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 巡检对象集合查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionObjectCollectionQueryServiceImpl implements InspectionObjectCollectionQueryService {

    private final InspectionObjectCollectionMapper collectionMapper;

    @Override
    public InspectionObjectCollectionRespVO getCollection(Long id) {
        InspectionObjectCollectionDO collection = validateCollectionExists(id);
        return toRespVO(collection);
    }

    @Override
    public java.util.List<InspectionObjectCollectionRespVO> getCollectionPage(InspectionObjectCollectionPageReqVO reqVO) {
        Page<InspectionObjectCollectionDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        LambdaQueryWrapperX<InspectionObjectCollectionDO> wrapper = new LambdaQueryWrapperX<InspectionObjectCollectionDO>()
                .likeIfPresent(InspectionObjectCollectionDO::getCollectionCode, reqVO.getCollectionCode())
                .likeIfPresent(InspectionObjectCollectionDO::getCollectionName, reqVO.getCollectionName())
                .eqIfPresent(InspectionObjectCollectionDO::getStatus, reqVO.getStatus())
                .orderByDesc(InspectionObjectCollectionDO::getCreateTime);

        IPage<InspectionObjectCollectionDO> result = collectionMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<InspectionObjectCollectionRespVO> getSimpleCollectionList() {
        return collectionMapper.selectListByStatus("enabled").stream()
                .map(this::toRespVO)
                .collect(Collectors.toList());
    }

    private InspectionObjectCollectionRespVO toRespVO(InspectionObjectCollectionDO collection) {
        InspectionObjectCollectionRespVO respVO = BeanUtils.toBean(collection, InspectionObjectCollectionRespVO.class);
        // 设置统计信息：从 content 结构中计算
        if (collection.getContent() != null) {
            int objectCount = 0;
            int itemCount = 0;
            // 统计模板组中的对象和巡检项
            for (InspectionContent.ObjectGroup group : collection.getContent().getGroups()) {
                objectCount += group.getObjects() != null ? group.getObjects().size() : 0;
                if (group.getObjects() != null) {
                    for (InspectionContent.ObjectContent object : group.getObjects()) {
                        itemCount += object.getItems() != null ? object.getItems().size() : 0;
                    }
                }
            }
            // 统计自定义对象
            objectCount += collection.getContent().getCustomObjects() != null 
                    ? collection.getContent().getCustomObjects().size() : 0;
            respVO.setObjectCount(objectCount);
            respVO.setItemCount(itemCount);
        } else {
            respVO.setObjectCount(0);
            respVO.setItemCount(0);
        }
        return respVO;
    }

    private InspectionObjectCollectionDO validateCollectionExists(Long id) {
        InspectionObjectCollectionDO collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "巡检对象集合不存在");
        }
        return collection;
    }
}
