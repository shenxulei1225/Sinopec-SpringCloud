package cn.cheers.x.inspection.inspection_content.service.enhance;

import cn.cheers.x.inspection.inspection_content.service.source.ObjectSourceAdapter;
import cn.cheers.x.inspection.inspection_content.service.source.ObjectSourceRegistry;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 巡检内容增强服务。
 *
 * <p>用于在展示任务详情时，通过适配器查询原始业务系统的对象详情，</p>
 * <p>补充到 InspectionContent 中返回给前端。</p>
 *
 * <p>使用方式：</p>
 * <ul>
 *     <li>查询任务详情后，调用 {@link #enhanceContent(InspectionContent)} 增强内容</li>
 *     <li>或使用 {@link #enhanceContent(InspectionContent, boolean)} 控制是否增强</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentEnhancementService {

    private final ObjectSourceRegistry objectSourceRegistry;

    /**
     * 增强巡检内容，补充对象详情。
     *
     * <p>遍历 InspectionContent 中所有对象（groups 中的对象 + customObjects），根据 sourceType 调用对应适配器获取详情。</p>
     *
     * @param content 巡检内容（会被直接修改）
     * @return 增强后的巡检内容
     */
    public InspectionContent enhanceContent(InspectionContent content) {
        return enhanceContent(content, true);
    }

    /**
     * 增强巡检内容。
     *
     * @param content 巡检内容
     * @param enhanceDetail 是否增强对象详情
     * @return 增强后的巡检内容
     */
    public InspectionContent enhanceContent(InspectionContent content, boolean enhanceDetail) {
        if (content == null || !enhanceDetail) {
            return content;
        }

        // 收集所有对象：从 groups 和 customObjects
        List<InspectionContent.ObjectContent> allObjects = collectAllObjects(content);
        if (allObjects.isEmpty()) {
            return content;
        }

        // 按 sourceType 分组
        Map<String, List<InspectionContent.ObjectContent>> groupedByType = allObjects.stream()
                .filter(obj -> obj.getSourceType() != null)
                .collect(Collectors.groupingBy(InspectionContent.ObjectContent::getSourceType));

        // 逐类型查询详情
        for (Map.Entry<String, List<InspectionContent.ObjectContent>> entry : groupedByType.entrySet()) {
            String sourceType = entry.getKey();
            List<InspectionContent.ObjectContent> typeObjects = entry.getValue();

            List<String> objectCodes = typeObjects.stream()
                    .map(InspectionContent.ObjectContent::getObjectCode)
                    .filter(code -> code != null)
                    .collect(Collectors.toList());

            if (objectCodes.isEmpty()) {
                continue;
            }

            // 批量查询
            Map<String, ObjectSourceAdapter.ObjectDetail> details =
                    objectSourceRegistry.batchGetDetails(sourceType, objectCodes);

            // 填充详情
            for (InspectionContent.ObjectContent obj : typeObjects) {
                ObjectSourceAdapter.ObjectDetail detail = details.get(obj.getObjectCode());
                if (detail != null) {
                    obj.setDetail(convertToObjectDetail(detail));
                }
            }
        }

        return content;
    }

    /**
     * 收集 InspectionContent 中所有对象。
     *
     * <p>包括 groups 中的对象和 customObjects。</p>
     */
    private List<InspectionContent.ObjectContent> collectAllObjects(InspectionContent content) {
        List<InspectionContent.ObjectContent> result = new ArrayList<>();

        // 收集 groups 中的对象
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group.getObjects() != null) {
                    result.addAll(group.getObjects());
                }
            }
        }

        // 收集 customObjects
        if (content.getCustomObjects() != null) {
            result.addAll(content.getCustomObjects());
        }

        return result;
    }

    /**
     * 转换适配器详情为内容详情。
     */
    private InspectionContent.ObjectDetail convertToObjectDetail(ObjectSourceAdapter.ObjectDetail source) {
        InspectionContent.ObjectDetail target = new InspectionContent.ObjectDetail();
        target.setLocation(source.getLocation());
        target.setManager(source.getManager());
        target.setManagerPhone(source.getManagerPhone());
        target.setStatus(source.getStatus());
        target.setExtra(source.getExtra());
        return target;
    }
}
