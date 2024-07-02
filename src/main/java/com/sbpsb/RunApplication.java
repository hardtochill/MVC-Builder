package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;
import com.easyjava.utils.ThreadLocalUtils;

import java.util.List;

public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tables = BuildTable.getTables();
        for (TableInfo table : tables) {
            BuildDTO.execute(table);
            BuildPageQueryDTO.execute(table);
            BuildEntity.execute(table);
            BuildVO.execute(table);
            BuildMapper.execute(table);
            BuildMapperXML.execute(table);
            BuildService.execute(table);
            BuildServiceImpl.execute(table);
            BuildController.execute(table);
            //移除ThreadLocal内容，防止OOM
            ThreadLocalUtils.removeMethodNameList();
            BuildWebMvcConfiguration.execute();
        }
        BuildCommon.execute();
    }
}
