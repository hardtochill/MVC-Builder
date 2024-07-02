package com.easyjava.builder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class BuildComment {
    /**
     * 给类加注释
     * @param bw
     * @param comment
     */
    public static void buildClassComment(BufferedWriter bw,String comment) throws IOException {
        bw.write("/**");
        bw.newLine();
        bw.write(" * "+comment);
        bw.newLine();
        bw.write(" */");
    }

    /**
     * 给属性加注释
     * @param bw
     * @param comment
     */
    public static void buildFieldComment(BufferedWriter bw,String comment) throws IOException{
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * "+ (comment!=null?comment:""));
        bw.newLine();
        bw.write("\t */");
    }

    /**
     * 给方法加注释
     * @param bw
     * @param paramList
     * @param returnType
     * @throws IOException
     */
    public static void buildMethodComment(BufferedWriter bw, String comment,List<String> paramList, Boolean returnType) throws IOException{
        bw.write("\t/**");
        bw.newLine();
        if(comment!=null){
            bw.write("\t * "+comment);
            bw.newLine();
        }
        if(paramList!=null&&paramList.size()>0){
            bw.write("\t *");
            bw.newLine();
            for (String param : paramList) {
                bw.write("\t * @param "+param);
                bw.newLine();
            }
        }
        if(returnType==true){
            bw.write("\t * @return");
            bw.newLine();
        }
        bw.write("\t */");
    }
}
