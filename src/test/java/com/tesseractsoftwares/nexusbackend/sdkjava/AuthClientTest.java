package com.tesseractsoftwares.nexusbackend.sdkjava;

import com.tesseractsoftwares.nexusbackend.sdkjava.callbacks.AuthCallbacks;
import com.tesseractsoftwares.nexusbackend.sdkjava.dtos.NexusAuthenticationRequestDto;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Nested
class AuthClientTest {

    private AuthClient authClient;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockResponse;
    private AuthCallbacks.OnSuccess onSuccessCallback;
    private AuthCallbacks.OnInvalidCredentials onInvalidCredentialsCallback;
    private AuthCallbacks.OnError onErrorCallback;

    @BeforeEach
    void setUp() {
        // Iniciar los mocks de Mockito
        openMocks(this);

        // Crear mocks para los callbacks
        onSuccessCallback = mock(AuthCallbacks.OnSuccess.class);
        onInvalidCredentialsCallback = mock(AuthCallbacks.OnInvalidCredentials.class);
        onErrorCallback = mock(AuthCallbacks.OnError.class);

        // Mockear el CloseableHttpClient y CloseableHttpResponse
        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(CloseableHttpResponse.class);

        // Establecer el authClient utilizando la URL base y la secret key
        authClient = new AuthClient("http://localhost:5009", "dummySecretKey");
    }

    @Test
    void authenticateSuccessTest() throws IOException {
        // Simulamos que la respuesta del servidor devuelve 200 (éxito)
        when(mockResponse.getCode()).thenReturn(200);

        // Simular la ejecución del HttpClient
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        // Usar mockStatic para interceptar HttpClients.createDefault()
        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Ejecutar la autenticación
            authClient.authenticate(
                    new NexusAuthenticationRequestDto("email@test.com", "password123"),
                    onSuccessCallback,
                    onInvalidCredentialsCallback,
                    onErrorCallback
            );

            // Verificar que el callback de éxito fue invocado
            verify(onSuccessCallback, times(1)).execute("Authentication success");
            verify(onInvalidCredentialsCallback, never()).execute(any());
            verify(onErrorCallback, never()).execute(any());
        }
    }


    @Test
    void authenticateInvalidCredentialsTest() throws IOException {
        // Simulamos que la respuesta del servidor devuelve 400 (credenciales inválidas)
        when(mockResponse.getCode()).thenReturn(400);

        // Simular la ejecución del HttpClient
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        // Usar mockStatic para interceptar HttpClients.createDefault()
        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Ejecutar la autenticación
            authClient.authenticate(
                    new NexusAuthenticationRequestDto("email@test.com", "wrongpassword"),
                    onSuccessCallback,
                    onInvalidCredentialsCallback,
                    onErrorCallback
            );

            // Verificar que el callback de credenciales inválidas fue invocado
            verify(onInvalidCredentialsCallback, times(1)).execute("Invalid credentials");
            verify(onSuccessCallback, never()).execute(any());
            verify(onErrorCallback, never()).execute(any());
        }
    }

    @Test
    void authenticateErrorTest() throws IOException {
        // Simulamos un error de red lanzando una IOException
        when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Network error"));

        // Usar mockStatic para interceptar HttpClients.createDefault()
        try (MockedStatic<HttpClients> mockedStatic = mockStatic(HttpClients.class)) {
            mockedStatic.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Ejecutar la autenticación
            authClient.authenticate(
                    new NexusAuthenticationRequestDto("email@test.com", "password123"),
                    onSuccessCallback,
                    onInvalidCredentialsCallback,
                    onErrorCallback
            );

            // Verificar que el callback de error fue invocado
            verify(onErrorCallback, times(1)).execute(any(IOException.class));
            verify(onSuccessCallback, never()).execute(any());
            verify(onInvalidCredentialsCallback, never()).execute(any());
        }
    }
}

