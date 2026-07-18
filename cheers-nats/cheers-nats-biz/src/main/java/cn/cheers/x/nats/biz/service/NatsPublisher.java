package cn.cheers.x.nats.biz.service;



import cn.cheers.x.nats.api.packet.RuntimePacket;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;



import java.nio.charset.StandardCharsets;



/**

 * NATS 发布器

 */

@Slf4j

@Component

@RequiredArgsConstructor

public class NatsPublisher {



    private final Connection connection;

    private final ObjectMapper objectMapper;



    /**

     * 发布消息

     */

    public void publish(String subject, Object data) {

        try {

            RuntimePacket<Object> packet = RuntimePacket.<Object>builder()

                    .subject(subject)

                    .timestamp(System.currentTimeMillis())

                    .payload(data)

                    .build();



            String json = objectMapper.writeValueAsString(packet);

            connection.publish(subject, json.getBytes(StandardCharsets.UTF_8));

            log.debug("[NATS] 发布消息: subject={}, data={}", subject, json);

        } catch (JsonProcessingException e) {

            log.error("[NATS] JSON 序列化失败: {}", e.getMessage());

        } catch (Exception e) {

            log.error("[NATS] 发布失败: {}", e.getMessage());

        }

    }



    /**

     * 发布事件

     */

    public void publishEvent(String subject, String type, Object data) {

        try {

            RuntimePacket<Object> packet = RuntimePacket.<Object>builder()

                    .subject(subject)

                    .timestamp(System.currentTimeMillis())

                    .type(type)

                    .payload(data)

                    .build();



            String json = objectMapper.writeValueAsString(packet);

            connection.publish(subject, json.getBytes(StandardCharsets.UTF_8));

            log.debug("[NATS] 发布事件: subject={}, type={}, data={}", subject, type, json);

        } catch (Exception e) {

            log.error("[NATS] 发布事件失败: {}", e.getMessage());

        }

    }

}

