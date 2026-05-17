# 方案A：NATS + WebSocket 按需推送架构

> 方案日期：2026-05-10
> 架构选型：NATS（后端内部总线）+ WebSocket（前端传输协议）
> 选型依据：按需推送、节省带宽、按Actor订阅

---

## 一、整体架构

```
┌──────────────────────────────────────────────────────────────────┐
│                        后端 Spring Cloud                         │
│                                                                  │
│  ┌──────────────┐     NATS      ┌──────────────────┐            │
│  │ 数据采集服务   │─────────────▶│  变化检测服务      │            │
│  │ (Agent/SCADA)│               │  (Diff Engine)    │            │
│  └──────────────┘               └────────┬─────────┘            │
│                                          │ NATS publish         │
│                                          ▼                      │
│  ┌──────────────────┐     ┌──────────────────────┐             │
│  │  Redis 缓存       │◀─── │  WebSocket Gateway    │             │
│  │  (持久化/查询)    │     │  (NATS Subscriber)    │             │
│  └──────────────────┘     └──────────┬───────────┘             │
│                                      │ WebSocket              │
└──────────────────────────────────────┼─────────────────────────┘
                                       │ ws://domain/api/ws
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│                      Three.js 前端 (浏览器)                       │
│                                                                  │
│  ┌──────────────┐     ┌──────────────────┐     ┌─────────────┐ │
│  │ WebSocket    │────▶│  ActorManager     │────▶│  Three.js   │ │
│  │ 客户端       │     │  (按Actor订阅)     │     │  渲染引擎    │ │
│  └──────────────┘     └──────────────────┘     └─────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

---

## 二、NATS 与 WebSocket 职责分工

| 组件 | 职责 | 位置 |
|------|------|------|
| **NATS** | 后端服务间事件总线，解耦数据采集、变化检测、推送桥接 | 后端内部 |
| **WebSocket Gateway** | NATS 消息桥接到前端 WebSocket，做协议转换和权限校验 | 后端→前端 |
| **Redis** | Actor 运行时数据缓存（24h TTL），支持历史查询 | 后端存储 |

### 为什么不直连 NATS？

- ❌ 前端（浏览器）直连 NATS 不安全，暴露内网地址
- ❌ NATS JS 客户端浏览器兼容性有限
- ❌ CORS 问题处理复杂
- ✅ WebSocket 是浏览器标准协议，前端无需额外依赖

---

## 三、Subject 命名规范

### 3.1 Subject 结构

```
{scope}.{category}.{instance_id}.{metric}
```

### 3.2 Subject 定义表

| 场景 | Subject 模式 | 示例 | Actor 类型 | 推送内容 |
|------|-------------|------|-----------|---------|
| **仪表** | `factory.{factory_id}.device.{device_id}.metric` | `factory.PL01.device.Meter_001.metric` | 仪表/仪表板 | 实时数值+单位+状态 |
| **巡检设备** | `factory.{factory_id}.inspect.{device_id}.location` | `factory.PL01.inspect.Rover_002.location` | 巡检机器人/无人机 | 位置坐标 |
| **阀门** | `factory.{factory_id}.valve.{valve_id}.metric` | `factory.PL01.valve.V_001.metric` | 阀门 | 开度/状态/温度 |
| **管道** | `factory.{factory_id}.pipe.{pipe_id}.metric` | `factory.PL01.pipe.P_001.metric` | 管道 | 压力/流量/温度 |

### 3.3 WebSocket Topic 映射

NATS Subject → WebSocket STOMP Topic 映射规则：

```
NATS: factory.PL01.device.Meter_001.metric
        ↓ 转换
WebSocket: /topic/factory/PL01/device/Meter_001/metric
```

前端订阅：
```javascript
client.subscribe('/topic/factory/PL01/device/Meter_001/metric', handler);
```

---

## 四、核心服务实现

### 4.1 RuntimePacket 消息结构（NATS 载体）

```java
@Data
@Builder
public class RuntimePacket {
    /** 场景编码 */
    private String sceneCode;
    
    /** Actor 实例编码 */
    private String instanceCode;
    
    /** Actor 类型 */
    private String actorCategory;
    
