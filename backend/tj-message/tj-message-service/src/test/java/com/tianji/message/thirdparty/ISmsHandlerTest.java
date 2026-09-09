package com.tianji.message.thirdparty;

import com.tianji.api.dto.sms.SmsInfoDTO;
import com.tianji.message.domain.enums.SmsTemplate;
import com.tianji.message.service.ISmsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

// 外部服务测试显式开启，并由环境变量提供测试数据。
@EnabledIfEnvironmentVariable(named = "RUN_SMS_INTEGRATION_TESTS", matches = "true")
@SpringBootTest
class ISmsHandlerTest {

    @Autowired
    private ISmsService smsService;

    @Test
    void send() {
        SmsInfoDTO dto = new SmsInfoDTO();
        dto.setPhones(List.of(System.getenv("SMS_TEST_PHONE")));
        dto.setTemplateCode(SmsTemplate.VERIFY_CODE.name());
        HashMap<String, String> params = new HashMap<>(1);
        params.put("code", "518518");
        dto.setTemplateParams(params);
        smsService.sendMessage(dto);
    }
}
