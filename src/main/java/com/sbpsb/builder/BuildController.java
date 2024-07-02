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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);
    //属性名-属性类型
    private static Map<String,String> nameToTypeMap = new HashMap<>();
    public static void execute(TableInfo tableInfo){
        File controllerPackage = new File(Constants.CONTROLLER_PATH);
        if(!controllerPackage.exists()){
            controllerPackage.mkdirs();
        }
        File file = new File(controllerPackage,tableInfo.getBeanName()+"Controller.java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter opsw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsw);
        ){
            String className = tableInfo.getBeanName();
            buildNameToTypeMap(tableInfo);
            //导包
            bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.CONTROLLER_PACKAGE+";");
            bw.newLine();
            bw.newLine();
            String basePojoPackage = Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE;
            bw.write("import "+basePojoPackage+"."+Constants.ENTITY_PACKAGE+"."+className+";");
            bw.newLine();
            bw.write("import "+basePojoPackage+"."+Constants.DTO_PACKAGE+"."+className+"DTO;");
            bw.newLine();
            bw.write("import "+basePojoPackage+"."+Constants.DTO_PACKAGE+"."+className+Constants.pageQueryEntitySuffix+";");
            bw.newLine();
            bw.write("import "+basePojoPackage+"."+Constants.VO_PACKAGE+"."+className+"VO;");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.RESULT_PACKAGE+"."+Constants.Result_CLASS+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.RESULT_PACKAGE+"."+Constants.PageResult_CLASS+";");
            bw.newLine();
            bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.SERVICE_PACKAGE+"."+className+"Service;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.*;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            //注解
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\"/"+ StringUtils.lowercaseFirstLetter(className+"\")"));
            bw.newLine();
            //类声明
            bw.write("public class "+className+"Controller {");
            bw.newLine();
            //依赖注入
            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\tprivate "+className+"Service "+StringUtils.lowercaseFirstLetter(className)+"Service;");
            bw.newLine();
            bw.newLine();
            //取出ThreadLocal中的方法名列表,方法名列表形如Id、IdAndCode，根据"And"切分再转小写就能得到主键属性
            List<String> methodNameList = ThreadLocalUtils.getMethodNameList();
            //构建get方法
            buildGet(bw,methodNameList,tableInfo);
            //构建update方法
            buildUpdate(bw,methodNameList,tableInfo);
            //构建delete方法
            buildDelete(bw,methodNameList,tableInfo);
            //分页查询方法
            buildListByPage(bw,tableInfo);
            //add方法
            buildSave(bw,tableInfo);
            //addBatch方法
            buildSaveBatch(bw,tableInfo);
            //类结尾大括号
            bw.write("}");
        }catch (Exception e){
            logger.error("构建controller失败",e);
        }
    }
    /**
     * 编写get方法
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildGet(BufferedWriter bw, List<String> methodNameList, TableInfo tableInfo)throws IOException{
        String className = tableInfo.getBeanName();
        String voClassName = className+"VO";
        String voObjectName = StringUtils.lowercaseFirstLetter(voClassName);
        for (String methodName : methodNameList) {
            //参数列表：IdAndSkuType——>id,skuType
            List<String> keyList = transferNameToParam(methodName);
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,0),keyList,true);
            bw.newLine();
            //GetMapping("/getById")
            bw.write("\t@GetMapping(\"/getBy"+methodName+"\")");
            bw.newLine();
            //构建方法形参表和service方法的实参
            StringBuilder paramBuilder = new StringBuilder();
            StringBuilder serviceParamBuilder = new StringBuilder();
            for(int i=0;i<keyList.size();i++){
                String param = keyList.get(i);
                paramBuilder.append(nameToTypeMap.get(param)).append(" ").append(param);
                serviceParamBuilder.append(param);
                if(i<keyList.size()-1){
                    paramBuilder.append(", ");
                    serviceParamBuilder.append(", ");
                }
            }
            //public Result<EmployeeVO> getById(Long id){
            bw.write("\tpublic Result<"+voClassName+"> getBy"+methodName+"("+paramBuilder+") {");
            bw.newLine();
            //EmployeeVO employeeVO = employeeService.findById(id);
            bw.write("\t\t"+voClassName+" "+voObjectName+" = "+StringUtils.lowercaseFirstLetter(className)+"Service.findBy"+methodName+"("+serviceParamBuilder+");");
            bw.newLine();
            //return Result.success(employeeVO);
            bw.write("\t\treturn Result.success("+voObjectName+");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }

    /**
     * 编写update方法
     * @param bw
     * @param methodNameList
     * @param tableInfo
     */
    private static void buildUpdate(BufferedWriter bw,List<String> methodNameList,TableInfo tableInfo)throws IOException{
        String className = tableInfo.getBeanName();
        String dtoClassName = className+"DTO";
        String dtoObjectName = StringUtils.lowercaseFirstLetter(dtoClassName);
        for (String methodName : methodNameList) {
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,1),Arrays.asList(dtoObjectName),false);
            bw.newLine();
            //GetMapping("/getById")
            bw.write("\t@PutMapping(\"/updateBy"+methodName+"\")");
            bw.newLine();
            //public Result updateById(@RequestBody EmployeeDTO employeeDTO){
            bw.write("\tpublic Result updateBy"+methodName+"(@RequestBody "+dtoClassName+" "+dtoObjectName+") {");
            bw.newLine();
            //employeeService.updateById(employeeDTO);
            bw.write("\t\t"+StringUtils.lowercaseFirstLetter(className)+"Service.updateBy"+methodName+"("+dtoObjectName+");");
            bw.newLine();
            //return Result.success();
            bw.write("\t\treturn Result.success();");
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
        String className = tableInfo.getBeanName();
        for (String methodName : methodNameList) {
            //参数列表：IdAndSkuType——>id,skuType
            List<String> keyList = transferNameToParam(methodName);
            BuildComment.buildMethodComment(bw,transferNameToComment(methodName,2),keyList,false);
            //GetMapping("/getById")
            bw.write("\t@DeleteMapping(\"/deleteBy"+methodName+"\")");
            bw.newLine();
            //构建方法形参表和service方法的实参
            StringBuilder paramBuilder = new StringBuilder();
            StringBuilder serviceParamBuilder = new StringBuilder();
            for(int i=0;i<keyList.size();i++){
                String param = keyList.get(i);
                paramBuilder.append(nameToTypeMap.get(param)).append(" ").append(param);
                serviceParamBuilder.append(param);
                if(i<keyList.size()-1){
                    paramBuilder.append(", ");
                    serviceParamBuilder.append(", ");
                }
            }
            //public Result deleteById(Long id){
            bw.write("\tpublic Result deleteBy"+methodName+"("+paramBuilder+") {");
            bw.newLine();
            //employeeService.deleteById(id);
            bw.write("\t\t"+StringUtils.lowercaseFirstLetter(className)+"Service.deleteBy"+methodName+"("+serviceParamBuilder+");");
            bw.newLine();
            //return Result.success();
            bw.write("\t\treturn Result.success();");
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
        //entity的类型和名字
        String entityClass = tableInfo.getBeanName();
        String entityObject = StringUtils.lowercaseFirstLetter(entityClass);
        //分页查询dto的类型和名字
        String pageQueryDTOClass = entityClass+Constants.pageQueryEntitySuffix;
        String pageQueryDTO = StringUtils.lowercaseFirstLetter(pageQueryDTOClass);
        //vo的类型和名字
        String voClass = entityClass+"VO";
        String voObject = StringUtils.lowercaseFirstLetter(voClass);
        BuildComment.buildMethodComment(bw,"分页查询",Arrays.asList(pageQueryDTO),true);
        bw.newLine();
        //@GetMapping("/listByPage")
        bw.write("\t@GetMapping(\"/listByPage\")");
        bw.newLine();
        //public Result<PageResult> listByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        bw.write("\tpublic Result<PageResult> listByPage("+pageQueryDTOClass+" "+pageQueryDTO+") {");
        bw.newLine();
        //PageResult pageResult = employeeService.listByPage(employeePageQueryDTO);
        bw.write("\t\tPageResult pageResult = "+entityObject+"Service.listByPage("+pageQueryDTO+");");
        bw.newLine();
        //return Result.success(pageResult);
        bw.write("\t\treturn Result.success(pageResult);");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
    }

    /**
     * save方法
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildSave(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        String className = tableInfo.getBeanName();
        String dtoClassName = className+"DTO";
        String dtoObjectName = StringUtils.lowercaseFirstLetter(dtoClassName);
        BuildComment.buildMethodComment(bw,"新增",Arrays.asList(dtoObjectName),false);
        bw.newLine();
        //@PostMapping("/save")
        bw.write("\t@PostMapping(\"/save\")");
        bw.newLine();
        //public Result save(@RequestBody EmployeeDTO employeeDTO) {
        bw.write("\tpublic Result save(@RequestBody "+dtoClassName+" "+dtoObjectName+") {");
        bw.newLine();
        //employeeService.add(employeeDTO);
        bw.write("\t\t"+StringUtils.lowercaseFirstLetter(className)+"Service.add("+dtoObjectName+");");
        bw.newLine();
        //return Result.success();
        bw.write("\t\treturn Result.success();");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
    }

    /**
     * saveBatch方法
     * @param bw
     * @param tableInfo
     * @throws IOException
     */
    private static void buildSaveBatch(BufferedWriter bw,TableInfo tableInfo) throws IOException{
        String className = tableInfo.getBeanName();
        String dtoClassName = className+"DTO";
        String dtoObjectName = StringUtils.lowercaseFirstLetter(dtoClassName);
        BuildComment.buildMethodComment(bw,"批量新增",Arrays.asList(dtoObjectName+"List"),false);
        bw.newLine();
        //@PostMapping("/saveBatch")
        bw.write("\t@PostMapping(\"/saveBatch\")");
        bw.newLine();
        //public Result save(@RequestBody List<EmployeeDTO> employeeDTOList) {
        bw.write("\tpublic Result save(@RequestBody List<"+dtoClassName+"> "+dtoObjectName+"List) {");
        bw.newLine();
        //employeeService.addBatch(employeeDTOList);
        bw.write("\t\t"+StringUtils.lowercaseFirstLetter(className)+"Service.addBatch("+dtoObjectName+"List);");
        bw.newLine();
        //return Result.success();
        bw.write("\t\treturn Result.success();");
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
