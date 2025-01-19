package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.service.CardService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {

    private CardController cardController;
    private CardService cardService;
    private UserService userService;

    @BeforeEach
    void setup() {
        cardService = mock(CardService.class);
        userService = mock(UserService.class);
        cardController = new CardController(cardService, userService);
    }

    @Test
    void handleGetAllCards_ShouldReturnAllCards() {
        // Arrange
        Request request = new Request();
        request.setPath("/cards");
        request.setMethod(Method.GET);
        request.setHeader("Authorization", "Bearer valid-token");

        List<CardDTO> mockCardDTOs = List.of(
                new CardDTO("1", "Fire Dragon", 50.0, "user1"),
                new CardDTO("2", "Water Spell", 30.0, "user1")
        );

        when(userService.validateToken("Bearer valid-token")).thenReturn("valid-token");
        when(cardService.getCardDTOsByUserToken("valid-token")).thenReturn(mockCardDTOs);

        // Act
        Response response = cardController.handle(request);

        // Assert
        assertEquals(Status.OK, response.getStatus());
        assertTrue(response.getBody().toString().contains("Fire Dragon"));
        assertTrue(response.getBody().toString().contains("Water Spell"));
        verify(userService, times(1)).validateToken("Bearer valid-token");
        verify(cardService, times(1)).getCardDTOsByUserToken("valid-token");
    }

    @Test
    void handleGetAllCards_ShouldReturnUnauthorizedForInvalidToken() {
        // Arrange
        Request request = new Request();
        request.setPath("/cards");
        request.setMethod(Method.GET);
        request.setHeader("Authorization", "Bearer invalid-token");

        when(userService.validateToken("Bearer invalid-token")).thenThrow(new IllegalArgumentException("Invalid token"));

        // Act
        Response response = cardController.handle(request);

        // Assert
        assertEquals(Status.UNAUTHORIZED, response.getStatus());
        assertEquals("Invalid token", response.getBody());
        verify(userService, times(1)).validateToken("Bearer invalid-token");
        verifyNoInteractions(cardService);
    }

    @Test
    void handleGetAllCards_ShouldReturnErrorForUnexpectedException() {
        // Arrange
        Request request = new Request();
        request.setPath("/cards");
        request.setMethod(Method.GET);
        request.setHeader("Authorization", "Bearer valid-token");

        when(userService.validateToken("Bearer valid-token")).thenReturn("valid-token");
        when(cardService.getCardDTOsByUserToken("valid-token")).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        Response response = cardController.handle(request);

        // Assert
        assertEquals(Status.INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals("An error occurred: Unexpected error", response.getBody());
        verify(userService, times(1)).validateToken("Bearer valid-token");
        verify(cardService, times(1)).getCardDTOsByUserToken("valid-token");
    }
}
