package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.services.CinemaService;
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


@WebMvcTest(CinemaController.class)
public class CinemaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CinemaService service;

    private String route = "/cinemas";

    /**
     * Testing method findAll()
     * @throws Exception
     */
    @Test
    public void testFindAllCinemas() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route))
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Testing method findById when id is not valid
     * @throws Exception
     */
    @Test
    public void testFindOneCinemaWhereCinemaNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/1000000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Testing method findById with a valid id
     * @throws Exception
     */
    @Test
    public void testFindOneCinema() throws Exception {
        CinemaDTO cinemaDTO = new CinemaDTO("11111111111", "Luxor");
        BDDMockito.given(service.findByID(cinemaDTO.getId())).willReturn(Optional.of(cinemaDTO));
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+cinemaDTO.getId()))
                                        .andExpect(MockMvcResultMatchers.status().isOk())
                                        .andReturn();
        Assertions.assertEquals(cinemaDTO,
                                new Gson().fromJson(result.getResponse().getContentAsString(), CinemaDTO.class));
    }

    /**
     * Testing method save()
     * @throws Exception
     */
    @Test
    public void testSaveCinema() throws Exception {
        CinemaDTO cinemaDTO = new CinemaDTO("11111111111", "Luxor");
        BDDMockito.when(service.save(cinemaDTO)).thenReturn(cinemaDTO);
        Gson json = new Gson();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .content(json.toJson(cinemaDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();

        CinemaDTO response = json.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                            CinemaDTO.class);
        Assertions.assertEquals(cinemaDTO, response);
    }

    /**
     * Testing method save() for updating
     * @throws Exception
     */
    @Test
    public void testUpdateCinema() throws Exception {
        Gson json = new GsonBuilder().disableHtmlEscaping().create();
        // Create a cinema
        CinemaDTO oldCinemaDTO = new CinemaDTO("11111111111", "Luxor");

        // Modify the cinema
        CinemaDTO newCinemaDTO = new CinemaDTO(oldCinemaDTO.getId(), "Pathé");

        // Create the BDD mock
        BDDMockito.when(service.save(ArgumentMatchers.any(CinemaDTO.class))).thenReturn(newCinemaDTO);

        // Send the update request
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .characterEncoding(StandardCharsets.UTF_8)
                                                                        .content(json.toJson(newCinemaDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CinemaDTO resultCinemaDTO = json.fromJson(response, CinemaDTO.class);

        // Assert
        Assertions.assertEquals(newCinemaDTO, resultCinemaDTO);
        Assertions.assertNotEquals(oldCinemaDTO, resultCinemaDTO);
    }

    /**
     * Testing delete() method
     * @throws Exception
     */
    @Test
    public void testDeleteCinema() throws Exception {
        CinemaDTO cinemaDTO = new CinemaDTO("11111111111", "Luxor");
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(new Gson().toJson(cinemaDTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }
}
