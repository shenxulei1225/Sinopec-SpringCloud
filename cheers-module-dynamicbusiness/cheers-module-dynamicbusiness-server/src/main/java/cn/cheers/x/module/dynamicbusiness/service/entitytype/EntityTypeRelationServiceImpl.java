package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.RelatableEntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.entitytype.EntityTypeRelationConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

@Service
@Validated
@Slf4j
public class EntityTypeRelationServiceImpl implements EntityTypeRelationService {

    @Resource
    private EntityTypeRelationMapper relationMapper;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRelation(EntityTypeRelationCreateReqVO reqVO) {
        if (entityTypeMapper.selectByCode(reqVO.getSourceEntityTypeCode()) == null
                || entityTypeMapper.selectByCode(reqVO.getTargetEntityTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (existsRelation(reqVO.getSourceEntityTypeCode(), reqVO.getTargetEntityTypeCode())) {
            throw new ServiceException(400, "业务类型关联已存在");
        }
        EntityTypeRelationDO relation = EntityTypeRelationConvert.INSTANCE.convert(reqVO);
        relationMapper.insert(relation);
        return relation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRelation(Long id) {
        relationMapper.deleteById(id);
    }

    @Override
    public EntityTypeRelationRespVO getRelation(Long id) {
        return EntityTypeRelationConvert.INSTANCE.convert(relationMapper.selectById(id));
    }

    @Override
    public List<EntityTypeRelationRespVO> getRelationsBySourceCode(String sourceEntityTypeCode) {
        return EntityTypeRelationConvert.INSTANCE.convertList(relationMapper.selectBySourceEntityTypeCode(sourceEntityTypeCode));
    }

    @Override
    public List<EntityTypeRelationRespVO> getRelationsByTargetCode(String targetEntityTypeCode) {
        return EntityTypeRelationConvert.INSTANCE.convertList(relationMapper.selectByTargetEntityTypeCode(targetEntityTypeCode));
    }

    @Override
    public List<EntityTypeRelationRespVO> getAllRelations() {
        return EntityTypeRelationConvert.INSTANCE.convertList(relationMapper.selectList());
    }

    @Override
    public boolean existsRelation(String sourceEntityTypeCode, String targetEntityTypeCode) {
        return relationMapper.existsBySourceAndTarget(sourceEntityTypeCode, targetEntityTypeCode);
    }

    @Override
    public List<RelatableEntityTypeRespVO> getAvailableTargets(String currentEntityTypeCode, String currentEntityTypeName) {
        return entityTypeMapper.selectAllList().stream()
                .filter(it -> !it.getCode().equals(currentEntityTypeCode))
                .filter(it -> !relationMapper.existsBySourceAndTarget(currentEntityTypeCode, it.getCode()))
                .map(it -> new RelatableEntityTypeRespVO(it.getCode(), it.getName()))
                .toList();
    }
}
