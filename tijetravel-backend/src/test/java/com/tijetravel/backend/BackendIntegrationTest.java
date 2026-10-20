package com.tijetravel.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tijetravel.backend.dto.RegistroClienteDTO;
import com.tijetravel.backend.model.Sucursal;
import com.tijetravel.backend.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class BackendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SucursalRepository sucursalRepository;

    @BeforeEach
    void setUp() {
        if (sucursalRepository.findByCodigo("SUC-BUE").isEmpty()) {
            Sucursal s = new Sucursal();
            s.setCodigo("SUC-BUE");
            s.setCiudad("Buenos Aires");
            s.setDireccion("Av. Corrientes 1000");
            s.setTelefono("011-4000-0000");
            sucursalRepository.save(s);
        }
    }

    @Test
    void visitante_puedeConsultarSucursalesPublicamente() throws Exception {
        mockMvc.perform(get("/api/v1/public/sucursales"))
                .andExpect(status().isOk());
    }

    @Test
    void visitante_puedeAutorregistrarseComoCliente() throws Exception {
        RegistroClienteDTO dto = new RegistroClienteDTO();
        dto.setDni("41999888");
        dto.setNombre("Test");
        dto.setApellido("Cliente");
        dto.setDireccion("Calle Falsa 123");
        dto.setEmail("test.cliente@email.com");
        dto.setTelefonoCelular("011-15-1111-2222");
        dto.setSucursalCodigo("SUC-BUE");
        dto.setUsername("testclient");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testclient"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void visitante_noPuedeAccederABalanceSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/v1/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cliente_noPuedeAccederABalance() throws Exception {
        mockMvc.perform(get("/api/v1/balance")
                        .with(httpBasic("testclient", "password123")))
                .andExpect(status().isForbidden());
    }
}

