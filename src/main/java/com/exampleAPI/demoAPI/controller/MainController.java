package com.exampleAPI.demoAPI.controller;

import com.exampleAPI.demoAPI.entity.Character;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Controller
public class MainController {

     private static final String URL = "https://rickandmortyapi.com/api/character/";

    @GetMapping("/characters")
    public String showCharacter(Model out) {

        Random randomNumberGenerator = new Random();
        int[] idsInt = randomNumberGenerator.ints(6, 1, 591).toArray();
        String ids = Arrays.toString(idsInt);
        WebClient webClient = WebClient.create(URL);

        Mono<String> call = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/{id}")
                    .build(ids))
                .retrieve()
                .bodyToMono(String.class);
        String response = call.block();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            Character[] characters = objectMapper.convertValue(root, Character[].class);
            List<Character> characterList = new ArrayList<>(Arrays.asList(characters));
            Map<Character, String> mapCharacters = new LinkedHashMap<>();
            for (Character character : characterList) {
                String url = character.getEpisode()[0];
                WebClient webClient1 = WebClient.create(url);

                Mono<String> call1 = webClient1.get()
                        .retrieve()
                        .bodyToMono(String.class);
                String response1 = call1.block();

                ObjectMapper objectMapper1 = new ObjectMapper();
                JsonNode root1 = objectMapper1.readTree(response1);
                mapCharacters.put(character, root1.get("name").asText());
            }
            out.addAttribute("characters", mapCharacters);
            return "characters";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/charactersName")
    public String showCharactersByName(Model out,
                                       @RequestParam String name) {

        WebClient webClient = WebClient.create(URL);

        Mono<String> call = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .queryParam("name", name)
                    .build())
                .retrieve()
                .bodyToMono(String.class);
        String response = call.block();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            Character[] characters = objectMapper.convertValue(root.get("results"), Character[].class);
            List<Character> characterList = new ArrayList<>(Arrays.asList(characters));
            Map<Character, String> mapCharacters = new LinkedHashMap<>();
            for (Character character : characterList) {
                String url = character.getEpisode()[0];
                WebClient webClient1 = WebClient.create(url);

                Mono<String> call1 = webClient1.get()
                        .retrieve()
                        .bodyToMono(String.class);
                String response1 = call1.block();

                ObjectMapper objectMapper1 = new ObjectMapper();
                JsonNode root1 = objectMapper1.readTree(response1);
                mapCharacters.put(character, root1.get("name").asText());
            }
            out.addAttribute("characters", mapCharacters);
            return "characters";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return response;
    }


}
