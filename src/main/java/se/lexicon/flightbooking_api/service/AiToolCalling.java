package se.lexicon.flightbooking_api.service;

import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import se.lexicon.flightbooking_api.dto.AvailableFlightDTO;
import se.lexicon.flightbooking_api.dto.BookFlightRequestDTO;
import se.lexicon.flightbooking_api.dto.FlightBookingDTO;
import se.lexicon.flightbooking_api.dto.FlightListDTO;

import java.util.List;

@Component
public class AiToolCalling {

  private FlightBookingServiceImpl flightBookingServiceImpl;

  public AiToolCalling(FlightBookingServiceImpl flightBookingServiceImpl) {
    this.flightBookingServiceImpl = flightBookingServiceImpl;
  }

  @Tool(description = """
    Create a new booking of a flight with following data:
    FlightId, passengerName and passengerEmail
  """)
  public FlightBookingDTO bookFlight(Long flightId, BookFlightRequestDTO bookingRequest) {
    return flightBookingServiceImpl.bookFlight(flightId, bookingRequest);
  }

  @Tool(description = "Cancel a booked flight with following data: flightId and passengerEmail")
  public void cancelFlight(Long flightId, String passengerEmail) {
    flightBookingServiceImpl.cancelFlight(flightId, passengerEmail);
  }

  @Tool(description = "Get a list of all flights")
  public List<FlightListDTO> findAll() {

    return flightBookingServiceImpl.findAll();
  }

  @Tool(description = "Get a list of all flights that are available for booking")
  public List<AvailableFlightDTO> findAvailableFlights() {

    return flightBookingServiceImpl.findAvailableFlights();
  }

  @Tool(description = "Get a list of all booked flights with following data: passangerEmail")
  public List<FlightBookingDTO> findBookingsByEmail(String passangerEmail) {

    return flightBookingServiceImpl.findBookingsByEmail(passangerEmail);
  }

}
