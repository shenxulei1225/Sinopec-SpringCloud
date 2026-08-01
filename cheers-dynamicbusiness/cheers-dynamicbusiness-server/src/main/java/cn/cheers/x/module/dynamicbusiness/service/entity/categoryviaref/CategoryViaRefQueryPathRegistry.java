package cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 经 REF 分类反查的路径白名单。未知 {@code pathCode} 时 {@link #require(String)} 抛出 400。
 */
@Component
public class CategoryViaRefQueryPathRegistry {

    /**
     * 设备分类 → 设备 → 任务上的「关联设备」REF。
     * <p>
     * 业务示例：点设备分类树「阀门」，列出 REF 指向挂在该分类（及子树）下设备的任务；任务本身不必挂「阀门」。
     * <p>
     * 读表（查询期，不写）：
     * <ul>
     *   <li>{@code dynamic_category_type} / 分类子树 — 确认维度为设备简单分类（equipment，SIMPLE）</li>
     *   <li>{@code dynamic_entity_category_relation} — 分类节点下的设备 id</li>
     *   <li>{@code dynamic_entity_relation} — 任务字段 {@code FLD-TSK-016}（关联设备，多选 REF）；本机库核对（2026-07-27）任务类型尚未分配该 field_code，仅有 {@code FLD-TSK-024}（所属设施）；路径启用前须与 {@code dynamic_model_field_assignment} 对齐真实 code</li>
     * </ul>
     */
    public static final String TASK_VIA_EQUIPMENT_CATEGORY = "task_via_equipment_category";

    /**
     * 运营区域分类 → 区域实体 → 设施上的「所属区域」REF。
     * <p>
     * 业务示例：点区域分类树某节点（高级分类绑定区域实体），列出 REF 指向该节点及子树所覆盖区域实体的设施；
     * 设施不必再挂区域分类。
     * <p>
     * 读表（查询期，不写）：
     * <ul>
     *   <li>{@code dynamic_category_type} — 维度为运营区域高级分类（region，ADVANCED）</li>
     *   <li>{@code dynamic_category_entity_link} — 分类节点绑定的区域实体 id（含子树）</li>
     *   <li>设施专用表 {@code region_id}（所属区域，单选 REF）</li>
     * </ul>
     * <p>
     * 当前字段分配下任务无区域 REF，故主体为 facility 而非 task。
     */
    public static final String FACILITY_VIA_REGION_CATEGORY = "facility_via_region_category";

    private final Map<String, CategoryViaRefQueryPath> byCode;

    public CategoryViaRefQueryPathRegistry() {
        this.byCode = Stream.of(
                taskViaEquipmentCategory(),
                facilityViaRegionCategory()
        ).collect(Collectors.toMap(CategoryViaRefQueryPath::pathCode, Function.identity()));
    }

    private static CategoryViaRefQueryPath taskViaEquipmentCategory() {
        return new CategoryViaRefQueryPath(
                TASK_VIA_EQUIPMENT_CATEGORY,
                "equipment",
                TargetEntityResolveMode.CATEGORY_ENTITY_RELATION,
                "equipment",
                "FLD-TSK-016",
                "task"
        );
    }

    private static CategoryViaRefQueryPath facilityViaRegionCategory() {
        return new CategoryViaRefQueryPath(
                FACILITY_VIA_REGION_CATEGORY,
                "region",
                TargetEntityResolveMode.CATEGORY_ENTITY_LINK,
                "region",
                "region_id",
                "facility"
        );
    }

    public CategoryViaRefQueryPath require(String pathCode) {
        return Optional.ofNullable(byCode.get(pathCode))
                .orElseThrow(() -> new ServiceException(400, "未知反查路径: " + pathCode));
    }
}
