package cn.iocoder.yudao.module.scene.platform.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 场景平台错误码
 */
public interface ErrorCodeConstants {
    /**
     * 场景不存在
     */
    ErrorCode SCENE_NOT_EXISTS = new ErrorCode(1_030_000_000, "场景不存在");

    /**
     * 组件渲染配置非法
     */
    ErrorCode SCENE_RENDER_CONFIG_INVALID = new ErrorCode(1_030_000_001, "组件【{}】渲染配置非法，原因：{}");

    /**
     * 设施场景绑定不存在
     */
    ErrorCode FACILITY_SCENE_BINDING_NOT_EXISTS = new ErrorCode(1_030_000_002, "设施场景绑定不存在");

    /**
     * 资产类型不是 scene_asset 分类节点
     */
    ErrorCode SCENE_ASSET_CATEGORY_INVALID = new ErrorCode(
            1_030_000_003,
            "资源类型【{}】不是分类种类【{}】下的有效节点，请先 seed scene_asset 或在分类管理中创建"
    );
}

