package com.jl.project.controller;

import com.jl.project.common.BaseResponse;
import com.jl.project.common.ResultUtils;
import com.jl.project.model.dto.InsertDTO;
import com.jl.project.service.JsonService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/json")
public class JsonController {
    @Resource
    private JsonService jsonService;

    @PostMapping("/getValue")
    public BaseResponse<List<String>> getArrayVal(@RequestBody InsertDTO insertDTO) {
        List<String> string = jsonService.getArrayVal(insertDTO);
        return ResultUtils.success(string);
    }

    @PostMapping("/getKeys")
    public BaseResponse<List<String>> getKeys(@RequestBody String jsonStr) {
        List<String> string = jsonService.getKeys(jsonStr);
        return ResultUtils.success(string);
    }

    @PostMapping("/getChildKeys/{needKey}")
    public BaseResponse<List<String>> getChildKeys(@RequestBody String jsonStr, @PathVariable(value = "needKey") String needKey) {
        List<String> string = jsonService.getChildKeys(jsonStr, needKey);
        return ResultUtils.success(string);
    }

}
