package com.easyjava.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//表信息bean
public class TableInfo {
    //表在sql表中的名称
    private String tableName;
    //表在java中作为entity的名称
    private String beanName;
    //表的注释
    private String comment;
    //表的字段信息
    private List<FieldInfo> fieldList;
    //表的索引集合，用LinkedHashMap保证索引的有序性。String为索引名keyName，List<FieldInfo>为该索引关联的字段——联合索引：一个索引可以关联多个字段
    private Map<String,List<FieldInfo>> keyIndexMap = new LinkedHashMap();
    //是否存在日期类型字段
    private Boolean haveDate;;
    //是否存在时间类型字段
    private Boolean haveDateTime;
    //是否存在bigdecimal类型字段
    private Boolean haveBigDecimal;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }
}
