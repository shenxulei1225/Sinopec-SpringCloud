package cn.cheers.x.module.dynamicbusiness.convert.page;

import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Named;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PageConvert {

    PageConvert INSTANCE = Mappers.getMapper(PageConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uiSchema", qualifiedByName = "mapToSerializable")
    @Mapping(target = "dataSource", qualifiedByName = "mapToSerializable")
    PageDO convert(PageSaveReqVO bean);

    @Mapping(target = "uiSchema", qualifiedByName = "mapToSerializable")
    @Mapping(target = "dataSource", qualifiedByName = "mapToSerializable")
    PageDO convertUpdate(PageSaveReqVO bean);

    PageRespVO convert(PageDO bean);

    List<PageRespVO> convertList(List<PageDO> list);

    @Named("mapToSerializable")
    default Map<String, Serializable> mapToSerializable(Map<String, Object> value) {
        if (value == null) {
            return new HashMap<>();
        }
        Map<String, Serializable> result = new HashMap<>(value.size());
        value.forEach((key, val) -> result.put(key, (Serializable) val));
        return result;
    }
}
