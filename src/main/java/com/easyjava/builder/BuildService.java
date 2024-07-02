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

public class BuildService {
    private final static Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo){
        File servicePackage = new File(Constants.SERVICE_PATH);
        if(!servicePackage.exists()){
            servicePackage.mkdirs();
        }
        File file = new File(servicePackage,tableInfo.getBeanName()+"Service.java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter opsw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsw);
        ){
            //导包
            bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.SERVICE_PACKAGE+";");
            bw.newLine();
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.DTO_PACKAGE+"."+tableInfo.getBeanName()+"DTO;");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.DTO_PACKAGE+"."+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.ENTITY_PACKAGE+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.VO_PACKAGE+"."+tableInfo.getBeanName()+"VO;");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.RESULT_PACKAGE+"."+"PageResult;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            //类声明
            bw.write("public interface "+tableInfo.getBeanName()+"Service {");
            bw.newLine();
            //存储所有组合方法字段，例如IdAndCode
            List<String> methodNameList = new ArrayList<>();
            //keyList存入ThreadLocal，用于后续构建ServiceImpl
            List<List<FieldInfo>> keyList = new ArrayList<>();
            //根据表的所有索引创建select、update、delete方法
            for (Map.Entry<String, List<FieldInfo>> keyIndexMap : tableInfo.getKeyIndexMap().entrySet()) {
                //拼接出联合索引的名字
                List<FieldInfo> keyIndexList = keyIndexMap.getValue();
                keyList.add(keyIndexList);
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
        }catch (Exception e){
            logger.error("创建Service失败",e);
        }
    }
    /**
     * 查询
     * @param bw
     * @param tableInfo
     * @param methodNameBuilder
     * @param methodParamBuilder
     * @param commentBuilder
     * @param commentParamList
     * @throws IOException
     */
    private static void buildSelect(BufferedWriter bw,TableInfo tableInfo,StringBuilder methodNameBuilder,StringBuilder methodParamBuilder,StringBuilder commentBuilder,List<String> commentParamList)throws IOException{
        String selectMethodName = "findBy"+methodNameBuilder;
        BuildComment.buildMethodComment(bw,commentBuilder.append("查询").toString(),commentParamList,true);
        bw.newLine();
        bw.write("\t"+tableInfo.getBeanName()+"VO "+selectMethodName+"("+methodParamBuilder+");");
    }

    /**
     * 修改
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
        BuildComment.buildMethodComment(bw,commentBuilder.delete(commentBuilder.length()-2,commentBuilder.length()).append("修改").toString(), Arrays.asList(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"DTO"),false);
        bw.newLine();
        bw.write("\tvoid "+updateMethodName+"("+tableInfo.getBeanName()+"DTO "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"DTO);");
    }

    /**
     * 删除
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
        String pageQueryMethodName = "listByPage";
        //方法注释
        List<String> paramList = new ArrayList<>();
        paramList.add(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+Constants.pageQueryEntitySuffix);
        BuildComment.buildMethodComment(bw,"分页查询",paramList,true);
        bw.newLine();
        bw.write("\tPageResult "+pageQueryMethodName+"("+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+" "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+Constants.pageQueryEntitySuffix+");");
    }

    /**
     * 新增
     * @param bw
     * @param tableInfo
     */
    private static void buildInsert(BufferedWriter bw, TableInfo tableInfo) throws IOException{
        //方法注释
        List<String> paramList = new ArrayList<>();
        paramList.add(StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"DTO");
        BuildComment.buildMethodComment(bw,"新增",paramList,false);
        bw.newLine();

        bw.write("\tvoid add("+tableInfo.getBeanName()+"DTO "+StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"DTO);");
    }

    /**
     * 批量新增
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildInsertBatch(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        String param = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName()) + "DTOList";
        List<String> paramList = new ArrayList<>();
        paramList.add(param);
        BuildComment.buildMethodComment(bw,"批量新增",paramList,false);
        bw.newLine();

        bw.write("\tvoid addBatch(List<"+tableInfo.getBeanName()+"DTO> "+param+");");
    }
}