    /** 业务数据（因 Actor 类型而异） */
    private Map<String, Object> payload;
    
    /** 序列号，用于前端帧版本过滤 */
    private Long sequence;
    
    /** 时间戳 */
    private Long timestamp;
}
```

### 4.2 按 Actor 类型分类推送

```java
@Service
public class DataDispatcherService {
    
    /**
     * 仪表数据推送：推仪表读数
     */
    @NatsListener(subject = "factory.>.device.>.metric")
    public void dispatchDeviceMetric(RuntimePacket packet) {
        pushToWebSocket(packet);
    }
    
    /**
     * 巡检设备位置推送：推位置坐标
     */
    @NatsListener(subject = "factory.>.inspect.>.location")
    public void dispatchInspectLocation(RuntimePacket packet) {
        pushToWebSocket(packet);
    }
    
    /**
     * 阀门数据推送：推开度/温度
     */
    @NatsListener(subject = "factory.>.valve.>.metric")
    public void dispatchValveMetric(RuntimePacket packet) {
        pushToWebSocket(packet);
    }
    
    /**
     * 管道数据推送：推压力/流量/温度
     */
    @NatsListener(subject = "factory.>.pipe.>.metric")
    public void dispatchPipeMetric(RuntimePacket packet) {
        pushToWebSocket(packet);
    }
    
    private void pushToWebSocket(RuntimePacket packet) {
        String wsTopic = buildWSTopic(packet);
        messagingTemplate.convertAndSend(wsTopic, packet);
    }
    
    private String buildWSTopic(RuntimePacket packet) {
        return "/topic/" + packet.getSceneCode() + "/" +
               packet.getActorCategory() + "/" +
               packet.getInstanceCode();
    }
}
```

### 4.3 WebSocket 配置

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 前缀：订阅路径
        registry.enableSimpleBroker("/topic");
        // 应用消息路径前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 用户级点对点消息
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 端点 + CORS
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 兼容不支持 WebSocket 的浏览器
    }
}
```

### 4.4 前端订阅管理

```javascript
class SceneWebSocketClient {
    constructor() {
        this.client = null;
        this.subscriptions = new Map();
        this.seqCache = new Map(); // 序列号缓存，防重复
    }
    
    /**
     * 连接 WebSocket
     */
    connect() {
        this.client = new SockJS('/api/ws');
        this.stompClient = Stomp.over(this.client);
        
        this.stompClient.connect({}, () => {
            console.log('WebSocket connected');
        });
    }
    
    /**
     * 订阅特定 Actor 的数据
     */
    subscribeActor(sceneCode, actorCategory, instanceCode) {
        const topic = `/topic/${sceneCode}/${actorCategory}/${instanceCode}`;
        
        if (this.subscriptions.has(topic)) {
            console.warn(`Already subscribed: ${topic}`);
            return;
        }
        
        this.stompClient.subscribe(topic, (message) => {
            const packet = JSON.parse(message.body);
            
            // 帧版本过滤：只处理比缓存更新的序列号
            if (this.seqCache.get(instanceCode) >= packet.sequence) {
                console.log(`Duplicated frame, seq=${packet.sequence}, skip`);
                return;
            }
            
            // 更新序列号缓存
            this.seqCache.set(instanceCode, packet.sequence);
            
            // 交给业务处理
            this.onDataReceived(packet);
        });
        
        this.subscriptions.set(topic, true);
        console.log(`Subscribed: ${topic}`);
    }
    
    /**
     * 收到数据，更新 Three.js 场景
     */
    onDataReceived(packet) {
        const actor = this.scene.actors.get(packet.instanceCode);
        if (!actor) return;
        
        switch (packet.actorCategory) {
            case 'device':
                this.updateMeterActor(actor, packet.payload);
                break;
            case 'inspect':
                this.updateInspectActor(actor, packet.payload);
                break;
            case 'valve':
                this.updateValveActor(actor, packet.payload);
                break;
            case 'pipe':
                this.updatePipeActor(actor, packet.payload);
                break;
        }
    }
    
    /**
     * 更新仪表 Actor
     */
    updateMeterActor(actor, payload) {
        actor.meterValue = payload.value;
        actor.status = payload.status;
        // 触发 UI 更新
        this.updateMeterLabel(actor);
    }
    
    /**
     * 更新巡检设备 Actor
     */
    updateInspectActor(actor, payload) {
        const pos = new THREE.Vector3(
            payload.location.x,
            payload.location.y,
            payload.location.z
        );
        // 平滑插值移动
        this.lerpPosition(actor, pos);
    }
}
```

