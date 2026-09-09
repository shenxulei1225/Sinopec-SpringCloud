package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopInstanceBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopMethodBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopInstanceBindingMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopMethodBindingMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 通用 SOP 绑定实现。
 *
 * <p><b>权威</b>：V80 {@code dynamic_sop_method_binding} / {@code dynamic_sop_instance_binding}。</p>
 * <p><b>键全部入参</b>：subjectType / hostType / dimensionKey 由调用方传入，本类不写死业务类型码。</p>
 * <p><b>禁止</b>：多宿主共用同一实例；读路径补绑定。</p>
 */
@Service
public class SopBindingServiceImpl implements SopBindingService {

    private static final String INSPECTION_ITEM_SUBJECT_TYPE = "inspection_item";

    @Resource
    private SopMethodBindingMapper methodBindingMapper;

    @Resource
    private SopInstanceBindingMapper instanceBindingMapper;

    @Resource
    private EntityService entityService;

    @Override
    public List<SopMethodBindingRespVO> listMethods(String subjectType, long subjectId) {
        String type = requireTypeCode(subjectType, "subjectType");
        List<SopMethodBindingDO> rows = methodBindingMapper.selectBySubject(type, subjectId);
        List<SopMethodBindingRespVO> out = new ArrayList<>(rows.size());
        for (SopMethodBindingDO row : rows) {
            out.add(toMethodResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsertMethod(SopMethodBindingUpsertReqVO req) {
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        requireSop(req.getSopId(), "sopId");

        SopMethodBindingDO existing = methodBindingMapper.selectByIdentity(
                subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        // SOP 一期改造后，“该查什么”统一由 SOP 标准包维护。
        // 旧链路仅保留读与既有数据观测，禁止再新增检查项维度绑定，避免双权威。
        if (existing == null && INSPECTION_ITEM_SUBJECT_TYPE.equalsIgnoreCase(subjectType)) {
            throw new ServiceException(400,
                    "SOP 标准包已接管检查项选用，旧方法绑定不再允许新增。请在 SOP 详情的标准包里维护。");
        }
        try {
            if (existing != null) {
                existing.setSopId(req.getSopId());
                methodBindingMapper.updateById(existing);
                return existing.getId();
            }
            SopMethodBindingDO created = SopMethodBindingDO.builder()
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .sopId(req.getSopId())
                    .build();
            methodBindingMapper.insert(created);
            return created.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "方法选用唯一约束冲突（同对象同维度已存在）");
        }
    }

    @Override
    public SopInstanceBindingRespVO getInstanceBinding(
            String hostType,
            long hostId,
            String subjectType,
            long subjectId,
            String dimensionKey,
            String dimensionValue) {
        SopInstanceBindingDO row = instanceBindingMapper.selectByIdentity(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId,
                requireTypeCode(dimensionKey, "dimensionKey"),
                normalizeDimensionValue(dimensionValue));
        return row == null ? null : toInstanceResp(row);
    }

    @Override
    public List<SopInstanceBindingRespVO> listInstanceBindings(
            String hostType, long hostId, String subjectType, long subjectId) {
        List<SopInstanceBindingDO> rows = instanceBindingMapper.selectByHostAndSubject(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId);
        List<SopInstanceBindingRespVO> out = new ArrayList<>(rows.size());
        for (SopInstanceBindingDO row : rows) {
            out.add(toInstanceResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertInstanceBinding(SopInstanceBindingUpsertReqVO req) {
        String hostType = requireTypeCode(req.getHostType(), "hostType");
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        requireSop(req.getSopInstanceId(), "sopInstanceId");
        assertInstanceNotBoundToOtherHost(req.getSopInstanceId(), hostType, req.getHostId());

        SopInstanceBindingDO existing = instanceBindingMapper.selectByIdentity(
                hostType, req.getHostId(), subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        try {
            if (existing != null) {
                existing.setSopInstanceId(req.getSopInstanceId());
                instanceBindingMapper.updateById(existing);
                return;
            }
            SopInstanceBindingDO created = SopInstanceBindingDO.builder()
                    .hostType(hostType)
                    .hostId(req.getHostId())
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .sopInstanceId(req.getSopInstanceId())
                    .build();
            instanceBindingMapper.insert(created);
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "实例绑定唯一约束冲突（同宿主同对象同维度已存在）");
        }
    }

    private void assertInstanceNotBoundToOtherHost(Long sopInstanceId, String hostType, Long hostId) {
        List<SopInstanceBindingDO> rows = instanceBindingMapper.selectBySopInstanceId(sopInstanceId);
        for (SopInstanceBindingDO row : rows) {
            if (!Objects.equals(hostType, row.getHostType()) || !hostId.equals(row.getHostId())) {
                throw new ServiceException(400,
                        "SOP 实例已被其它宿主占用，禁止多宿主共用同一可写实例：" + sopInstanceId);
            }
        }
    }

    private EntityRespVO requireSop(Long sopId, String fieldName) {
        EntityRespVO sop = entityService.get(sopId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, fieldName + " 对应 SOP 不存在：" + sopId);
        }
        return sop;
    }

    private SopMethodBindingRespVO toMethodResp(SopMethodBindingDO row) {
        SopMethodBindingRespVO vo = new SopMethodBindingRespVO();
        vo.setId(row.getId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setSopId(row.getSopId());
        if (row.getSopId() != null) {
            EntityRespVO sop = entityService.get(row.getSopId(), SopFieldCodes.ENTITY_TYPE_CODE);
            if (sop != null) {
                vo.setSopName(sop.getName());
            }
        }
        return vo;
    }

    private SopInstanceBindingRespVO toInstanceResp(SopInstanceBindingDO row) {
        SopInstanceBindingRespVO vo = new SopInstanceBindingRespVO();
        vo.setId(row.getId());
        vo.setHostType(row.getHostType());
        vo.setHostId(row.getHostId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setSopInstanceId(row.getSopInstanceId());
        return vo;
    }

    /** 类型码 / 维度键：去空白，禁止空串；不校验业务枚举。 */
    static String requireTypeCode(String raw, String fieldName) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, fieldName + " 不能为空");
        }
        return raw.trim();
    }

    /** 维度取值：去空白并统一大写（与常见手段枚举一致）；不限制取值集合。 */
    static String normalizeDimensionValue(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "dimensionValue 不能为空");
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }
}
