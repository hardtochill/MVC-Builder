package com.easyjava.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YmlUtils {
    //日志对象
    private static final Logger logger = LoggerFactory.getLogger(YmlUtils.class);
    //用于读取yml配置文件
    private static Yaml yaml = new Yaml();
    //存储读到的配置信息
    private static Map<String,Object> map = new ConcurrentHashMap<>();

    static{
        InputStream ips = null;
        try{
            //获取输入流
            ips = YmlUtils.class.getClassLoader().getResourceAsStream("application.yml");
            //载入到map中
            map = yaml.loadAs(ips,Map.class);
        }catch (Exception e){
            logger.error("读取配置文件异常",e);
        }finally {
            //关闭输入流
            if(ips!=null){
                try {
                    ips.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 读取yml文件的配置信息——对于多层配置，用.连接，例如db.driver.name
     * @param key
     * @return
     */
    public static String getValue(String key){
        //对key进行切分,split()规定按"."切分必须写成"\\."的形式
        List<String> keys = Arrays.asList(key.split("\\."));
        if(keys.size()==1){//单层key
            return String.valueOf(map.get(key));
        }else if(keys.size()==2){//双层key，对取出的Object对象强转成Map
            return String.valueOf(((Map<String,Object>)map.get(keys.get(0))).get(keys.get(1)));
        }
        //逐层剥离
        Map<String,Object> tempMap = map;
        for(int i=0;i<keys.size()-1;i++){
            tempMap = (Map<String,Object>)tempMap.get(keys.get(i));
        }
        //返回最终key对应的value
        return String.valueOf(tempMap.get(keys.get(keys.size()-1)));
    }
}
