package com.jl.project.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class SqlForm {
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 选中的key值
     */
    private String key;
    /**
     * 替换列表
     */
    private List<String> replaceList;
    /**
     * 类型（INSERT、UPDATE）
     */
    private String type;

    /**
     * 后置语句（可作为条件语句、新增标识等）
     */
    private String statement;

    /**
     * 去除List
     */
    private List<String> removeList;

}