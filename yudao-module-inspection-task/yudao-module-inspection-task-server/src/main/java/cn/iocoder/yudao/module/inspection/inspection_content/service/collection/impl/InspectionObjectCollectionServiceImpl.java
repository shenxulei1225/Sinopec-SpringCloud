package cn.iocoder.yudao.module.inspection.inspection_content.service.collection.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionCreateReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionUpdateReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.collection.InspectionObjectCollectionDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.collection.InspectionObjectCollectionMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.service.collection.InspectionObjectCollectionService;
import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.*;

/**
 * 巡检对象集合服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionObjectCollectionServiceImpl implements InspectionObjectCollectionService {

    private final InspectionObjectCollectionMapper collectionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCollection(InspectionObjectCollectionCreateReqVO createReqVO) {
        // 校验编码唯一性
        InspectionObjectCollectionDO existing = collectionMapper.selectByCollectionCode(createReqVO.getCollectionCode());
        if (existing != null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "集合编码已存在：{}", createReqVO.getCollectionCode());
        }

        // 创建集合
        InspectionObjectCollectionDO collection = new InspectionObjectCollectionDO();
        collection.setCollectionCode(createReqVO.getCollectionCode());
        collection.setCollectionName(createReqVO.getCollectionName());
        collection.setDescription(createReqVO.getDescription());
        collection.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : "enabled");
        collection.setRemark(createReqVO.getRemark());
        collection.setContent(createReqVO.getContent());
        collectionMapper.insert(collection);

        return collection.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollection(InspectionObjectCollectionUpdateReqVO updateReqVO) {
        // 校验集合存在
        InspectionObjectCollectionDO collection = validateCollectionExists(updateReqVO.getId());

        // 更新集合
        if (updateReqVO.getCollectionName() != null) {
            collection.setCollectionName(updateReqVO.getCollectionName());
        }
        if (updateReqVO.getDescription() != null) {
            collection.setDescription(updateReqVO.getDescription());
        }
        if (updateReqVO.getStatus() != null) {
            collection.setStatus(updateReqVO.getStatus());
        }
        if (updateReqVO.getRemark() != null) {
            collection.setRemark(updateReqVO.getRemark());
        }
        if (updateReqVO.getContent() != null) {
            collection.setContent(updateReqVO.getContent());
        }
        collectionMapper.updateById(collection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCollection(Long id) {
        // 校验集合存在
        validateCollectionExists(id);
        // 删除集合
        collectionMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollectionContent(Long id, InspectionContent content) {
        InspectionObjectCollectionDO collection = validateCollectionExists(id);
        collection.setContent(content);
        collectionMapper.updateById(collection);
    }

    private InspectionObjectCollectionDO validateCollectionExists(Long id) {
        InspectionObjectCollectionDO collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "巡检对象集合不存在");
        }
        return collection;
    }
}
