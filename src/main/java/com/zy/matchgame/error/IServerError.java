package com.zy.matchgame.error;

/**
 * <p>错误码定义的统一接口，用于统一管理服务中出现的错误码定义</p>
 */
public interface IServerError {

    /**
     * 返回错误码
     *
     * @return 错误码
     */
    Integer getErrorCode();

    /**
     * 返回错误详细信息
     *
     * @return 错误详细信息
     */
    String getErrorDesc();
}
