package cn.cheers.x.module.dynamicbusiness.service.unit;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.UnitCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.unit.UnitDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.unit.UnitMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
@Validated
public class UnitServiceImpl implements UnitService {

    @Resource
    private UnitMapper unitMapper;
    @Resource
    private FieldMapper fieldMapper;

    @Override
    public Long createUnit(UnitCreateReqVO reqVO) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        UnitDO existed = unitMapper.selectByCode(reqVO.getValue(), tenantId);
        if (existed != null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "单位编码已存在: " + reqVO.getValue());
        }
        UnitDO unit = UnitDO.builder()
                .name(StrUtil.trim(reqVO.getLabel()))
                .code(StrUtil.trim(reqVO.getValue()))
                .unitType(StrUtil.trimToNull(reqVO.getUnitType()))
                .sort(reqVO.getSort())
                .status(reqVO.getStatus())
                .remark(reqVO.getRemark())
                .build();
        unit.setTenantId(tenantId);
        unitMapper.insert(unit);
        return unit.getId();
    }

    @Override
    public void updateUnit(Long id, UnitCreateReqVO reqVO) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        UnitDO db = unitMapper.selectByIdAndTenant(id, tenantId);
        if (db == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND);
        }
        UnitDO sameCode = unitMapper.selectByCode(reqVO.getValue(), tenantId);
        if (sameCode != null && !sameCode.getId().equals(id)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "单位编码已存在: " + reqVO.getValue());
        }
        // 若从启用改为禁用，需校验是否被字段引用
        boolean disabling = db.getStatus() != null && db.getStatus() == 1 && reqVO.getStatus() != null && reqVO.getStatus() == 0;
        if (disabling) {
            Long usedCount = fieldMapper.selectCountByUnit(db.getCode(), tenantId);
            if (usedCount != null && usedCount > 0) {
                throw ServiceExceptionUtil.exception(BAD_REQUEST, "该单位已被字段引用，无法禁用");
            }
        }

        db.setName(StrUtil.trim(reqVO.getLabel()));
        db.setCode(StrUtil.trim(reqVO.getValue()));
        db.setUnitType(StrUtil.trimToNull(reqVO.getUnitType()));
        db.setSort(reqVO.getSort());
        db.setStatus(reqVO.getStatus());
        db.setRemark(reqVO.getRemark());
        unitMapper.updateById(db);
    }

    @Override
    public void deleteUnit(Long id) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        UnitDO db = unitMapper.selectByIdAndTenant(id, tenantId);
        if (db == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND);
        }
        Long usedCount = fieldMapper.selectCountByUnit(db.getCode(), tenantId);
        if (usedCount != null && usedCount > 0) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "该单位已被字段引用，无法删除");
        }
        unitMapper.deleteById(id);
    }

    @Override
    public List<UnitDO> listUnits(String unitType) {
        return unitMapper.selectListByType(StrUtil.trimToNull(unitType));
    }
}
