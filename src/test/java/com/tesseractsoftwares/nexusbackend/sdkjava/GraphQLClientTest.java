package com.tesseractsoftwares.nexusbackend.sdkjava;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphQLClientTest {

    private GraphQLClient graphQLClient;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        graphQLClient = new GraphQLClient("http://localhost:5009");

        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(CloseableHttpResponse.class);
    }

    @Test
    void testExecuteGraphQLQuery_success() throws IOException {
        when(mockResponse.getEntity()).thenReturn(new StringEntity("{\"data\":{\"user\":{\"id\":\"123\",\"name\":\"John Doe\"}}}", ContentType.parse("UTF-8")));
        when(mockResponse.getCode()).thenReturn(200);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            String query = "{ user { id, name } }";
            String response = graphQLClient.executeGraphQLQuery(query);

            // Verificar el resultado
            assertNotNull(response);
            assertTrue(response.contains("John Doe"));
        }
    }

    @Test
    void testExecuteGraphQLQuery_failure() throws IOException {
        // Simular un código de estado 500 para una respuesta fallida
        when(mockResponse.getCode()).thenReturn(500);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        // Simular HttpClients.createDefault() usando mockStatic
        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Ejecutar la consulta GraphQL y esperar una RuntimeException
            String query = "{ user { id, name } }";
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                graphQLClient.executeGraphQLQuery(query);
            });

            // Verificar el mensaje de error
            assertTrue(exception.getMessage().contains("GraphQL query failed"));
        }
    }

    @Test
    void testExecuteGraphQLQuery_networkError() throws IOException {
        // Simular un error de red lanzando una IOException
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Network error"));

        // Simular HttpClients.createDefault() usando mockStatic
        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Ejecutar la consulta GraphQL y esperar una RuntimeException
            String query = "{ user { id, name } }";
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                graphQLClient.executeGraphQLQuery(query);
            });

            // Verificar que el mensaje de la excepción contiene "Network error"
            assertTrue(exception.getMessage().contains("Network error"));
        }
    }

}
