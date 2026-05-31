package cn.cheers.x.module.dynamicbusiness.service.pagetemplate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.module.dynamicbusiness.config.TemplateProperties;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pagetemplate.vo.PageTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面模板 Service 实现类
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class PageTemplateServiceImpl implements PageTemplateService {

    @Resource
    private TemplateProperties templateProperties;

    /**
     * 模板缓存
     * key: templateId, value: PageTemplateVO
     */
    private final Map<String, PageTemplateVO> templateCache = new ConcurrentHashMap<>();

    /**
     * 缓存加载时间
     */
    private volatile long cacheLoadTime = 0;
    
    /**
     * 加载锁（防止并发加载）
     */
    private final java.util.concurrent.locks.ReentrantLock loadLock = new java.util.concurrent.locks.ReentrantLock();
    
    /**
     * 是否正在加载
     */
    private volatile boolean loading = false;

    @Override
    public List<PageTemplateVO> loadPageTemplates() {
        // 检查缓存
        if (templateProperties.isEnableCache() && isCacheValid()) {
            log.debug("[loadPageTemplates][从缓存加载页面模板]");
            List<PageTemplateVO> templates = new ArrayList<>(templateCache.values());
            templates.sort(Comparator.comparing(PageTemplateVO::getTemplateId));
            return templates;
        }

        // 防止并发加载
        if (loading) {
            log.debug("[loadPageTemplates][模板正在加载中，等待完成]");
            loadLock.lock();
            try {
                // 再次检查缓存
                if (isCacheValid()) {
                    List<PageTemplateVO> templates = new ArrayList<>(templateCache.values());
                    templates.sort(Comparator.comparing(PageTemplateVO::getTemplateId));
                    return templates;
                }
            } finally {
                loadLock.unlock();
            }
        }

        loadLock.lock();
        try {
            // 双重检查
            if (isCacheValid()) {
                List<PageTemplateVO> templates = new ArrayList<>(templateCache.values());
                templates.sort(Comparator.comparing(PageTemplateVO::getTemplateId));
                return templates;
            }

            loading = true;
            
            // 加载模板
            List<PageTemplateVO> templates = loadTemplatesFromFiles();

            // 更新缓存加载时间
            if (templateProperties.isEnableCache()) {
                cacheLoadTime = System.currentTimeMillis();
            }

            return templates;
        } finally {
            loading = false;
            loadLock.unlock();
        }
    }

    @Override
    public PageTemplateVO getTemplateById(String templateId) {
        // 参数验证
        if (templateId == null || templateId.trim().isEmpty()) {
            log.warn("[getTemplateById][模板ID为空]");
            return null;
        }

        // 先从缓存获取
        if (templateProperties.isEnableCache() && templateCache.containsKey(templateId)) {
            PageTemplateVO template = templateCache.get(templateId);
            if (template != null) {
                return template;
            }
        }

        // 缓存未命中，重新加载
        try {
            List<PageTemplateVO> templates = loadPageTemplates();
            
            if (templates == null || templates.isEmpty()) {
                log.warn("[getTemplateById][没有可用的模板]");
                return null;
            }

            return templates.stream()
                .filter(t -> t != null && templateId.equals(t.getTemplateId()))
                .findFirst()
                .orElse(null);
                
        } catch (Exception e) {
            log.error("[getTemplateById][获取模板失败，templateId={}]", templateId, e);
            return null;
        }
    }

    /**
     * 从文件系统加载所有模板
     */
    private List<PageTemplateVO> loadTemplatesFromFiles() {
        List<PageTemplateVO> templates = new ArrayList<>();
        
        try {
            // 获取模板目录
            File templateDir = getTemplateDirectory();
            
            if (!templateDir.exists()) {
                log.warn("[loadTemplatesFromFiles][模板目录不存在，路径={}]", templateDir.getAbsolutePath());
                return templates;
            }

            if (!templateDir.isDirectory()) {
                log.error("[loadTemplatesFromFiles][模板路径不是目录，路径={}]", templateDir.getAbsolutePath());
                return templates;
            }

            // 读取模板文件
            File[] files = templateDir.listFiles((dir, name) -> 
                name.endsWith(templateProperties.getTemplateFileExtension())
            );

            if (files == null || files.length == 0) {
                log.warn("[loadTemplatesFromFiles][模板目录为空，路径={}]", templateDir.getAbsolutePath());
                return templates;
            }

            int successCount = 0;
            int failCount = 0;

            // 逐个解析模板文件
            for (File file : files) {
                try {
                    // 读取文件内容
                    String jsonContent = FileUtil.readString(file, StandardCharsets.UTF_8);
                    
                    // 解析 JSON
                    PageTemplateVO template = parseTemplate(jsonContent);
                    
                    if (template != null && StrUtil.isNotBlank(template.getTemplateId())) {
                        templates.add(template);
                        
                        // 更新缓存
                        if (templateProperties.isEnableCache()) {
                            templateCache.put(template.getTemplateId(), template);
                        }
                        successCount++;
                        log.debug("[loadTemplatesFromFiles][成功加载模板，file={}, templateId={}]", 
                                file.getName(), template.getTemplateId());
                    } else {
                        failCount++;
                        log.error("[loadTemplatesFromFiles][模板解析失败，templateId为空，file={}]", 
                                file.getName());
                    }
                    
                } catch (Exception e) {
                    failCount++;
                    log.error("[loadTemplatesFromFiles][加载模板失败，file={}]", file.getName(), e);
                }
            }

            // 按模板ID排序
            templates.sort(Comparator.comparing(PageTemplateVO::getTemplateId));

            log.info("[loadTemplatesFromFiles][页面模板加载完成，成功={}, 失败={}]", successCount, failCount);
            
        } catch (Exception e) {
            log.error("[loadTemplatesFromFiles][加载模板目录失败]", e);
        }

        return templates;
    }

    /**
     * 获取模板目录
     */
    private File getTemplateDirectory() {
        String path = templateProperties.getPageTemplatePath();
        
        // 判断是否为绝对路径
        Path templatePath = Paths.get(path);
        if (templatePath.isAbsolute()) {
            return templatePath.toFile();
        }
        
        // 相对路径，相对于项目根目录
        String projectRoot = System.getProperty("user.dir");
        return Paths.get(projectRoot, path).toFile();
    }

    /**
     * 检查缓存是否有效
     */
    private boolean isCacheValid() {
        if (templateCache.isEmpty()) {
            return false;
        }

        long now = System.currentTimeMillis();
        long expireTime = templateProperties.getCacheExpireSeconds() * 1000;
        
        return (now - cacheLoadTime) < expireTime;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        templateCache.clear();
        cacheLoadTime = 0;
        log.info("[clearCache][模板缓存已清除]");
    }

    /**
     * 解析模板 JSON
     * 
     * @param jsonContent JSON 内容
     * @return 页面模板 VO
     */
    private PageTemplateVO parseTemplate(String jsonContent) {
        try {
            // 使用 Hutool 的 JSONUtil 解析
            PageTemplateVO template = new PageTemplateVO();
            
            // 解析为 Map
            Map<String, Object> jsonMap = JSONUtil.parseObj(jsonContent);
            
            // 映射基础字段
            template.setTemplateId(getStringValue(jsonMap, "templateId"));
            template.setTemplateName(getStringValue(jsonMap, "templateName"));
            template.setTemplateVersion(getStringValue(jsonMap, "templateVersion"));
            template.setTemplateType(getStringValue(jsonMap, "templateType"));
            template.setPageType(getStringValue(jsonMap, "pageType"));
            template.setPageName(getStringValue(jsonMap, "pageName"));
            template.setPageDescription(getStringValue(jsonMap, "pageDescription"));
            template.setPageIcon(getStringValue(jsonMap, "pageIcon"));
            template.setAuthor(getStringValue(jsonMap, "author"));
            template.setCreateTime(getStringValue(jsonMap, "createTime"));
            template.setUpdateTime(getStringValue(jsonMap, "updateTime"));
            
            // 映射数组字段
            template.setApplicableScenarios(getListValue(jsonMap, "适用场景"));
            template.setTags(getListValue(jsonMap, "tags"));
            
            // 映射对象字段
            template.setConfig(getMapValue(jsonMap, "config"));
            template.setConfigDescription(getMapValue(jsonMap, "配置说明"));
            
            return template;
            
        } catch (Exception e) {
            log.error("[parseTemplate][解析模板JSON失败]", e);
            return null;
        }
    }

    /**
     * 从 Map 中获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 从 Map 中获取列表值
     */
    @SuppressWarnings("unchecked")
    private List<String> getListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }

    /**
     * 从 Map 中获取 Map 值
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
}
