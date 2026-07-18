package cn.cheers.x.scene.platform.enums;

/**
 * 场景模型分类节点编码约定。
 * <p>
 * {@code asset_resource.asset_type} 存平台分类种类 {@link #CATEGORY_TYPE_CODE} 下的
 * <strong>分类节点 code</strong>（纯分类），不再使用本地枚举校验。
 * 节点由 dynamicbusiness seed {@code dynamic_category_scene_asset.sql} 提供。
 */
public final class SceneAssetCategoryCodes {

    /** 分类种类（categoryType）编码 */
    public static final String CATEGORY_TYPE_CODE = "scene_asset";

    public static final String ROOT = "scene_asset_root";
    public static final String BUILDING = "BUILDING";
    public static final String TANK = "TANK";
    public static final String WALL = "WALL";
    public static final String WALL_HIGH = "WALL_HIGH";
    public static final String WALL_LOW = "WALL_LOW";
    public static final String ROAD = "ROAD";
    public static final String EQUIPMENT_MESH = "EQUIPMENT_MESH";

    private SceneAssetCategoryCodes() {
    }
}
