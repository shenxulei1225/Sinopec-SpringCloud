package cn.iocoder.yudao.module.emergency.framework.mybatis;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Long列表 JSONB 类型处理器
 * 专门处理 List<Long> 类型的 JSONB 字段
 * 确保反序列化时将所有元素正确转换为 Long 类型（即使JSON中存储的是字符串）
 *
 * @author 芋道源码
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({List.class})
public class LongListJsonbTypeHandler extends AbstractJsonTypeHandler<List<Long>> {

    public LongListJsonbTypeHandler(Class<?> type) {
        super(type);
    }

    public LongListJsonbTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        // 使用 PostgreSQL 的 PGobject 来正确处理 JSONB
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        
        // 将 List<Long> 序列化为 JSON 字符串
        String jsonString = toJson(parameter);
        pgObject.setValue(jsonString);
        ps.setObject(i, pgObject);
    }

    @Override
    public List<Long> parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        // 解析 JSON 数组
        Object parsed = JsonUtils.parseObject(json, Object.class);
        if (parsed == null) {
            return null;
        }
        
        // 转换为 List<Long>
        List<Long> result = new ArrayList<>();
        if (parsed instanceof List) {
            List<?> list = (List<?>) parsed;
            for (Object item : list) {
                if (item == null) {
                    continue;
                }
                // 处理字符串或数字类型
                if (item instanceof String) {
                    try {
                        result.add(Long.parseLong((String) item));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无法将字符串 '" + item + "' 解析为Long类型", e);
                    }
                } else if (item instanceof Number) {
                    result.add(((Number) item).longValue());
                } else {
                    throw new IllegalArgumentException("无法将类型 " + item.getClass().getName() + " 转换为Long类型");
                }
            }
        } else {
            throw new IllegalArgumentException("JSON解析结果不是数组类型: " + parsed.getClass().getName());
        }
        
        return result.isEmpty() ? null : result;
    }

    @Override
    public String toJson(List<Long> obj) {
        if (obj == null) {
            return null;
        }
        return JsonUtils.toJsonString(obj);
    }
}












































