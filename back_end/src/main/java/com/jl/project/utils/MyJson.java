package com.jl.project.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jl.project.common.ErrorCode;
import com.jl.project.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MyJson {


    /**
     * 提取所有的key
     *
     * @param jsonStr json字符串
     * @param list    结果list（用来接收结果）
     */
    public static void getAllKey(String jsonStr, List<String> list) {
        JSONObject entries;
        // 是否是一个json对象
        try {
            entries = JSONUtil.parseObj(jsonStr);
        } catch (Exception e) {
            return;
        }
        // 是一个json对象
        entries = JSONUtil.parseObj(jsonStr);
        // 获取它的所有key
        Set<String> strings = entries.keySet();
        for (String string : strings) {
            // 判断是否为json字符串
            String jsonStr1 = entries.get(string).toString();
            boolean typeJSON = JSONUtil.isTypeJSON(jsonStr1);
            // 是json字符串
            if (typeJSON) {
                list.add(string);
                // 判断是否为一个数组
                boolean jsonArray = JSONUtil.isJsonArray(jsonStr1);
                if (jsonArray) { // json数组
                    JSONArray objects = JSONUtil.parseArray(jsonStr1);
                    for (Object object : objects) {
                        getAllKey(object.toString(), list);
                    }
                } else { // json对象
                    getAllKey(jsonStr1, list);
                }
            }
        }
    }

    /**
     * 根据指定的key提取所有value
     *
     * @param jsonStr json字符串
     * @param needKey 指定的key
     * @param list    结果list（用来接收结果）
     */
    public static void allValue(String jsonStr, String needKey, List<String> list) {
        // 判断是否是一个json
        boolean isJson = JSONUtil.isTypeJSON(jsonStr);
        if (!isJson) {
            return;
        }
        JSONObject entries = null;
        try {
            entries = JSONUtil.parseObj(jsonStr);
        } catch (Exception e) {
            return;
        }
        // 获取它的所有key
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            // 获取每个key对应的value
            Object value = entries.get(key);
            // 判断这个是不是想要的key
            if (key.equals(needKey)) {
                if (JSONUtil.isTypeJSONArray(value.toString())) { // 是数组的话，解析一下
                    parseMultArray(value.toString(), list);
                } else {
                    list.add(value.toString());
                }
                return;
            }
            // 判断是否是数组
            if (JSONUtil.isTypeJSONArray(value.toString())) {// 是json数组
                for (Object val : (JSONArray) value) { // 遍历整个数组
                    allValue(val.toString(), needKey, list);
                }
            }
            if (JSONUtil.isTypeJSONObject(value.toString())) { // 是json对象
                allValue(value.toString(), needKey, list);
            }

        }
    }


    /**
     * （带removeList的jsonToMap可以实现该方法的功能）
     * 将解析出来的json字符串列表，转换为map
     * 例如：{type:'xxx',name:'xxx'}这种
     *
     * @param replaceList 可选参数（当没有解析的字符串没有key的时候必须指定）
     * @param resultList  json结果列表
     * @return
     */
    @Deprecated
    public static List<Map<String, String>> jsonToMap(List<String> replaceList, List<String> resultList) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        int index;
        for (Object val : resultList) {
            index = 0;
            map = new LinkedHashMap<>();
            // 判断是否为json对象
            try {
                if (JSONUtil.isTypeJSONArray(val.toString())) {
                    JSONArray entries = JSONUtil.parseArray(val.toString());
                    for (Object entry : entries) {
                        if (replaceList != null && replaceList.size() != 0) {
                            map.put(replaceList.get(index++), entry.toString());
                        } else {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必须给定ReplaceList");
                        }
                    }

                } else {
                    JSONObject entries = JSONUtil.parseObj(val.toString());
                    Set<String> keys = entries.keySet();
                    for (String key : keys) {
                        // 判断是否有给的replaceList
                        if (replaceList != null && replaceList.size() != 0) {
                            map.put(replaceList.get(index++), entries.get(key).toString());
                        } else {
                            map.put(key, entries.get(key).toString());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Json格式有误");
            } catch (IndexOutOfBoundsException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "给定字段数量不匹配");
            }
            list.add(map);
        }
        return list;
    }


    /**
     * 将json结果转为Map集合
     *
     * @param replaceList 替换列表
     * @param resultList  json结果字符串列表
     * @param removeList  需要移除的key列表
     * @return map列表
     */
    public static List<Map<String, String>> jsonToMap(List<String> replaceList, List<String> resultList, List<String> removeList) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        int index;
        for (Object val : resultList) {
            index = 0;
            map = new LinkedHashMap<>();
            // 判断是否为json对象
            try {
                if (JSONUtil.isTypeJSONArray(val.toString())) {
                    JSONArray entries = JSONUtil.parseArray(val.toString());
                    for (Object entry : entries) {
                        if (replaceList != null && replaceList.size() != 0) {
                            map.put(replaceList.get(index++), entry.toString());
                        } else {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必须给定ReplaceList");
                        }
                    }
                } else {
                    JSONObject entries = JSONUtil.parseObj(val.toString());
                    Set<String> keys = entries.keySet();
                    for (String key : keys) {
                        // 排除要去除的key
                        if (removeList != null && removeList.contains(key)) {
                            continue;
                        }
                        // 判断是否有给的replaceList
                        if (replaceList != null && replaceList.size() != 0) {
                            map.put(replaceList.get(index++), entries.get(key).toString());
                        } else {
                            map.put(key, entries.get(key).toString());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Json格式有误");
            } catch (IndexOutOfBoundsException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "给定字段数量不匹配");
            }
            list.add(map);
        }
        return list;
    }


    /**
     * 将map集合转为insert语句
     *
     * @param maps      结果集合
     * @param tableName 表名称
     * @param statement 额外字段
     * @return
     */
    public static List<String> getInsertSql(List<Map<String, String>> maps, String tableName, String statement) {
        if (maps == null || maps.size() == 0) {
            System.out.println("map为空");
            return null;
        }
        // 获取额外的字段map
        Map<String, String> additional = getMap(statement);
        List<String> list = new ArrayList<>();
        Map<String, String> map = maps.get(0);
        Set<String> keySet = map.keySet();
        // 使用list，保证属性的有序(Set是无序的)
        List<String> replaceList = new ArrayList<>(keySet);
        if (additional != null) {
            for (String key : additional.keySet()) {
                // 判断额外的字段不能有重复key
                for (String existKey : replaceList) {
                    if (existKey.equalsIgnoreCase(key)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的额外字段与原字段有重复");
                    }
                }
                replaceList.add(key);
            }
        }
        for (Map<String, String> stringMap : maps) {
            String insetSql = "INSERT INTO " + tableName + replaceList.toString().replaceAll("\\[(.*?)\\]", "($1)") + " VALUES(";
            for (String value : stringMap.values()) {
                insetSql = insetSql + value + ",";
            }
            // 添加额外条件
            if (additional != null) {
                for (String value : additional.values()) {
                    insetSql = insetSql + value + ",";
                }
            }

            // 去除最后一个逗号
            if (insetSql.lastIndexOf(",") == insetSql.length() - 1) {
                insetSql = insetSql.substring(0, insetSql.lastIndexOf(","));
            }
            insetSql = insetSql + ");";
            list.add(insetSql);
        }
        return list;
    }


    /**
     * 将map集合转为insert语句（removeList已经在jsonToMap中做了处理）
     *
     * @param maps       结果集合
     * @param removeList 需要移除的key列表
     * @param tableName  表名称
     * @param statement  额外字段
     * @return
     */
    @Deprecated
    public static List<String> getInsertSql(List<Map<String, String>> maps, List<String> removeList, String tableName, String statement) {
        if (maps == null || maps.size() == 0) {
            System.out.println("map为空");
            return null;
        }
        List<String> list = new ArrayList<>();
        // 获取额外的字段map
        Map<String, String> additional = getMap(statement);
        Map<String, String> map = maps.get(0);
        Set<String> keySet = map.keySet();
        if (additional != null) {
            keySet = new HashSet<>();
            for (String key : map.keySet()) {
                keySet.add(key);
            }
            for (String key : additional.keySet()) {
                // 判断额外的字段不能有重复key
                for (String existKey : keySet) {
                    if (existKey.equalsIgnoreCase(key)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的额外字段与原字段有重复");
                    }
                }
                keySet.add(key);
            }
        }
        // 排除removeList中的值(key)
        keySet = keySet.stream().filter(key -> !removeList.contains(key)).collect(Collectors.toSet());

        for (Map<String, String> stringMap : maps) {
            String insetSql = "INSERT INTO " + tableName + keySet.toString().replaceAll("\\[(.*?)\\]", "($1)") + " VALUES(";
            for (Map.Entry<String, String> value : stringMap.entrySet()) {
                // 排除在removeList中的key对应的value
                if (removeList.contains(value.getKey())) {
                    continue;
                }

                insetSql = insetSql + value.getValue() + ",";

            }
            // 添加额外条件
            if (additional != null) {
                for (String value : additional.values()) {
                    insetSql = insetSql + value + ",";
                }
            }
            // 去除最后一个逗号
            if (insetSql.lastIndexOf(",") == insetSql.length() - 1) {
                insetSql = insetSql.substring(0, insetSql.lastIndexOf(","));
            }
            insetSql = insetSql + ");";
            list.add(insetSql);
        }
        return list;
    }


    /**
     * 将map集合转为update语句
     *
     * @param mapList    结果集合
     * @param tableName  表名称
     * @param conditions 额外字段
     * @return
     */
    public static List<String> getUpdateSql(List<Map<String, String>> mapList, String tableName, String conditions) {
        if (mapList == null || mapList.size() == 0) {
            System.out.println("mapList为空");
            return null;
        }
        List<String> list = new ArrayList<>();
        for (Map<String, String> stringMap : mapList) {
            String updateSql = "UPDATE " + tableName + " SET ";
            for (Map.Entry<String, String> value : stringMap.entrySet()) {
                updateSql = updateSql + value.getKey() + " = " + value.getValue() + ",";
            }
            if (StringUtils.isNotBlank(conditions)) {
                updateSql = updateSql + " " + conditions;
            }

            // 去除最后一个逗号
            if (updateSql.lastIndexOf(",") == updateSql.length() - 1) {
                updateSql = updateSql.substring(0, updateSql.lastIndexOf(","));
            }
            updateSql = updateSql + " ;";
            list.add(updateSql);
        }
        return list;
    }


    /**
     * 将map集合转为update语句（removeList已经在jsonToMap中做了处理）
     *
     * @param mapList    结果集合
     * @param removeList 需要移除的key列表
     * @param tableName  表名称
     * @param conditions 额外字段
     * @return
     */
    @Deprecated
    public static List<String> getUpdateSql(List<Map<String, String>> mapList, List<String> removeList, String tableName, String conditions) {
        if (mapList == null || mapList.size() == 0) {
            System.out.println("mapList为空");
            return null;
        }
        List<String> list = new ArrayList<>();
        for (Map<String, String> stringMap : mapList) {
            String updateSql = "UPDATE " + tableName + " SET ";
            int count = 1;
            for (Map.Entry<String, String> value : stringMap.entrySet()) {
                // 如果key在removeList中，不添加
                if (removeList != null) {
                    if (removeList.contains(value.getKey())) {
                        continue;
                    }
                }
                if (count != stringMap.size()) {
                    updateSql = updateSql + value.getKey() + " = " + value.getValue() + ",";
                } else {
                    updateSql = updateSql + value.getKey() + " = " + value.getValue();
                }
                count++;
            }
            // 去除最后一个逗号
            if (updateSql.lastIndexOf(",") == updateSql.length() - 1) {
                updateSql = updateSql.substring(0, updateSql.lastIndexOf(","));
            }
            if (StringUtils.isNotBlank(conditions)) {
                updateSql = updateSql + " " + conditions;
            }
            updateSql = updateSql + " ;";
            list.add(updateSql);
        }
        return list;
    }


    /**
     * 解析多维数组（将多维数组打平）
     *
     * @param jsonStr json字符串
     * @param list    结果列表（用来存最终的结果）
     */
    private static void parseMultArray(String jsonStr, List<String> list) {
        // 一维数组
        if (JSONUtil.isTypeJSONArray(jsonStr) && !JSONUtil.isTypeJSON(JSONUtil.parseArray(jsonStr).get(0).toString())) { // 是一维数组
            list.add(jsonStr);
        } else if (JSONUtil.isTypeJSONArray(jsonStr) && JSONUtil.isTypeJSONObject(JSONUtil.parseArray(jsonStr).get(0).toString())) { // 是数组套对象
            for (Object o : JSONUtil.parseArray(jsonStr)) {
                list.add(o.toString());
            }
        } else { // 是一个对象
            for (Object obj : JSONUtil.parseArray(jsonStr)) {
                parseMultArray(obj.toString(), list);
            }
        }
    }


    /**
     * 处理INSERT传的额外字段参数
     * 格式必须是：（x=1,y=2）这种
     *
     * @param str 额外字段字符串
     * @return
     */
    private static Map<String, String> getMap(String str) {
        if (StringUtils.isBlank(str) || str.length() == 0) {
            return null;
        }
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        String[] entry = str.split(",");
        if (entry.length != 0) {
            for (String s : entry) {
                String[] entryArr = s.split("=");
                if (entryArr.length != 2) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "INSERT额外字段格式错误");
                }
                map.put(entryArr[0], entryArr[1]);
            }
            return map;
        }
        return null;

    }


    /**
     * 获取指定key下的子key
     *
     * @param jsonStr json字符串
     * @param needKey 指定的key
     * @param list    存结果的list
     */
    public static void getChildKey(String jsonStr, String needKey, List<String> list) {
        JSONObject entries;
        // 是否是一个json对象
        try {
            entries = JSONUtil.parseObj(jsonStr);
        } catch (Exception e) {
            return;
        }
        // 是一个json对象
        entries = JSONUtil.parseObj(jsonStr);
        // 获取它的所有key
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            // 判断是否是想要的key
            // 判断value是否为json字符串
            String value = entries.get(key).toString();
            boolean isJSON = JSONUtil.isTypeJSON(value);
            if (key.equals(needKey)) {
                // 将子属性添加到list中
                if (isJSON) {
                    // 判断是否为一个数组
                    boolean jsonArray = JSONUtil.isTypeJSONArray(value);
                    if (jsonArray) { // json数组
                        // 将数组打平
                        ArrayList<String> resList = new ArrayList<>();
                        parseMultArray(value, resList);
                        if (JSONUtil.isTypeJSON(resList.get(0))) {
                            // 判断是否为数组(这里判断的是数组套数组的情况)
                            if (JSONUtil.isTypeJSONArray(resList.get(0))) {
                                return;
                            } else { // 数组里边是对象的情况
                                JSONObject entries1 = JSONUtil.parseObj(resList.get(0));
                                list.addAll(entries1.keySet());
                            }
                        }
                    } else { // json对象
                        // 将所有key加入list
                        JSONObject entries1 = JSONUtil.parseObj(value);
                        list.addAll(entries1.keySet());
                    }
                    return;
                }
            } else { // 不是需要的key
                // 是json字符串
                if (isJSON) {
                    // 判断是否为一个数组
                    boolean jsonArray = JSONUtil.isJsonArray(value);
                    if (jsonArray) { // json数组
                        JSONArray objects = JSONUtil.parseArray(value);
                        for (Object object : objects) {
                            getChildKey(object.toString(), needKey, list);
                        }
                    } else { // json对象
                        getChildKey(value, needKey, list);
                    }
                }
            }

        }
    }

}
