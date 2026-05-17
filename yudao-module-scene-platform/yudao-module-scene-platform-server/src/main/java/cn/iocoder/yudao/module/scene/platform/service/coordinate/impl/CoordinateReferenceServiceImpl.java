package cn.iocoder.yudao.module.scene.platform.service.coordinate.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceUpdateRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateValidationIssueRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateReferenceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate.CoordinateReferenceMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateCrsCatalogService;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateReferenceService;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateTransformProfileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class CoordinateReferenceServiceImpl implements CoordinateReferenceService {

    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private CoordinateReferenceMapper coordinateReferenceMapper;
    @Resource
    private CoordinateCrsCatalogService coordinateCrsCatalogService;
    @Resource
    private CoordinateTransformProfileService coordinateTransformProfileService;

    @Override
    public CoordinateReferenceRespVO getBySceneCode(String sceneCode) {
        SceneDO scene = getRequiredScene(sceneCode);
        CoordinateReferenceDO reference = coordinateReferenceMapper.selectBySceneId(scene.getId());
        if (reference == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景坐标参考不存在");
        }
        return buildResp(reference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoordinateReferenceUpdateRespVO updateBySceneCode(String sceneCode, CoordinateReferenceSaveReqVO reqVO, boolean strict) {
        SceneDO scene = getRequiredScene(sceneCode);
        CoordinateReferenceSaveReqVO mergedReqVO = applyProfileDefaults(reqVO);

        List<CoordinateValidationIssueRespVO> issues = new ArrayList<>();
        validateCatalogReference(mergedReqVO.getGeographicCrsCode(), "geographicCrsCode", "geographic CRS", strict, issues);
        validateOptionalCatalogReference(mergedReqVO.getProjectedCrsCode(), "projectedCrsCode", "projected CRS", strict, issues);
        validateReferenceConsistency(mergedReqVO, strict, issues);

        CoordinateReferenceDO reference = coordinateReferenceMapper.selectBySceneId(scene.getId());
        if (reference == null) {
            reference = new CoordinateReferenceDO();
            reference.setSceneId(scene.getId());
            fillReference(reference, mergedReqVO);
            coordinateReferenceMapper.insert(reference);
        } else {
            fillReference(reference, mergedReqVO);
            coordinateReferenceMapper.updateById(reference);
        }

        CoordinateReferenceUpdateRespVO respVO = new CoordinateReferenceUpdateRespVO();
        respVO.setSuccess(true);
        respVO.setStrict(strict);
        respVO.setIssues(issues);
        respVO.setWarnings(issues.stream()
                .filter(issue -> "WARNING".equals(issue.getLevel()))
                .map(CoordinateValidationIssueRespVO::getMessage)
                .toList());
        return respVO;
    }

    private CoordinateReferenceSaveReqVO applyProfileDefaults(CoordinateReferenceSaveReqVO reqVO) {
        if (!hasText(reqVO.getTransformProfileCode())) {
            return reqVO;
        }
        CoordinateReferenceSaveReqVO template = coordinateTransformProfileService.buildReferenceTemplate(reqVO.getTransformProfileCode());
        CoordinateReferenceSaveReqVO merged = BeanUtils.toBean(reqVO, CoordinateReferenceSaveReqVO.class);
        fillIfBlank(merged::getGeographicCrsCode, merged::setGeographicCrsCode, template.getGeographicCrsCode());
        fillIfBlank(merged::getProjectedCrsCode, merged::setProjectedCrsCode, template.getProjectedCrsCode());
        fillIfBlank(merged::getDatumCode, merged::setDatumCode, template.getDatumCode());
        fillIfBlank(merged::getEllipsoidCode, merged::setEllipsoidCode, template.getEllipsoidCode());
        fillIfBlank(merged::getPlanetShape, merged::setPlanetShape, template.getPlanetShape());
        fillIfBlank(merged::getReferenceFrameType, merged::setReferenceFrameType, template.getReferenceFrameType());
        fillIfBlank(merged::getLocalFrameType, merged::setLocalFrameType, template.getLocalFrameType());
        fillIfBlank(merged::getEngineFrameType, merged::setEngineFrameType, template.getEngineFrameType());
        fillIfBlank(merged::getAxisOrder, merged::setAxisOrder, template.getAxisOrder());
        fillIfBlank(merged::getHandedness, merged::setHandedness, template.getHandedness());
        fillIfBlank(merged::getLinearUnit, merged::setLinearUnit, template.getLinearUnit());
        fillIfBlank(merged::getAngularUnit, merged::setAngularUnit, template.getAngularUnit());
        fillIfBlank(merged::getTransformConfigJson, merged::setTransformConfigJson, template.getTransformConfigJson());
        return merged;
    }

    private void validateReferenceConsistency(CoordinateReferenceSaveReqVO reqVO, boolean strict,
                                              List<CoordinateValidationIssueRespVO> issues) {
        if (hasText(reqVO.getProjectedCrsCode())
                && (reqVO.getOriginProjectedX() == null || reqVO.getOriginProjectedY() == null)) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "PROJECTED_ORIGIN_MISSING",
                    "originProjectedX/originProjectedY",
                    "WARNING",
                    "已配置 projected CRS，但未完整提供投影原点坐标",
                    "建议同时填写 originProjectedX 与 originProjectedY"
            ));
        }
        if (!hasText(reqVO.getProjectedCrsCode())
                && (reqVO.getOriginProjectedX() != null || reqVO.getOriginProjectedY() != null || reqVO.getOriginProjectedZ() != null)) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "PROJECTED_CRS_MISSING",
                    "projectedCrsCode",
                    "ERROR",
                    "已填写投影原点坐标，但 projected CRS 为空",
                    "请补充 projectedCrsCode，或移除投影原点坐标"
            ));
        }
        if (!hasText(reqVO.getGeographicCrsCode())) {
            addIssue(true, issues, new CoordinateValidationIssueRespVO(
                    "GEOGRAPHIC_CRS_REQUIRED",
                    "geographicCrsCode",
                    "ERROR",
                    "地理坐标系编码不能为空",
                    "请提供 geographicCrsCode"
            ));
        }
        if (!hasText(reqVO.getLinearUnit())) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "LINEAR_UNIT_MISSING",
                    "linearUnit",
                    "WARNING",
                    "线性单位未填写",
                    "建议明确填写 linearUnit，例如 meter"
            ));
        }
        if (!hasText(reqVO.getAngularUnit())) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "ANGULAR_UNIT_MISSING",
                    "angularUnit",
                    "WARNING",
                    "角度单位未填写",
                    "建议明确填写 angularUnit，例如 degree"
            ));
        }
        if (hasAny(reqVO.getOriginEcefX(), reqVO.getOriginEcefY(), reqVO.getOriginEcefZ())
                && !allPresent(reqVO.getOriginEcefX(), reqVO.getOriginEcefY(), reqVO.getOriginEcefZ())) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "ECEF_ORIGIN_INCOMPLETE",
                    "originEcefX/originEcefY/originEcefZ",
                    "ERROR",
                    "ECEF 原点坐标填写不完整",
                    "请同时提供 originEcefX、originEcefY、originEcefZ"
            ));
        }
        if (hasText(reqVO.getReferenceFrameType()) && "LOCAL".equalsIgnoreCase(reqVO.getReferenceFrameType())
                && !hasText(reqVO.getLocalFrameType())) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "LOCAL_FRAME_REQUIRED",
                    "localFrameType",
                    "ERROR",
                    "referenceFrameType 为 LOCAL 时必须提供 localFrameType",
                    "请补充 localFrameType，例如 ENU"
            ));
        }
    }

    private void fillReference(CoordinateReferenceDO target, CoordinateReferenceSaveReqVO reqVO) {
        target.setGeographicCrsCode(reqVO.getGeographicCrsCode());
        target.setProjectedCrsCode(reqVO.getProjectedCrsCode());
        target.setDatumCode(reqVO.getDatumCode());
        target.setEllipsoidCode(reqVO.getEllipsoidCode());
        target.setOriginLng(reqVO.getOriginLng());
        target.setOriginLat(reqVO.getOriginLat());
        target.setOriginHeight(reqVO.getOriginHeight());
        target.setOriginProjectedX(reqVO.getOriginProjectedX());
        target.setOriginProjectedY(reqVO.getOriginProjectedY());
        target.setOriginProjectedZ(reqVO.getOriginProjectedZ());
        target.setOriginEcefX(reqVO.getOriginEcefX());
        target.setOriginEcefY(reqVO.getOriginEcefY());
        target.setOriginEcefZ(reqVO.getOriginEcefZ());
        target.setOriginHeading(reqVO.getOriginHeading());
        target.setOriginPitch(reqVO.getOriginPitch());
        target.setOriginRoll(reqVO.getOriginRoll());
        target.setLocalFrameType(reqVO.getLocalFrameType());
        target.setEngineFrameType(reqVO.getEngineFrameType());
        target.setReferenceFrameType(reqVO.getReferenceFrameType());
        target.setPlanetShape(reqVO.getPlanetShape());
        target.setAxisOrder(reqVO.getAxisOrder());
        target.setHandedness(reqVO.getHandedness());
        target.setLinearUnit(reqVO.getLinearUnit());
        target.setAngularUnit(reqVO.getAngularUnit());
        target.setTransformProfileCode(reqVO.getTransformProfileCode());
        target.setTransformConfigJson(reqVO.getTransformConfigJson());
    }

    private CoordinateReferenceRespVO buildResp(CoordinateReferenceDO reference) {
        CoordinateReferenceRespVO respVO = BeanUtils.toBean(reference, CoordinateReferenceRespVO.class);
        CoordinateCrsCatalogRespVO geographicCatalog = coordinateCrsCatalogService.getByCrsCode(reference.getGeographicCrsCode());
        CoordinateCrsCatalogRespVO projectedCatalog = coordinateCrsCatalogService.getByCrsCode(reference.getProjectedCrsCode());
        respVO.setGeographicCrsCatalog(geographicCatalog);
        respVO.setProjectedCrsCatalog(projectedCatalog);
        if (hasText(reference.getTransformProfileCode())) {
            CoordinateTransformProfileRespVO profile = coordinateTransformProfileService.getProfile(reference.getTransformProfileCode());
            respVO.setTransformProfile(profile);
        }
        return respVO;
    }

    private void validateCatalogReference(String crsCode, String field, String label, boolean strict,
                                          List<CoordinateValidationIssueRespVO> issues) {
        CoordinateCrsCatalogRespVO catalog = coordinateCrsCatalogService.getByCrsCode(crsCode);
        if (catalog == null) {
            addIssue(strict, issues, new CoordinateValidationIssueRespVO(
                    "CRS_NOT_REGISTERED",
                    field,
                    "ERROR",
                    label + " 未在常用坐标系目录中登记: " + crsCode,
                    "建议选择目录中已登记的 CRS，或先维护 catalog"
            ));
            return;
        }
        if (catalog.getWarningMessage() != null && !catalog.getWarningMessage().isBlank()) {
            issues.add(new CoordinateValidationIssueRespVO(
                    "CRS_CATALOG_WARNING",
                    field,
                    "WARNING",
                    label + " 提示: " + catalog.getWarningMessage(),
                    "请根据目录提示确认是否继续使用该 CRS"
            ));
        }
    }

    private void validateOptionalCatalogReference(String crsCode, String field, String label, boolean strict,
                                                  List<CoordinateValidationIssueRespVO> issues) {
        if (crsCode == null || crsCode.isBlank()) {
            return;
        }
        validateCatalogReference(crsCode, field, label, strict, issues);
    }

    private void addIssue(boolean strict, List<CoordinateValidationIssueRespVO> issues,
                          CoordinateValidationIssueRespVO issue) {
        if (strict && "ERROR".equals(issue.getLevel())) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, issue.getMessage());
        }
        issues.add(issue);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasAny(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return true;
            }
        }
        return false;
    }

    private boolean allPresent(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return false;
            }
        }
        return true;
    }

    private void fillIfBlank(java.util.function.Supplier<String> getter,
                             java.util.function.Consumer<String> setter,
                             String templateValue) {
        if (!hasText(getter.get()) && hasText(templateValue)) {
            setter.accept(templateValue);
        }
    }

    private SceneDO getRequiredScene(String sceneCode) {
        SceneDO scene = sceneMapper.selectBySceneCode(sceneCode);
        if (scene == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景不存在");
        }
        return scene;
    }
}
