package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.facility.StationOwnedCatalogSetup;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据管理左侧「安防管理」这一组数据目录。
 *
 * 不管什么：不写告警分类，不改告警图层的订阅。
 * 禁止：启动、开通、查询时调用。缺目录不得在这里补。底座账不存在时不得改建成自己的专用表。
 * 人员仍用用户目录，巡检仍用任务账，物防仍用设备账，技防仍用告警账。
 */
@Service
@Slf4j
public class SecurityCatalogEnsure {

    public static final String GROUP_NAME = "安防管理";

    private static final List<Catalog> CATALOGS = List.of(
            new Catalog("security_personnel", "人员管理", "user_account", false, 701),
            new Catalog("security_patrol", "巡检", "task", true, 702),
            new Catalog("security_phone", "有线电话", "equipment", true, 711),
            new Catalog("security_radio", "无线通讯", "equipment", true, 712),
            new Catalog("security_video", "视频监控", "equipment", true, 713),
            new Catalog("security_perimeter", "周界报警", "equipment", true, 714),
            new Catalog("security_intrusion", "入侵检测", "equipment", true, 715),
            new Catalog("security_access", "门禁", "equipment", true, 716),
            new Catalog("security_broadcast", "广播", "equipment", true, 717),
            new Catalog("security_resource", "安全资源位置", "equipment", true, 718),
            new Catalog("security_person", "人员识别", "alarm", true, 721),
            new Catalog("security_vehicle", "车辆定位", "alarm", true, 722),
            new Catalog("security_behavior", "行为识别", "alarm", true, 723)
    );

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private StationOwnedCatalogSetup stationOwnedCatalogSetup;

    /** 侧边栏要出现的目录。测试核对名称和所用的账，不连数据库。 */
    public static List<Catalog> catalogs() {
        return CATALOGS;
    }

    /**
     * 禁止调用。开通和启动都不得靠这个方法补目录。
     */
    public void ensure() {
        List<String> missing = new ArrayList<>();
        for (Catalog catalog : CATALOGS) {
            if (entityTypeMapper.selectByCode(catalog.code()) != null) {
                continue;
            }
            EntityTypeDO base = entityTypeMapper.selectByCode(catalog.baseCode());
            if (base == null) {
                missing.add(catalog.name() + "（要用的账「" + catalog.baseCode() + "」还没有）");
                log.error("安防目录「{}」没有登记：账 {} 还不存在", catalog.name(), catalog.baseCode());
                continue;
            }
            EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
            request.setSort(catalog.sort());
            request.setName(catalog.name());
            request.setCode(catalog.code());
            request.setAlias(catalog.name());
            request.setStatus("active");
            request.setEntryKind(EntityTypeDO.ENTRY_KIND_REUSE);
            request.setBaseEntityTypeCode(catalog.baseCode());
            request.setGroupName(GROUP_NAME);
            request.setDescription(description(catalog));
            request.setAssociationFields("{}");
            entityTypeService.create(request);
            if (catalog.stationOwned()) {
                stationOwnedCatalogSetup.setUpNewCatalog(catalog.code(), false);
            }
        }
        if (!missing.isEmpty()) {
            log.error("安防目录没有全部登记：{}", String.join("；", missing));
        }
    }

    private static String description(Catalog catalog) {
        String book = switch (catalog.baseCode()) {
            case "user_account" -> "用户目录";
            case "task" -> "任务账";
            case "equipment" -> "设备账";
            case "alarm" -> "告警账";
            default -> catalog.baseCode();
        };
        return "数据管理里的「" + catalog.name() + "」。使用已有的" + book + "，不另建表。";
    }

    /**
     * 一本安防数据目录。
     *
     * @param code 目录注册编码
     * @param name 侧边栏上的名字
     * @param baseCode 打开后读写的那本已有账
     * @param stationOwned 每条记录属于一座站：建好后写归属声明「所属场站 → 场站」
     * @param sort 分组内顺序
     */
    public record Catalog(String code, String name, String baseCode, boolean stationOwned, int sort) {
    }
}
