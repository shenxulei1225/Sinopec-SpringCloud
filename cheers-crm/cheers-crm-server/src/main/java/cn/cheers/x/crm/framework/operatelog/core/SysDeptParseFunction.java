package cn.cheers.x.crm.framework.operatelog.core;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import com.mzt.logapi.service.IParseFunction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 管理员名字的 {@link IParseFunction} 实现类
 *
 * @author HUIHUI
 */
@Slf4j
@Component
public class SysDeptParseFunction implements IParseFunction {

    public static final String NAME = "getDeptById";

    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // 获取部门信息
        DepartmentDTO dept = departmentDirectoryApi.getDepartment(Long.parseLong(value.toString())).getCheckedData();
        if (dept == null) {
            log.warn("[apply][获取部门{{}}为空", value);
            return "";
        }
        return dept.getName();
    }

}
