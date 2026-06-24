package com.opus.erp.common.exception;

import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.result.R;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

/**
 * GlobalExceptionHandler 单元测试
 * 测试全局异常处理器的各种异常处理逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 单元测试")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("处理业务异常 - LOGIN_FAILED")
    void handleBusinessException_loginFailed() {
        // given
        BusinessException exception = new BusinessException(ErrorCode.LOGIN_FAILED);

        // when
        R<?> result = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(2006);
        assertThat(result.getMsg()).isEqualTo("用户名或密码错误");
    }

    @Test
    @DisplayName("处理业务异常 - ACCOUNT_DISABLED")
    void handleBusinessException_accountDisabled() {
        // given
        BusinessException exception = new BusinessException(ErrorCode.ACCOUNT_DISABLED);

        // when
        R<?> result = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(2005);
        assertThat(result.getMsg()).isEqualTo("账号已被禁用");
    }

    @Test
    @DisplayName("处理业务异常 - STOCK_INSUFFICIENT")
    void handleBusinessException_stockInsufficient() {
        // given
        BusinessException exception = new BusinessException(ErrorCode.STOCK_INSUFFICIENT);

        // when
        R<?> result = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(3001);
        assertThat(result.getMsg()).isEqualTo("库存不足");
    }

    @Test
    @DisplayName("处理业务异常 - 自定义消息")
    void handleBusinessException_customMessage() {
        // given
        BusinessException exception = new BusinessException(ErrorCode.NOT_FOUND, "物料不存在");

        // when
        R<?> result = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(1002);
        assertThat(result.getMsg()).isEqualTo("物料不存在");
    }

    @Test
    @DisplayName("处理业务异常 - 自定义错误码")
    void handleBusinessException_customCode() {
        // given
        BusinessException exception = new BusinessException(9999, "系统内部错误");

        // when
        R<?> result = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(9999);
        assertThat(result.getMsg()).isEqualTo("系统内部错误");
    }

    @Test
    @DisplayName("处理非法参数异常")
    void handleIllegalArgumentException() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("参数不能为空");

        // when
        R<?> result = exceptionHandler.handleIllegalArgumentException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(1001);
        assertThat(result.getMsg()).contains("参数不能为空");
    }

    @Test
    @DisplayName("处理未知异常")
    void handleException() {
        // given
        Exception exception = new Exception("未知错误");

        // when
        R<?> result = exceptionHandler.handleException(exception);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(9999);
        assertThat(result.getMsg()).isEqualTo("系统内部错误");
    }
}
