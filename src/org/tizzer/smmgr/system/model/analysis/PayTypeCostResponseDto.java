package org.tizzer.smmgr.system.model.analysis;

import java.util.HashMap;
import java.util.Map;

public class PayTypeCostResponseDto {

    private DataSet[] data;

    public Map<String, Double> getData() {
        if (data != null) {
            Map<String, Double> map = new HashMap<>();
            for (DataSet dataSet : data) {
                Double value = dataSet.getValue();
                map.put(dataSet.getName(), value == null ? 0 : value);
            }
            return map;
        }
        return null;
    }

    public void setData(DataSet[] data) {
        this.data = data;
    }

    static class DataSet {
        private String name;
        private Double value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }
}
