package com.easyjava.builder;

import com.easyjava.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 构建通用类
 */
public class BuildCommon {
    private static final Logger logger = LoggerFactory.getLogger(BuildDTO.class);
    private static final String templateName = "template";

    public static void execute(){
        // 创建消息转化器对象
        buildTarget(Constants.JacksonObjectMapper_CLASS,Constants.UTILS_PATH,Constants.UTILS_PACKAGE);
        // 创建Result对象
        buildTarget(Constants.Result_CLASS,Constants.RESULT_PATH,Constants.RESULT_PACKAGE);
        // 创建PageResult对象
        buildTarget(Constants.PageResult_CLASS,Constants.RESULT_PATH,Constants.RESULT_PACKAGE);
        //创建BaseException对象
        buildTarget(Constants.Exception_CLASS,Constants.EXCEPTION_PATH,Constants.EXCEPTION_PACKAGE);
        //创建ExceptionHandler对象
        buildTarget(Constants.ExceptionHandler_CLASS,Constants.HANDLER_PATH,Constants.HANDLER_PACKAGE);
    }

    /**
     * 读取模板文件，构建通用类
     * @param targetName
     */
    private static void buildTarget(String targetName,String basePath,String basePackage){
        //创建utils包
        File parentPackage = new File(basePath);
        if(!parentPackage.exists()){
            parentPackage.mkdirs();
        }
        File targetFile = new File(parentPackage,targetName+".java");
        // 从resources/template中读取txt模板文件，写入目标文件
        try(InputStream resourceIps =  BuildCommon.class.getClassLoader().getResourceAsStream(templateName+"/"+targetName+".txt");
            InputStreamReader resourceIpsR =  new InputStreamReader(resourceIps);
            BufferedReader bufferedReader = new BufferedReader(resourceIpsR);
            OutputStream targetOps = new FileOutputStream(targetFile);
            OutputStreamWriter targetOpsW = new OutputStreamWriter(targetOps);
            BufferedWriter bufferedWriter = new BufferedWriter(targetOpsW);
        ) {
            //包路径声明
            bufferedWriter.write("package "+Constants.BASE_PACKAGE+"."+basePackage+";");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            //如果是ExceptionHandler类，还需要额外导入Result类和BaseException类
            if(targetName.equals(Constants.ExceptionHandler_CLASS)){
                bufferedWriter.write("import "+Constants.BASE_PACKAGE+"."+Constants.EXCEPTION_PACKAGE+"."+Constants.Exception_CLASS+";");
                bufferedWriter.newLine();
                bufferedWriter.write("import "+Constants.BASE_PACKAGE+"."+Constants.RESULT_PACKAGE+"."+Constants.Result_CLASS+";");
                bufferedWriter.newLine();
            }
            String line;
            /**
             * bufferedReader.readLine()在读到空行时会返回空字符""
             * bufferedReader.readLine()在读到文件结束时会返回null。因此readLine()可以成功读取目标文件的空行
             * 但在写入时空行是写入""而不是/r/n。因此需要再newLine()才能表示空行
             */
            while((line = bufferedReader.readLine())!=null){
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }catch (Exception e){
            logger.error("创建"+targetName+"失败",e);
        }
    }
}
