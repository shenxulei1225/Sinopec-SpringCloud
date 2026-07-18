package cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo;

import cn.iocoder.yudao.module.scene.platform.enums.SceneAssetCategoryCodes;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 资产资源保存 VO 校验。
 * <p>
 * 三维模型管理（asset resource）不得持久化场景坐标；经度、纬度、position 等字段
 * 仅允许出现在场景编排（actor instance + Transform），不在本 VO 定义范围内。
 * {@code assetType} 存 scene_asset 分类节点 code，存在性由 service 层校验。
 */
class AssetResourceSaveReqVOTest {

    private static final Pattern COORDINATE_FIELD_PATTERN = Pattern.compile(
            ".*(lon|lat|longitude|latitude|position|transform|gps|coord).*",
            Pattern.CASE_INSENSITIVE
    );

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validVo_passesValidation() {
        AssetResourceSaveReqVO vo = validVo();

        assertTrue(validator.validate(vo).isEmpty());
    }

    @Test
    void blankAssetType_failsValidation() {
        AssetResourceSaveReqVO vo = validVo();
        vo.setAssetType("  ");

        Set<String> messages = validator.validate(vo).stream()
                .map(v -> v.getMessage())
                .collect(Collectors.toSet());

        assertTrue(messages.stream().anyMatch(message -> message.contains("资源类型不能为空")));
    }

    @Test
    void assetSaveReqVo_hasNoCoordinateFields() {
        Set<String> fieldNames = Arrays.stream(AssetResourceSaveReqVO.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        Set<String> coordinateLikeFields = fieldNames.stream()
                .filter(name -> COORDINATE_FIELD_PATTERN.matcher(name).matches())
                .collect(Collectors.toSet());

        assertTrue(
                coordinateLikeFields.isEmpty(),
                () -> "AssetResourceSaveReqVO must not define coordinate fields, found: " + coordinateLikeFields
        );

        Set<String> expectedFields = Set.of(
                "assetCode", "assetName", "assetType", "assetUrl",
                "format", "previewUrl", "engineProfile", "metadataJson"
        );
        assertEquals(expectedFields, fieldNames);
    }

    @Test
    void sceneAssetCategoryTypeCode_isSceneAsset() {
        assertEquals("scene_asset", SceneAssetCategoryCodes.CATEGORY_TYPE_CODE);
    }

    private static AssetResourceSaveReqVO validVo() {
        AssetResourceSaveReqVO vo = new AssetResourceSaveReqVO();
        vo.setAssetCode("MDL-BUILDING-01");
        vo.setAssetName("主控楼");
        vo.setAssetType(SceneAssetCategoryCodes.BUILDING);
        vo.setAssetUrl("https://asset.example.com/models/building.fbx");
        vo.setFormat("fbx");
        vo.setPreviewUrl("https://asset.example.com/previews/building.png");
        vo.setEngineProfile("three-gltf");
        vo.setMetadataJson("{\"convertStatus\":\"pending\"}");
        return vo;
    }
}
