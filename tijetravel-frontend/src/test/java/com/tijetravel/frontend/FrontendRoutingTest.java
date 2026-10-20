package com.tijetravel.frontend;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.service.BackendApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FrontendRoutingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackendApiClient apiClient;

    @Test
    void visitante_puedeAccederAInicio() throws Exception {
        when(apiClient.obtenerOrigenes()).thenReturn(Collections.singletonList("Buenos Aires"));
        when(apiClient.obtenerDestinos()).thenReturn(Collections.singletonList("Córdoba"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void visitante_puedeAccederALogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void usuarioAutenticado_alVisitarLogin_esRedirigidoAMiCuenta() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mi-cuenta"));
    }

    @Test
    void visitante_noPuedeAccederAPanelAdminSinLogin() throws Exception {
        mockMvc.perform(get("/admin/balance"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void cliente_noPuedeAccederAPanelAdmin() throws Exception {
        mockMvc.perform(get("/admin/balance"))
                .andExpect(status().isForbidden());
    }
}

