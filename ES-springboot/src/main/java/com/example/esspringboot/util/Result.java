package com.example.esspringboot.util;


import lombok.Data;

// 统一返回结果类，所有接口都返回这个格式
@Data
public class Result<T> {
    private boolean success; // 是否成功（true/false）
    private String message;  // 提示信息（成功/失败原因）
    private T data;          // 返回数据（如用户信息、token）
    private int code;        // 状态码（200=成功，500=失败）

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setData(data);
        result.setCode(200);
        return result;
    }
    //两个参数（数据+消息）：成功，自定义消息，带数据
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        result.setCode(200);
        return result;
    }

    // 成功响应（不带数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setData(null);
        result.setCode(500);
        return result;
    }


}