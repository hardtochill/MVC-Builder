package com.easyjava.utils;

import com.easyjava.constants.Constants;
import org.apache.commons.lang3.ArrayUtils;

public class StringUtils {
    /**
     * 将字段的首字母转大写后返回
     * @param field
     * @return
     */
    public static String uppercaseFirstLetter(String field){
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toUpperCase() + field.substring(1);
    }

    /**
     * 将字段的首字母转小写后返回
     * @param field
     * @return
     */
    public static String lowercaseFirstLetter(String field){
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toLowerCase() + field.substring(1);
    }

    /**
     * 将传入的字段转成驼峰命名  people_emp_info_ ---> peopleEmpInfo/PeopleEmpInfo
     * @param field 字段名
     * @param isFirstLetterUpperCase 第一个分字段是否驼峰
     * @return
     */
    public static String processFieldName(String field,Boolean isFirstLetterUpperCase){
        StringBuilder sb = new StringBuilder();
        String[] fieldList = field.split("_");
        sb.append(isFirstLetterUpperCase?uppercaseFirstLetter(fieldList[0]):fieldList[0]);
        for(int i=1;i<fieldList.length;i++){
            sb.append(uppercaseFirstLetter(fieldList[i]));
        }
        return sb.toString();
    }
    /**
     * 将sql中的类型转换为java中的类型
     * @param sqlType
     * @return
     */
    public static String processType(String sqlType){
        if(ArrayUtils.contains(Constants.SQL_INTEGER_TYPES,sqlType)){
            return "Integer";
        }
        if(ArrayUtils.contains(Constants.SQL_LONG_TYPES,sqlType)){
            return "Long";
        }
        if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,sqlType)){
            return "String";
        }
        if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES,sqlType)){
            return "BigDecimal";
        }
        if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,sqlType) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,sqlType)){
            return "LocalDateTime";
        }else{
            throw new RuntimeException("无法识别的类型:"+sqlType);
        }
    }
}
