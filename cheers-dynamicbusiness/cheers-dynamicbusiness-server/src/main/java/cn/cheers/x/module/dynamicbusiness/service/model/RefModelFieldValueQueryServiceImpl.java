package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.mount.PayloadMounts;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 引用型号字段的取值。
 *
 * 负责：型号已经写明时直接读那个型号。型号空着时，读当前设备主云台挂着的那台设备的型号。
 * 点了名字就只返回这些字段。没点名字时只返回型号名。值是型号字段上存的那一份。
 * 不管什么：挂着的那台设备自己记下的数。
 * 禁止：把设备上的自定义字段当成型号字段的值。
 */
@Service
public class RefModelFieldValueQueryServiceImpl implements RefModelFieldValueQueryService {

    private static final String MAIN_GIMBAL_POINT = "0";

    private final EntityService entityService;
    private final ModelFieldAssignmentService modelFieldAssignmentService;
    private final ModelMapper modelMapper;

    public RefModelFieldValueQueryServiceImpl(EntityService entityService,
                                               ModelFieldAssignmentService modelFieldAssignmentService,
                                               ModelMapper modelMapper) {
        this.entityService = entityService;
        this.modelFieldAssignmentService = modelFieldAssignmentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefModelFieldValueRespVO read(RefModelFieldValueReqVO req) {
        List<Long> fieldIds = distinctIds(req.getSourceFieldIds());
        Long modelId = req.getSourceModelId();
        if (modelId != null && modelId <= 0) {
            throw new ServiceException(400, "型号编号不正确");
        }
        if (modelId == null) {
            ResolvedModel resolved = resolveMountedModel(req);
            if (resolved.notice() != null) {
                return RefModelFieldValueRespVO.ofNotice(resolved.notice());
            }
            modelId = resolved.modelId();
        }
        List<ModelFieldAssignmentRespVO> assigned = modelFieldAssignmentService.getModelFields(modelId);
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        List<RefModelFieldValueRespVO.Line> lines = new ArrayList<>();
        lines.add(nameLine(model.getName()));
        if (!fieldIds.isEmpty()) {
            lines.addAll(toLines(fieldIds, assigned));
        }
        RefModelFieldValueRespVO resp = RefModelFieldValueRespVO.ofLines(lines);
        resp.setModelId(modelId);
        return resp;
    }

    private ResolvedModel resolveMountedModel(RefModelFieldValueReqVO req) {
        if (req.getHostEntityId() == null || req.getHostEntityId() <= 0
                || !StringUtils.hasText(req.getHostEntityTypeCode())
                || !StringUtils.hasText(req.getEntityTypeCode())) {
            return ResolvedModel.notice("主云台还没有挂设备。");
        }
        EntityRespVO host = entityService.get(req.getHostEntityId(), req.getHostEntityTypeCode().trim());
        Long equipmentId = mainGimbalEquipmentId(host);
        if (equipmentId == null) {
            return ResolvedModel.notice("主云台还没有挂设备。");
        }
        EntityRespVO mounted = entityService.get(equipmentId, req.getEntityTypeCode().trim());
        Long modelId = mounted == null ? null : mounted.getModelId();
        if (modelId == null || modelId <= 0) {
            return ResolvedModel.notice("主云台上的设备没有型号。");
        }
        return ResolvedModel.model(modelId);
    }

    private static Long mainGimbalEquipmentId(EntityRespVO host) {
        if (host == null) {
            return null;
        }
        Object raw = null;
        if (host.getCustomFields() != null) {
            raw = host.getCustomFields().get(PayloadMounts.FIELD_CODE);
        }
        if (raw == null && host.getBaseFields() != null) {
            raw = host.getBaseFields().get(PayloadMounts.FIELD_CODE);
        }
        if (raw == null) {
            return null;
        }
        for (PayloadMounts.Row row : PayloadMounts.parse(raw)) {
            if (MAIN_GIMBAL_POINT.equals(row.mountPoint())) {
                return row.equipmentId();
            }
        }
        return null;
    }

    private static RefModelFieldValueRespVO.Line nameLine(String modelName) {
        RefModelFieldValueRespVO.Line line = new RefModelFieldValueRespVO.Line();
        line.setLabel("型号");
        line.setValue(modelName == null ? "" : modelName.trim());
        return line;
    }

    static List<RefModelFieldValueRespVO.Line> toLines(List<Long> fieldIds,
                                                        List<ModelFieldAssignmentRespVO> assigned) {
        Map<Long, ModelFieldAssignmentRespVO> byId = new LinkedHashMap<>();
        if (assigned != null) {
            for (ModelFieldAssignmentRespVO row : assigned) {
                if (row != null && row.getFieldId() != null) {
                    byId.putIfAbsent(row.getFieldId(), row);
                }
            }
        }
        List<RefModelFieldValueRespVO.Line> lines = new ArrayList<>();
        for (Long fieldId : fieldIds) {
            ModelFieldAssignmentRespVO row = byId.get(fieldId);
            RefModelFieldValueRespVO.Line line = new RefModelFieldValueRespVO.Line();
            line.setFieldId(fieldId);
            if (row == null) {
                line.setLabel("字段 " + fieldId);
                line.setValue("");
            } else {
                FieldRespVO field = row.getField();
                String label = field != null && StringUtils.hasText(field.getName())
                        ? field.getName().trim()
                        : row.getFieldCode();
                line.setLabel(StringUtils.hasText(label) ? label.trim() : "字段 " + fieldId);
                String unit = field == null ? null : field.getUnit();
                line.setValue(ModelFieldStoredValue.display(row.getDefaultValue(), unit));
            }
            lines.add(line);
        }
        return lines;
    }

    private static List<Long> distinctIds(List<Long> raw) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        if (raw == null) {
            return List.of();
        }
        for (Long id : raw) {
            if (id != null && id > 0) {
                ids.add(id);
            }
        }
        return List.copyOf(ids);
    }

    private record ResolvedModel(Long modelId, String notice) {
        static ResolvedModel model(Long modelId) {
            return new ResolvedModel(modelId, null);
        }

        static ResolvedModel notice(String notice) {
            return new ResolvedModel(null, notice);
        }
    }
}
