package com.easyjava.builder;

import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import com.easyjava.utils.StringUtils;
import com.easyjava.utils.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BuildMapper {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo){
        //创建mapper包
        File mapperPackage = new File(Constants.MAPPER_PATH);
        if(!mapperPackage.exists()){
            mapperPackage.mkdirs();
        }
        File file = new File(mapperPackage,tableInfo.getBeanName()+"Mapper.java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter opsw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsw);
            ){
            //导包
            bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.MAPPER_PACKAGE+";");
            bw.newLine();
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.DTO_PACKAGE+"."+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.ENTITY_PACKAGE+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.VO_PACKAGE+"."+tableInfo.getBeanName()+"VO;");
            bw.newLine();
            bw.newLine();
            bw.write("import com.github.pagehelper.Page;");
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.*;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            //Mapper注解
            bw.write("@Mapper");
            bw.newLine();
            //类声明
            bw.write("public interface "+tableInfo.getBeanName()+"Mapper {");
            bw.newLine();
            //存储所有组合方法字段，例如IdAndCode
            List<String> methodNameList = new ArrayList<>();
            //根据表的所有索引创建select、update、delete方法
            for (Map.Entry<String, List<FieldInfo>> keyIndexMap : tableInfo.getKeyIndexMap().entrySet()) {
                //拼接出联合索引的名字
                List<FieldInfo> keyIndexList = keyIndexMap.getValue();
                StringBuilder methodNameBuilder = new StringBuilder();//方法名
                StringBuilder methodParamBuilder = new StringBuilder();//方法形参表
                StringBuilder commentBuilder = new StringBuilder();//方法注释的comment语句
                List<String> commentParamList = new ArrayList<>();//方法注释的@param 成员对象
                int index = 0;
                for (FieldInfo keyindex : keyIndexList) {
                    index++;
                    methodNameBuilder.append(StringUtils.uppercaseFirstLetter(keyindex.getPropertyName()));
                    methodParamBuilder.append(keyindex.getJavaType()).append(" ").append(keyindex.getPropertyName());
                    commentParamList.add(keyindex.getPropertyName());
                    if(index==1){
                        commentBuilder.append("根据").append(keyindex.getPropertyName());
                    }else{
                        commentBuilder.append("和").append(keyindex.getPropertyName());
                    }
                    if(index<keyIndexList.size()){
                        methodNameBuilder.append("And");
                        methodParamBuilder.append(",");
                    }
                }
                methodNameList.add(methodNameBuilder.toString());
                //select
                buildSelect(bw,tableInfo,methodNameBuilder,methodParamBuilder,commentBuilder,commentParamList);
                bw.newLine();
                bw.newLine();
                //update
                buildUpdate(bw,tableInfo,methodNameBuilder,methodParamBuilder,commentBuilder,commentParamList);
                bw.newLine();
                bw.newLine();
                //delete
                buildDelete(bw,tableInfo,methodNameBuilder,methodParamBuilder,commentBuilder,commentParamList);
                bw.newLine();
                bw.newLine();
            }
            //pageQuery
            buildPageQuery(bw,tableInfo);
            bw.newLine();
            bw.newLine();
            //insert
            buildInsert(bw,tableInfo);
            bw.newLine();
            bw.newLine();
            //insertBatch
            buildInsertBatch(bw,tableInfo);
            bw.newLine();
            //类结尾大括号
            bw.write("}");
            //将方法名列表存入ThreadLocal，用于下文构建mapper_XML
            ThreadLocalUtils.setMethodNameList(methodNameList);
        }catch (Exception e){
            logger.error("创建Mapper失败",e);
        }
    }
    /**
     * 根据索引select
     * @param bw
     * @param tableInfo
     * @param methodNameBuilder
     * @param methodParamBuilder
     * @param commentBuilder
     * @param commentParamList
     * @throws IOException
     */
    private static void buildSelect(BufferedWriter bw,TableInfo tableInfo,StringBuilder methodNameBuilder,StringBuilder methodParamBuilder,StringBuilder commentBuilder,List<String> commentParamList)throws IOException{
        String selectMethodName = "selectBy"+methodNameBuilder;
        BuildComment.buildMethodComment(bw,commentBuilder.append("查询").toString(),commentParamList,true);
        bw.newLine();
        bw.write("\t"+tableInfo.getBeanName()+" "+selectMethodName+"("+methodParamBuilder+");");
    }

    /**
     * 根据索引update
     * @param bw
     * @param tableInfo
     * @param methodNameBuilder
     * @param methodParamBuilder
     * @param commentBuilder
     * @param commentParamList
     * @throws IOException
     */
    private static void buildUpdate(BufferedWriter bw,TableInfo tableInfo,StringBuilder methodNameBuilder,StringBuilder methodParamBuilder,StringBuilder commentBuilder,List<String> commentParamList)throws IOException{
        String updateMethodName = "updateBy"+methodNameBuilder;
        BuildComment.buildMethodComment(bw,commentBuilder.delete(commentBuilder.length()-2,commentBuilder.length()).append("修改").toString(), Arrays.asList(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())),false);
        bw.newLine();
        bw.write("\tvoid "+updateMethodName+"("+tableInfo.getBeanName()+" "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+");");
    }

    /**
     * 根据索引delete
     * @param bw
     * @param tableInfo
     * @param methodNameBuilder
     * @param methodParamBuilder
     * @param commentBuilder
     * @param commentParamList
     * @throws IOException
     */
    private static void buildDelete(BufferedWriter bw,TableInfo tableInfo,StringBuilder methodNameBuilder,StringBuilder methodParamBuilder,StringBuilder commentBuilder,List<String> commentParamList)throws IOException{
        String deleteMethodName = "deleteBy"+methodNameBuilder;
        BuildComment.buildMethodComment(bw,commentBuilder.delete(commentBuilder.length()-2,commentBuilder.length()).append("删除").toString(),commentParamList,false);
        bw.newLine();
        bw.write("\tvoid "+deleteMethodName+"("+methodParamBuilder+");");
    }

    /**
     * 分页查询
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildPageQuery(BufferedWriter bw,TableInfo tableInfo)throws IOException{
        //方法名
        String pageQueryMethodName = Constants.pageQueryMethodNamePrefix+ tableInfo.getBeanName()+Constants.pageQueryMethodNameSuffix;
        //方法注释
        List<String> paramList = new ArrayList<>();
        paramList.add(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+Constants.pageQueryEntitySuffix);
        BuildComment.buildMethodComment(bw,"分页查询",paramList,true);
        bw.newLine();

        bw.write("\tPage<"+tableInfo.getBeanName()+"VO> "+pageQueryMethodName+"("+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+" "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+Constants.pageQueryEntitySuffix+");");
    }

    /**
     * 单条插入insert
     * @param bw
     * @param tableInfo
     */
    private static void buildInsert(BufferedWriter bw, TableInfo tableInfo) throws IOException{
        //方法注释
        List<String> paramList = new ArrayList<>();
        paramList.add(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName()));
        BuildComment.buildMethodComment(bw,"插入",paramList,false);
        bw.newLine();

        bw.write("\tvoid insert("+tableInfo.getBeanName()+" "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+");");
    }

    /**
     * 批量插入insertBatch
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildInsertBatch(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        String param = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName()) + "List";
        List<String> paramList = new ArrayList<>();
        paramList.add(param);
        BuildComment.buildMethodComment(bw,"批量插入",paramList,false);
        bw.newLine();

        bw.write("\tvoid insertBatch(@Param(\""+param+"\") List<"+tableInfo.getBeanName()+"> "+param+");");
    }

}
