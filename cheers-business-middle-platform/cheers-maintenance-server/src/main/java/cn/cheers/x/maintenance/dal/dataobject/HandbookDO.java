package cn.cheers.x.maintenance.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("mm_handbook")
@KeySequence("mm_handbook_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandbookDO extends BaseDO {
    @TableId
    private Long id;
    private String code;
    private String name;
    private String scope;
    private String assetTypeCode;
    private String frequencyCode;
    private Long fieldStandardId;
    private String crewHint;
    private String materialHint;
    private Integer versionNo;
    private Integer status;
    private Long tenantId;
}