---

## 五、按需推送机制

### 5.1 前端按需订阅

```javascript
// 场景初始化时，根据当前视角只订阅可视范围内的 Actor
function subscribeVisibleActors() {
    const visibleActors = findVisibleActors(); // 基于摄像机视锥检测
    
    // 取消不可见的订阅
    unsubscribeInvisibleActors(visibleActors);
    
    // 订阅可见 Actor
    for (const actor of visibleActors) {
        webSocketClient.subscribeActor(
            actor.sceneCode,
            actor.actorCategory,
            actor.instanceCode
        );
    }
}

// 相机移动/缩放后重新计算
camera.addEventListener('change', subscribeVisibleActors);
```

### 5.2 后端推送触发机制

```java
@Service
public class ChangeDetectionEngine {
    
    private final Map<String, Double> prevValues = new ConcurrentHashMap<>();
    
    /**
     * 变化检测阈值配置
     */
    @Data
    public static class Threshold {
        private double meterValueThreshold = 0.01;      // 仪表值变化阈值
        private double positionThreshold = 0.5;          // 位置变化阈值
        private double temperatureThreshold = 0.5;        // 温度变化阈值
        private double pressureThreshold = 0.01;          // 压力变化阈值
    }
    
    /**
     * 仪表值变化检测
     */
    public boolean shouldPush(String instanceCode, double newValue, double prevValue) {
        if (prevValue == 0) return true; // 首次推送
        double diff = Math.abs(newValue - prevValue);
        return diff > 0.01; // 变化超过阈值才推送
    }
    
    /**
     * 巡检位置变化检测
     */
    public boolean shouldPushLocation(Vector3 newPos, Vector3 prevPos) {
        double dist = newPos.distanceTo(prevPos);
        return dist > 0.5; // 位移超过阈值才推送
    }
}
```

---

## 六、数据流转时序

```
数据采集(Agent/SCADA)
       │ 1. 采集到数据
       ▼
后端服务
       │ 2. 判断是否变化（ChangeDetectionEngine）
       ├── 未变化 ──→ 丢弃，不推送
       ▼
       │ 3. 变化超过阈值 → NATS publish
       ▼
NATS Server
       │ 4. NATS 广播
       ▼
WebSocket Gateway
       │ 5. NATS Subscriber 接收 → convertAndSend
       ▼
WebSocket 服务端
       │ 6. 查找订阅了该 Actor 的前端客户端
       ▼
前端 Three.js
       │ 7. SockJS/Stomp 接收
       │ 8. 帧版本过滤（seq 检查）
       ▼
Three.js 渲染引擎
       │ 9. 插值动画更新 Actor
```

---

## 七、NATS 集成配置

### 7.1 Maven 依赖

```xml
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats</artifactId>
    <version>2.16.8</version>
</dependency>
```

### 7.2 application.yml

```yaml
nats:
  server:
    - nats://192.168.1.100:4222
  max-connections: 10
  max-reconnects: 5
  reconnect-wait: 2000
  ping-interval: 30000
  max-ping-out: 3
  connection-name: scene-platform
```

### 7.3 NATS Starter 配置

```java
@Data
@ConfigurationProperties(prefix = "nats")
public class NatsProperties {
    private List<String> server;
    private int maxConnections = 10;
    private int maxReconnects = 5;
    private long reconnectWait = 2000;
}
```

---

## 八、Redis 缓存策略

### 8.1 缓存目的

| 场景 | 说明 |
|------|------|
| 实时监控 | 前端页面初始化时拉取最新状态（REST API） |
| 历史查询 | 查询最近 N 分钟的 Actor 状态 |
| 离线恢复 | 前端断线重连后获取最后已知状态 |

### 8.2 Redis Key 设计

