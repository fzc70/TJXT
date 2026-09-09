package com.tianji.user.service.impl;

import com.tianji.user.domain.dto.StudentFormDTO;
import com.tianji.user.service.IStudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 外部服务测试显式开启，并由环境变量提供测试数据。
@EnabledIfEnvironmentVariable(named = "RUN_USER_INTEGRATION_TESTS", matches = "true")
@SpringBootTest
class StudentServiceImplTest {

    @Autowired
    private IStudentService studentService;

    @Test
    void saveStudent() {
        for (int i = 1; i < 20; i++) {
            StudentFormDTO dto = new StudentFormDTO();
            dto.setCellPhone((Long.parseLong(System.getenv("TEST_STUDENT_PHONE_BASE")) + i) + "");
            dto.setPassword(System.getenv("TEST_STUDENT_PASSWORD"));
            studentService.saveStudent(dto);
        }
    }
}
