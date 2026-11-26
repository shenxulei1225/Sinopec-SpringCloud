package cn.cheers.x.module.dynamicbusiness.service.pipeline;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ScopeRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 管廊实时监控打开时，读这座站管线记录上已经保存的 GPS，算出整段管廊的经纬度外框。
 *
 * <p>管什么：按场站编号汇总管线、设备上已经保存的 {@code coordinate_gis}，取最外一圈经纬度。
 * 不管什么：不写入坐标，不在没有 GPS 时编一个范围，不改镜头。
 * 禁止：用页面上写死的经纬度代替库里的坐标。
 */
@Service
public class PipelineGpsExtentQueryService {

    /** 管线、设备目录。各管廊段上的点 GPS 写在 coordinate_gis。没有这本目录就不查。 */
    static final String PIPELINE_CATALOG = "pipeline";
    static final String EQUIPMENT_CATALOG = "equipment";

    private static final Pattern TABLE_NAME = Pattern.compile("ent_[a-z0-9_]+");

    private final EntityTableNameHandler entityTableNameHandler;
    private final JdbcTemplate jdbcTemplate;
    private final DataScopeQueryService dataScopeQueryService;

    public PipelineGpsExtentQueryService(EntityTableNameHandler entityTableNameHandler,
                                         JdbcTemplate jdbcTemplate,
                                         DataScopeQueryService dataScopeQueryService) {
        this.entityTableNameHandler = entityTableNameHandler;
        this.jdbcTemplate = jdbcTemplate;
        this.dataScopeQueryService = dataScopeQueryService;
    }

    /**
     * 这座站上管线 GPS 的外框。
     * 登录人的任职盖不住这座站时拒绝。一座点都没有时返回 null，调用方不要自己补范围。
     */
    public PipelineGpsExtent extentOfFacility(Long facilityId) {
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "场站编号不对");
        }
        boolean covered = dataScopeQueryService.listMyFacilities().stream()
                .map(ScopeRecord::id)
                .anyMatch(facilityId::equals);
        if (!covered) {
            throw new ServiceException(403, "没有这座站的查看范围");
        }
        List<String> tables = new ArrayList<>();
        tables.add(physicalTable(PIPELINE_CATALOG));
        if (entityTableNameHandler.isDynamicTable(EQUIPMENT_CATALOG)) {
            tables.add(physicalTable(EQUIPMENT_CATALOG));
        }
        StringBuilder union = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for (String table : tables) {
            if (union.length() > 0) {
                union.append(" UNION ALL ");
            }
            // 管线列是文本，设备列是 jsonb，统一转成 jsonb 再取经纬度。
            union.append("SELECT (coordinate_gis::jsonb->>'longitude')::float8 AS lng,")
                    .append(" (coordinate_gis::jsonb->>'latitude')::float8 AS lat FROM ")
                    .append(table)
                    .append(" WHERE deleted = false AND facility_id = ?")
                    .append(" AND coordinate_gis IS NOT NULL AND btrim(coordinate_gis::text) <> ''");
            args.add(facilityId);
        }
        String sql = "SELECT min(lng) AS west, max(lng) AS east, min(lat) AS south, max(lat) AS north"
                + " FROM (" + union + ") pts WHERE lng IS NOT NULL AND lat IS NOT NULL";
        List<PipelineGpsExtent> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            double west = rs.getDouble("west");
            if (rs.wasNull()) {
                return null;
            }
            double east = rs.getDouble("east");
            if (rs.wasNull()) {
                return null;
            }
            double south = rs.getDouble("south");
            if (rs.wasNull()) {
                return null;
            }
            double north = rs.getDouble("north");
            if (rs.wasNull()) {
                return null;
            }
            return extentOrNull(west, south, east, north);
        }, args.toArray());
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    private String physicalTable(String catalog) {
        String table = entityTableNameHandler.resolvePhysicalTableName(catalog);
        if (!TABLE_NAME.matcher(table).matches()) {
            throw new IllegalStateException("表名无法用于查询：" + table);
        }
        return table;
    }

    /** 四边不是一块有面积的地时，当作没有范围。 */
    static PipelineGpsExtent extentOrNull(double west, double south, double east, double north) {
        if (!Double.isFinite(west) || !Double.isFinite(south) || !Double.isFinite(east) || !Double.isFinite(north)) {
            return null;
        }
        if (east <= west || north <= south) {
            return null;
        }
        return new PipelineGpsExtent(west, south, east, north);
    }
}
