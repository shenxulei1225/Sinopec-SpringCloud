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

/**
 * PostgreSQL JSONB type handler for Map/List/Object JSON fields.
 * Local to emergency module — framework no longer ships PostgreSQLJsonbTypeHandler.
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({Object.class})
public class PostgreSQLJsonbTypeHandler extends AbstractJsonTypeHandler<Object> {

    public PostgreSQLJsonbTypeHandler(Class<?> type) {
        super(type);
    }

    public PostgreSQLJsonbTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(toJson(parameter));
        ps.setObject(i, pgObject);
    }

    @Override
    public Object parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return JsonUtils.parseObject(json, Object.class);
    }

    @Override
    public String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return JsonUtils.toJsonString(obj);
    }
}
