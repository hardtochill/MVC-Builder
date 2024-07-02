package com.easyjava.builder;

import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import com.easyjava.utils.StringUtils;
import com.easyjava.utils.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);
    //属性名-属性类型
    private static Map<String,String> nameToTypeMap = new HashMap<>();
    public static void execute(TableInfo tableInfo){
        File serviceImplPackage = new File(Constants.SERVICE_IMPL_PATH);
        if(!serviceImplPackage.exists()){
            serviceImplPackage.mkdirs();
        }
        File file = new File(serviceImplPackage,tableInfo.getBeanName()+"Service"+Constants.SERVICE_IMPL_SUFFIX+".java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter opsw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsw);
        ){
            String serviceName = tableInfo.getBeanName()+"Service";
            String mapperName = tableInfo.getBeanName()+"Mapper";
            buildNameToTypeMap(tableInfo);
            //导包
            bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.SERVICE_PACKAGE+"."+Constants.SERVICE_IMPL_PACKAGE+";");
            bw.newLine();
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.SERVICE_PACKAGE+"."+serviceName+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.MAPPER_PACKAGE+"."+mapperName+";");
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
            bw.write("import com.github.pagehelper.Page;");
            bw.newLine();
            bw.write("import com.github.pagehelper.PageHelper;");
            bw.newLine();
            bw.write("import org.springframework.beans.BeanUtils;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.ArrayList;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            //service注解
            bw.write("@Service");
            bw.newLine();
            //类声明
            bw.write("public class "+serviceName+Constants.SERVICE_IMPL_SUFFIX+" implements "+serviceName+" {");
            bw.newLine();
            //依赖注入
            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\tprivate "+mapperName+" "+StringUtils.lowercaseFirstLetter(mapperName)+";");
            bw.newLine();
            //取出ThreadLocal中的方法名列表,方法名列表形如Id、IdAndCode，根据"And"切分再转小写就能得到主键属性
            List<String> methodNameList = ThreadLocalUtils.getMethodNameList();
            //构建find方法
            buildFind(bw,methodNameList,tableInfo);
            //构建update方法
            buildUpdate(bw,methodNameList,tableInfo);
            //构建delete方法
            buildDelete(bw,methodNameList,tableInfo);
            //分页查询方法
            buildListByPage(bw,tableInfo);
            //add方法
            buildAdd(bw,tableInfo);
            //addBatch方法
            buildAddBatch(bw,tableInfo);
            //类结尾大括号
            bw.write("}");
        }catch (Exception e){
            logger.error("构建serviceImpl失败",e);
        }
    }
    /**
     * 编写Find方法
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildFind(BufferedWriter bw, List<String> methodNameList, TableInfo tableInfo)throws IOException{
        String className = tableInfo.getBeanName();
        String voClassName = className+"VO";
        String objectName = StringUtils.lowercaseFirstLetter(className);
        String voObjectName = StringUtils.lowercaseFirstLetter(voClassName);
        for (String methodName : methodNameList) {
            //根据methodName拿到对应的主键参数：Id——>id，IdAndCode——>id、code
            List<String> keyList = transferNameToParam(methodName);
            //注释
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,0),keyList,true);
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            //构建形参表和mapper层方法的形参表
            StringBuilder paramBuilder = new StringBuilder();
            StringBuilder mapperParamBuilder = new StringBuilder();
            for(int i=0;i<keyList.size();i++){
                String name = keyList.get(i);
                paramBuilder.append(nameToTypeMap.get(name)).append(" ").append(name);
                mapperParamBuilder.append(name);
                if (i<keyList.size()-1){
                    paramBuilder.append(", ");
                    mapperParamBuilder.append(", ");
                }
            }
            bw.write("\tpublic "+voClassName+" findBy"+methodName+"("+paramBuilder+") {");
            bw.newLine();
            //Employee employee = employeeMapper.selectById(id);
            bw.write("\t\t"+className+" "+objectName+" = "+objectName+"Mapper.selectBy"+methodName+"("+mapperParamBuilder+");");
            bw.newLine();
            //if(employee==null){return null};
            bw.write("\t\tif ("+objectName+" == null) return null;");
            bw.newLine();
            //EmployeeVO employeeVO = new EmployeeVO();
            bw.write("\t\t"+voClassName+" "+voObjectName+" = new "+voClassName+"();");
            bw.newLine();
            //BeanUtils.copyProperties(employee,employeeVO);
            bw.write("\t\tBeanUtils.copyProperties("+objectName+", "+voObjectName+");");
            bw.newLine();
            bw.write("\t\treturn "+voObjectName+";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }

    /**
     * 编写buildUpdate方法
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildUpdate(BufferedWriter bw,List<String> methodNameList,TableInfo tableInfo)throws IOException{
        String className = tableInfo.getBeanName();
        String dtoClassName = className+"DTO";
        String objectName = StringUtils.lowercaseFirstLetter(className);
        String dtoObjectName = StringUtils.lowercaseFirstLetter(dtoClassName);
        for (String methodName : methodNameList) {
            //注释
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,1),Arrays.asList(dtoObjectName),true);
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic void updateBy"+methodName+"("+dtoClassName+" "+dtoObjectName+") {");
            bw.newLine();
            //Employee employee = new Employee();
            bw.write("\t\t"+className+" "+objectName+" = new "+className+"();");
            bw.newLine();
            //BeanUtils.copyProperties(employeeDTO,employee);
            bw.write("\t\tBeanUtils.copyProperties("+dtoObjectName+", "+objectName+");");
            bw.newLine();
            //employeeMapper.updateById(employee);
            bw.write("\t\t"+objectName+"Mapper.updateBy"+methodName+"("+objectName+");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }

    }

    /**
     * 编写delete方法
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    //delete
    private static void buildDelete(BufferedWriter bw,List<String> methodNameList, TableInfo tableInfo)throws IOException{
        String mapperObjectName = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"Mapper";
        for (String methodName : methodNameList) {
            List<String> keyList = transferNameToParam(methodName);
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,2),keyList,false);
            bw.newLine();
            StringBuilder paramBuilder = new StringBuilder();
            StringBuilder mapperParamBuilder = new StringBuilder();
            for(int i=0;i<keyList.size();i++){
                String param = keyList.get(i);
                paramBuilder.append(nameToTypeMap.get(param)).append(" ").append(param);
                mapperParamBuilder.append(param);
                if(i<keyList.size()-1){
                    paramBuilder.append(", ");
                    mapperParamBuilder.append(", ");
                }
            }
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic void deleteBy"+methodName+"("+paramBuilder+") {");
            bw.newLine();
            bw.write("\t\t"+mapperObjectName+".deleteBy"+methodName+"("+mapperParamBuilder+");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }

    /**
     * 分页查询方法
     * @param bw
     * @param tableInfo
     */
    private static void buildListByPage(BufferedWriter bw,TableInfo tableInfo) throws Exception{
        //employee——>employeePageQueryDTO
        String pageQueryObjectName = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+Constants.pageQueryEntitySuffix;
        //mapper对象的名字
        String mapperObjectName = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"Mapper";
        //mapper对象的分页查询方法名字
        String mapperPageQueryMethodName = Constants.pageQueryMethodNamePrefix+ tableInfo.getBeanName()+Constants.pageQueryMethodNameSuffix;
        //VO对象名字
        String voObjectName = StringUtils.lowercaseFirstLetter(tableInfo.getBeanName())+"VO";
        BuildComment.buildMethodComment(bw,"分页查询",Arrays.asList(pageQueryObjectName),true);
        bw.newLine();
        bw.write("\tpublic PageResult listByPage("+StringUtils.uppercaseFirstLetter(pageQueryObjectName)+" "+pageQueryObjectName+") {");
        bw.newLine();
        //PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        bw.write("\t\tPageHelper.startPage("+pageQueryObjectName+".getPage(), "+pageQueryObjectName+".getPageSize());");
        bw.newLine();
        //Page<EmployeeVO> page = employeeMapper.selectEmployeePage(employeePageQueryDTO);
        bw.write("\t\tPage<"+StringUtils.uppercaseFirstLetter(voObjectName)+"> page = "+mapperObjectName+"."+mapperPageQueryMethodName+"("+pageQueryObjectName+");");
        bw.newLine();
        //return new PageResult(employeeVO.getTotal(),employeeVO.getResult());
        bw.write("\t\treturn new PageResult(page.getTotal(), page.getResult());");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
    }

    /**
     * add方法
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildAdd(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        //entity对象类型
        String className = tableInfo.getBeanName();
        //entity对象名字
        String objectName = StringUtils.lowercaseFirstLetter(className);
        //dto对象名字
        String dtoObjectName = objectName+"DTO";
        BuildComment.buildMethodComment(bw,"添加",Arrays.asList(dtoObjectName),false);
        bw.newLine();
        bw.write("\tpublic void add("+StringUtils.uppercaseFirstLetter(dtoObjectName)+" "+dtoObjectName+") {");
        bw.newLine();
        //Employee employee = new Employee()
        bw.write("\t\t"+className+" "+objectName+" = new "+className+"();");
        bw.newLine();
        //BeanUtils.copyProperties(employeeDTO,employee);
        bw.write("\t\tBeanUtils.copyProperties("+dtoObjectName+", "+objectName+");");
        bw.newLine();
        //employeeMapper.insert(employee);
        bw.write("\t\t"+objectName+"Mapper.insert("+objectName+");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
    }

    /**
     * addBatch方法
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildAddBatch(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        //entity对象类型
        String entityClassName = tableInfo.getBeanName();
        //entity对象名称
        String entityName = StringUtils.lowercaseFirstLetter(entityClassName);
        //dto对象类型
        String dtoClassName = entityClassName+"DTO";
        //dto对象名称
        String dtoName = entityName+"DTO";
        //方法形参:employeeDTOList
        String dtoListName = dtoName+"List";
        //mapper方法形参:employeeList
        String entityListName = entityName+"List";
        StringBuilder methodParam = new StringBuilder();
        methodParam.append("List<").append(dtoClassName).append(">").append(" ").append(dtoListName);
        BuildComment.buildMethodComment(bw,"批量添加",Arrays.asList(dtoListName),false);
        bw.newLine();
        //public void addBatch(List<EmployeeDTO> employeeDTOList){
        bw.write("\tpublic void addBatch("+methodParam+") {");
        bw.newLine();
        //List<Employee> employeeList = new ArrayList<>();
        bw.write("\t\tList<"+entityClassName+"> "+entityListName+" = new ArrayList<>();");
        bw.newLine();
        //for(EmployeeDTO employeeDTO:employeeDTOList){
        bw.write("\t\tfor ("+dtoClassName+" "+dtoName+" : "+dtoListName+") {");
        bw.newLine();
        //Employee employee = new Employee();
        bw.write("\t\t\t"+entityClassName+" "+entityName+" = new "+entityClassName+"();");
        bw.newLine();
        //BeanUtils.copyProperties(employeeDTO,employee);
        bw.write("\t\t\tBeanUtils.copyProperties("+dtoName+", "+entityName+");");
        bw.newLine();
        //employeeList.add(employee);
        bw.write("\t\t\t"+entityListName+".add("+entityName+");");
        bw.newLine();
        bw.write("\t\t}");
        bw.newLine();
        //employeeMapper.insertBatch(employeeList);
        bw.write("\t\t"+entityName+"Mapper.insertBatch("+entityListName+");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
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
     * 构建属性名-属性类型哈希表
     * @param tableInfo
     */
    private static void buildNameToTypeMap(TableInfo tableInfo){
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        for (FieldInfo fieldInfo : fieldList) {
            nameToTypeMap.put(fieldInfo.getPropertyName(),fieldInfo.getJavaType());
        }
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
}
