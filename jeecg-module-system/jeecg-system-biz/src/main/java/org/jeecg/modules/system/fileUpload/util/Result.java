//package org.jeecg.modules.system.fileUpload.util;
//
//import lombok.Data;
//
//@Data
//public class Result {
//
//    private Integer code;
//    private String msg;
//    private Object data;
//
//    public Result(Integer code, String msg) {
//        this.code = code;
//        this.msg = msg;
//    }
//
//    public Result(Integer code, String msg, Object data) {
//        this.code = code;
//        this.msg = msg;
//        this.data = data;
//    }
//
//    public static Result success() {
//        return Result.success(MsgConstant.HANDLE_SUCCESS);
//    }
//
//    public static Result success(String msg) {
//        return Result.success(msg, null);
//    }
//
//    public static Result success(Object data) {
//        return Result.success(MsgConstant.HANDLE_SUCCESS, data);
//    }
//
//    public static Result success(String msg, Object data) {
//        return new Result(HttpStatus.SUCCESS, msg, data);
//    }
//
//    public static Result error() {
//        return Result.error(MsgConstant.HANDLE_FAIL);
//    }
//
//    public static Result error(String msg) {
//        return Result.error(msg, null);
//    }
//
//    public static Result error(Object data) {
//        return Result.error(MsgConstant.HANDLE_FAIL, data);
//    }
//
//    public static Result error(String msg, Object data) {
//        return new Result(HttpStatus.ERROR, msg, data);
//    }
//}
