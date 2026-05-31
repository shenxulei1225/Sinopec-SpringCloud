package cn.cheers.x.module.dynamicbusiness.service.migration;

import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationResultVO;
import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationStatusVO;

/**
 * 数据迁移服务接口
 * 
 * <p>提供业务动态关联功能的数据迁移能力，包括：</p>
 * <ul>
 *   <li>现有关联字段迁移到新架构（关联字段库）</li>
 *   <li>智能默认设置应用到现有字段</li>
 *   <li>迁移状态查询和回滚支持</li>
 * </ul>
 * 
 * <h3>迁移场景</h3>
 * <ol>
 *   <li>关联字段迁移：将现有 ENTITY_REF 类型字段迁移到关联字段库架构</li>
 *   <li>智能默认应用：为现有字段应用智能默认的可查询和索引策略设置</li>
 * </ol>
 * 
 * @author yudao
 * @since 2026-01-07
 */
public interface DataMigrationService {

    /**
     * 迁移现有关联字段到关联字段库
     * 
     * <p>扫描所有 ENTITY_REF 类型的字段，将其迁移到关联字段库架构：</p>
     * <ul>
     *   <li>为每个关联字段在关联字段库中创建对应记录</li>
     *   <li>更新 ModelFieldAssignment 的 refLibraryId 引用</li>
     *   <li>保留原有字段定义，确保向后兼容</li>
     * </ul>
     * 
     * @param dryRun 是否为试运行模式（true: 只分析不执行，false: 实际执行迁移）
     * @return 迁移结果
     */
    MigrationResultVO migrateRelationFieldsToLibrary(boolean dryRun);

    /**
     * 应用智能默认设置到现有字段
     * 
     * <p>为所有未设置可查询属性的字段应用智能默认设置：</p>
     * <ul>
     *   <li>根据字段类型设置 isSearchable 属性</li>
     *   <li>根据字段类型设置 isSortable 属性</li>
     *   <li>根据字段类型设置 indexStrategy 属性</li>
     * </ul>
     * 
     * @param dryRun 是否为试运行模式
     * @param overwriteExisting 是否覆盖已有设置（true: 覆盖，false: 只设置 null 值）
     * @return 迁移结果
     */
    MigrationResultVO applySmartDefaultsToExistingFields(boolean dryRun, boolean overwriteExisting);

    /**
     * 执行完整迁移
     * 
     * <p>按顺序执行所有迁移任务：</p>
     * <ol>
     *   <li>迁移关联字段到关联字段库</li>
     *   <li>应用智能默认设置到现有字段</li>
     * </ol>
     * 
     * @param dryRun 是否为试运行模式
     * @return 迁移结果
     */
    MigrationResultVO executeFullMigration(boolean dryRun);

    /**
     * 获取迁移状态
     * 
     * <p>返回当前系统的迁移状态，包括：</p>
     * <ul>
     *   <li>待迁移的关联字段数量</li>
     *   <li>待应用智能默认的字段数量</li>
     *   <li>已完成迁移的记录数量</li>
     * </ul>
     * 
     * @return 迁移状态
     */
    MigrationStatusVO getMigrationStatus();

    /**
     * 回滚关联字段迁移
     * 
     * <p>将已迁移的关联字段回滚到迁移前状态：</p>
     * <ul>
     *   <li>清除 ModelFieldAssignment 的 refLibraryId 引用</li>
     *   <li>删除迁移创建的关联字段库记录（仅删除迁移创建的，不删除手动创建的）</li>
     * </ul>
     * 
     * @return 回滚结果
     */
    MigrationResultVO rollbackRelationFieldMigration();

    /**
     * 验证迁移数据完整性
     * 
     * <p>检查迁移后的数据是否完整和一致：</p>
     * <ul>
     *   <li>检查所有 ENTITY_REF 字段是否都有对应的关联字段库记录</li>
     *   <li>检查关联字段库记录的目标是否存在</li>
     *   <li>检查字段的智能默认设置是否正确</li>
     * </ul>
     * 
     * @return 验证结果
     */
    MigrationResultVO validateMigrationIntegrity();
}
