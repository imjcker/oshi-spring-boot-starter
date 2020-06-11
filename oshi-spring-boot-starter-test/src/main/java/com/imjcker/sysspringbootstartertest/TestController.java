package com.imjcker.sysspringbootstartertest;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("测试swagger")
@RestController
public class TestController {

    @ApiOperation("测试aaa")
    @GetMapping("/aaa")
    public String index() {
        return "aaa";
    }

    @PostMapping("/bbb")
    public String index2() {
        return "bbb";
    }
}
