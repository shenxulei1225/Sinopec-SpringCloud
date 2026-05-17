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
}

