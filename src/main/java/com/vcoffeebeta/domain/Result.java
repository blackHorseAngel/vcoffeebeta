package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @description controller返回的结果
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

    public Result(int code) {
        this.code = code;
    }

}
