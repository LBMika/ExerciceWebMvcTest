package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.FilmController;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.FilmService;
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
import java.util.Date;
import java.util.Optional;


@WebMvcTest(FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    private String route = "/films";


    /**
     * Testing method findAll()
     * @throws Exception
     */
    @Test
    public void testFindAllFilm() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route))
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    /**
     * Testing method findById when id is not valid
     * @throws Exception
     */
    @Test
    public void testFindOneFilmWhereFilmNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/21fhd564dnfd53g"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Testing method findById with a valid id
     * @throws Exception
     */
    @Test
    public void testFindOneFilm() throws Exception {
        // DTO
        Cinema cinema = new Cinema("d54fg65df", "Gaumont");
        Salle salle = new Salle("456", 1, 123, cinema);
        Seance seance = new Seance("456ds4gds", new Date(), salle);
        FilmDTO filmDTO = new FilmDTO("dfghjk", "La dernière", 120f, seance);

        // Request
        BDDMockito.given(service.findByID(filmDTO.getId())).willReturn(Optional.of(filmDTO));
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(route+"/"+filmDTO.getId()))
                                        .andExpect(MockMvcResultMatchers.status().isOk())
                                        .andReturn();
        Assertions.assertEquals(filmDTO,
                new Gson().fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8), FilmDTO.class));
    }

    /**
     * Testing method save()
     * @throws Exception
     */
    @Test
    public void testSaveFilm() throws Exception {
        //DTO
        Cinema cinema = new Cinema("d54fg65df", "Gaumont");
        Salle salle = new Salle("456", 1, 123, cinema);
        Seance seance = new Seance("456ds4gds", new Date(), salle);
        FilmDTO filmDTO = new FilmDTO("dfghjk", "La dernière", 120f, seance);

        // BDD Mock
        BDDMockito.when(service.save(filmDTO)).thenReturn(filmDTO);

        // Request
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(route)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json.toJson(filmDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();

        FilmDTO response = json.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                        FilmDTO.class);
        Assertions.assertEquals(filmDTO, response);
    }

    /**
     * Testing method save() for updating
     * @throws Exception
     */
    @Test
    public void testUpdateFilm() throws Exception {
        // DTO
        Cinema cinema = new Cinema("d54fg65df", "Gaumont");
        Salle salle = new Salle("456", 1, 123, cinema);
        Seance seance = new Seance("456ds4gds", new Date(), salle);
        FilmDTO oldFilmDTO = new FilmDTO("dfghjk", "La dernière", 120f, seance);

        // Modify
        FilmDTO newFilmDTO = new FilmDTO(oldFilmDTO.getId(), "C'était pour rire!", 120f, seance);

        // Create the BDD mock
        BDDMockito.when(service.save(ArgumentMatchers.any(FilmDTO.class))).thenReturn(newFilmDTO);

        // Send the update request
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put(route)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .characterEncoding(StandardCharsets.UTF_8)
                                                                        .content(json.toJson(newFilmDTO)))
                                        .andExpect(MockMvcResultMatchers.status().isCreated())
                                        .andReturn();
        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        FilmDTO resultCinemaDTO = json.fromJson(response, FilmDTO.class);

        // Assert
        Assertions.assertEquals(newFilmDTO, resultCinemaDTO);
        Assertions.assertNotEquals(oldFilmDTO, resultCinemaDTO);
    }

    /**
     * Testing delete() method
     * @throws Exception
     */
    @Test
    public void testDeleteFilm() throws Exception {
        Cinema cinema = new Cinema("d54fg65df", "Gaumont");
        Salle salle = new Salle("456", 1, 123, cinema);
        Seance seance = new Seance("456ds4gds", new Date(), salle);
        FilmDTO filmDTO = new FilmDTO("dfghjk", "La dernière", 120f, seance);

        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        this.mockMvc.perform(MockMvcRequestBuilders.delete(route)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(json.toJson(filmDTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isBoolean())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));
    }
}
