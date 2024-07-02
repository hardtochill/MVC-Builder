package com.easyjava.builder;

import com.easyjava.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WebMvc配置类
 */
public class BuildWebMvcConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(BuildWebMvcConfiguration.class);
    public static void execute(){
        //创建config包
        File configPackage = new File(Constants.CONFIG_PATH);
        if(!configPackage.exists()){
            configPackage.mkdirs();
        }
        //创建WebMvc类
        File file = new File(configPackage,Constants.WebMvcConfiguration_CLASS+".java");
        try(OutputStream ops = new FileOutputStream(file);
            OutputStreamWriter ows = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(ows);
                ){
            //导包
            buildImportPackage(bw);
            BuildComment.buildClassComment(bw,"WebMvc配置类");
            bw.newLine();
            bw.write("@Configuration");
            bw.newLine();
            //类声明
            bw.write("public class "+Constants.WebMvcConfiguration_CLASS+" implements WebMvcConfigurer {");
            bw.newLine();
            //消息转化器方法
            buildExtendMessageConverters(bw);
            bw.newLine();
            //类结束大括号
            bw.write("}");
        }catch (Exception e){
            logger.error("创建WebMvc配置类失败",e);
        }
    }

    /**
     * 导包
     * @param bw
     * @throws IOException
     */
    public static void buildImportPackage(BufferedWriter bw)throws IOException{
        bw.write("package "+Constants.BASE_PACKAGE+"."+Constants.CONFIG_PACKAGE+";");
        bw.newLine();
        bw.newLine();
        //"import com.easyjava.utils.JacksonObjectMapper;\n"
        bw.write("import "+Constants.BASE_PACKAGE+"."+Constants.UTILS_PACKAGE+"."+Constants.JacksonObjectMapper_CLASS+";\n");
        bw.newLine();
        bw.newLine();
        bw.write("import org.springframework.context.annotation.Configuration;");
        bw.newLine();
        bw.write("import org.springframework.http.converter.HttpMessageConverter;");
        bw.newLine();
        bw.write("import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;");
        bw.newLine();
        bw.write("import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;");
        bw.newLine();
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.newLine();
    }

    /**
     * 写入extendMessageConverters方法——消息转换器
     * @param bw
     * @throws IOException
     */
    public static void buildExtendMessageConverters(BufferedWriter bw)throws IOException{
        BuildComment.buildMethodComment(bw,"拓展MVC框架消息转换器",null,false);
        bw.newLine();
        bw.write("\t@Override");
        bw.newLine();
        bw.write("\tpublic void extendMessageConverters(List<HttpMessageConverter<?>> converters) {");
        bw.newLine();
        bw.write("\t\t//创建一个消息转换器对象");
        bw.newLine();
        bw.write("\t\tMappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();");
        bw.newLine();
        bw.write("\t\t//为该对象设置消息转换器");
        bw.newLine();
        bw.write("\t\tconverter.setObjectMapper(new "+ Constants.JacksonObjectMapper_CLASS+"());");
        bw.newLine();
        bw.write("\t\t////将该消息转换器对象加入列表，同时设置为首位，优先使用");
        bw.newLine();
        bw.write("\t\tconverters.add(0, converter);");
        bw.newLine();
        //方法结束大括号
        bw.write("\t}");
    }
}
