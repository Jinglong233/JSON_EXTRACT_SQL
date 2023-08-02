package com.jl.project.service;

import com.jl.project.model.dto.InsertDTO;

import java.util.List;

public interface JsonService {
    /**
     * 获取value
     *
     * @param insertDTO
     * @return
     */
    List<String> getArrayVal(InsertDTO insertDTO);


    /**
     * 获取json中所有的key（不包括最内层的key）
     *
     * @param jsonStr
     * @return
     */
    List<String> getKeys(String jsonStr);


    /**
     * 获取key下的子key
     *
     * @param jsonStr json字符串
     * @param needKey 父key
     * @return
     */
    List<String> getChildKeys(String jsonStr, String needKey);
}
