package test.java.com.example.nac;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.java.com.example.nac.controller.v1.NACUserController;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static java.nio.file.Paths.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
class NacApplicationTests {

    @Test
    void contextLoads() throws Exception {

    }
}
