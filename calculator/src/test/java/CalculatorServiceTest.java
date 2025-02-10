import com.manteigueiro.CalculatorRequestModel;
import com.manteigueiro.CalculatorResponseModel;
import com.manteigueiro.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @Mock
    private KafkaTemplate<String, CalculatorResponseModel> kafkaTemplate;

    @InjectMocks
    private CalculatorService calculatorService;

    /**
     * Test for a valid sum operation
     * It verifies that when a sum calculation is requested,
     * the service returns the correct result and sends the proper message
     */
    @Test
    void calculate_SumOperation_Success() {
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                "sum",
                "1678886404"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the response that is sent to the Kafka topic
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886404"), responseCaptor.capture());

            CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("sum", response.getOperation());
        assertEquals("1678886404", response.getRequestId());
        assertEquals("8", response.getResult());
        assertTrue(response.getSuccessfullyProcessed());
    }

    /**
     * Test for a valid subtraction operation
     * This test checks that the CalculatorService correctly calculates subtraction
     */
    @Test
    void calculate_SubtractionOperation_Success() {
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                "subtraction",
                "1678886405"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the response sent to Kafka
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886405"), responseCaptor.capture());

        CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("subtraction", response.getOperation());
        assertEquals("1678886405", response.getRequestId());
        assertEquals("2", response.getResult());
        assertTrue(response.getSuccessfullyProcessed());
    }

    /**
     * Test for a valid multiplication operation.
     * This verifies that the service returns a correct multiplication result
     */
    @Test
    void calculate_MultiplicationOperation_Success() {
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                "multiplication",
                "1678886406"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the response sent to Kafka
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886406"), responseCaptor.capture());

        CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("multiplication", response.getOperation());
        assertEquals("1678886406", response.getRequestId());
        assertEquals("15", response.getResult());
        assertTrue(response.getSuccessfullyProcessed());
    }

    /**
     * Test for a valid division operation
     * The result is expected to be formatted to a scale of 10 decimal places
     */
    @Test
    void calculate_DivisionOperation_Success() {
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(6),
                BigDecimal.valueOf(3),
                "division",
                "1678886407"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the response sent to Kafka
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886407"), responseCaptor.capture());

        CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("division", response.getOperation());
        assertEquals("1678886407", response.getRequestId());
        assertEquals("2.0000000000", response.getResult()); // Verify the formatting
        assertTrue(response.getSuccessfullyProcessed());
    }

    /**
     * Test for a division by zero scenario
     * This test verifies that the CalculatorService correctly handles division by zero,
     * returning an error response with no result and an unsuccessful processing flag
     */
    @Test
    void calculate_DivisionByZero_ErrorResponse() {
        // 'B' is zero (division by zero)
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(6),
                BigDecimal.valueOf(0),
                "division",
                "1678886408"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the error response sent to Kafka
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886408"), responseCaptor.capture());

        CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("division", response.getOperation());
        assertEquals("1678886408", response.getRequestId());
        assertNull(response.getResult()); // No result should be given
        assertFalse(response.getSuccessfullyProcessed()); // Processing should be marked as unsuccessful
    }

    /**
     * Test for an unsupported operation
     * When an invalid operation is provided, the service should return an error response
     */
    @Test
    void calculate_UnsupportedOperation_ErrorResponse() {
        CalculatorRequestModel request = new CalculatorRequestModel(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                "wit_challenge",
                "1678886409"
        );

        // Execute the calculation
        calculatorService.calculate(request);

        // Capture the error response sent to Kafka
        ArgumentCaptor<CalculatorResponseModel> responseCaptor = ArgumentCaptor.forClass(CalculatorResponseModel.class);
        verify(kafkaTemplate).send(eq("calculate-answer"), eq("1678886409"), responseCaptor.capture());

        CalculatorResponseModel response = responseCaptor.getValue();
        assertEquals("wit_challenge", response.getOperation());
        assertEquals("1678886409", response.getRequestId());
        assertNull(response.getResult()); // No result for an unsupported operation
        assertFalse(response.getSuccessfullyProcessed()); // Processing should be marked as unsuccessful
    }
}
