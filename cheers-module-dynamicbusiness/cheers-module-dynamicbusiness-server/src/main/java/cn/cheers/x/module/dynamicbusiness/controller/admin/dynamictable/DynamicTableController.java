package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo.*;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableAuditLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableColumnDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableDO;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 动态表管理 Controller
 * 
 * 提供 DEDICATED_DYNAMIC 存储类型的动态表管理接口
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 动态表管理")
@RestController
@RequestMapping("/dynamicbusiness/dynamic-table")
@Validated
public class DynamicTableController {

    @Resource
    private DynamicTableService dynamicTableService;

    // ==================== 表管理接口 ====================

    @PostMapping("/create")
    @Operation(summary = "创建动态表", description = "根据模型ID创建对应的动态数据表")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:create')")
    public CommonResult<Long> createDynamicTable(@RequestParam("modelId") Long modelId) {
        return success(dynamicTableService.createDynamicTable(modelId));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除动态表", description = "逻辑删除动态表配置（物理表保留）")
    @Parameter(name = "id", description = "动态表ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:delete')")
    public CommonResult<Boolean> deleteDynamicTable(@RequestParam("id") Long id) {
        dynamicTableService.deleteDynamicTable(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取动态表详情")
    @Parameter(name = "id", description = "动态表ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<DynamicTableRespVO> getDynamicTable(@RequestParam("id") Long id) {
        DynamicTableDO dynamicTable = dynamicTableService.getDynamicTable(id);
        return success(convertToRespVO(dynamicTable));
    }

    @GetMapping("/get-by-model")
    @Operation(summary = "根据模型ID获取动态表")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<DynamicTableRespVO> getDynamicTableByModelId(@RequestParam("modelId") Long modelId) {
        DynamicTableDO dynamicTable = dynamicTableService.getDynamicTableByModelId(modelId);
        return success(convertToRespVO(dynamicTable));
    }

    @GetMapping("/list-by-entity-type")
    @Operation(summary = "根据业务类型获取动态表列表")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "task")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<List<DynamicTableRespVO>> listByEntityType(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        List<DynamicTableDO> list = dynamicTableService.listDynamicTablesByEntityType(entityTypeCode);
        return success(list.stream().map(this::convertToRespVO).collect(Collectors.toList()));
    }

    @GetMapping("/has-dynamic-table")
    @Operation(summary = "检查模型是否已创建动态表")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    public CommonResult<Boolean> hasDynamicTable(@RequestParam("modelId") Long modelId) {
        return success(dynamicTableService.hasDynamicTable(modelId));
    }

    // ==================== 字段同步接口 ====================

    @PostMapping("/sync-fields")
    @Operation(summary = "同步模型字段到动态表", description = "将模型的字段分配同步到动态表结构")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:update')")
    public CommonResult<Boolean> syncModelFields(@RequestParam("modelId") Long modelId) {
        dynamicTableService.syncModelFieldsToDynamicTable(modelId);
        return success(true);
    }

    @GetMapping("/columns")
    @Operation(summary = "获取动态表的字段列表")
    @Parameter(name = "dynamicTableId", description = "动态表ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<List<DynamicTableColumnRespVO>> listColumns(
            @RequestParam("dynamicTableId") Long dynamicTableId) {
        List<DynamicTableColumnDO> columns = dynamicTableService.listDynamicTableColumns(dynamicTableId);
        return success(columns.stream().map(this::convertColumnToRespVO).collect(Collectors.toList()));
    }

    // ==================== 数据操作接口 ====================

    @PostMapping("/data/insert")
    @Operation(summary = "插入数据到动态表")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:create')")
    public CommonResult<Long> insertData(@Valid @RequestBody DynamicTableDataReqVO reqVO) {
        Map<Long, Object> data = convertDataMap(reqVO.getData());
        return success(dynamicTableService.insertData(reqVO.getModelId(), data));
    }

    @PutMapping("/data/update")
    @Operation(summary = "更新动态表数据")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:update')")
    public CommonResult<Boolean> updateData(@Valid @RequestBody DynamicTableDataUpdateReqVO reqVO) {
        Map<Long, Object> data = convertDataMap(reqVO.getData());
        dynamicTableService.updateData(reqVO.getModelId(), reqVO.getRecordId(), data);
        return success(true);
    }

    @DeleteMapping("/data/delete")
    @Operation(summary = "删除动态表数据")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:delete')")
    public CommonResult<Boolean> deleteData(
            @RequestParam("modelId") Long modelId,
            @RequestParam("recordId") Long recordId) {
        dynamicTableService.deleteData(modelId, recordId);
        return success(true);
    }

    @GetMapping("/data/get")
    @Operation(summary = "获取动态表单条数据")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<Map<String, Object>> getData(
            @RequestParam("modelId") Long modelId,
            @RequestParam("recordId") Long recordId) {
        Map<Long, Object> data = dynamicTableService.getData(modelId, recordId);
        return success(convertDataMapToString(data));
    }

    @GetMapping("/data/list")
    @Operation(summary = "查询动态表数据列表")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<DynamicTableDataListRespVO> listData(
            @RequestParam("modelId") Long modelId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<Map<Long, Object>> list = dynamicTableService.listData(modelId, pageNo, pageSize, null);
        long total = dynamicTableService.countData(modelId, null);
        
        DynamicTableDataListRespVO respVO = new DynamicTableDataListRespVO();
        respVO.setList(list.stream().map(this::convertDataMapToString).collect(Collectors.toList()));
        respVO.setTotal(total);
        return success(respVO);
    }

    // ==================== 审计日志接口 ====================

    @GetMapping("/audit-logs")
    @Operation(summary = "获取动态表审计日志")
    @Parameter(name = "dynamicTableId", description = "动态表ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:dynamic-table:query')")
    public CommonResult<List<DynamicTableAuditLogRespVO>> listAuditLogs(
            @RequestParam("dynamicTableId") Long dynamicTableId) {
        List<DynamicTableAuditLogDO> logs = dynamicTableService.listAuditLogs(dynamicTableId);
        return success(logs.stream().map(this::convertAuditLogToRespVO).collect(Collectors.toList()));
    }

    // ==================== 私有方法 ====================

    private DynamicTableRespVO convertToRespVO(DynamicTableDO dynamicTable) {
        if (dynamicTable == null) {
            return null;
        }
        DynamicTableRespVO respVO = new DynamicTableRespVO();
        respVO.setId(dynamicTable.getId());
        respVO.setModelId(dynamicTable.getModelId());
        respVO.setEntityTypeCode(dynamicTable.getEntityTypeCode());
        respVO.setTableName(dynamicTable.getTableName());
        respVO.setTableComment(dynamicTable.getTableComment());
        respVO.setStatus(dynamicTable.getStatus());
        respVO.setVersion(dynamicTable.getVersion());
        respVO.setLastSyncTime(dynamicTable.getLastSyncTime());
        respVO.setCreateTime(dynamicTable.getCreateTime());
        return respVO;
    }

    private DynamicTableColumnRespVO convertColumnToRespVO(DynamicTableColumnDO column) {
        if (column == null) {
            return null;
        }
        DynamicTableColumnRespVO respVO = new DynamicTableColumnRespVO();
        respVO.setId(column.getId());
        respVO.setDynamicTableId(column.getDynamicTableId());
        respVO.setFieldId(column.getFieldId());
        respVO.setColumnName(column.getColumnName());
        respVO.setDataType(column.getDataType());
        respVO.setNullable(column.getNullable());
        respVO.setColumnComment(column.getColumnComment());
        respVO.setSortOrder(column.getSortOrder());
        respVO.setStatus(column.getStatus());
        return respVO;
    }

    private DynamicTableAuditLogRespVO convertAuditLogToRespVO(DynamicTableAuditLogDO log) {
        if (log == null) {
            return null;
        }
        DynamicTableAuditLogRespVO respVO = new DynamicTableAuditLogRespVO();
        respVO.setId(log.getId());
        respVO.setDynamicTableId(log.getDynamicTableId());
        respVO.setOperationType(log.getOperationType());
        respVO.setOperationDesc(log.getOperationDesc());
        respVO.setExecutedSql(log.getExecutedSql());
        respVO.setExecuteResult(log.getExecuteResult());
        respVO.setErrorMessage(log.getErrorMessage());
        respVO.setOperationTime(log.getOperationTime());
        respVO.setOperatorName(log.getOperatorName());
        return respVO;
    }

    private Map<Long, Object> convertDataMap(Map<String, Object> data) {
        if (data == null) {
            return new HashMap<>();
        }
        Map<Long, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            try {
                Long fieldId = Long.parseLong(entry.getKey());
                result.put(fieldId, entry.getValue());
            } catch (NumberFormatException e) {
                // 忽略非数字的 key
            }
        }
        return result;
    }

    private Map<String, Object> convertDataMapToString(Map<Long, Object> data) {
        if (data == null) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Long, Object> entry : data.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }
}
