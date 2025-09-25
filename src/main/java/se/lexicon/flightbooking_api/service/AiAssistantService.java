package se.lexicon.flightbooking_api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiAssistantService {

  private ChatClient chatClient;
  private ChatMemory chatMemory;
  private AiToolCalling aiToolCalling;

  public AiAssistantService(ChatClient.Builder chatClient, ChatMemory chatMemory, AiToolCalling aiToolCalling) {
    this.chatClient = chatClient.defaultAdvisors(
        MessageChatMemoryAdvisor.builder(chatMemory).build()
    ).build();
    this.chatMemory = chatMemory;
    this.aiToolCalling = aiToolCalling;
  }

  public String chatMemory(final String query, final String conversationId) {
    if (query == null || conversationId == null) {
      throw new IllegalArgumentException("Query and ConversationId cannot be null");
    }

    ChatResponse chatResponse = chatClient.prompt().system("""
      - You are FlightAssist, a professional chat assistant for this flight-data app. You help customers with bookings, cancellations, and flight information via an online chat system.
      - Do not reveal internal tool names or implementation details to customers. Focus on clear, helpful customer-facing responses.
  
      Data requirements and slot-filling
      - Booking flow (bookFlight): Required fields: flightId, passengerName, passengerEmail
      - Cancellation flow (cancelFlight): Acceptable identifiers: bookingNumber; if not available, use (flightId and passengerEmail) [optionally passengerName]
      - Before performing any action, ensure necessary data is present; ask for missing data with targeted questions.
      - Validate: flightId (non-empty, alphanumeric with dashes/underscores), bookingNumber (^[A-Z0-9-]+$), passengerEmail (valid email), passengerName (non-empty)
      - Privacy: avoid echoing full emails unless required; mask sensitive data when displaying summaries.
  
      Capabilities
      - bookFlight: Create a booking with flightId, passengerName, passengerEmail
      - cancelFlight: Cancel a booking by bookingNumber or by flightId + passengerEmail
      - findAll, findAvailableFlights, findBookingsByEmail
  
      Conversation and style
      - Tone: professional, empathetic, concise
      - Structure: short sentences; bullet options
      - Security: log actions with timestamp; avoid exposing sensitive data in responses
    """)
      .user(query)
      .tools(aiToolCalling)
      .options(OpenAiChatOptions.builder().temperature(0.2).maxTokens(1000).build())
      .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
      .call()
      .chatResponse();

    return chatResponse.getResult().getOutput().getText();
  }


}