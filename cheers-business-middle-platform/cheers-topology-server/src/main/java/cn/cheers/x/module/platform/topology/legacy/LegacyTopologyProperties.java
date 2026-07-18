package cn.cheers.x.module.platform.topology.legacy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "platform.topology.legacy")
public class LegacyTopologyProperties {

    /** 是否启用旧库导入 */
    private boolean enabled = false;

    /** 旧表所在 schema，如 public / dynamicbusiness */
    private String schema = "public";

    private String checkPointTable = "bs_check_point";

    private String distanceRouteTable = "bs_check_point_distance_route";

    private String weightTable = "bs_check_point_weight";

    /** 旧库站场字段名（默认 dept_id） */
    private String siteColumn = "dept_id";

    /** 经纬度转 Scene 局部坐标：1 度经度约等于多少 Scene 米（X 轴） */
    private double metersPerDegreeLng = 85000D;

    /** 经纬度转 Scene 局部坐标：1 度纬度约等于多少 Scene 米（Z 轴） */
    private double metersPerDegreeLat = 111000D;

    /** 罐组边界多边形相对 bbox 的外扩米数 */
    private double zonePaddingMeters = 5D;

}
