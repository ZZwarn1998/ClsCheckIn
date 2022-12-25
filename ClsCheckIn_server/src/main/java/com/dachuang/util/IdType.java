package com.dachuang.util;

public enum IdType {
    NONE("none"), STU("stu"), TCHR("tchr");
    private String idType;
    private IdType(String idType){
        this.idType = idType;
    }
    public String getIdType(){
        return this.idType;
    }

}
