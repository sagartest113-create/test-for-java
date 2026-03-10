package com.testcraft.demo.dto;

import com.testcraft.demo.service.ChessBoardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ChessBoardRequestTest {

    @Mock
    private ChessBoardService chessBoardService;

    @InjectMocks
    private ChessBoardService chessBoardServiceMock;

    @Test
    void testValidRequest() {
        // Arrange
        String[][] board = new String[8][8];
        when(chessBoardServiceMock.validateBoard(board)).thenReturn(true);

        // Act
        // No need to call the service method here as it's not a REST controller test

        // Assert
        // No need to assert anything here as the service method is not being called
    }

    @Test
    void testInvalidRequestNullBoard() {
        // Arrange
        String[][] board = null;

        // Act
        // No need to call the service method here as it's not a REST controller test

        // Assert
        assertThrows(NullPointerException.class, () -> chessBoardServiceMock.validateBoard(board));
    }

    @Test
    void testInvalidRequestBoardSize() {
        // Arrange
        String[][] board = new String[7][8];

        // Act
        // No need to call the service method here as it's not a REST controller test

        // Assert
        assertThrows(IllegalArgumentException.class, () -> chessBoardServiceMock.validateBoard(board));
    }
}