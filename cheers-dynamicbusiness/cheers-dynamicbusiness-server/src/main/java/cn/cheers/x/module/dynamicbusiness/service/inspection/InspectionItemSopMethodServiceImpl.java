package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.inspection.InspectionItemSopMethodDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.inspection.InspectionItemSopMethodMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopFieldCodes;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 检查项方法命令/查询。
 *
 * <p><b>权威</b>：V44 {@code dynamic_inspection_item_sop}；{@code sopId} = SOP 模板 id。</p>
 * <p><b>禁止</b>：读路径补方法行；引用非模板 SOP。</p>
 */
@Service
public class InspectionItemSopMethodServiceImpl implements InspectionItemSopMethodService {

    @Resource
    private InspectionItemSopMethodMapper inspectionItemSopMethodMapper;

    @Resource
    private EntityService entityService;

    @Override
    public List<InspectionItemSopMethodRespVO> listByInspectionItemId(long inspectionItemId) {
        List<InspectionItemSopMethodDO> rows =
                inspectionItemSopMethodMapper.selectByInspectionItemId(inspectionItemId);
        List<InspectionItemSopMethodRespVO> out = new ArrayList<>(rows.size());
        for (InspectionItemSopMethodDO row : rows) {
            out.add(toResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsert(InspectionItemSopMethodUpsertReqVO req) {
        String means = normalizeMeans(req.getExecutionMeans());
        requireSopTemplate(req.getSopTemplateId());

        InspectionItemSopMethodDO existing =
                inspectionItemSopMethodMapper.selectByItemAndMeans(req.getInspectionItemId(), means);
        try {
            if (existing != null) {
                existing.setSopId(req.getSopTemplateId());
                if (req.getSort() != null) {
                    existing.setSort(req.getSort());
                }
                inspectionItemSopMethodMapper.updateById(existing);
                return existing.getId();
            }
            InspectionItemSopMethodDO created = InspectionItemSopMethodDO.builder()
                    .inspectionItemId(req.getInspectionItemId())
                    .sopId(req.getSopTemplateId())
                    .executionMeans(means)
                    .sort(req.getSort() != null ? req.getSort() : 0)
                    .build();
            inspectionItemSopMethodMapper.insert(created);
            return created.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "检查项方法唯一约束冲突（同检查项同 SOP 已存在）");
        }
    }

    private InspectionItemSopMethodRespVO toResp(InspectionItemSopMethodDO row) {
        InspectionItemSopMethodRespVO vo = new InspectionItemSopMethodRespVO();
        vo.setId(row.getId());
        vo.setInspectionItemId(row.getInspectionItemId());
        vo.setSopTemplateId(row.getSopId());
        vo.setExecutionMeans(row.getExecutionMeans());
        vo.setSort(row.getSort());
        if (row.getSopId() != null) {
            EntityRespVO sop = entityService.get(row.getSopId(), SopFieldCodes.ENTITY_TYPE_CODE);
            if (sop != null) {
                vo.setSopName(sop.getName());
                Map<String, Object> base = sop.getBaseFields();
                Object isTpl = base != null ? base.get(SopFieldCodes.IS_TEMPLATE) : null;
                vo.setSopIsTemplate(readBool(isTpl));
            }
        }
        return vo;
    }

    private void requireSopTemplate(Long sopTemplateId) {
        EntityRespVO sop = entityService.get(sopTemplateId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, "SOP 模板不存在：" + sopTemplateId);
        }
        Map<String, Object> base = sop.getBaseFields();
        if (!readBool(base != null ? base.get(SopFieldCodes.IS_TEMPLATE) : null)) {
            throw new ServiceException(400, "sopTemplateId 必须指向 SOP 模板行（is_template=true）");
        }
    }

    static String normalizeMeans(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "executionMeans 不能为空");
        }
        String means = raw.trim().toUpperCase(Locale.ROOT);
        if (!means.equals("MANUAL")
                && !means.equals("UAV")
                && !means.equals("ROBOT")
                && !means.equals("FIXED_CAMERA")) {
            throw new ServiceException(400, "不支持的执行手段：" + raw);
        }
        return means;
    }

    private static boolean readBool(Object v) {
        if (v instanceof Boolean b) {
            return b;
        }
        if (v instanceof Number n) {
            return n.intValue() != 0;
        }
        if (v instanceof String s) {
            return "true".equalsIgnoreCase(s.trim()) || "1".equals(s.trim());
        }
        return false;
    }
}
