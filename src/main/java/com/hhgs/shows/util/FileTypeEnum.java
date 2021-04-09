package com.hhgs.shows.util;

public enum FileTypeEnum {
    ZIP(".zip"),XLS(".xls"),XLSX(".xlsx");

    private String val;

    FileTypeEnum(String val) {
        this.val = val;
    }

    FileTypeEnum() {
    }

    public String getVal() {
        return val;
    }


}
