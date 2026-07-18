package cn.iocoder.yudao.module.inspection.task;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 巡检任务模块启动类
 *
 * @author yudao
 */
@SpringBootApplication(scanBasePackages = "cn.iocoder.yudao.module.inspection")
@MapperScan(value = {
        "cn.iocoder.yudao.module.inspection.task.dal.mysql",
        "cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql"
}, annotationClass = Mapper.class)
public class InspectionTaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InspectionTaskServerApplication.class, args);
    }

}
