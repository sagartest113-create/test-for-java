package com.testcraft.demo.dto;

import com.testcraft.demo.service.EchoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EchoRequestTest {

    @Autowired
    private EchoService echoService;

    @Mock
    private EchoService mockEchoService;

    @InjectMocks
    private EchoService injectedEchoService;

    @Test
    public void testEchoRequest() throws Exception {
        // Test valid input
        String input = "Hello, World!";
        when(mockEchoService.echo(input)).thenReturn(input);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new EchoController(echoService)).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/echo")
                .contentType("application/json")
                .content("{\"input\":\"" + input + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(input));

        // Test invalid input
        input = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/echo")
                .contentType("application/json")
                .content("{\"input\":\"" + input + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testEchoRequestService() {
        // Test valid input
        String input = "Hello, World!";
        when(mockEchoService.echo(input)).thenReturn(input);
        injectedEchoService.echo(input);
        org.junit.Assert.assertEquals(input, injectedEchoService.echo(input));

        // Test invalid input
        input = "";
        injectedEchoService.echo(input);
        org.junit.Assert.assertEquals("", injectedEchoService.echo(input));
    }
}

class EchoController {
    private final EchoService echoService;

    public EchoController(EchoService echoService) {
        this.echoService = echoService;
    }

    public String echo(String input) {
        return echoService.echo(input);
    }
}