```
scene:runtime:{sceneCode}:actor:{instanceCode}
    TTL: 24小时

scene:runtime:{sceneCode}:online
    Value: 在线用户数
```

### 8.3 写入时机

```java
@Service
public class DataSyncService {
    
    @NatsListener(subject = "factory.>.device.>.metric")
    public void onDeviceMetric(RuntimePacket packet) {
        // 1. 写 Redis（持久化/查询）
        runtimeService.updateActorRuntime(
            packet.getSceneCode(),
            packet.getInstanceCode(),
            packet.getPayload(),
            packet.getActorCategory()
        );
        
        // 2. 推 NATS（实时推送）
        natsTemplate.publish(buildSubject(packet), packet);
    }
}
```

---

## 九、前端 Three.js 数据更新器

```javascript
class DataUpdateManager {
    constructor(scene) {
        this.scene = scene;
        this.updateQueue = new PriorityQueue();
        this.lastUpdate = {};
    }
    
    /**
     * 接收 WebSocket 数据，加入更新队列
     */
    addUpdate(packet) {
        this.updateQueue.enqueue(packet);
    }
    
    /**
     * 每帧处理更新队列
     */
    processUpdates() {
        while (!this.updateQueue.isEmpty()) {
            const packet = this.updateQueue.dequeue();
            const actor = this.getActor(packet.instanceCode);
            
            if (!actor) continue;
            
            const last = this.lastUpdate[packet.instanceCode];
            
            switch (packet.actorCategory) {
                case 'device':
                    this.updateMeter(actor, packet.payload);
                    break;
                case 'inspect':
                    this.updateInspectPosition(actor, packet.payload);
                    break;
                case 'valve':
                    this.updateValveState(actor, packet.payload);
                    break;
                case 'pipe':
                    this.updatePipeData(actor, packet.payload);
                    break;
            }
            
            this.lastUpdate[packet.instanceCode] = packet;
        }
    }
    
    /**
     * 仪表数值更新
     */
    updateMeter(actor, payload) {
        actor.meterValue = payload.value;
        // 更新数字标签
        this.updateLabel(actor, payload.value);
    }
    
    /**
     * 巡检设备位置更新（Lerp 平滑）
     */
    updateInspectPosition(actor, payload) {
        const target = new THREE.Vector3(
            payload.location.x,
            payload.location.y,
            payload.location.z
        );
        const speed = 0.1; // Lerp 速度
        actor.position.lerp(target, speed);
        actor.quaternion.slerp(targetRotation, speed);
    }
}
```

---

## 十、方案总结

### 10.1 架构优势

| 优势 | 说明 |
|------|------|
| **按需推送** | 前端只订阅可视范围内的 Actor，节省 90%+ 带宽 |
| **变化触发** | 后端变化检测，数据不变不推送，节省 70%+ 流量 |
| **按 Actor 粒度** | 前端可按需订阅/取消订阅，灵活控制推送范围 |
| **解耦** | NATS 解耦数据采集、变化检测、推送桥接 |
| **高可靠** | Redis 缓存兜底，前端断线可恢复 |

### 10.2 带宽优化效果

| 场景 | 传统全量轮询 | 方案A（按需+变化检测） | 优化幅度 |
|------|-------------|---------------------|---------|
| 仪表数据 | 3000仪表×1KB×2次/秒 = 6MB/s | 约 600仪表×1KB×0.1次/秒 = 60KB/s | **99%** |
| 巡检位置 | 30台×500B×10次/秒 = 15MB/s | 5台×500B×2次/秒 = 5KB/s | **99.97%** |

### 10.3 关键文件清单

| 文件 | 说明 |
|------|------|
| `WebSocketConfig.java` | WebSocket 配置（STOMP + SockJS） |
| `WebSocketNatsBridge.java` | NATS→WebSocket 桥接服务 |
| `RuntimePacket.java` | NATS 消息载体 |
| `DataDispatcherService.java` | 按 Actor 类型分发推送 |
| `DataUpdateManager.java` | 前端数据更新器 |
| `SceneWebSocketClient.js` | 前端 WebSocket 客户端 |

---

*文档生成日期：2026-05-10*
