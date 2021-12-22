package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SeanceController;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.dtos.seance.SeanceDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.services.SeanceService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@WebMvcTest(SeanceController.class)
public class SeanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeanceService service;

    private String route = "/seances";

    /**
     * Testing method findAll()
     * @throws Exception
     */
    @Test
    public void testFindAllSeance() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Testing method findById when id is not valid
     * @throws Exception
     */
    @Test
    public void testFindOneSeanceWhereCinemaNotSeance() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/dskllbgequl"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    /**
     * Testing method findById with a valid id
     * @throws Exception
     */
    @Test
    public void testFindOneSeance() throws Exception {
        // DTO
        Cinema cinema = new Cinema("gb4fdg65df7g45", "Luxor");
        Salle salle = new Salle("46nf45bx", 1, 99, cinema);
        SeanceDTO seanceDTO = new SeanceDTO("sdfghjk", new Date(), salle);

        // Request
        BDDMockito.given(service.findByID(seanceDTO.getId())).willReturn(Optional.of(seanceDTO));
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+seanceDTO.getId()))
                                        .andExpect(MockMvcResultMatchers.status().isOk())
                                        .andReturn();

        // Assert
        SeanceDTO response = new Gson().fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8), SeanceDTO.class);
        Assertions.assertEquals(seanceDTO, response);
    }

    /**
     * Testing method save()
     * @throws Exception
     */
    @Test
    public void testSaveSeance() throws Exception {
        // DTO creation
        Cinema cinema = new Cinema("gb4fdg65df7g45", "Luxor");
        Salle salle = new Salle("46nf45bx", 1, 99, cinema);
        SeanceDTO seanceDTO = new SeanceDTO("sdfghjk", new Date(), salle);

        // BDD Mock
        BDDMockito.when(service.save(ArgumentMatchers.any(SeanceDTO.class))).thenReturn(seanceDTO);

        // POST request
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .content(json.toJson(seanceDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();

        // Assert
        SeanceDTO response = json.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                                 SeanceDTO.class);
        Assertions.assertEquals(seanceDTO, response);
    }

    /**
     * Testing method save() for updating
     * @throws Exception
     */
    @Test
    public void testUpdateSeance() throws Exception {
        // DTO
        Cinema cinema = new Cinema("gb4fdg65df7g45", "Luxor");
        Salle oldSalle = new Salle("46nf45bx", 1, 99, cinema);
        SeanceDTO oldSeanceDTO = new SeanceDTO("sdfghjk", new Date(), oldSalle);

        // Modify
        Salle newSalle = new Salle("46nf45bx", 4, 99, cinema);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateInString = "2022-01-24 12:00:00";
        Date date = formatter.parse(dateInString);
        SeanceDTO newSeanceDTO = new SeanceDTO("sdfghjk", date, newSalle);

        // Create the BDD mock
        BDDMockito.when(service.save(ArgumentMatchers.any(SeanceDTO.class))).thenReturn(newSeanceDTO);

        // Send the update request
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .characterEncoding(StandardCharsets.UTF_8)
                                                                        .content(json.toJson(newSeanceDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        SeanceDTO resultSeanceDTO = json.fromJson(response, SeanceDTO.class);

        // Assert
        Assertions.assertEquals(newSeanceDTO, resultSeanceDTO);
        Assertions.assertNotEquals(oldSeanceDTO, resultSeanceDTO);
    }

    /**
     * Testing delete() method
     * @throws Exception
     */
    @Test
    public void testDeleteSeance() throws Exception {
        // DTO
        Cinema cinema = new Cinema("gb4fdg65df7g45", "Luxor");
        Salle salle = new Salle("46nf45bx", 1, 99, cinema);
        SeanceDTO seanceDTO = new SeanceDTO("sdfghjk", new Date(), salle);

        // Request
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(json.toJson(seanceDTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }
}
