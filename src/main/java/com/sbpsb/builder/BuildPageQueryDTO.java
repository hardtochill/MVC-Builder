package com.easyjava.builder;

import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BuildPageQueryDTO {
    private static final Logger logger = LoggerFactory.getLogger(BuildPageQueryDTO.class);

    /**
     * 构建分页查询的pojo
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo){
        //主函数先执行了BuildDTO已经创建好dto包，因此PageQueryDTO对象直接生成到具体路径即可
        File file = new File(Constants.DTO_PATH+"/"+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+".java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(osw);
        ){
            //指定包
            bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.DTO_PACKAGE+";");
            bw.newLine();
            bw.newLine();
            //类声明
            bw.write("public class "+tableInfo.getBeanName()+Constants.pageQueryEntitySuffix+" extends "+tableInfo.getBeanName()+"DTO {");
            bw.newLine();
            //页数
            bw.write("\tprivate int page;");
            bw.newLine();
            bw.newLine();
            //页长
            bw.write("\tprivate int pageSize;");
            bw.newLine();
            bw.newLine();
            //set()、get()方法
            bw.write("\tpublic void setPage(int page) {");
            bw.newLine();
            bw.write("\t\tthis.page = page;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic int getPage() {");
            bw.newLine();
            bw.write("\t\treturn page;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic void setPageSize(int pageSize) {");
            bw.newLine();
            bw.write("\t\tthis.pageSize = pageSize;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic int getPageSize() {");
            bw.newLine();
            bw.write("\t\treturn pageSize;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            //类结尾大括号
            bw.write("}");
        }catch (Exception e){
            logger.error("构建PageQueryDTO对象失败",e);
        }
    }
}
