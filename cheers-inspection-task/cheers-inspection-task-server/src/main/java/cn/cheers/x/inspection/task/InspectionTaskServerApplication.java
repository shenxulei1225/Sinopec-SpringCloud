package cn.cheers.x.inspection.task;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 巡检任务模块启动类
 *
 * @author yudao
 */
@SpringBootApplication(scanBasePackages = "cn.cheers.x.inspection")
@MapperScan(value = {
        "cn.cheers.x.inspection.task.dal.mysql",
        "cn.cheers.x.inspection.inspection_content.dal.mysql"
}, annotationClass = Mapper.class)
public class InspectionTaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InspectionTaskServerApplication.class, args);
    }

}
