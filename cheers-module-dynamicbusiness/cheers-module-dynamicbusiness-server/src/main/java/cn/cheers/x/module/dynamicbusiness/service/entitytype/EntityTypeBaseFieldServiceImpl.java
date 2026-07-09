package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entitytype.EntityTypeBaseFieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Validated
@Slf4j
public class EntityTypeBaseFieldServiceImpl implements EntityTypeBaseFieldService {

    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    @Lazy
    private BusinessCapabilityService businessCapabilityService;

    private void notifyEntityTypeFieldDefinitionChanged(String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return;
        }
        businessCapabilityService.refreshAfterEntityTypeFieldDefinitionChanged(entityTypeCode.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBaseField(EntityTypeBaseFieldSaveReqVO reqVO) {
        if (entityTypeMapper.selectByCode(reqVO.getEntityTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (baseFieldMapper.existsByFieldCode(reqVO.getEntityTypeCode(), reqVO.getFieldCode(), null)) {
            throw new ServiceException(400, "字段编码已存在");
        }
        EntityTypeBaseFieldDO field = EntityTypeBaseFieldConvert.INSTANCE.convert(reqVO);
        if (field.getStatus() == null) {
            field.setStatus(1);
        }
        if (field.getSortOrder() == null) {
            field.setSortOrder(baseFieldMapper.selectMaxSortOrder(reqVO.getEntityTypeCode()) + 1);
        }
        baseFieldMapper.insert(field);
        notifyEntityTypeFieldDefinitionChanged(reqVO.getEntityTypeCode());
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBaseField(EntityTypeBaseFieldSaveReqVO reqVO) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(reqVO.getId());
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        if (!Objects.equals(field.getEntityTypeCode(), reqVO.getEntityTypeCode())
                && entityTypeMapper.selectByCode(reqVO.getEntityTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (baseFieldMapper.existsByFieldCode(reqVO.getEntityTypeCode(), reqVO.getFieldCode(), reqVO.getId())) {
            throw new ServiceException(400, "字段编码已存在");
        }
        EntityTypeBaseFieldConvert.INSTANCE.update(field, reqVO);
        baseFieldMapper.updateById(field);
        notifyEntityTypeFieldDefinitionChanged(reqVO.getEntityTypeCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBaseField(Long id) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            return;
        }
        String entityTypeCode = field.getEntityTypeCode();
        baseFieldMapper.deleteById(id);
        notifyEntityTypeFieldDefinitionChanged(entityTypeCode);
    }

    @Override
    public EntityTypeBaseFieldDO getBaseField(Long id) {
        return baseFieldMapper.selectById(id);
    }

    @Override
    public EntityTypeBaseFieldRespVO getBaseFieldRespVO(Long id) {
        return EntityTypeBaseFieldConvert.INSTANCE.convert(baseFieldMapper.selectById(id));
    }

    @Override
    public List<EntityTypeBaseFieldRespVO> listByEntityTypeCode(String entityTypeCode) {
        return EntityTypeBaseFieldConvert.INSTANCE.convertList(baseFieldMapper.selectByEntityTypeCode(entityTypeCode));
    }

    @Override
    public List<EntityTypeBaseFieldRespVO> listAllByEntityTypeCode(String entityTypeCode) {
        return EntityTypeBaseFieldConvert.INSTANCE.convertList(baseFieldMapper.selectAllByEntityTypeCode(entityTypeCode));
    }

    @Override
    public List<EntityTypeBaseFieldDO> getBaseFieldsByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.selectAllByEntityTypeCode(entityTypeCode);
    }

    @Override
    public EntityTypeBaseFieldDO getBaseFieldByCode(String entityTypeCode, String fieldCode) {
        return baseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode);
    }

    @Override
    public boolean existsFieldCode(String entityTypeCode, String fieldCode) {
        return baseFieldMapper.existsByFieldCode(entityTypeCode, fieldCode, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBaseFieldStatus(Long id, Integer status) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        field.setStatus(status);
        baseFieldMapper.updateById(field);
        notifyEntityTypeFieldDefinitionChanged(field.getEntityTypeCode());
    }

    @Override
    public Long countByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.countByEntityTypeCode(entityTypeCode);
    }

    @Override
    public List<String> getFieldCodes(String entityTypeCode) {
        return baseFieldMapper.selectList(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .select(EntityTypeBaseFieldDO::getFieldCode)
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getStatus, 1))
                .stream().map(EntityTypeBaseFieldDO::getFieldCode).toList();
    }

    @Override
    public String validateFieldValue(String entityTypeCode, String fieldCode, Object value) {
        EntityTypeBaseFieldDO field = getBaseFieldByCode(entityTypeCode, fieldCode);
        if (field == null) {
            return "字段不存在";
        }
        if (!field.isEnabled()) {
            return "字段已禁用";
        }
        return null;
    }
}
