package se.lexicon.flightbooking_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import se.lexicon.flightbooking_api.service.AiAssistantService;


@RestController
@RequestMapping("/api/ai")
public class OpenAiAssistantController {
  // http://localhost:8080/api/ai
  private final AiAssistantService aiAssistantService;

  @Autowired
  public OpenAiAssistantController(AiAssistantService aiAssistantService) {
    this.aiAssistantService = aiAssistantService;
  }


  @GetMapping
  public String welcome() {
    return "Welcome to the Flight Booking AI chat bot!";
  }


  //http://localhost:8080/api/ai/chat?chatId=qwer1234&question=when%20is%20the%20next%20flight?
  @GetMapping("/chat")
  public String askWithContext(@RequestParam
                               @NotNull(message = "Question cannot be null")
                                 @NotBlank(message = "Question cannot be blank")
                                 @Size(max = 200, message = "Question cannot exceed 200 characters")
                                 String question,
                               @NotNull(message = "chatId cannot be null")
                                 @NotBlank(message = "chatId cannot be blank")
                                 @Size(max = 200, message = "chatId cannot exceed 200 characters")
                                 String chatId) {
    System.out.println("\nid: " + chatId + " | question: " + question);
    return aiAssistantService.chatMemory(question, chatId);
  }


}