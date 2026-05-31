package cn.cheers.x.module.dynamicbusiness.service.model.core;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model Core Service 实现。
 *
 * <p>定位说明：</p>
 * <ul>
 *   <li>Core 层只提供“模型主表(dynamic_model)的基础能力”；</li>
 *   <li>不在 Core 层做跨业务编排、分类关系聚合、前端场景拼装；</li>
 *   <li>复杂查询（如多 businessTypeCode 汇总排序）应由上层 Service 编排后调用 Core/Mapper。</li>
 * </ul>
 */
@Service
@Slf4j
public class ModelCoreServiceImpl implements ModelCoreService {

    @Resource
    private ModelMapper modelMapper;

    /**
     * 判断模型是否存在（按“模型ID + 业务类型”双条件）。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>关系绑定前校验模型是否属于当前业务；</li>
     *   <li>批量操作前做轻量存在性校验；</li>
     *   <li>避免跨业务误操作同 ID 模型。</li>
     * </ul>
     *
     * <p>返回规则：入参为空或业务类型空白时直接返回 false，不抛异常。</p>
     */
    @Override
    public boolean existsById(Long modelId, String businessTypeCode) {
        if (modelId == null || businessTypeCode == null || businessTypeCode.isBlank()) {
            return false;
        }
        return modelMapper.selectByIdAndBusinessTypeCode(modelId, businessTypeCode) != null;
    }

    /**
     * 批量过滤“当前业务下真实存在”的模型ID集合。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>批量绑定/解绑前，快速筛出有效 modelId；</li>
     *   <li>批量请求容错：忽略不存在或跨业务的 ID；</li>
     *   <li>上层用于构建 notFound 列表与成功列表。</li>
     * </ul>
     *
     * <p>返回规则：参数不合法时返回空集合。</p>
     */
    @Override
    public Set<Long> filterExistingModelIds(List<Long> modelIds, String businessTypeCode) {
        if (modelIds == null || modelIds.isEmpty() || businessTypeCode == null || businessTypeCode.isBlank()) {
            return new HashSet<>();
        }
        List<ModelDO> existing = modelMapper.selectByIds(modelIds).stream()
                .filter(model -> businessTypeCode.equals(model.getBusinessTypeCode()))
                .toList();
        return existing.stream().map(ModelDO::getId).collect(Collectors.toSet());
    }

    /**
     * 创建模型主数据。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>上层 Service 完成业务校验与默认值计算后落库；</li>
     *   <li>仅负责主表写入，不处理分类关系、事件发布等副作用。</li>
     * </ul>
     */
    @Override
    public Long create(ModelDO model) {
        modelMapper.insert(model);
        return model.getId();
    }

    /**
     * 更新模型主数据（按主键 updateById）。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>模型名称/描述/状态/sort 等基础字段更新；</li>
     *   <li>上层已完成唯一性和业务约束校验后执行落库。</li>
     * </ul>
     */
    @Override
    public void update(ModelDO model) {
        modelMapper.updateById(model);
    }

    /**
     * 删除模型主数据（按主键）。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>上层已完成“是否允许删除”的业务校验后执行物理/逻辑删除；</li>
     *   <li>关联关系清理由上层服务负责编排。</li>
     * </ul>
     */
    @Override
    public void delete(Long id) {
        modelMapper.deleteById(id);
    }

    /**
     * 按主键获取模型。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>详情查询、更新前读取旧值、删除前存在性校验；</li>
     *   <li>仅查主表，不附带分类/字段等扩展信息。</li>
     * </ul>
     */
    @Override
    public ModelDO get(Long id) {
        return id == null ? null : modelMapper.selectById(id);
    }

    /**
     * 按业务类型查询有序模型列表（扁平列表）。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>业务树点击后加载该业务下模型；</li>
     *   <li>上层在此基础上继续做 VO 组装或关系补全。</li>
     * </ul>
     *
     * <p>排序语义由 Mapper 统一保证：sort ASC, createTime DESC。</p>
     */
    @Override
    public List<ModelDO> listByBusinessTypeCode(String businessTypeCode) {
        return modelMapper.selectByBusinessTypeCode(businessTypeCode);
    }

    /**
     * 查询“指定业务类型下启用状态的有序模型列表”。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>只展示可用模型的下拉/选择器；</li>
     *   <li>按业务类型隔离的启用模型清单。</li>
     * </ul>
     *
     * <p>说明：Core 层提供的是“单业务”能力；跨业务合并由上层 Service 编排。</p>
     */
    @Override
    public List<ModelDO> listEnabledModelsByBusinessTypeCode(String businessTypeCode) {
        return modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                .eq(ModelDO::getBusinessTypeCode, businessTypeCode)
                .eq(ModelDO::getStatus, 1)
                .orderByAsc(ModelDO::getSort)
                .orderByDesc(ModelDO::getCreateTime));
    }

    /**
     * 单业务类型关键字模糊搜索模型（名称/描述）。
     */
    @Override
    public List<ModelDO> searchLikeInBusinessType(String keyword, String businessTypeCode) {
        return modelMapper.searchLikeInBusinessType(keyword, businessTypeCode);
    }


    /**
     * 按“名称 + 业务类型”获取单条记录。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>创建/更新前执行“按业务唯一”校验；</li>
     *   <li>同租户内允许跨业务重名。</li>
     * </ul>
     */
    @Override
    public ModelDO getByNameInBusinessType(String name, String businessTypeCode) {
        return modelMapper.selectByNameAndBusinessTypeCode(name, businessTypeCode);
    }

    /**
     * 按 ID 列表批量查询模型。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>避免 N+1 的批量详情回查；</li>
     *   <li>上层按输入顺序自行重排结果。</li>
     * </ul>
     */
    @Override
    public List<ModelDO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return modelMapper.selectByIds(ids);
    }

    /**
     * 单业务类型分页查询模型（基础分页能力）。
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>模型管理页常规分页；</li>
     *   <li>按业务类型/状态/关键词组合过滤。</li>
     * </ul>
     *
     * <p>说明：该方法仅负责主表分页，不负责 includeChildren、多业务合并排序、分类上下文排序等编排。</p>
     */
    @Override
    public PageResult<ModelDO> pageModels(String businessTypeCode, String keyword, Integer status, Integer pageNo, Integer pageSize) {
        return modelMapper.selectPage(businessTypeCode, keyword, status, pageNo, pageSize);
    }
}
