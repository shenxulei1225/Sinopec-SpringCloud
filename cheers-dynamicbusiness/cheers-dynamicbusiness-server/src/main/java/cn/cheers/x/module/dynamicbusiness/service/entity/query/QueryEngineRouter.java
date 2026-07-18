package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.engine.QueryEngine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants.ENGINE_NOT_AVAILABLE;

/**
 * 查询引擎路由器
 *
 * <p>负责管理和路由查询引擎，支持：</p>
 * <ul>
 *   <li>根据配置选择默认引擎</li>
 *   <li>运行时切换引擎</li>
 *   <li>引擎可用性检查</li>
 *   <li>引擎优先级排序</li>
 * </ul>
 *
 * <h3>配置方式</h3>
 * <p>通过 cheers.entity.search.type 配置项指定默认引擎类型：</p>
 * <ul>
 *   <li>postgresql - PostgreSQL JSONB + 索引表方案（默认）</li>
 *   <li>mysql - MySQL 索引表方案</li>
 *   <li>es / elasticsearch - Elasticsearch 方案</li>
 * </ul>
 *
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-001: 系统必须提供统一的扩展字段查询服务接口，屏蔽底层查询引擎差异</li>
 *   <li>FR-002: 系统必须支持通过配置切换查询引擎（postgresql、mysql、es）</li>
 *   <li>BR-ENG-001: 查询引擎通过配置 cheers.entity.search.type 切换</li>
 *   <li>BR-ENG-002: 支持 postgresql、mysql、es 三种配置值</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueryEngineRouter {

    /**
     * 所有可用的查询引擎列表
     * 通过 Spring 自动注入所有 QueryEngine 实现
     */
    private final List<QueryEngine> engines;

    /**
     * 配置的引擎类型
     * 默认使用 postgresql
     */
    @Value("${cheers.entity.search.type:postgresql}")
    private String configuredEngineType;

    /**
     * 当前使用的引擎
     */
    private volatile QueryEngine currentEngine;

    /**
     * 引擎类型到引擎实例的映射
     */
    private Map<String, QueryEngine> engineMap;

    /**
     * 初始化路由器
     *
     * <p>在 Spring 容器初始化完成后执行：</p>
     * <ol>
     *   <li>构建引擎类型映射</li>
     *   <li>按优先级排序引擎</li>
     *   <li>选择配置的默认引擎</li>
     * </ol>
     */
    @PostConstruct
    public void init() {
        // 构建引擎映射
        engineMap = new ConcurrentHashMap<>();
        for (QueryEngine engine : engines) {
            String type = engine.getType().toLowerCase();
            engineMap.put(type, engine);
            log.info("注册查询引擎: type={}, class={}, priority={}",
                type, engine.getClass().getSimpleName(), engine.getPriority());
        }

        // 选择默认引擎
        selectEngine(configuredEngineType);

        log.info("查询引擎路由器初始化完成: 当前引擎={}, 可用引擎={}",
            currentEngine != null ? currentEngine.getType() : "无",
            engineMap.keySet());
    }

    /**
     * 获取当前查询引擎
     *
     * @return 当前使用的查询引擎
     * @throws cn.cheers.x.framework.common.exception.ServiceException 如果没有可用引擎
     */
    public QueryEngine getEngine() {
        if (currentEngine == null) {
            throw exception(ENGINE_NOT_AVAILABLE, configuredEngineType);
        }
        return currentEngine;
    }

    /**
     * 获取指定类型的查询引擎
     *
     * @param engineType 引擎类型
     * @return 查询引擎，如果不存在返回 null
     */
    public QueryEngine getEngine(String engineType) {
        return engineMap.get(normalizeEngineType(engineType));
    }

    /**
     * 切换查询引擎（运行时）
     *
     * <p>支持在运行时动态切换查询引擎，无需重启服务。
     * 切换后的新请求将使用新引擎处理。</p>
     *
     * @param newEngineType 新的引擎类型
     * @throws cn.cheers.x.framework.common.exception.ServiceException 如果引擎类型不存在或不可用
     */
    public void switchEngine(String newEngineType) {
        String normalizedType = normalizeEngineType(newEngineType);
        QueryEngine newEngine = engineMap.get(normalizedType);

        if (newEngine == null) {
            throw exception(ENGINE_NOT_AVAILABLE, newEngineType);
        }

        if (!newEngine.isAvailable()) {
            throw exception(ENGINE_NOT_AVAILABLE, newEngineType);
        }

        QueryEngine oldEngine = this.currentEngine;
        this.currentEngine = newEngine;
        this.configuredEngineType = normalizedType;

        log.info("查询引擎切换完成: {} -> {}",
            oldEngine != null ? oldEngine.getType() : "无",
            newEngine.getType());
    }

    /**
     * 获取当前引擎类型
     *
     * @return 当前引擎类型
     */
    public String getCurrentEngineType() {
        return currentEngine != null ? currentEngine.getType() : null;
    }

    /**
     * 获取所有可用的引擎类型
     *
     * @return 引擎类型列表，按优先级排序
     */
    public List<String> getAvailableEngineTypes() {
        return engines.stream()
            .filter(QueryEngine::isAvailable)
            .sorted(Comparator.comparingInt(QueryEngine::getPriority))
            .map(QueryEngine::getType)
            .collect(Collectors.toList());
    }

    /**
     * 检查指定引擎是否可用
     *
     * @param engineType 引擎类型
     * @return 是否可用
     */
    public boolean isEngineAvailable(String engineType) {
        QueryEngine engine = engineMap.get(normalizeEngineType(engineType));
        return engine != null && engine.isAvailable();
    }

    /**
     * 获取引擎数量
     *
     * @return 已注册的引擎数量
     */
    public int getEngineCount() {
        return engineMap.size();
    }

    // ==================== 私有方法 ====================

    /**
     * 选择引擎
     */
    private void selectEngine(String engineType) {
        String normalizedType = normalizeEngineType(engineType);
        QueryEngine engine = engineMap.get(normalizedType);

        if (engine != null && engine.isAvailable()) {
            this.currentEngine = engine;
            log.info("选择查询引擎: type={}", normalizedType);
        } else {
            // 如果配置的引擎不可用，尝试选择优先级最高的可用引擎
            QueryEngine fallbackEngine = engines.stream()
                .filter(QueryEngine::isAvailable)
                .min(Comparator.comparingInt(QueryEngine::getPriority))
                .orElse(null);

            if (fallbackEngine != null) {
                this.currentEngine = fallbackEngine;
                log.warn("配置的引擎 {} 不可用，回退到 {}", engineType, fallbackEngine.getType());
            } else {
                log.error("没有可用的查询引擎！配置的引擎类型: {}", engineType);
            }
        }
    }

    /**
     * 标准化引擎类型
     *
     * <p>支持的别名：</p>
     * <ul>
     *   <li>es -> elasticsearch</li>
     *   <li>pg -> postgresql</li>
     * </ul>
     */
    private String normalizeEngineType(String engineType) {
        if (engineType == null) {
            return "postgresql";
        }

        String type = engineType.toLowerCase().trim();

        // 处理别名
        switch (type) {
            case "es":
                return "elasticsearch";
            case "pg":
                return "postgresql";
            default:
                return type;
        }
    }
}
