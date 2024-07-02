package com.easyjava.builder;

import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import com.easyjava.utils.StringUtils;
import com.easyjava.utils.ThreadLocalUtils;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public class BuildMapperXML {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXML.class);
    //构建哈希表映射表属性的 propertyName:fieldName
    private static Map<String,String> propertyToFieldMap;
    /**
     * 构建表的Mapper对应的XML文件
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo){
        File mapperXmlPackage = new File(Constants.MAPPER_XML_PATH);
        if(!mapperXmlPackage.exists()){
            mapperXmlPackage.mkdirs();
        }
        File file = new File(mapperXmlPackage,tableInfo.getBeanName()+"Mapper.xml");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(osw);
            ){
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                    "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
            bw.newLine();
            bw.write("<mapper namespace=\""+Constants.BASE_PACKAGE+"."+Constants.MAPPER_PACKAGE+"."+tableInfo.getBeanName()+"Mapper\">");
            bw.newLine();
            //构建哈希表映射表属性的 propertyName:fieldName
            List<FieldInfo> fieldList = tableInfo.getFieldList();
            buildPropertyToFieldMap(fieldList);
            //取出ThreadLocal中的方法名列表,方法名列表形如Id、IdAndCode，根据"And"切分再转小写就能得到主键属性
            List<String> methodNameList = ThreadLocalUtils.getMethodNameList();
            //构建select语句
            buildSelectXML(bw,methodNameList,tableInfo);
            //构建update语句
            buildUpdateXML(bw,methodNameList,tableInfo);
            //构建delete语句
            buildDeleteXML(bw,methodNameList,tableInfo);
            //分页查询语句
            buildPageQuery(bw,tableInfo);
            //insert语句
            buildInsert(bw,tableInfo);
            //insertBatch语句
            buildInsertBatch(bw,tableInfo);
            bw.write("</mapper>");
        }catch(Exception e){
            logger.error("创建MapperXML失败",e);
        }
    }

    /**
     * 编写主键的select语句
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildSelectXML(BufferedWriter bw, List<String> methodNameList, TableInfo tableInfo)throws IOException{
        //id就是在Mapper文件中的方法名
        String id;
        //构建Entity对象所在包名，用于接收返回值
        String entityPackage = new StringBuilder().append(Constants.BASE_PACKAGE).append(".").append(Constants.POJO_PACKAGE).append(".").append(Constants.ENTITY_PACKAGE).append(".").append(tableInfo.getBeanName()).toString();
        for (String methodName : methodNameList) {
            //根据methodName拿到对应的主键参数：Id——>id，IdAndCode——>id、code
            List<String> keyList = transferNameToParam(methodName);
            //注释
            bw.write("\t<!--"+transferNameToComment(methodName,0)+"-->");
            bw.newLine();
            id = new StringBuilder().append("selectBy").append(methodName).toString();
            bw.write("\t<select id=\""+id+"\" resultType=\""+entityPackage+"\">");
            bw.newLine();
            bw.write("\t\tselect * from "+tableInfo.getTableName()+" where "+propertyToFieldMap.get(keyList.get(0))+" = #{"+keyList.get(0)+"} ");
            //联合主键
            if(keyList.size()>1){
                for(int i=1;i<keyList.size();i++){
                    bw.write("and "+propertyToFieldMap.get(keyList.get(i))+" = #{"+keyList.get(i)+"} ");
                }
            }
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
        }
    }

    /**
     * 编写主键的update语句
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildUpdateXML(BufferedWriter bw,List<String> methodNameList,TableInfo tableInfo)throws IOException{
        //id就是在Mapper文件中的方法名
        String id;
        for (String methodName : methodNameList) {
            //根据methodName拿到对应的主键参数：Id——>id，IdAndCode——>id、code
            List<String> keyList = transferNameToParam(methodName);
            //注释
            bw.write("\t<!--"+transferNameToComment(methodName,1)+"-->");
            bw.newLine();
            id = new StringBuilder().append("updateBy").append(methodName).toString();
            bw.write("\t<update id=\""+id+"\">");
            bw.newLine();
            bw.write("\t\tupdate "+tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<set>");
            bw.newLine();
            for (FieldInfo field : tableInfo.getFieldList()) {
                //主键不能修改
                if(field.getPrimaryKey()){
                    continue;
                }
                /**
                 * 对于String类型，仅需判断其!=null即可，不用判断!=''
                 * 因为update方法传入的entity对象，其String类型属性的初值都为null
                 * 因此若String属性为null说明未设置初值，因此不作为更新内容
                 * 而若是String属性''，说明就是要把该字段置为空
                 */
                bw.write("\t\t\t<if test=\""+field.getPropertyName()+"!=null\">");
                bw.write(field.getFieldName()+"=#{"+field.getPropertyName()+"},");
                bw.write("</if>");
                bw.newLine();
            }
            bw.write("\t\t</set>");
            bw.newLine();
            bw.write("\t\twhere "+propertyToFieldMap.get(keyList.get(0))+" = #{"+keyList.get(0)+"} ");
            //联合主键
            if(keyList.size()>1){
                for(int i=1;i<keyList.size();i++){
                    bw.write("and "+propertyToFieldMap.get(keyList.get(i))+" = #{"+keyList.get(i)+"} ");
                }
            }
            bw.newLine();
            bw.write("\t</update>");
            bw.newLine();
        }
    }

    /**
     * 编写主键的delete语句
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    //delete
    private static void buildDeleteXML(BufferedWriter bw,List<String> methodNameList, TableInfo tableInfo)throws IOException{
        //id就是在Mapper文件中的方法名
        String id;
        for (String methodName : methodNameList) {
            //根据methodName拿到对应的主键参数：Id——>id，IdAndCode——>id、code
            List<String> keyList = transferNameToParam(methodName);
            //注释
            bw.write("\t<!--"+transferNameToComment(methodName,2)+"-->");
            bw.newLine();
            id = new StringBuilder().append("deleteBy").append(methodName).toString();
            bw.write("\t<delete id=\""+id+"\">");
            bw.newLine();
            bw.write("\t\tdelete from "+tableInfo.getTableName()+" where "+propertyToFieldMap.get(keyList.get(0))+" = #{"+keyList.get(0)+"} ");
            //联合主键
            if(keyList.size()>1){
                for(int i=1;i<keyList.size();i++){
                    bw.write("and "+propertyToFieldMap.get(keyList.get(i))+" = #{"+keyList.get(i)+"} ");
                }
            }
            bw.newLine();
            bw.write("\t</delete>");
            bw.newLine();
        }
    }

    /**
     * 分页查询
     * @param bw
     * @param tableInfo
     */
    private static void buildPageQuery(BufferedWriter bw,TableInfo tableInfo) throws Exception{
        //id = Mapper中的方法名
        String id = Constants.pageQueryMethodNamePrefix+ tableInfo.getBeanName()+Constants.pageQueryMethodNameSuffix;
        //返回值为对应的VO对象
        String resultType = new StringBuilder().append(Constants.BASE_PACKAGE).append(".").append(Constants.POJO_PACKAGE).append(".").append(Constants.VO_PACKAGE).append(".").append(tableInfo.getBeanName()).append("VO").toString();
        bw.write("\t<!--分页查询-->");
        bw.newLine();
        bw.write("\t<select id=\""+id+"\" resultType=\""+resultType+"\">");
        bw.newLine();
        bw.write("\t\tselect * from "+tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<where>");
        bw.newLine();
        //默认情况下，创建出来的PageQueryDTO对象就是在原DTO对象的基础上加上page和pageSize属性，因此PageQueryDTO进行条件查询的属性也就是表的所有属性
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        for (FieldInfo fieldInfo : fieldList) {
            if(fieldInfo.getJavaType().equals("LocalDateTime")){
                //日期类型需要按照具体需求比较
                continue;
            }else if(fieldInfo.getJavaType().equals("String")){
                String propertyName = fieldInfo.getPropertyName();
                //String用模糊查询
                bw.write("\t\t\t<if test=\""+propertyName+"!=null and "+propertyName+"!=''\">");
                bw.write("and "+fieldInfo.getFieldName()+" like concat("+"'%',#{"+propertyName+"},'%')</if>");
                bw.newLine();
            }else{
                String propertyName = fieldInfo.getPropertyName();
                bw.write("\t\t\t<if test=\""+propertyName+"!=null\">");
                bw.write("and "+fieldInfo.getFieldName()+"=#{"+propertyName+"}</if>");
                bw.newLine();
            }
        }
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
    }

    /**
     * 插入
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildInsert(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        //id
        String id = "insert";
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        //注释
        bw.write("\t<!--插入-->");
        bw.newLine();
        bw.write("\t<insert id=\""+id+"\">");
        bw.newLine();
        bw.write("\t\tinsert into "+tableInfo.getTableName()+" (");
        for(int i=0;i<fieldList.size();i++){
            //主键不插入
            if(fieldList.get(i).getPrimaryKey()){
                continue;
            }
            bw.write(fieldList.get(i).getFieldName());
            if(i!=fieldList.size()-1){
                bw.write(", ");
            }else{
                bw.write(")");
            }
        }
        bw.newLine();
        bw.write("\t\t\tvalues ");
        bw.newLine();
        bw.write("\t\t(");
        for(int i=0;i<fieldList.size();i++){
            //主键不插入
            if(fieldList.get(i).getPrimaryKey()){
                continue;
            }
            if(i!=fieldList.size()-1){
                bw.write("#{"+fieldList.get(i).getPropertyName()+"}, ");
            }else{
                bw.write("#{"+fieldList.get(i).getPropertyName()+"}");
            }
        }
        bw.write(")");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
    }

    /**
     * 批量插入
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildInsertBatch(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        //id
        String id = "insertBatch";
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        //注释
        bw.write("\t<!--批量插入-->");
        bw.newLine();
        bw.write("\t<insert id=\""+id+"\">");
        bw.newLine();
        bw.write("\t\tinsert into "+tableInfo.getTableName()+" (");
        for (int i = 0; i < fieldList.size(); i++) {
            if(fieldList.get(i).getPrimaryKey()){
                continue;
            }
            bw.write(fieldList.get(i).getFieldName());
            if(i!=fieldList.size()-1){
                bw.write(", ");
            }else{
                bw.write(")");
            }
        }
        bw.newLine();
        bw.write("\t\t\tvalue");
        bw.newLine();
        String item = "o";
        bw.write("\t\t<foreach collection=\""+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"List"+"\" item=\""+item+"\" separator=\",\">");
        bw.newLine();
        bw.write("\t\t\t(");
        for (int i = 0; i < fieldList.size(); i++) {
            if(fieldList.get(i).getPrimaryKey()){
                continue;
            }
            bw.write("#{"+item+"."+fieldList.get(i).getPropertyName()+"}");
            if(i!=fieldList.size()-1){
                bw.write(",");
            }
        }
        bw.write(")");
        bw.newLine();
        bw.write("\t\t</foreach>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
    }
    /**
     * 根据方法名解析出参数列表，例如：IdAndCode——>id、code
     * @param name
     * @return
     */
    private static List<String> transferNameToParam(String name){
        List<String> paramList = Arrays.asList(name.split("And"));
        for(int i=0;i<paramList.size();i++){
            paramList.set(i,StringUtils.lowercaseFirstLetter(paramList.get(i)));
        }
        return paramList;
    }

    /**
     * 构建注释
     * @param methodName
     * @param type
     * @return
     */
    private static String transferNameToComment(String methodName,Integer type){
        List<String> paramList = Arrays.asList(methodName.split("And"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.lowercaseFirstLetter(paramList.get(0)));
        for(int i=1;i<paramList.size();i++){
            stringBuilder.append("和").append(StringUtils.lowercaseFirstLetter(paramList.get(i)));
        }
        //构建注释，把方法名的And替换成"和"
        switch (type){
            case 0:
                return new StringBuilder().append("根据").append(stringBuilder).append("查询").toString();
            case 1:
                return new StringBuilder().append("根据").append(stringBuilder).append("修改").toString();
            default:
                return new StringBuilder().append("根据").append(stringBuilder).append("删除").toString();
        }
    }

    /**
     * 构建哈希表映射表属性的 propertyName:fieldName
     * @param fieldInfoList
     * @return
     */
    private static void buildPropertyToFieldMap(List<FieldInfo> fieldInfoList){
        //刷新
        propertyToFieldMap = new HashMap<>();
        for (FieldInfo fieldInfo : fieldInfoList) {
            propertyToFieldMap.put(fieldInfo.getPropertyName(),fieldInfo.getFieldName());
        }
    }
}
