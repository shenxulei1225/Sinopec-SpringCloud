package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.RelatableBusinessTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.businesstype.BusinessTypeRelationConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

@Service
@Validated
@Slf4j
public class BusinessTypeRelationServiceImpl implements BusinessTypeRelationService {

    @Resource
    private BusinessTypeRelationMapper relationMapper;

    @Resource
    private BusinessTypeMapper businessTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRelation(BusinessTypeRelationCreateReqVO reqVO) {
        if (businessTypeMapper.selectByCode(reqVO.getSourceBusinessTypeCode()) == null
                || businessTypeMapper.selectByCode(reqVO.getTargetBusinessTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        if (existsRelation(reqVO.getSourceBusinessTypeCode(), reqVO.getTargetBusinessTypeCode())) {
            throw new ServiceException(400, "业务类型关联已存在");
        }
        BusinessTypeRelationDO relation = BusinessTypeRelationConvert.INSTANCE.convert(reqVO);
        relationMapper.insert(relation);
        return relation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRelation(Long id) {
        relationMapper.deleteById(id);
    }

    @Override
    public BusinessTypeRelationRespVO getRelation(Long id) {
        return BusinessTypeRelationConvert.INSTANCE.convert(relationMapper.selectById(id));
    }

    @Override
    public List<BusinessTypeRelationRespVO> getRelationsBySourceCode(String sourceBusinessTypeCode) {
        return BusinessTypeRelationConvert.INSTANCE.convertList(relationMapper.selectBySourceBusinessTypeCode(sourceBusinessTypeCode));
    }

    @Override
    public List<BusinessTypeRelationRespVO> getRelationsByTargetCode(String targetBusinessTypeCode) {
        return BusinessTypeRelationConvert.INSTANCE.convertList(relationMapper.selectByTargetBusinessTypeCode(targetBusinessTypeCode));
    }

    @Override
    public List<BusinessTypeRelationRespVO> getAllRelations() {
        return BusinessTypeRelationConvert.INSTANCE.convertList(relationMapper.selectList());
    }

    @Override
    public boolean existsRelation(String sourceBusinessTypeCode, String targetBusinessTypeCode) {
        return relationMapper.existsBySourceAndTarget(sourceBusinessTypeCode, targetBusinessTypeCode);
    }

    @Override
    public List<RelatableBusinessTypeRespVO> getAvailableTargets(String currentBusinessTypeCode, String currentBusinessTypeName) {
        return businessTypeMapper.selectAllList().stream()
                .filter(it -> !it.getCode().equals(currentBusinessTypeCode))
                .filter(it -> !relationMapper.existsBySourceAndTarget(currentBusinessTypeCode, it.getCode()))
                .map(it -> new RelatableBusinessTypeRespVO(it.getCode(), it.getName()))
                .toList();
    }
}
