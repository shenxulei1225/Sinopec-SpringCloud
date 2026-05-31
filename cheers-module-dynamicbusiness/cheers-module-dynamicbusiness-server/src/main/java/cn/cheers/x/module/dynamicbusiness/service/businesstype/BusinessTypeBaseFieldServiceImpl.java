package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.businesstype.BusinessTypeBaseFieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Validated
@Slf4j
public class BusinessTypeBaseFieldServiceImpl implements BusinessTypeBaseFieldService {

    @Resource
    private BusinessTypeBaseFieldMapper baseFieldMapper;

    @Resource
    private BusinessTypeMapper businessTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBaseField(BusinessTypeBaseFieldSaveReqVO reqVO) {
        if (businessTypeMapper.selectByCode(reqVO.getBusinessTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (baseFieldMapper.existsByFieldCode(reqVO.getBusinessTypeCode(), reqVO.getFieldCode(), null)) {
            throw new ServiceException(400, "字段编码已存在");
        }
        BusinessTypeBaseFieldDO field = BusinessTypeBaseFieldConvert.INSTANCE.convert(reqVO);
        if (field.getStatus() == null) {
            field.setStatus(1);
        }
        if (field.getSortOrder() == null) {
            field.setSortOrder(baseFieldMapper.selectMaxSortOrder(reqVO.getBusinessTypeCode()) + 1);
        }
        baseFieldMapper.insert(field);
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBaseField(BusinessTypeBaseFieldSaveReqVO reqVO) {
        BusinessTypeBaseFieldDO field = baseFieldMapper.selectById(reqVO.getId());
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        if (!Objects.equals(field.getBusinessTypeCode(), reqVO.getBusinessTypeCode())
                && businessTypeMapper.selectByCode(reqVO.getBusinessTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (baseFieldMapper.existsByFieldCode(reqVO.getBusinessTypeCode(), reqVO.getFieldCode(), reqVO.getId())) {
            throw new ServiceException(400, "字段编码已存在");
        }
        BusinessTypeBaseFieldConvert.INSTANCE.update(field, reqVO);
        baseFieldMapper.updateById(field);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBaseField(Long id) {
        BusinessTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            return;
        }
        baseFieldMapper.deleteById(id);
    }

    @Override
    public BusinessTypeBaseFieldDO getBaseField(Long id) {
        return baseFieldMapper.selectById(id);
    }

    @Override
    public BusinessTypeBaseFieldRespVO getBaseFieldRespVO(Long id) {
        return BusinessTypeBaseFieldConvert.INSTANCE.convert(baseFieldMapper.selectById(id));
    }

    @Override
    public List<BusinessTypeBaseFieldRespVO> listByBusinessTypeCode(String businessTypeCode) {
        return BusinessTypeBaseFieldConvert.INSTANCE.convertList(baseFieldMapper.selectByBusinessTypeCode(businessTypeCode));
    }

    @Override
    public List<BusinessTypeBaseFieldRespVO> listAllByBusinessTypeCode(String businessTypeCode) {
        return BusinessTypeBaseFieldConvert.INSTANCE.convertList(baseFieldMapper.selectAllByBusinessTypeCode(businessTypeCode));
    }

    @Override
    public List<BusinessTypeBaseFieldDO> getBaseFieldsByBusinessTypeCode(String businessTypeCode) {
        return baseFieldMapper.selectAllByBusinessTypeCode(businessTypeCode);
    }

    @Override
    public BusinessTypeBaseFieldDO getBaseFieldByCode(String businessTypeCode, String fieldCode) {
        return baseFieldMapper.selectByBusinessTypeCodeAndFieldCode(businessTypeCode, fieldCode);
    }

    @Override
    public boolean existsFieldCode(String businessTypeCode, String fieldCode) {
        return baseFieldMapper.existsByFieldCode(businessTypeCode, fieldCode, null);
    }

    @Override
    public void updateBaseFieldStatus(Long id, Integer status) {
        BusinessTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        field.setStatus(status);
        baseFieldMapper.updateById(field);
    }

    @Override
    public Long countByBusinessTypeCode(String businessTypeCode) {
        return baseFieldMapper.countByBusinessTypeCode(businessTypeCode);
    }

    @Override
    public List<String> getFieldCodes(String businessTypeCode) {
        return baseFieldMapper.selectList(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .select(BusinessTypeBaseFieldDO::getFieldCode)
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .eq(BusinessTypeBaseFieldDO::getStatus, 1))
                .stream().map(BusinessTypeBaseFieldDO::getFieldCode).toList();
    }

    @Override
    public String validateFieldValue(String businessTypeCode, String fieldCode, Object value) {
        BusinessTypeBaseFieldDO field = getBaseFieldByCode(businessTypeCode, fieldCode);
        if (field == null) {
            return "字段不存在";
        }
        if (!field.isEnabled()) {
            return "字段已禁用";
        }
        return null;
    }
}
