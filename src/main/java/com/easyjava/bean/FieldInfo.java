package com.easyjava.bean;
//字段信息bean
public class FieldInfo {
    //在sql表中的字段名称
    private String fieldName;
    //在java中作为属性的名称
    private String propertyName;
    //在sql表中的字段类型
    private String sqlType;
    //在java中对应的类型
    private String javaType;
    //在sql表中的字段备注
    private String comment;
    //字段是否自增长
    private Boolean isAutoIncrement;
    //字段是否为主键
    private Boolean isPrimaryKey;
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}
