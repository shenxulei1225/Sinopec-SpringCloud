package cn.iocoder.yudao.module.emergency.framework.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Long 反序列化器
 * 支持从字符串或数字反序列化为 Long
 * 解决 JavaScript 大整数精度丢失问题，允许前端传递字符串形式的 Long ID
 *
 * @author 芋道源码
 */
public class LongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        
        if (token == JsonToken.VALUE_STRING) {
            // 从字符串解析
            String value = p.getText();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw ctxt.weirdStringException(value, Long.class, 
                    "无法将字符串 '" + value + "' 解析为 Long 类型: " + e.getMessage());
            }
        }
        
        if (token == JsonToken.VALUE_NUMBER_INT) {
            // 从数字解析
            return p.getLongValue();
        }
        
        // 其他类型尝试转换
        if (token.isNumeric()) {
            return p.getLongValue();
        }
        
        throw new IOException("无法反序列化 Long，期望字符串或数字，但得到: " + token);
    }
}

