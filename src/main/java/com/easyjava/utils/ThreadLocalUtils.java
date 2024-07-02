package com.easyjava.utils;

import java.util.List;

/**
 * 使用ThreadLocal传递方法名，用于构建mapper和mappe_XML
 */
public class ThreadLocalUtils {
    public static ThreadLocal<List<String>>  threadLocal = new ThreadLocal<>();
    public static void setMethodNameList(List<String> list){
        threadLocal.set(list);
    }
    public static List<String> getMethodNameList(){
        return threadLocal.get();
    }
    public static void removeMethodNameList(){
        threadLocal.remove();
    }
}
