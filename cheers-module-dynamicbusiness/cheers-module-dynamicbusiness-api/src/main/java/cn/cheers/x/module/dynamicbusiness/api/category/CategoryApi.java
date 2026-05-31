package cn.cheers.x.module.dynamicbusiness.api.category;

import cn.iocoder.yudao.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 分类服务 RPC 接口
 * 
 * 继承 CategoryCommonApi，可以添加 System 模块特定的扩展方法
 * 
 * @author 系统生成
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 分类管理")
public interface CategoryApi extends CategoryCommonApi {
    
    // 如果需要，可以添加 System 模块特定的扩展方法
    // 例如：System模块特有的分类统计方法等
}

