package cn.iocoder.yudao.module.scene.platform.service.coordinate.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateConvertReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateConvertRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinatePreviewRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateValidateRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateReferenceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneIdentityDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate.CoordinateReferenceMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneIdentityMapper;
import cn.iocoder.yudao.module.scene.platform.enums.CoordinateSpaceTypeEnum;
import cn.iocoder.yudao.module.scene.platform.enums.EngineFrameTypeEnum;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateCrsCatalogService;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateTransformService;
import jakarta.annotation.Resource;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class CoordinateTransformServiceImpl implements CoordinateTransformService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final double WGS84_A = 6378137.0d;
    private static final double WGS84_F = 1.0d / 298.257223563d;
    private static final double WGS84_E2 = WGS84_F * (2.0d - WGS84_F);

    @Resource
    private SceneIdentityMapper sceneIdentityMapper;
    @Resource
    private CoordinateReferenceMapper coordinateReferenceMapper;
    @Resource
    private CoordinateCrsCatalogService coordinateCrsCatalogService;

    @Override
    public CoordinateConvertRespVO convert(CoordinateConvertReqVO reqVO) {
        CoordinateReferenceDO reference = getCoordinateReference(reqVO);
        Vector3 source = new Vector3(reqVO.getX(), reqVO.getY(), reqVO.getZ());
        CoordinateSpaceTypeEnum sourceType = CoordinateSpaceTypeEnum.of(reqVO.getSourceSpaceType());
        CoordinateSpaceTypeEnum targetType = CoordinateSpaceTypeEnum.of(reqVO.getTargetSpaceType());
        Vector3 result = transform(source, sourceType, targetType, reference);
        CoordinateConvertRespVO respVO = new CoordinateConvertRespVO();
        respVO.setSourceSpaceType(sourceType.name());
        respVO.setTargetSpaceType(targetType.name());
        respVO.setX(result.x);
        respVO.setY(result.y);
        respVO.setZ(result.z);
        respVO.setPipeline(resolvePipeline(sourceType, targetType));
        respVO.setWarnings(resolveWarnings(reqVO, reference));
        return respVO;
    }

    @Override
    public CoordinatePreviewRespVO preview(CoordinateConvertReqVO reqVO) {
        CoordinateReferenceDO reference = getCoordinateReference(reqVO);
        CoordinateSpaceTypeEnum sourceType = CoordinateSpaceTypeEnum.of(reqVO.getSourceSpaceType());
        Vector3 source = new Vector3(reqVO.getX(), reqVO.getY(), reqVO.getZ());
        CoordinatePreviewRespVO respVO = new CoordinatePreviewRespVO();
        respVO.setGeographic(buildPreviewItem(source, sourceType, CoordinateSpaceTypeEnum.GEOGRAPHIC, reference));
        respVO.setProjected(buildPreviewItem(source, sourceType, CoordinateSpaceTypeEnum.PROJECTED, reference));
        respVO.setEcef(buildPreviewItem(source, sourceType, CoordinateSpaceTypeEnum.ECEF, reference));
        respVO.setLocalTangent(buildPreviewItem(source, sourceType, CoordinateSpaceTypeEnum.LOCAL_TANGENT, reference));
        respVO.setEngine(buildPreviewItem(source, sourceType, CoordinateSpaceTypeEnum.ENGINE, reference));
        respVO.setWarnings(resolveWarnings(reqVO, reference));
        respVO.setGeographicCrsCatalog(resolveCatalog(reference.getGeographicCrsCode()));
        respVO.setProjectedCrsCatalog(resolveCatalog(reference.getProjectedCrsCode()));
        return respVO;
    }

    @Override
    public CoordinateValidateRespVO validate(String sceneCode) {
        SceneIdentityDO scene = getRequiredScene(sceneCode);
        CoordinateReferenceDO reference = coordinateReferenceMapper.selectBySceneId(scene.getId());
        CoordinateValidateRespVO respVO = new CoordinateValidateRespVO();
        respVO.setSceneCode(sceneCode);
        List<String> checks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        checks.add(reference != null ? "coordinate_reference: OK" : "coordinate_reference: MISSING");
        if (reference != null) {
            checks.add(reference.getGeographicCrsCode() != null ? "geographic_crs_code: OK" : "geographic_crs_code: MISSING");
            checks.add(reference.getProjectedCrsCode() != null ? "projected_crs_code: OK" : "projected_crs_code: MISSING");
            checks.add(reference.getLocalFrameType() != null ? "local_frame_type: OK" : "local_frame_type: MISSING");
            checks.add(reference.getEngineFrameType() != null ? "engine_frame_type: OK" : "engine_frame_type: MISSING");
            if (reference.getOriginEcefX() == null || reference.getOriginEcefY() == null || reference.getOriginEcefZ() == null) {
                warnings.add("缺少 origin ECEF，后续建议在保存参考时显式固化");
            }
            if (reference.getProjectedCrsCode() == null) {
                warnings.add("未配置 projected CRS，Projected 转换不可用");
            }
        } else {
            warnings.add("场景未配置 coordinate_reference，无法执行真实场景转换");
        }
        respVO.setChecks(checks);
        respVO.setWarnings(warnings);
        respVO.setPassed(reference != null);
        if (reference != null) {
            CoordinateCrsCatalogRespVO geographicCatalog = resolveCatalog(reference.getGeographicCrsCode());
            CoordinateCrsCatalogRespVO projectedCatalog = resolveCatalog(reference.getProjectedCrsCode());
            respVO.setGeographicCrsCatalog(geographicCatalog);
            respVO.setProjectedCrsCatalog(projectedCatalog);
            if (reference.getGeographicCrsCode() != null && geographicCatalog == null) {
                warnings.add("geographic CRS 未在常用坐标系目录中登记: " + reference.getGeographicCrsCode());
            }
            if (reference.getProjectedCrsCode() != null && projectedCatalog == null) {
                warnings.add("projected CRS 未在常用坐标系目录中登记: " + reference.getProjectedCrsCode());
            }
            if (geographicCatalog != null && geographicCatalog.getWarningMessage() != null) {
                warnings.add("geographic CRS 提示: " + geographicCatalog.getWarningMessage());
            }
            if (projectedCatalog != null && projectedCatalog.getWarningMessage() != null) {
                warnings.add("projected CRS 提示: " + projectedCatalog.getWarningMessage());
            }
        }
        return respVO;
    }

    private CoordinateConvertRespVO buildPreviewItem(Vector3 source, CoordinateSpaceTypeEnum sourceType,
                                                     CoordinateSpaceTypeEnum targetType, CoordinateReferenceDO reference) {
        Vector3 result = transform(source, sourceType, targetType, reference);
        CoordinateConvertRespVO respVO = new CoordinateConvertRespVO();
        respVO.setSourceSpaceType(sourceType.name());
        respVO.setTargetSpaceType(targetType.name());
        respVO.setX(result.x);
        respVO.setY(result.y);
        respVO.setZ(result.z);
        respVO.setPipeline(resolvePipeline(sourceType, targetType));
        respVO.setWarnings(new ArrayList<>());
        return respVO;
    }

    private Vector3 transform(Vector3 source, CoordinateSpaceTypeEnum sourceType,
                              CoordinateSpaceTypeEnum targetType, CoordinateReferenceDO reference) {
        if (sourceType == targetType) {
            return source;
        }
        if (sourceType == CoordinateSpaceTypeEnum.PROJECTED && targetType == CoordinateSpaceTypeEnum.GEOGRAPHIC) {
            return projectedToGeographic(source, reference);
        }
        if (sourceType == CoordinateSpaceTypeEnum.GEOGRAPHIC && targetType == CoordinateSpaceTypeEnum.PROJECTED) {
            return geographicToProjected(source, reference);
        }
        if (sourceType == CoordinateSpaceTypeEnum.PROJECTED) {
            source = projectedToGeographic(source, reference);
            sourceType = CoordinateSpaceTypeEnum.GEOGRAPHIC;
        }
        Vector3 ecef;
        switch (sourceType) {
            case GEOGRAPHIC -> ecef = geographicToEcef(source);
            case ECEF -> ecef = source;
            case LOCAL_TANGENT -> ecef = localToEcef(source, reference);
            case ENGINE -> ecef = localToEcef(engineToLocal(source, reference), reference);
            default -> throw new IllegalArgumentException("暂不支持的源空间类型: " + sourceType.name());
        }
        if (targetType == CoordinateSpaceTypeEnum.PROJECTED) {
            return geographicToProjected(ecefToGeographic(ecef), reference);
        }
        return switch (targetType) {
            case ECEF -> ecef;
            case GEOGRAPHIC -> ecefToGeographic(ecef);
            case LOCAL_TANGENT -> ecefToLocal(ecef, reference);
            case ENGINE -> localToEngine(ecefToLocal(ecef, reference), reference);
            default -> throw new IllegalArgumentException("暂不支持的目标空间类型: " + targetType.name());
        };
    }

    private Vector3 projectedToGeographic(Vector3 projected, CoordinateReferenceDO reference) {
        if (reference.getProjectedCrsCode() == null || reference.getGeographicCrsCode() == null) {
            throw new IllegalArgumentException("Projected/Geographic CRS 未配置，无法执行投影转换");
        }
        return transform2D(projected, reference.getProjectedCrsCode(), reference.getGeographicCrsCode());
    }

    private Vector3 geographicToProjected(Vector3 geographic, CoordinateReferenceDO reference) {
        if (reference.getProjectedCrsCode() == null || reference.getGeographicCrsCode() == null) {
            throw new IllegalArgumentException("Projected/Geographic CRS 未配置，无法执行投影转换");
        }
        return transform2D(geographic, reference.getGeographicCrsCode(), reference.getProjectedCrsCode());
    }

    private Vector3 transform2D(Vector3 source, String sourceCrsCode, String targetCrsCode) {
        try {
            CoordinateReferenceSystem sourceCrs = CRS.decode(sourceCrsCode, false);
            CoordinateReferenceSystem targetCrs = CRS.decode(targetCrsCode, false);
            MathTransform transform = CRS.findMathTransform(sourceCrs, targetCrs, true);
            double[] sourcePos = new double[]{valueOf(source.x), valueOf(source.y)};
            double[] targetPos = new double[2];
            transform.transform(sourcePos, 0, targetPos, 0, 1);
            return new Vector3(decimal(targetPos[0]), decimal(targetPos[1]), source.z == null ? decimal(0) : source.z);
        } catch (Exception ex) {
            throw new IllegalArgumentException("投影转换失败: " + sourceCrsCode + " -> " + targetCrsCode, ex);
        }
    }

    private Vector3 geographicToEcef(Vector3 geographic) {
        double lon = Math.toRadians(valueOf(geographic.x));
        double lat = Math.toRadians(valueOf(geographic.y));
        double h = valueOf(geographic.z);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);
        double sinLon = Math.sin(lon);
        double cosLon = Math.cos(lon);
        double n = WGS84_A / Math.sqrt(1.0d - WGS84_E2 * sinLat * sinLat);
        double x = (n + h) * cosLat * cosLon;
        double y = (n + h) * cosLat * sinLon;
        double z = (n * (1.0d - WGS84_E2) + h) * sinLat;
        return new Vector3(decimal(x), decimal(y), decimal(z));
    }

    private Vector3 ecefToGeographic(Vector3 ecef) {
        double x = valueOf(ecef.x);
        double y = valueOf(ecef.y);
        double z = valueOf(ecef.z);
        double b = WGS84_A * (1.0d - WGS84_F);
        double ep2 = (WGS84_A * WGS84_A - b * b) / (b * b);
        double p = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(z * WGS84_A, p * b);
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double lon = Math.atan2(y, x);
        double lat = Math.atan2(z + ep2 * b * sinTheta * sinTheta * sinTheta,
                p - WGS84_E2 * WGS84_A * cosTheta * cosTheta * cosTheta);
        double sinLat = Math.sin(lat);
        double n = WGS84_A / Math.sqrt(1.0d - WGS84_E2 * sinLat * sinLat);
        double h = p / Math.cos(lat) - n;
        return new Vector3(decimal(Math.toDegrees(lon)), decimal(Math.toDegrees(lat)), decimal(h));
    }

    private Vector3 ecefToLocal(Vector3 ecef, CoordinateReferenceDO reference) {
        Vector3 originGeographic = getOriginGeographic(reference);
        Vector3 originEcef = getOriginEcef(reference, originGeographic);
        double lon = Math.toRadians(valueOf(originGeographic.x));
        double lat = Math.toRadians(valueOf(originGeographic.y));
        double dx = valueOf(ecef.x) - valueOf(originEcef.x);
        double dy = valueOf(ecef.y) - valueOf(originEcef.y);
        double dz = valueOf(ecef.z) - valueOf(originEcef.z);
        double sinLon = Math.sin(lon);
        double cosLon = Math.cos(lon);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);
        double east = -sinLon * dx + cosLon * dy;
        double north = -sinLat * cosLon * dx - sinLat * sinLon * dy + cosLat * dz;
        double up = cosLat * cosLon * dx + cosLat * sinLon * dy + sinLat * dz;
        return new Vector3(decimal(east), decimal(north), decimal(up));
    }

    private Vector3 localToEcef(Vector3 local, CoordinateReferenceDO reference) {
        Vector3 originGeographic = getOriginGeographic(reference);
        Vector3 originEcef = getOriginEcef(reference, originGeographic);
        double lon = Math.toRadians(valueOf(originGeographic.x));
        double lat = Math.toRadians(valueOf(originGeographic.y));
        double east = valueOf(local.x);
        double north = valueOf(local.y);
        double up = valueOf(local.z);
        double sinLon = Math.sin(lon);
        double cosLon = Math.cos(lon);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);
        double dx = -sinLon * east - sinLat * cosLon * north + cosLat * cosLon * up;
        double dy = cosLon * east - sinLat * sinLon * north + cosLat * sinLon * up;
        double dz = cosLat * north + sinLat * up;
        return new Vector3(decimal(valueOf(originEcef.x) + dx), decimal(valueOf(originEcef.y) + dy),
                decimal(valueOf(originEcef.z) + dz));
    }

    private Vector3 engineToLocal(Vector3 engine, CoordinateReferenceDO reference) {
        EngineFrameTypeEnum frame = normalizeEngineFrame(reference == null ? null : reference.getEngineFrameType());
        if (frame == EngineFrameTypeEnum.UE) {
            return new Vector3(engine.y, engine.x, engine.z);
        }
        return engine;
    }

    private Vector3 localToEngine(Vector3 local, CoordinateReferenceDO reference) {
        EngineFrameTypeEnum frame = normalizeEngineFrame(reference == null ? null : reference.getEngineFrameType());
        if (frame == EngineFrameTypeEnum.UE) {
            return new Vector3(local.y, local.x, local.z);
        }
        return local;
    }

    private CoordinateReferenceDO getCoordinateReference(CoordinateConvertReqVO reqVO) {
        if (reqVO.getSceneCode() == null || reqVO.getSceneCode().isBlank()) {
            return buildFallbackReference();
        }
        SceneIdentityDO scene = getRequiredScene(reqVO.getSceneCode());
        CoordinateReferenceDO reference = coordinateReferenceMapper.selectBySceneId(scene.getId());
        return reference != null ? reference : buildFallbackReference();
    }

    private CoordinateReferenceDO buildFallbackReference() {
        CoordinateReferenceDO reference = new CoordinateReferenceDO();
        reference.setOriginLng(decimal(0));
        reference.setOriginLat(decimal(0));
        reference.setOriginHeight(decimal(0));
        reference.setGeographicCrsCode("EPSG:4326");
        reference.setProjectedCrsCode("EPSG:3857");
        reference.setLocalFrameType("ENU");
        reference.setEngineFrameType(EngineFrameTypeEnum.THREE.name());
        reference.setLinearUnit("meter");
        reference.setAngularUnit("degree");
        return reference;
    }

    private Vector3 getOriginGeographic(CoordinateReferenceDO reference) {
        return new Vector3(
                reference.getOriginLng() == null ? decimal(0) : reference.getOriginLng(),
                reference.getOriginLat() == null ? decimal(0) : reference.getOriginLat(),
                reference.getOriginHeight() == null ? decimal(0) : reference.getOriginHeight());
    }

    private Vector3 getOriginEcef(CoordinateReferenceDO reference, Vector3 originGeographic) {
        if (reference.getOriginEcefX() != null && reference.getOriginEcefY() != null && reference.getOriginEcefZ() != null) {
            return new Vector3(reference.getOriginEcefX(), reference.getOriginEcefY(), reference.getOriginEcefZ());
        }
        return geographicToEcef(originGeographic);
    }

    private List<String> resolvePipeline(CoordinateSpaceTypeEnum sourceType, CoordinateSpaceTypeEnum targetType) {
        List<String> pipeline = new ArrayList<>();
        pipeline.add(sourceType.name());
        if (sourceType == targetType) {
            return pipeline;
        }
        if (sourceType == CoordinateSpaceTypeEnum.PROJECTED && targetType != CoordinateSpaceTypeEnum.GEOGRAPHIC) {
            pipeline.add(CoordinateSpaceTypeEnum.GEOGRAPHIC.name());
        }
        if (sourceType != CoordinateSpaceTypeEnum.ECEF
                && sourceType != CoordinateSpaceTypeEnum.PROJECTED
                && targetType != CoordinateSpaceTypeEnum.GEOGRAPHIC
                && targetType != CoordinateSpaceTypeEnum.PROJECTED) {
            if (sourceType == CoordinateSpaceTypeEnum.ENGINE) {
                pipeline.add(CoordinateSpaceTypeEnum.LOCAL_TANGENT.name());
            }
            pipeline.add(CoordinateSpaceTypeEnum.ECEF.name());
        }
        if (targetType == CoordinateSpaceTypeEnum.PROJECTED && !pipeline.contains(CoordinateSpaceTypeEnum.GEOGRAPHIC.name())) {
            pipeline.add(CoordinateSpaceTypeEnum.GEOGRAPHIC.name());
        }
        if (targetType == CoordinateSpaceTypeEnum.ENGINE && !pipeline.contains(CoordinateSpaceTypeEnum.LOCAL_TANGENT.name())) {
            pipeline.add(CoordinateSpaceTypeEnum.LOCAL_TANGENT.name());
        }
        if (!pipeline.contains(targetType.name())) {
            pipeline.add(targetType.name());
        }
        return pipeline;
    }

    private List<String> resolveWarnings(CoordinateConvertReqVO reqVO, CoordinateReferenceDO reference) {
        List<String> warnings = new ArrayList<>();
        if (reqVO.getSceneCode() == null && reqVO.getProfileCode() == null) {
            warnings.add("未指定 sceneCode 或 profileCode，当前使用默认参考框架");
        }
        if (reference.getProjectedCrsCode() == null) {
            warnings.add("当前未配置 projected CRS，Projected 转换不可用");
        }
        if (reference.getOriginEcefX() == null || reference.getOriginEcefY() == null || reference.getOriginEcefZ() == null) {
            warnings.add("当前 origin ECEF 由 origin geographic 动态推导，建议后续固化以避免重复计算");
        }
        return warnings;
    }

    private CoordinateCrsCatalogRespVO resolveCatalog(String crsCode) {
        return coordinateCrsCatalogService.getByCrsCode(crsCode);
    }

    private SceneIdentityDO getRequiredScene(String sceneCode) {
        SceneIdentityDO scene = sceneIdentityMapper.selectBySceneCode(sceneCode);
        if (scene == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景不存在");
        }
        return scene;
    }

    private EngineFrameTypeEnum normalizeEngineFrame(String engineFrameType) {
        return EngineFrameTypeEnum.of(engineFrameType);
    }

    private BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value).round(MC);
    }

    private double valueOf(BigDecimal value) {
        return value == null ? 0.0d : value.doubleValue();
    }

    private record Vector3(BigDecimal x, BigDecimal y, BigDecimal z) {
    }
}
