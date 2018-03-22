package org.tizzer.smmgr.system.model.response;

import com.alee.utils.TimeUtils;

import java.util.Date;

public class QueryOneStoreResponseDto extends ResultResponse {
    private Long id;
    private String name;
    private String address;
    private Date foundDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }

    public Object[] getData() {
        return new Object[]{id, name, address, TimeUtils.formatDate("yyyy-MM-dd HH:mm:ss", foundDate)};
    }
}
