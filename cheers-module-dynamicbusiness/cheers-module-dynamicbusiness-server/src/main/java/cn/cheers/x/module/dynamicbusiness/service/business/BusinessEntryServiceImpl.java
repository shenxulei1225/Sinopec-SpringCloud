package cn.cheers.x.module.dynamicbusiness.service.business;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessEntryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessEntryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.business.BusinessEntryTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class BusinessEntryServiceImpl implements BusinessEntryService {

    @Resource
    private BusinessEntryMapper businessEntryMapper;
    @Resource
    private BusinessMapper businessMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BusinessEntryCreateReqVO reqVO) {
        assertBusinessExists(reqVO.getBusinessId());
        validateEntry(reqVO.getEntryType(), reqVO.getEntityTypeCode());
        if (businessEntryMapper.selectByBusinessIdAndCode(reqVO.getBusinessId(), reqVO.getCode()) != null) {
            throw new ServiceException(400, "业务入口编码已存在");
        }
        BusinessEntryDO entry = new BusinessEntryDO();
        copyFromVo(entry, reqVO);
        entry.setBusinessId(reqVO.getBusinessId());
        businessEntryMapper.insert(entry);
        return entry.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BusinessEntryUpdateReqVO reqVO) {
        BusinessEntryDO existing = businessEntryMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw new ServiceException(404, "业务入口不存在");
        }
        assertBusinessExists(reqVO.getBusinessId());
        validateEntry(reqVO.getEntryType(), reqVO.getEntityTypeCode());
        if (businessEntryMapper.existsByBusinessIdAndCodeExcludeId(
                reqVO.getBusinessId(), reqVO.getCode(), reqVO.getId())) {
            throw new ServiceException(400, "业务入口编码已存在");
        }
        copyFromVo(existing, reqVO);
        existing.setBusinessId(reqVO.getBusinessId());
        businessEntryMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (businessEntryMapper.selectById(id) == null) {
            throw new ServiceException(404, "业务入口不存在");
        }
        businessEntryMapper.deleteById(id);
    }

    @Override
    public BusinessEntryRespVO get(Long id) {
        BusinessEntryDO entry = businessEntryMapper.selectById(id);
        if (entry == null) {
            throw new ServiceException(404, "业务入口不存在");
        }
        return convertToVo(entry);
    }

    @Override
    public List<BusinessEntryRespVO> listByBusinessId(Long businessId) {
        return businessEntryMapper.selectByBusinessId(businessId).stream().map(this::convertToVo).toList();
    }

    private void assertBusinessExists(Long businessId) {
        BusinessDO business = businessMapper.selectById(businessId);
        if (business == null) {
            throw new ServiceException(404, "业务不存在");
        }
    }

    private void validateEntry(String entryType, String entityTypeCode) {
        if (BusinessEntryTypeEnum.ENTITY_ADMIN.getCode().equals(entryType)) {
            if (!StringUtils.hasText(entityTypeCode)) {
                throw new ServiceException(400, "实体台账入口必须绑定实体类型编码 entityTypeCode");
            }
            if (entityTypeMapper.selectByCode(entityTypeCode.trim()) == null) {
                throw new ServiceException(400, "实体类型不存在：" + entityTypeCode);
            }
        } else if (StringUtils.hasText(entityTypeCode)
                && entityTypeMapper.selectByCode(entityTypeCode.trim()) == null) {
            throw new ServiceException(400, "实体类型不存在：" + entityTypeCode);
        }
    }

    private void copyFromVo(BusinessEntryDO target, cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryBaseVO reqVO) {
        target.setCode(reqVO.getCode());
        target.setName(reqVO.getName());
        target.setEntryType(reqVO.getEntryType());
        target.setEntityTypeCode(StringUtils.hasText(reqVO.getEntityTypeCode()) ? reqVO.getEntityTypeCode().trim() : null);
        target.setScopeConfig(reqVO.getScopeConfig());
        target.setPageConfigId(reqVO.getPageConfigId());
        target.setSort(reqVO.getSort());
        target.setStatus(reqVO.getStatus());
    }

    private BusinessEntryRespVO convertToVo(BusinessEntryDO entry) {
        BusinessEntryRespVO vo = new BusinessEntryRespVO();
        vo.setId(entry.getId());
        vo.setBusinessId(entry.getBusinessId());
        vo.setCode(entry.getCode());
        vo.setName(entry.getName());
        vo.setEntryType(entry.getEntryType());
        vo.setEntityTypeCode(entry.getEntityTypeCode());
        vo.setScopeConfig(entry.getScopeConfig());
        vo.setPageConfigId(entry.getPageConfigId());
        vo.setSort(entry.getSort());
        vo.setStatus(entry.getStatus());
        vo.setCreateTime(entry.getCreateTime());
        return vo;
    }
}
