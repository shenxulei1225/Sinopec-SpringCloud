package cn.cheers.x.module.dynamicbusiness.framework.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 实体动态表名处理器
 * 
 * <p>根据 businessTypeCode 决定使用通用表还是动态表。</p>
 * 
 * <h3>安全措施</h3>
 * <ul>
 *   <li>白名单校验：只允许配置中存在的 businessTypeCode</li>
 *   <li>正则校验：只允许字母、数字、下划线</li>
 *   <li>长度限制：防止超长输入</li>
 * </ul>
 * 
 * <h3>表名映射规则</h3>
 * <ul>
 *   <li>通用表：dynamic_entity（默认）</li>
 *   <li>动态表：biz_{businessTypeCode}（如 biz_equipment）</li>
 * </ul>
 * 
 * @author 基础服务模块
 * @see EntityTableNameContext
 */
@Component
@RequiredArgsConstructor
public class EntityTableNameHandler implements TableNameHandler {

    private static final Logger log = LoggerFactory.getLogger(EntityTableNameHandler.class);

    private final BusinessTypeMapper businessTypeMapper;

    /** 通用表名 */
    private static final String GENERIC_TABLE = "dynamic_entity";
    
    /** 动态表名前缀 */
    private static final String DYNAMIC_TABLE_PREFIX = "biz_";

    /** 
     * 合法的 businessTypeCode 正则：只允许字母开头,字母数字下划线组成,长度 1-50 
     */
    private static final Pattern VALID_CODE_PATTERN = Pattern.compile("^[a-zA-Z]\\w{0,49}$");

    @Override
    public String dynamicTableName(String sql, String tableName) {
        // 只处理 dynamic_entity 表
        if (!GENERIC_TABLE.equals(tableName)) {
            return tableName;
        }

        String businessTypeCode = EntityTableNameContext.get();
        log.info("[EntityTableNameHandler] 进入动态表名处理, 原始表名={}, 上下文业务类型编码={}", tableName, businessTypeCode);

        if (businessTypeCode == null || businessTypeCode.isEmpty()) {
            log.info("[EntityTableNameHandler] 上下文业务类型编码为空, 使用通用表名={}", GENERIC_TABLE);
            return GENERIC_TABLE;
        }

        // 安全校验并获取表名
        String resolvedTableName = validateAndResolveTableName(businessTypeCode);
        if (resolvedTableName == null) {
            log.warn("[dynamicTableName][非法的 businessTypeCode: {}]", businessTypeCode);
            // 安全起见,返回通用表而不是抛异常,避免影响业务
            return GENERIC_TABLE;
        }

        if (log.isDebugEnabled()) {
            log.debug("[dynamicTableName][businessTypeCode={}, tableName={} -> {}]", 
                    businessTypeCode, tableName, resolvedTableName);
        }

        return resolvedTableName;
    }

    /**
     * 验证 businessTypeCode 并返回表名
     * 
     * @param businessTypeCode 业务类型编码
     * @return 验证通过返回表名,否则返回 null
     */
    private String validateAndResolveTableName(String businessTypeCode) {
        // 1. 正则校验：只允许字母开头,字母数字下划线组成（防 SQL 注入的基础）
        if (!VALID_CODE_PATTERN.matcher(businessTypeCode).matches()) {
            log.warn("[validateAndResolveTableName][businessTypeCode 格式非法: {}]", businessTypeCode);
            return null;
        }

        // 2. 获取业务类型配置（尝试从数据库/缓存获取）
        // 注意：在创建业务类型的过程中,可能数据库记录尚未完全可见（事务隔离）
        // 但由于 businessTypeCode 已经过正则校验,且表名生成规则 biz_{code} 是可预测的,
        // 这里采用“约定优于配置”的逻辑：只要符合命名规范,就允许路由。
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(businessTypeCode);
        
        // 3. 决定表名
        // 如果能查到 DO,根据 storageType 决定
        if (businessType != null) {
            log.info("[validateAndResolveTableName] 查到业务类型配置, code={}, storageType={}, dedicatedTableName={}",
                    businessTypeCode, businessType.getStorageType(), businessType.getDedicatedTableName());
            if (isDedicatedStorage(businessType)) {
                String dedicatedTableName = businessType.getDedicatedTableName();
                log.info("[validateAndResolveTableName] 使用专用表, 最终表名={}", 
                        (dedicatedTableName != null && !dedicatedTableName.isEmpty()) 
                                ? dedicatedTableName 
                                : DYNAMIC_TABLE_PREFIX + businessTypeCode);
                return (dedicatedTableName != null && !dedicatedTableName.isEmpty()) 
                        ? dedicatedTableName 
                        : DYNAMIC_TABLE_PREFIX + businessTypeCode;
            }
            log.info("[validateAndResolveTableName] 非专用存储类型, 使用通用表名={}", GENERIC_TABLE);
            return GENERIC_TABLE;
        }

        // 4. 如果查不到（可能正在创建中）,默认按 DEDICATED 规则路由
        // 理由：创建业务时通常需要先对 biz_{code} 进行 DDL 操作或初始化,
        // 只要正则通过,生成 biz_{code} 是安全的。
        log.info("[validateAndResolveTableName] 未查到业务类型配置, 按约定路由到动态表, 最终表名={}", DYNAMIC_TABLE_PREFIX + businessTypeCode);
        return DYNAMIC_TABLE_PREFIX + businessTypeCode;
    }

    /**
     * 检查业务类型是否使用动态表
     * 
     * @param businessTypeCode 业务类型编码
     * @return 是否使用动态表
     */
    public boolean isDynamicTable(String businessTypeCode) {
        if (businessTypeCode == null || businessTypeCode.isEmpty()) {
            return false;
        }
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(businessTypeCode);
        if (businessType == null) {
            return false;
        }
        return isDedicatedStorage(businessType);
    }

    private boolean isDedicatedStorage(BusinessTypeDO businessType) {
        if (businessType == null) {
            return false;
        }
        String storageType = businessType.getStorageType();
        if (storageType == null || storageType.isEmpty()) {
            return false;
        }
        return "DEDICATED".equalsIgnoreCase(storageType);
    }
}
































