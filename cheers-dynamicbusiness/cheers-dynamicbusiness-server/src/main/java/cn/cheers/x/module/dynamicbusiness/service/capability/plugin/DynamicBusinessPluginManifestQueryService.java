package cn.cheers.x.module.dynamicbusiness.service.capability.plugin;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.DynamicBusinessPluginManifestRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.DynamicBusinessPluginManifestDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.DynamicBusinessPluginManifestMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 动态业务插件清单查询服务。
 *
 * 负责：按后端权威状态下发“可启用插件列表”。
 * 不负责：前端安装流程、运行时渲染分发。
 */
@Service
public class DynamicBusinessPluginManifestQueryService {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    @Resource
    private DynamicBusinessPluginManifestMapper pluginManifestMapper;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 查询插件清单（数据库权威）。
     */
    public List<DynamicBusinessPluginManifestRespVO> listPluginManifests() {
        return pluginManifestMapper.selectAllOrderByPluginId().stream()
                .map(this::toResp)
                .toList();
    }

    /**
     * 插件启停维护入口。
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePluginEnabled(String pluginId, boolean enabled) {
        String key = pluginId == null ? "" : pluginId.trim();
        if (!StringUtils.hasText(key)) {
            throw new ServiceException(400, "pluginId 不能为空");
        }
        DynamicBusinessPluginManifestDO item = pluginManifestMapper.selectByPluginId(key);
        if (item == null) {
            throw new ServiceException(404, "插件不存在: " + key);
        }
        item.setEnabled(enabled);
        pluginManifestMapper.updateById(item);
    }

    private DynamicBusinessPluginManifestRespVO toResp(DynamicBusinessPluginManifestDO item) {
        DynamicBusinessPluginManifestRespVO vo = new DynamicBusinessPluginManifestRespVO();
        vo.setPluginId(item.getPluginId());
        vo.setVersion(item.getVersion());
        vo.setEnabled(Boolean.TRUE.equals(item.getEnabled()));
        vo.setPlatformRange(item.getPlatformRange());
        vo.setEntry(item.getEntry());
        vo.setDependsOn(parseStringList(item.getDependsOn(), "depends_on", item.getPluginId()));
        vo.setPermissions(parseStringList(item.getPermissions(), "permissions", item.getPluginId()));
        vo.setSemanticContributions(parseStringList(item.getSemanticContributions(), "semantic_contributions", item.getPluginId()));
        return vo;
    }

    private List<String> parseStringList(String raw, String field, String pluginId) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        try {
            List<String> parsed = objectMapper.readValue(raw, STRING_LIST);
            return parsed == null ? List.of() : parsed.stream().filter(StringUtils::hasText).map(String::trim).toList();
        } catch (Exception ex) {
            throw new ServiceException(500, "插件清单字段格式错误: " + pluginId + "." + field);
        }
    }
}
