package se.lexicon.flightbooking_api.service;

import reactor.core.publisher.Flux;

public interface OpenAiAssistantService {
  String chatWithMemory(String question, String chatId);

}