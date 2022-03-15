package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * controller返回的结果
 * @author zhangshenming
 * @date 2022/3/6 19:54
 * @version 1.0
 */
@Setter
@Getter
public class Result {
    /**
     * 返回响应码
     */
    private int code;
    /**
     * 返回响应码信息
     */
    private String message;

    public Result(int code,String message) {
        this.code = code;
        this.message = message;
    }

}
