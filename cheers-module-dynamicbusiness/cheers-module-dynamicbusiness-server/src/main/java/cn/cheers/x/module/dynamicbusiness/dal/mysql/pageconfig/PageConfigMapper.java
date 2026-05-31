package cn.cheers.x.module.dynamicbusiness.dal.mysql.pageconfig;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPageReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pageconfig.PageConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 页面配置 Mapper
 *
 * @author yudao
 */
@Mapper
public interface PageConfigMapper extends BaseMapperX<PageConfigDO> {

    /**
     * 根据页面类型查询页面配置列表
     *
     * @param pageType 页面类型
     * @return 页面配置列表
     */
    default List<PageConfigDO> selectListByPageType(String pageType) {
        return selectList(new LambdaQueryWrapperX<PageConfigDO>()
                .eqIfPresent(PageConfigDO::getPageType, pageType)
                .orderByDesc(PageConfigDO::getId));
    }

    /**
     * 根据业务类型查询页面配置列表
     *
     * 通过 config_code 字段匹配（格式：{businessType}-{pageName}）
     *
     * @param businessType 业务类型代码
     * @return 页面配置列表
     */
    default List<PageConfigDO> selectListByBusinessType(String businessType) {
        return selectList(new LambdaQueryWrapperX<PageConfigDO>()
                .likeLeft(PageConfigDO::getConfigCode, businessType + "-")
                .orderByDesc(PageConfigDO::getCreateTime));
    }

    /**
     * 分页查询页面配置
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    default PageResult<PageConfigDO> selectPage(PageConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PageConfigDO>()
                .eqIfPresent(PageConfigDO::getPageType, reqVO.getPageType())
                .orderByDesc(PageConfigDO::getId));
    }

    /**
     * 根据配置代码查询页面配置
     *
     * @param configCode 配置代码
     * @return 页面配置
     */
    default PageConfigDO selectByConfigCode(String configCode) {
        return selectOne(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getConfigCode, configCode));
    }

    /**
     * 根据页面代码查询页面配置
     *
     * @param pageCode 页面代码
     * @return 页面配置
     */
    default PageConfigDO selectByPageCode(String pageCode) {
        return selectOne(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getPageCode, pageCode));
    }

    /**
     * 检查配置代码是否已存在
     *
     * @param configCode 配置代码
     * @param excludeId 排除的ID（用于更新时排除自身）
     * @return 是否存在
     */
    default boolean existsByConfigCode(String configCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getConfigCode, configCode)
                .neIfPresent(PageConfigDO::getId, excludeId)) > 0;
    }

    /**
     * 检查页面代码是否已存在
     *
     * @param pageCode 页面代码
     * @param excludeId 排除的ID（用于更新时排除自身）
     * @return 是否存在
     */
    default boolean existsByPageCode(String pageCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<PageConfigDO>()
                .eq(PageConfigDO::getPageCode, pageCode)
                .neIfPresent(PageConfigDO::getId, excludeId)) > 0;
    }
}
