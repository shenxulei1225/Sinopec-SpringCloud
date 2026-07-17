package cn.iocoder.yudao.module.scene.platform.service.asset.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.scene.platform.enums.SceneAssetCategoryCodes;
import cn.iocoder.yudao.module.scene.platform.service.asset.SceneAssetCategoryValidator;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.iocoder.yudao.module.scene.platform.enums.ErrorCodeConstants.SCENE_ASSET_CATEGORY_INVALID;

/**
 * 通过分类表校验模型类型 code。
 * <p>
 * 过渡期直接读 {@code dynamicbusiness.dynamic_category}（与平台分类框架抽离前一致）；
 * 抽到 platform catalog 后改为窄 API，本类只换数据源。
 */
@Service
public class SceneAssetCategoryValidatorImpl implements SceneAssetCategoryValidator {

    private static final String EXISTS_SQL = """
            SELECT COUNT(1)
            FROM dynamicbusiness.dynamic_category
            WHERE deleted = false
              AND status = 1
              AND category_type_code = ?
              AND code = ?
              AND code <> ?
            """;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void validateAssetTypeCode(String assetType) {
        if (StrUtil.isBlank(assetType)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "资源类型不能为空");
        }
        String code = assetType.trim();
        Integer count = jdbcTemplate.queryForObject(
                EXISTS_SQL,
                Integer.class,
                SceneAssetCategoryCodes.CATEGORY_TYPE_CODE,
                code,
                SceneAssetCategoryCodes.ROOT
        );
        if (count == null || count < 1) {
            throw ServiceExceptionUtil.exception(
                    SCENE_ASSET_CATEGORY_INVALID,
                    code,
                    SceneAssetCategoryCodes.CATEGORY_TYPE_CODE
            );
        }
    }
}
