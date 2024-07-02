package com.easyjava.builder;

import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import com.easyjava.utils.StringUtils;
import com.easyjava.utils.YmlUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable {
    //日志对象
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    //数据库连接
    private static Connection conn = null;
    //预编译sql语句
    private static final String SQL_SHOW_TABLE_STATUS = "show table status";//展示表信息
    private static final String SQL_SHOW_TABLE_COLUMNS = "show full columns from %s";//展示表的所有字段信息
    private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";//展示表的所有字段

    //连接数据库
    static{
        String driverName = YmlUtils.getValue("db.driver.name");
        String url = YmlUtils.getValue("db.url");
        String username = YmlUtils.getValue("db.username");
        String password = YmlUtils.getValue("db.password");
        try{
            //注册并载入驱动
            Class.forName(driverName);
            //建立连接
            conn = DriverManager.getConnection(url,username,password);
        }catch (Exception e){
            logger.error("数据库连接失败",e);
        }
    }
    //读取数据库的表
    public static List<TableInfo> getTables(){
        //用于预编译sql语句
        PreparedStatement preparedStatement = null;
        //sql执行结果，存储所有的表信息
        ResultSet tableResult = null;
        //存储所有表信息
        List<TableInfo> tableInfoList = new ArrayList();
        try{
            //预编译
            preparedStatement = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            //执行
            tableResult = preparedStatement.executeQuery();
            //遍历所有的表信息
            while(tableResult.next()){
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                //将表的tableName转化为在java中要使用的beanName
                String beanName = tableName;
                //——1.判断是否要切掉表名的第一个前缀，例如tb_emp ---> emp
                if(Constants.ignoreTablePrefix){
                    beanName = beanName.substring(beanName.indexOf("_")+1);
                }
                //——2.转驼峰命名法，例如 emp_info ---> EmpInfo。表名的beanName设置为第一个分字段也驼峰
                beanName = StringUtils.processFieldName(beanName,true);
                //设置表的基本信息
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                //设置表的字段信息
                setFields(tableInfo);
                //设置表的索引集合
                setKeyIndexMap(tableInfo);

                tableInfoList.add(tableInfo);
            }
        }catch (Exception e){
            logger.error("获取表信息失败",e);
        }finally {
            if(tableResult!=null){
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //logger.info(JSON.toJSONString(tableInfoList,SerializerFeature.DisableCircularReferenceDetect));
        return tableInfoList;
    }

    /**
     * 设置表对象tableInfo的所有字段信息
     * @param tableInfo
     * @return
     */
    public static void setFields(TableInfo tableInfo){
        PreparedStatement preparedStatement = null;
        ResultSet fielsResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList();
        try{
            //拼接SQL语句：show full columns from tableInfo.getTableName();
            preparedStatement = conn.prepareStatement(String.format(SQL_SHOW_TABLE_COLUMNS,tableInfo.getTableName()));
            fielsResult = preparedStatement.executeQuery();
            //将几个Boolean属性的默认值设置为false，后续就无需再判断是否为null
            tableInfo.setHaveDate(false);
            tableInfo.setHaveDateTime(false);
            tableInfo.setHaveBigDecimal(false);
            //遍历每一行表字段信息
            while(fielsResult.next()){
                String fieldName = fielsResult.getString("Field");
                String sqlType = fielsResult.getString("Type");
                //varchar(10)--->varvhar
                if(sqlType.indexOf("(")>0){
                    sqlType = sqlType.substring(0,sqlType.indexOf("("));
                }
                String comment = fielsResult.getString("Comment");
                String autoIncrement = fielsResult.getString("Extra");
                Boolean isPrimaryKey = fielsResult.getString("Key").equals("PRI");
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(fieldName);
                fieldInfo.setSqlType(sqlType);
                fieldInfo.setComment(comment);
                //将filedName转成驼峰命名的propertyName，属性的第一个分字段不驼峰
                String propertyName = StringUtils.processFieldName(fieldName,false);
                //将sqlType转化为javaType
                String javaType = StringUtils.processType(sqlType);
                //判断自增长类型
                Boolean isAutoIncrement = "auto_increment".equals(autoIncrement);
                //判断是否为主键
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(javaType);
                fieldInfo.setAutoIncrement(isAutoIncrement);
                fieldInfo.setPrimaryKey(isPrimaryKey);
                fieldInfoList.add(fieldInfo);
                //补充表的havaDte、haveDateTime、haveBigDecimal属性
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,sqlType)){
                    tableInfo.setHaveDate(true);
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,sqlType)){
                    tableInfo.setHaveDateTime(true);
                }
                if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES,sqlType)) {
                    tableInfo.setHaveBigDecimal(true);
                }
            }
            tableInfo.setFieldList(fieldInfoList);
        }catch (Exception e){
            logger.error("解析表字段失败",e);
        }finally {
            if(fielsResult!=null){
                try {
                    fielsResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 设置表的索引集合
     * @param tableInfo
     */
    public static void setKeyIndexMap(TableInfo tableInfo){
        PreparedStatement preparedStatement = null;
        ResultSet indexsResult = null;
        try{
            preparedStatement = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX,tableInfo.getTableName()));
            indexsResult = preparedStatement.executeQuery();
            Map<String,FieldInfo> fieldInfoMap = new HashMap();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                fieldInfoMap.put(fieldInfo.getFieldName(),fieldInfo);
            }
            while(indexsResult.next()){
                String keyName = indexsResult.getString("Key_name");
                String columnName = indexsResult.getString("Column_name");
                Integer nonUnique = indexsResult.getInt("Non_unique");
                //非唯一索引，不添加
                if(nonUnique==1){
                    continue;
                }
                //根据keyName获取该索引关联的字段列表
                List<FieldInfo> fieldInfoList = tableInfo.getKeyIndexMap().get(keyName);
                //如果还未创建关联字段列表则创建
                if(fieldInfoList==null){
                    fieldInfoList = new ArrayList();
                    tableInfo.getKeyIndexMap().put(keyName,fieldInfoList);
                }
                fieldInfoList.add(fieldInfoMap.get(columnName));
            }
        }catch (Exception e){
            logger.error("解析表索引失败",e);
        }finally {
            if (indexsResult!=null){
                try {
                    indexsResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
