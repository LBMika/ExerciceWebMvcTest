package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SalleController;
import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.services.SalleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


@WebMvcTest(SalleController.class)
public class SalleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalleService service;

    private String route = "/salles";

    /**
     * Testing method findAll()
     * @throws Exception
     */
    @Test
    public void testFindAllSalle() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Testing method findById when id is not valid
     * @throws Exception
     */
    @Test
    public void testFindOneSalleWhereSalleNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/146546376"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Testing method findById with a valid id
     * @throws Exception
     */
    @Test
    public void testFindOneSalle() throws Exception {
        // DTO creation
        Cinema cinema = new Cinema("111111111", "Pathé");
        SalleDTO salleDTO = new SalleDTO("8888888", 4, 86, cinema);

        // BDD Mock
        BDDMockito.given(service.findByID(salleDTO.getId())).willReturn(Optional.of(salleDTO));

        // Request
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+salleDTO.getId()))
                                        .andExpect(MockMvcResultMatchers.status().isOk())
                                        .andReturn();

        // Assert
        SalleDTO response = new Gson().fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                                SalleDTO.class);
        Assertions.assertEquals(salleDTO, response);
    }

    /**
     * Testing method save()
     * @throws Exception
     */
    @Test
    public void testSaveSalle() throws Exception {
        // DTO creation
        Cinema cinema = new Cinema("111111111", "Pathé");
        SalleDTO salleDTO = new SalleDTO("8888888", 4, 86, cinema);

        // BDD Mock
        BDDMockito.when(service.save(salleDTO)).thenReturn(salleDTO);

        // POST request
        Gson json = new Gson();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .content(json.toJson(salleDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();

        // Assert
        SalleDTO response = json.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                            SalleDTO.class);
        Assertions.assertEquals(salleDTO, response);
    }

    /**
     * Testing method save() for updating
     * @throws Exception
     */
    @Test
    public void testUpdateSalle() throws Exception {
        Gson json = new GsonBuilder().disableHtmlEscaping().create();
        // Create
        Cinema cinema = new Cinema("111111111", "Pathé");
        SalleDTO oldSalleDTO = new SalleDTO("8888888", 4, 86, cinema);

        // Modify
        SalleDTO newSalleDTO = new SalleDTO(oldSalleDTO.getId(), 4, 99999, cinema);

        // Create the BDD mock
        BDDMockito.when(service.save(ArgumentMatchers.any(SalleDTO.class))).thenReturn(newSalleDTO);

        // Send the update request
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .characterEncoding(StandardCharsets.UTF_8)
                                                                        .content(json.toJson(newSalleDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        SalleDTO resultSalleDTO = json.fromJson(response, SalleDTO.class);

        // Assert
        Assertions.assertEquals(newSalleDTO, resultSalleDTO);
        Assertions.assertNotEquals(oldSalleDTO, resultSalleDTO);
    }

    /**
     * Testing delete() method
     * @throws Exception
     */
    @Test
    public void testDeleteSalle() throws Exception {
        Cinema cinema = new Cinema("111111111", "Pathé");
        SalleDTO salleDTO = new SalleDTO("8888888", 4, 86, cinema);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(new Gson().toJson(salleDTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }
}
