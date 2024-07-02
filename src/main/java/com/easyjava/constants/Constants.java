package com.easyjava.constants;

import com.easyjava.utils.YmlUtils;

public class Constants {
    //是否忽略表的第一个前缀
    public static Boolean ignoreTablePrefix;
    //分页查询对象的后缀名
    public static String pageQueryEntitySuffix;
    //分页查询方法名前缀
    public static String pageQueryMethodNamePrefix;
    //分页查询方法名后缀
    public static String pageQueryMethodNameSuffix;
    //java包下路径
    private static String JAVA_PATH;
    //resources包路径
    private static String RESOURCES_PATH;
    //java包下的基础包路径：com.XXX
    private static String BASE_PATH;
    //java包下的基础包包名
    public static String BASE_PACKAGE;
    //pojo包包名和路径
    public static String POJO_PACKAGE;
    private static String POJO_PATH;
    //entity包包名和路径
    public static String ENTITY_PACKAGE;
    public static String ENTITY_PATH;
    //vo包包名和路径
    public static String VO_PACKAGE;
    public static String VO_PATH;
    //dto包包名和路径
    public static String DTO_PACKAGE;
    public static String DTO_PATH;
    //utils包包名和路径
    public static String UTILS_PACKAGE;
    public static String UTILS_PATH;
    //对象转换器jacksonObjectMapper类类名
    public static String JacksonObjectMapper_CLASS = "JacksonObjectMapper";
    //日期格式
    public static String DEFAULT_DATE_FORMAT;
    public static String DEFAULT_DATE_TIME_FORMAT;
    public static String DEFAULT_TIME_FORMAT;
    //config包包名和路径
    public static String CONFIG_PACKAGE;
    public static String CONFIG_PATH;
    //mapper包包名和路径
    public static String MAPPER_PACKAGE;
    public static String MAPPER_PATH;
    //mapper_xml包包名和路径
    public static String MAPPER_XML_PACKGE;
    public static String MAPPER_XML_PATH;
    //exception包包名和路径
    public static String EXCEPTION_PACKAGE;
    public static String EXCEPTION_PATH;
    //handler包包名和路径
    public static String HANDLER_PACKAGE;
    public static String HANDLER_PATH;
    //result包包名和路径
    public static String RESULT_PACKAGE;
    public static String RESULT_PATH;
    //service包包名和路径
    public static String SERVICE_PACKAGE;
    public static String SERVICE_PATH;
    //serviceImpl包包名、路径、类名后缀
    public static String SERVICE_IMPL_PACKAGE;
    public static String SERVICE_IMPL_PATH;
    public static String SERVICE_IMPL_SUFFIX;
    //controller包包名和路径
    public static String CONTROLLER_PACKAGE;
    public static String CONTROLLER_PATH;
    //WebMvc配置类类名
    public static String WebMvcConfiguration_CLASS = "WebMvcConfiguration";
    //返回结果对象Result类类名
    public static String Result_CLASS = "Result";
    //分页查询返回结果对象Result类类名
    public static String PageResult_CLASS = "PageResult";
    //基础异常类类名
    public static String Exception_CLASS = "BaseException";
    //全局异常处理器类名
    public static String ExceptionHandler_CLASS = "GlobalExceptionHandler";
    static {
        ignoreTablePrefix = Boolean.valueOf(YmlUtils.getValue("ignoreTablePrefix"));
        pageQueryEntitySuffix = YmlUtils.getValue("pageQueryEntitySuffix");
        pageQueryMethodNamePrefix = YmlUtils.getValue("pageQueryMethodNamePrefix");
        pageQueryMethodNameSuffix = YmlUtils.getValue("pageQueryMethodNameSuffix");
        JAVA_PATH = YmlUtils.getValue("path.java");
        RESOURCES_PATH = YmlUtils.getValue("path.resources");
        BASE_PACKAGE = YmlUtils.getValue("package.base");
        POJO_PACKAGE = YmlUtils.getValue("package.po.base");
        ENTITY_PACKAGE = YmlUtils.getValue("package.po.entity");
        VO_PACKAGE = YmlUtils.getValue("package.po.vo");
        DTO_PACKAGE = YmlUtils.getValue("package.po.dto");
        UTILS_PACKAGE = YmlUtils.getValue("package.utils.base");
        DEFAULT_DATE_FORMAT = YmlUtils.getValue("dateformat.date");
        DEFAULT_DATE_TIME_FORMAT = YmlUtils.getValue("dateformat.datetime");
        DEFAULT_TIME_FORMAT = YmlUtils.getValue("dateformat.time");
        CONFIG_PACKAGE = YmlUtils.getValue("package.config.base");
        MAPPER_PACKAGE = YmlUtils.getValue("package.mapper.base");
        MAPPER_XML_PACKGE = YmlUtils.getValue("package.mapper_xml.base");
        EXCEPTION_PACKAGE = YmlUtils.getValue("package.exception.base");
        HANDLER_PACKAGE = YmlUtils.getValue("package.handler.base");
        RESULT_PACKAGE = YmlUtils.getValue("package.result.base");
        SERVICE_PACKAGE = YmlUtils.getValue("package.service.base");
        SERVICE_IMPL_PACKAGE = YmlUtils.getValue("package.service.impl.base");
        SERVICE_IMPL_SUFFIX = YmlUtils.getValue("package.service.impl.suffix");
        CONTROLLER_PACKAGE = YmlUtils.getValue("package.controller.base");
        //将配置文件的包名转绝对路径
        BASE_PATH = JAVA_PATH + "/" + BASE_PACKAGE.replace(".","/");
        POJO_PATH = BASE_PATH + "/" + POJO_PACKAGE;
        ENTITY_PATH = POJO_PATH + "/" + ENTITY_PACKAGE;
        VO_PATH = POJO_PATH + "/" + VO_PACKAGE;
        DTO_PATH = POJO_PATH + "/" + DTO_PACKAGE;
        UTILS_PATH = BASE_PATH + "/" + UTILS_PACKAGE;
        CONFIG_PATH = BASE_PATH + "/" + CONFIG_PACKAGE;
        MAPPER_PATH = BASE_PATH + "/" + MAPPER_PACKAGE;
        MAPPER_XML_PATH = RESOURCES_PATH + "/" + MAPPER_PACKAGE;
        EXCEPTION_PATH = BASE_PATH + "/" + EXCEPTION_PACKAGE;
        HANDLER_PATH = BASE_PATH + "/" + HANDLER_PACKAGE;
        RESULT_PATH = BASE_PATH + "/" + RESULT_PACKAGE;
        SERVICE_PATH = BASE_PATH + "/" + SERVICE_PACKAGE;
        SERVICE_IMPL_PATH = SERVICE_PATH + "/" + SERVICE_IMPL_PACKAGE;
        CONTROLLER_PATH = BASE_PATH + "/" + CONTROLLER_PACKAGE;
    }
    //SQL中的类型
    public final static String[] SQL_DATE_TYPES = new String[]{"date"};
    public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime","timestamp"};
    public final static String[] SQL_DECIMAL_TYPES = new String[]{"decimal","double","float"};
    public final static String[] SQL_STRING_TYPES = new String[]{"char","varchar","text","mediumtext","longtext"};
    public final static String[] SQL_INTEGER_TYPES = new String[]{"int","tinyint"};
    public final static String[] SQL_LONG_TYPES = new String[]{"bigint"};

}
