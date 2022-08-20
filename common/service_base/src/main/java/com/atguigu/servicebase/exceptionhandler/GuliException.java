package com.atguigu.servicebase.exceptionhandler;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuliException extends RuntimeException {

    @ApiModelProperty("状态码")
    private Integer code;
    private String msg;
}
