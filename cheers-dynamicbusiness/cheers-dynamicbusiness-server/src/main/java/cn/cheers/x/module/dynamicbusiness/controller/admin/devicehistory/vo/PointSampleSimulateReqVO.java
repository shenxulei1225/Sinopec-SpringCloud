package cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 模拟上报一批采集读数。顺序就是判断时的先后。
 */
public class PointSampleSimulateReqVO {

    @NotEmpty
    @Valid
    private List<Reading> readings;

    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }

    public static class Reading {

        @NotNull
        private Long deviceId;
        @NotNull
        private String pointCode;
        @NotNull
        private Double value;

        public Long getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Long deviceId) {
            this.deviceId = deviceId;
        }

        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }
}
