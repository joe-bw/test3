/*
 * Create by jhong on 2022. 1. 19.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data

/** ## StateFlow 를 통한 Return callback 정의
 *
 * - Loading - 데이터 요청 처리 상태 callback
 * - Success - 데이터 처리 성공시 상태값<T> 을 callback
 * - Error - 데이터 처리 실패시 상태값<ErrorCode> 을 callback
 *
 * */
sealed class ResultState<T>(
    val data: T? = null,
    val errorCode: Int? = null
) {
    class Loading<T>(data: T? = null) : ResultState<T>(data)
    class Success<T>(data: T) : ResultState<T>(data)
    class Error<T>(errorCode: Int) : ResultState<T>(null, errorCode)
}
