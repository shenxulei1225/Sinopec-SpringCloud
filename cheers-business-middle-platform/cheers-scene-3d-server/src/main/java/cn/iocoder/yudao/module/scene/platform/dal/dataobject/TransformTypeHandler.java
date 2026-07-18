package cn.iocoder.yudao.module.scene.platform.dal.dataobject;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link Transform} 的 JSONB 处理器。
 */
public class TransformTypeHandler extends BaseTypeHandler<Transform> {

    public TransformTypeHandler() {
    }

    public TransformTypeHandler(Class<?> type) {
    }

    public TransformTypeHandler(Class<?> type, Field field) {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Transform parameter, JdbcType jdbcType)
            throws SQLException {
        PGobject json = new PGobject();
        json.setType("jsonb");
        json.setValue(JsonUtils.toJsonString(parameter));
        ps.setObject(i, json);
    }

    @Override
    public Transform getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Transform getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Transform getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static Transform parse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return JsonUtils.parseObject(json, Transform.class);
    }
}
