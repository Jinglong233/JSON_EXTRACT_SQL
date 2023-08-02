package com.jl.project.service.impl;

import com.jl.project.common.ErrorCode;
import com.jl.project.exception.BusinessException;
import com.jl.project.model.dto.InsertDTO;
import com.jl.project.model.entity.SqlForm;
import com.jl.project.service.JsonService;
import com.jl.project.utils.MyJson;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JsonServiceImpl implements JsonService {

    private List<String> array;

    @Override
    public List<String> getArrayVal(InsertDTO insertDTO) {
        List<String> strings = new ArrayList<>();
        List<String> test = null;
        try {
            SqlForm sqlForm = insertDTO.getSqlForm();
            String jsonStr = insertDTO.getJsonStr();
            if (sqlForm == null || jsonStr == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少必要参数");
            }
            MyJson.allValue(jsonStr, sqlForm.getKey(), strings);
            List<Map<String, String>> maps;
            List<String> removeList = sqlForm.getRemoveList();
            maps = MyJson.jsonToMap(sqlForm.getReplaceList(), strings, removeList);
            if ("UPDATE".equals(insertDTO.getSqlForm().getType())) {
                test = MyJson.getUpdateSql(maps, sqlForm.getTableName(), sqlForm.getStatement());
            } else if ("INSERT".equals(insertDTO.getSqlForm().getType())) {
                test = MyJson.getInsertSql(maps, sqlForm.getTableName(), sqlForm.getStatement());
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        }
        return test;
    }

    @Override
    public List<String> getKeys(String jsonStr) {
        ArrayList<String> list = new ArrayList<>();
        MyJson.getAllKey(jsonStr, list);
        return list;
    }

    @Override
    public List<String> getChildKeys(String jsonStr, String needKey) {
        List<String> list = new ArrayList<>();
        MyJson.getChildKey(jsonStr, needKey, list);
        // 去重
        list = list.stream().distinct().collect(Collectors.toList());
        return list;
    }


}
