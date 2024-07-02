package com.easyjava.builder;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.constants.Constants;
import com.easyjava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;


public class BuildEntity {
    private static final Logger logger = LoggerFactory.getLogger(BuildEntity.class);
    /**
     * 根据传入的表，生成该表对应的entity类
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo){
        //创建存放dto象的包
        File folder = new File(Constants.ENTITY_PATH);
        if(!folder.exists()){
            folder.mkdirs();
        }
        //创建该表的dto类
        File file = new File(folder,tableInfo.getBeanName()+".java");
        //在try中创建流对象，会自动关闭，不用手动close()。
        // 如果你尝试使用FileOutputStream或者类似的类来写入数据到一个不存在的文件，Java会尝试创建这个文件，并在这个位置写入数据。
        //但是如果自动生成文件的前提是该文件的目录存在，若文件路径中的目录不存在则会抛出异常，也就是只能创建最底层文件。因此对于多层级文件一般要先执行mkdirs()把目录创建出来
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter opsw = new OutputStreamWriter(ops,"utf8");
            BufferedWriter bf = new BufferedWriter(opsw);
        ){
            //指定该类所在的包
            bf.write("package "+Constants.BASE_PACKAGE+"."+Constants.POJO_PACKAGE+"."+Constants.ENTITY_PACKAGE+";");
            bf.newLine();
            bf.newLine();
            //导入可序列化类
            bf.write("import java.io.Serializable;");
            bf.newLine();
            //根据是否有特殊类型导入对应包
            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bf.write("import java.time.LocalDateTime;");
                bf.newLine();
            }
            if(tableInfo.getHaveBigDecimal()){
                bf.write("import java.math.BigDecimal;");
                bf.newLine();
            }
            bf.newLine();
            BuildComment.buildClassComment(bf,tableInfo.getComment());
            bf.newLine();
            //类声明
            bf.write("public class "+tableInfo.getBeanName()+" implements Serializable {");
            bf.newLine();
            //属性声明
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildComment.buildFieldComment(bf,fieldInfo.getComment());
                bf.newLine();
                bf.write("\tprivate "+fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName()+";");
                bf.newLine();
                bf.newLine();
            }
            //set()、get()方法声明
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                //set
                bf.write("\tpublic void set"+ StringUtils.uppercaseFirstLetter(fieldInfo.getPropertyName()) + "("+ fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName() +")" +" {");
                bf.newLine();
                bf.write("\t\t"+"this."+fieldInfo.getPropertyName()+" = "+fieldInfo.getPropertyName()+";");
                bf.newLine();
                bf.write("\t"+"}");
                bf.newLine();
                bf.newLine();
                //get
                bf.write("\tpublic "+fieldInfo.getJavaType()+" get"+StringUtils.uppercaseFirstLetter(fieldInfo.getPropertyName())+"() {");
                bf.newLine();
                bf.write("\t\treturn "+fieldInfo.getPropertyName()+";");
                bf.newLine();
                bf.write("\t}");
                bf.newLine();
                bf.newLine();
            }
            //toString方法
            bf.write("\t@Override");
            bf.newLine();
            bf.write("\tpublic String toString() {");
            bf.newLine();
            bf.write("\t\treturn ");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\"").append(tableInfo.getBeanName()).append("{").append("\"");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                stringBuilder.append(" + ").append("\"").append(fieldInfo.getPropertyName()).append("=\"").append(" + ").append(fieldInfo.getPropertyName()).append(" + \"[").append(fieldInfo.getComment()).append("]\"").append(" + \",\"");
            }
            stringBuilder.append(" + \"").append("}").append("\"").append(";");
            stringBuilder.replace(stringBuilder.lastIndexOf("+ \",\""),stringBuilder.lastIndexOf("+ \",\"")+6,"");
            bf.write(stringBuilder.toString());
            bf.newLine();
            bf.write("\t}");
            bf.newLine();
            //类的结尾大括号
            bf.write("}");
        }catch (Exception e){
            logger.error("创建Entity对象失败",e);
        }
    }
}
