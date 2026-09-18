package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCapabilityItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCapabilityRespVO;

import java.util.List;

/**
 * 数据目录能力开关服务。
 *
 * 负责：维护“某目录启用了哪些可选能力”，以及步骤树的挂载配置。
 * 不负责：具体能力本身的业务实现（如 SOP 发布逻辑）。
 */
public interface EntityTypeCapabilityService {

    /** 版本管理能力码（用于 SOP 等按版本发布的业务） */
    String CAPABILITY_VERSION_MANAGEMENT = "version-management";
    /** 步骤管理能力码（启用后按用途补列，并写下挂载配置；界面名「步骤管理」） */
    String CAPABILITY_STEP_STRUCTURE = "step-structure";
    /** 流程图能力码 */
    String CAPABILITY_FLOW_GRAPH = "flow-graph";
    /** 参数定义能力码 */
    String CAPABILITY_PARAM_SCHEMA = "param-schema";
    /** 协议解析能力码 */
    String CAPABILITY_PROTOCOL_PARSER = "protocol-parser";
    /**
     * 实参配置能力码。
     * 启用后只改这个目录的详情展示（给当前对象填已有步骤/动作的真实参数）。
     * 不给本目录补步骤树列，也不改编标准步骤树。
     */
    String CAPABILITY_ACTUAL_PARAM = "actual-param";

    /**
     * 查询目录当前能力开关与步骤树挂载。
     */
    EntityTypeCapabilityRespVO getCapabilities(String entityTypeCode);

    /**
     * 查询目录当前能力开关列表（包含所有支持能力及其启用状态）。
     */
    List<EntityTypeCapabilityItemRespVO> listCapabilityItems(String entityTypeCode);

    /**
     * 覆盖保存目录能力开关（统一入口维护，非创建流程）。
     */
    void saveEnabledCapabilities(
            String entityTypeCode,
            List<String> enabledCapabilityCodes,
            List<String> stepTreeHangableTypeCodes,
            Boolean stepTreeAllowMixed);

    /**
     * 判断目录是否启用某个能力。
     */
    boolean hasCapability(String entityTypeCode, String capabilityCode);

    /**
     * 当前租户里已打开的能力，走与保存开关相同的补列写入。
     * 给「只写了开关、还没走过保存入口」的存量目录用。
     * 不负责：改开关；读路径补列。
     */
    void provisionEnabledCapabilities();
}
