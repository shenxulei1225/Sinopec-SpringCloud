package cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link ComponentTree} 的 JSONB 处理器。
 */
public class ComponentTreeTypeHandler extends BaseTypeHandler<ComponentTree> {

    public ComponentTreeTypeHandler() {
    }

    public ComponentTreeTypeHandler(Class<?> type) {
    }

    public ComponentTreeTypeHandler(Class<?> type, Field field) {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ComponentTree parameter, JdbcType jdbcType)
            throws SQLException {
        PGobject json = new PGobject();
        json.setType("jsonb");
        json.setValue(JsonUtils.toJsonString(parameter));
        ps.setObject(i, json);
    }

    @Override
    public ComponentTree getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public ComponentTree getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public ComponentTree getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private static ComponentTree parse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return JsonUtils.parseObject(json, ComponentTree.class);
    }
}
