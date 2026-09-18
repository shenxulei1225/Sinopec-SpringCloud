package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.RelationInstanceBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.RelationMethodBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.RelationInstanceBindingMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.RelationMethodBindingMapper;
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
 * 通用关系绑定实现。
 *
 * <p><b>权威</b>：V109 {@code dynamic_relation_method_binding} / {@code dynamic_relation_instance_binding}。</p>
 * <p><b>键全部入参</b>：subjectType / hostType / dimensionKey / targetType 均由调用方传入，本类不写死业务类型码。</p>
 * <p><b>禁止</b>：多宿主共用同一目标；读路径补绑定。</p>
 */
@Service
public class RelationBindingServiceImpl implements RelationBindingService {

    @Resource
    private RelationMethodBindingMapper methodBindingMapper;

    @Resource
    private RelationInstanceBindingMapper instanceBindingMapper;

    @Resource
    private EntityService entityService;

    @Override
    public List<RelationMethodBindingRespVO> listMethods(String subjectType, long subjectId) {
        String type = requireTypeCode(subjectType, "subjectType");
        List<RelationMethodBindingDO> rows = methodBindingMapper.selectBySubject(type, subjectId);
        List<RelationMethodBindingRespVO> out = new ArrayList<>(rows.size());
        for (RelationMethodBindingDO row : rows) {
            out.add(toMethodResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsertMethod(RelationMethodBindingUpsertReqVO req) {
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        String targetType = requireTypeCode(req.getTargetType(), "targetType");
        requireTargetEntity(targetType, req.getTargetId(), "targetId");

        RelationMethodBindingDO existing = methodBindingMapper.selectByIdentity(
                subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        try {
            if (existing != null) {
                existing.setTargetType(targetType);
                existing.setTargetId(req.getTargetId());
                methodBindingMapper.updateById(existing);
                return existing.getId();
            }
            RelationMethodBindingDO created = RelationMethodBindingDO.builder()
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .targetType(targetType)
                    .targetId(req.getTargetId())
                    .build();
            methodBindingMapper.insert(created);
            return created.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "方法选用唯一约束冲突（同对象同维度已存在）");
        }
    }

    @Override
    public RelationInstanceBindingRespVO getInstanceBinding(
            String hostType,
            long hostId,
            String subjectType,
            long subjectId,
            String dimensionKey,
            String dimensionValue) {
        RelationInstanceBindingDO row = instanceBindingMapper.selectByIdentity(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId,
                requireTypeCode(dimensionKey, "dimensionKey"),
                normalizeDimensionValue(dimensionValue));
        return row == null ? null : toInstanceResp(row);
    }

    @Override
    public List<RelationInstanceBindingRespVO> listInstanceBindings(
            String hostType, long hostId, String subjectType, long subjectId) {
        List<RelationInstanceBindingDO> rows = instanceBindingMapper.selectByHostAndSubject(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId);
        List<RelationInstanceBindingRespVO> out = new ArrayList<>(rows.size());
        for (RelationInstanceBindingDO row : rows) {
            out.add(toInstanceResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertInstanceBinding(RelationInstanceBindingUpsertReqVO req) {
        String hostType = requireTypeCode(req.getHostType(), "hostType");
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        String targetType = requireTypeCode(req.getTargetType(), "targetType");
        requireTargetEntity(targetType, req.getTargetId(), "targetId");
        assertTargetNotBoundToOtherHost(targetType, req.getTargetId(), hostType, req.getHostId());

        RelationInstanceBindingDO existing = instanceBindingMapper.selectByIdentity(
                hostType, req.getHostId(), subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        try {
            if (existing != null) {
                existing.setTargetType(targetType);
                existing.setTargetId(req.getTargetId());
                instanceBindingMapper.updateById(existing);
                return;
            }
            RelationInstanceBindingDO created = RelationInstanceBindingDO.builder()
                    .hostType(hostType)
                    .hostId(req.getHostId())
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .targetType(targetType)
                    .targetId(req.getTargetId())
                    .build();
            instanceBindingMapper.insert(created);
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "实例绑定唯一约束冲突（同宿主同对象同维度已存在）");
        }
    }

    private void assertTargetNotBoundToOtherHost(String targetType, Long targetId, String hostType, Long hostId) {
        List<RelationInstanceBindingDO> rows = instanceBindingMapper.selectByTarget(targetType, targetId);
        for (RelationInstanceBindingDO row : rows) {
            if (!Objects.equals(hostType, row.getHostType()) || !hostId.equals(row.getHostId())) {
                throw new ServiceException(400,
                        "目标实体已被其它宿主占用，禁止多宿主共用同一可写目标：" + targetId);
            }
        }
    }

    private EntityRespVO requireTargetEntity(String targetType, Long targetId, String fieldName) {
        if (targetId == null) {
            throw new ServiceException(400, fieldName + " 不能为空");
        }
        EntityRespVO target = entityService.get(targetId, targetType);
        if (target == null || target.getId() == null) {
            throw new ServiceException(404, fieldName + " 对应目标实体不存在：" + targetId);
        }
        return target;
    }

    private RelationMethodBindingRespVO toMethodResp(RelationMethodBindingDO row) {
        RelationMethodBindingRespVO vo = new RelationMethodBindingRespVO();
        vo.setId(row.getId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setTargetType(row.getTargetType());
        vo.setTargetId(row.getTargetId());
        if (row.getTargetId() != null && StringUtils.hasText(row.getTargetType())) {
            EntityRespVO target = entityService.get(row.getTargetId(), row.getTargetType());
            if (target != null) {
                vo.setTargetName(target.getName());
            }
        }
        return vo;
    }

    private RelationInstanceBindingRespVO toInstanceResp(RelationInstanceBindingDO row) {
        RelationInstanceBindingRespVO vo = new RelationInstanceBindingRespVO();
        vo.setId(row.getId());
        vo.setHostType(row.getHostType());
        vo.setHostId(row.getHostId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setTargetType(row.getTargetType());
        vo.setTargetId(row.getTargetId());
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
