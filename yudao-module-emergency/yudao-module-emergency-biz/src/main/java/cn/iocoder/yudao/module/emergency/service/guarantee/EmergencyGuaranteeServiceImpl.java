package cn.iocoder.yudao.module.emergency.service.guarantee;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo.*;
import cn.iocoder.yudao.module.emergency.convert.guarantee.EmergencyGuaranteeConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee.EmergencyGuaranteeDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee.EmergencyGuaranteeResourceDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.guarantee.EmergencyGuaranteeMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.guarantee.EmergencyGuaranteeResourceMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应急保障 Service 实现类
 */
@Service
@Slf4j
public class EmergencyGuaranteeServiceImpl implements EmergencyGuaranteeService {

    private final EmergencyGuaranteeMapper guaranteeMapper;
    private final EmergencyGuaranteeResourceMapper resourceMapper;

    public EmergencyGuaranteeServiceImpl(EmergencyGuaranteeMapper guaranteeMapper,
                                        EmergencyGuaranteeResourceMapper resourceMapper) {
        this.guaranteeMapper = guaranteeMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGuarantee(GuaranteeCreateReqVO createReqVO) {
        // 校验保障编号唯一性
        validateGuaranteeCodeUnique(null, createReqVO.getGuaranteeCode());
        
        // 转换并插入
        EmergencyGuaranteeDO guarantee = EmergencyGuaranteeConvert.INSTANCE.convert(createReqVO);
        if (guarantee.getStatus() == null) {
            guarantee.setStatus("available");
        }
        if (guarantee.getIsEnabled() == null) {
            guarantee.setIsEnabled(true);
        }
        guaranteeMapper.insert(guarantee);
        return guarantee.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGuarantee(GuaranteeUpdateReqVO updateReqVO) {
        // 校验存在
        validateGuaranteeExists(updateReqVO.getId());
        // 校验保障编号唯一性
        validateGuaranteeCodeUnique(updateReqVO.getId(), updateReqVO.getGuaranteeCode());
        
        // 更新
        EmergencyGuaranteeDO updateObj = EmergencyGuaranteeConvert.INSTANCE.convert(updateReqVO);
        guaranteeMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuarantee(Long id) {
        // 校验存在
        validateGuaranteeExists(id);
        // 删除
        guaranteeMapper.deleteById(id);
        // 删除保障资源
        resourceMapper.delete(new LambdaQueryWrapperX<EmergencyGuaranteeResourceDO>()
                .eq(EmergencyGuaranteeResourceDO::getGuaranteeId, id));
    }

    private EmergencyGuaranteeDO validateGuaranteeExists(Long id) {
        EmergencyGuaranteeDO guarantee = guaranteeMapper.selectById(id);
        if (guarantee == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GUARANTEE_NOT_EXISTS);
        }
        return guarantee;
    }

    private void validateGuaranteeCodeUnique(Long id, String guaranteeCode) {
        EmergencyGuaranteeDO guarantee = guaranteeMapper.selectOne(
                new LambdaQueryWrapperX<EmergencyGuaranteeDO>()
                        .eq(EmergencyGuaranteeDO::getGuaranteeCode, guaranteeCode)
                        .neIfPresent(EmergencyGuaranteeDO::getId, id)
                        .last("LIMIT 1"));
        if (guarantee != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GUARANTEE_CODE_DUPLICATE);
        }
    }

    @Override
    public GuaranteeRespVO getGuarantee(Long id) {
        EmergencyGuaranteeDO guarantee = validateGuaranteeExists(id);
        return EmergencyGuaranteeConvert.INSTANCE.convert(guarantee);
    }

    @Override
    public PageResult<GuaranteeRespVO> getGuaranteePage(GuaranteePageReqVO pageReqVO) {
        PageResult<EmergencyGuaranteeDO> pageResult = guaranteeMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<EmergencyGuaranteeDO>()
                        .likeIfPresent(EmergencyGuaranteeDO::getGuaranteeName, pageReqVO.getGuaranteeName())
                        .eqIfPresent(EmergencyGuaranteeDO::getGuaranteeType, pageReqVO.getGuaranteeType())
                        .eqIfPresent(EmergencyGuaranteeDO::getStatus, pageReqVO.getStatus())
                        .eqIfPresent(EmergencyGuaranteeDO::getIsEnabled, pageReqVO.getIsEnabled())
                        .orderByDesc(EmergencyGuaranteeDO::getId));
        return EmergencyGuaranteeConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addGuaranteeResource(GuaranteeResourceCreateReqVO createReqVO) {
        // 校验保障存在
        validateGuaranteeExists(createReqVO.getGuaranteeId());
        
        // 转换并插入
        EmergencyGuaranteeResourceDO resource = EmergencyGuaranteeConvert.INSTANCE.convert(createReqVO);
        if (resource.getQuantity() == null) {
            resource.setQuantity(1);
        }
        resourceMapper.insert(resource);
        return resource.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGuaranteeResource(Long id) {
        // 校验存在
        EmergencyGuaranteeResourceDO resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GUARANTEE_RESOURCE_NOT_EXISTS);
        }
        // 删除
        resourceMapper.deleteById(id);
    }
}



