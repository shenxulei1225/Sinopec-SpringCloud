package cn.iocoder.yudao.module.emergency.framework.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Long列表反序列化器
 * 支持从字符串数组或数字数组反序列化为List<Long>
 * 解决JavaScript大整数精度丢失问题
 *
 * @author 芋道源码
 */
public class LongListDeserializer extends JsonDeserializer<List<Long>> {

    @Override
    public List<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Long> result = new ArrayList<>();
        
        if (p.getCurrentToken() == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                    // 从字符串解析
                    String value = p.getText();
                    try {
                        result.add(Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        throw new IOException("无法将字符串 '" + value + "' 解析为Long类型", e);
                    }
                } else if (p.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                    // 从数字解析
                    result.add(p.getLongValue());
                } else if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
                    // 忽略null值
                    continue;
                } else {
                    throw new IOException("无法反序列化，期望字符串或数字，但得到: " + p.getCurrentToken());
                }
            }
        } else if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            // 返回null而不是空列表
            return null;
        } else {
            throw new IOException("无法反序列化List<Long>，期望数组，但得到: " + p.getCurrentToken());
        }
        
        return result.isEmpty() ? null : result;
    }
}

