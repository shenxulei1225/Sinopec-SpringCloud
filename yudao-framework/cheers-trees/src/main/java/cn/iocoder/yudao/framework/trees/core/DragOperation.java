package cn.iocoder.yudao.framework.trees.core;

import lombok.Data;

/**
 * 拖拽操作相关类
 */
public class DragOperation {

    /**
     * 拖拽位置枚举
     */
    public enum Position {
        /** 在目标节点前面 */
        BEFORE,
        /** 在目标节点后面 */
        AFTER,
        /** 在目标节点内部 */
        INNER
    }

    /**
     * 拖拽请求
     *
     * @param <ID> ID类型
     */
    @Data
    public static class DragRequest<ID> {
        /** 拖拽的节点ID */
        private ID dragId;
        
        /** 目标父节点ID */
        private ID targetParentId;
        
        /** 拖拽位置 */
        private Position position;
        
        /** 目标节点ID（用于before/after位置） */
        private ID targetId;
        
        public DragRequest() {}
        
        public DragRequest(ID dragId, ID targetParentId, Position position, ID targetId) {
            this.dragId = dragId;
            this.targetParentId = targetParentId;
            this.position = position;
            this.targetId = targetId;
        }
    }
}



