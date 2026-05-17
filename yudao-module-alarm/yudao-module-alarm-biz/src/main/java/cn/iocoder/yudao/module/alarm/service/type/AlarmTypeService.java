package cn.iocoder.yudao.module.alarm.service.type;

import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeCategoryVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeModelVO;

import java.util.List;

/**
 * 告警类型服务接口
 * 
 * <p>告警类型使用系统的三层模型（Category/Model/Entity）进行分类管理：
 * <ul>
 *   <li>Category（分类）：告警大类，如环境告警、设备告警、火灾告警、安防告警</li>
 *   <li>Model（模型）：告警子类，如水位告警、气体告警、温度告警</li>
 *   <li>Entity（实体）：具体告警项，如水位超标、水位过低、水位急升</li>
 * </ul>
 * </p>
 * 
 * <p>告警类型数据存储在 system_category 表中，通过 business_type_code = 'ALARM_TYPE' 区分，
 * 通过 level 字段区分层级：
 * <ul>
 *   <li>level = 1：根节点（ALARM_TYPE_ROOT）</li>
 *   <li>level = 2：Category 层（告警大类）</li>
 *   <li>level = 3：Model 层（告警子类）</li>
 *   <li>level = 4：Entity 层（具体告警项）</li>
 * </ul>
 * </p>
 *
 * @author 芋道源码
 */
public interface AlarmTypeService {

    /**
     * 告警类型业务类型编码
     */
    String BUSINESS_TYPE_CODE = "ALARM_TYPE";

    /**
     * 获取告警类型树（三层结构）
     * 
     * <p>返回完整的告警类型树，包含 Category -> Model -> Entity 三层结构</p>
     *
     * @return 告警类型分类列表（包含子节点）
     */
    List<AlarmTypeCategoryVO> getAlarmTypeTree();

    /**
     * 获取告警类型树（三层结构），支持状态过滤
     *
     * @param status 状态（1启用，0禁用），为 null 时查询全部
     * @return 告警类型分类列表（包含子节点）
     */
    List<AlarmTypeCategoryVO> getAlarmTypeTree(Integer status);

    /**
     * 根据分类ID获取告警模型列表
     *
     * @param categoryId 分类ID
     * @return 告警模型列表
     */
    List<AlarmTypeModelVO> getModelsByCategory(Long categoryId);

    /**
     * 根据模型ID获取告警实体列表
     *
     * @param modelId 模型ID
     * @return 告警实体列表
     */
    List<AlarmTypeEntityVO> getEntitiesByModel(Long modelId);

    /**
     * 获取告警类型完整路径
     * 
     * <p>根据实体ID获取完整路径，格式如：环境告警 > 水位告警 > 水位超标</p>
     *
     * @param entityId 实体ID（告警类型ID）
     * @return 完整路径字符串，如果不存在返回 null
     */
    String getAlarmTypePath(Long entityId);

    /**
     * 根据ID获取告警类型实体
     *
     * @param entityId 实体ID
     * @return 告警类型实体 VO，如果不存在返回 null
     */
    AlarmTypeEntityVO getAlarmTypeEntity(Long entityId);

    /**
     * 根据编码获取告警类型实体
     *
     * @param code 实体编码
     * @return 告警类型实体 VO，如果不存在返回 null
     */
    AlarmTypeEntityVO getAlarmTypeEntityByCode(String code);

    /**
     * 验证告警类型ID是否存在
     *
     * @param entityId 实体ID
     * @return 是否存在
     */
    boolean existsAlarmType(Long entityId);

    /**
     * 获取所有告警分类（Category层）
     *
     * @return 告警分类列表（不包含子节点）
     */
    List<AlarmTypeCategoryVO> getAllCategories();

    /**
     * 获取所有告警分类（Category层），支持状态过滤
     *
     * @param status 状态（1启用，0禁用），为 null 时查询全部
     * @return 告警分类列表（不包含子节点）
     */
    List<AlarmTypeCategoryVO> getAllCategories(Integer status);

    /**
     * 根据分类ID获取分类信息
     *
     * @param categoryId 分类ID
     * @return 分类 VO，如果不存在返回 null
     */
    AlarmTypeCategoryVO getCategory(Long categoryId);

    /**
     * 根据模型ID获取模型信息
     *
     * @param modelId 模型ID
     * @return 模型 VO，如果不存在返回 null
     */
    AlarmTypeModelVO getModel(Long modelId);

}
