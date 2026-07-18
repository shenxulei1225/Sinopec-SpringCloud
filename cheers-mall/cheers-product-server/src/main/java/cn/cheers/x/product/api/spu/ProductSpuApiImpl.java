package cn.cheers.x.product.api.spu;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.product.api.spu.dto.ProductSpuRespDTO;
import cn.cheers.x.product.convert.spu.ProductSpuConvert;
import cn.cheers.x.product.dal.dataobject.spu.ProductSpuDO;
import cn.cheers.x.product.service.spu.ProductSpuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 商品 SPU API 接口实现类
 *
 * @author LeeYan9
 * @since 2022-09-06
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ProductSpuApiImpl implements ProductSpuApi {

    @Resource
    private ProductSpuService spuService;

    @Override
    public CommonResult<List<ProductSpuRespDTO>> getSpuList(Collection<Long> ids) {
        List<ProductSpuDO> spus = spuService.getSpuList(ids);
        return success(BeanUtils.toBean(spus, ProductSpuRespDTO.class));
    }

    @Override
    public CommonResult<List<ProductSpuRespDTO>> validateSpuList(Collection<Long> ids) {
        List<ProductSpuDO> spus = spuService.validateSpuList(ids);
        return success(BeanUtils.toBean(spus, ProductSpuRespDTO.class));
    }

    @Override
    public CommonResult<ProductSpuRespDTO> getSpu(Long id) {
        ProductSpuDO spu = spuService.getSpu(id);
        return success(BeanUtils.toBean(spu, ProductSpuRespDTO.class));
    }

}
