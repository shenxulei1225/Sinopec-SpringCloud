package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueRespVO;

/**
 * 读取型号上的字段值。
 *
 * 负责：定型号。点了名字就只取这些字段。没点名字时只给出型号名。值是型号上存的那一份，收成可显示的一句。
 * 不管什么：改这些值；把值抄到当前这台设备上。
 * 禁止：用挂着的那台设备自己记下的数充当型号字段的值。
 */
public interface RefModelFieldValueQueryService {

    RefModelFieldValueRespVO read(RefModelFieldValueReqVO req);
}
