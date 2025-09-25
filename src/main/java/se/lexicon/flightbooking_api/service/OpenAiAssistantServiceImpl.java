package se.lexicon.flightbooking_api.service;


import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Service
public class OpenAiAssistantServiceImpl implements OpenAiAssistantService {

  private OpenAiChatModel openAiChatModel;
  private ChatMemory chatMemory;

  @Autowired
  public OpenAiAssistantServiceImpl(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
    this.openAiChatModel = openAiChatModel;
    this.chatMemory = chatMemory;
  }

  @Override
  public String chatWithMemory(String question, String chatId) {
    if (question == null || question.trim().isEmpty()) {
      throw new IllegalArgumentException("Query cannot be null or empty");
    }
    if (chatId == null || chatId.trim().isEmpty()) {
      throw new IllegalArgumentException("ConversationId cannot be null or empty");
    }

    UserMessage userMessage = UserMessage.builder()
                                  .text(question)
                                  .build();
    chatMemory.add(chatId, userMessage);

    Prompt prompt = Prompt.builder()
                        .messages(chatMemory.get(chatId))
                        .chatOptions(OpenAiChatOptions.builder()
                                         .model("gpt-4.1-mini")
                                         .temperature(0.2)
                                         .maxTokens(500)
                                         .build())
                        .build();

    ChatResponse chatResponse = openAiChatModel.call(prompt);
    chatMemory.add(chatId, chatResponse.getResult().getOutput());
    System.out.println("Current memory size: " + chatMemory.get(chatId).size());
    System.out.println("Messages in memory:");
    chatMemory.get(chatId).forEach(msg -> System.out.println(msg.getText()));

    return chatResponse.getResult().getOutput().getText();

  }

}