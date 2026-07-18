package cn.cheers.x.inspection.inspection_content.service.source;

import lombok.Data;

import java.util.List;

/**
 * 对象来源适配器接口。
 *
 * <p>各业务系统实现此接口，提供对象查询能力。</p>
 *
 * <p>使用策略模式：</p>
 * <ul>
 *     <li>InspectionObjectSourceDO.adapterClass 存储实现类名</li>
 *     <li>通过 Spring 容器获取对应实现 Bean</li>
 *     <li>调用适配器方法查询具体对象</li>
 * </ul>
 */
public interface ObjectSourceAdapter {

    /**
     * 获取来源编码。
     *
     * @return 来源编码
     */
    String getSourceCode();

    /**
     * 查询对象列表。
     *
     * @param categoryId 分类ID（可选）
     * @param objectModel 对象模型（可选）
     * @return 对象列表
     */
    List<InspectionObject> listObjects(Long categoryId, String objectModel);

    /**
     * 根据编码查询对象。
     *
     * @param objectCode 对象编码
     * @return 对象信息
     */
    InspectionObject getObjectByCode(String objectCode);

    /**
     * 查询对象模型列表。
     *
     * @return 对象模型列表
     */
    List<String> listObjectModels();

    /**
     * 根据编码查询对象详情。
     *
     * <p>用于在任务详情中展示对象的扩展信息（位置/负责人/状态等）。</p>
     *
     * @param objectCode 对象编码
     * @return 对象详情，包含基本信息 + 扩展属性
     */
    ObjectDetail getObjectDetail(String objectCode);

    /**
     * 批量查询对象详情。
     *
     * <p>优化接口，减少查询次数。</p>
     *
     * @param objectCodes 对象编码列表
     * @return 对象详情映射，key 为 objectCode
     */
    default java.util.Map<String, ObjectDetail> listObjectDetails(List<String> objectCodes) {
        if (objectCodes == null || objectCodes.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return objectCodes.stream()
                .collect(java.util.stream.Collectors.toMap(
                        code -> code,
                        this::getObjectDetail,
                        (v1, v2) -> v1
                ));
    }

    /**
     * 巡检对象。
     */
    @Data
    class InspectionObject {

        /**
         * 对象编码。
         */
        private String objectCode;

        /**
         * 对象名称。
         */
        private String objectName;

        /**
         * 分类ID。
         */
        private Long categoryId;

        /**
         * 对象模型。
         */
        private String objectModel;

        /**
         * 扩展信息（JSON格式）。
         */
        private String extra;
    }

    /**
     * 对象详情。
     *
     * <p>包含基本信息和扩展属性，从原始业务系统查询。</p>
     */
    @Data
    class ObjectDetail {

        /**
         * 对象编码。
         */
        private String objectCode;

        /**
         * 对象名称。
         */
        private String objectName;

        /**
         * 对象位置。
         */
        private String location;

        /**
         * 负责人。
         */
        private String manager;

        /**
         * 负责人联系方式。
         */
        private String managerPhone;

        /**
         * 当前状态。
         */
        private String status;

        /**
         * 扩展属性（JSON 格式，存储异构数据）。
         */
        private String extra;
    }
}
