package com.tesseractsoftwares.nexusbackend.sdkjava.test;

import com.tesseractsoftwares.nexusbackend.sdkjava.AuthClient;
import com.tesseractsoftwares.nexusbackend.sdkjava.callbacks.AuthCallbacks;
import com.tesseractsoftwares.nexusbackend.sdkjava.dtos.NexusAuthenticationRequestDto;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class AuthClientTest {
    @Mock
    CloseableHttpClient httpClient;

    @Mock
    CloseableHttpResponse httpResponse;

    @InjectMocks
    AuthClient authClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authClient = new AuthClient("http://localhost:8080", "secretKey");
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(new StringEntity("Authentication success"));

        AuthCallbacks.OnSuccess onSuccess = mock(AuthCallbacks.OnSuccess.class);
        AuthCallbacks.OnInvalidCredentials onInvalidCredentials = mock(AuthCallbacks.OnInvalidCredentials.class);
        AuthCallbacks.OnError onError = mock(AuthCallbacks.OnError.class);

        NexusAuthenticationRequestDto dto = new NexusAuthenticationRequestDto("testUser", "testPassword");

        authClient.authenticate(dto, onSuccess, onInvalidCredentials, onError);

        verify(onSuccess).execute("Authentication success");
        verify(onInvalidCredentials, never()).execute(anyString());
        verify(onError, never()).execute(any());
    }

    @Test
    void testAuthenticateInvalidCredentials() throws Exception {

        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getCode()).thenReturn(401);

        AuthCallbacks.OnSuccess onSuccess = mock(AuthCallbacks.OnSuccess.class);
        AuthCallbacks.OnInvalidCredentials onInvalidCredentials = mock(AuthCallbacks.OnInvalidCredentials.class);
        AuthCallbacks.OnError onError = mock(AuthCallbacks.OnError.class);

        // DTO de prueba
        NexusAuthenticationRequestDto dto = new NexusAuthenticationRequestDto("testUser", "wrongPassword");

        // Ejecutar la autenticación
        authClient.authenticate(dto, onSuccess, onInvalidCredentials, onError);

        // Verificar que se llamó al callback de credenciales inválidas
        verify(onInvalidCredentials).execute("Invalid Credentials");
        verify(onSuccess, never()).execute(anyString());
        verify(onError, never()).execute(any());
    }

    @Test
    void testAuthenticateError() throws Exception {

        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Network error"));

        // Crear callbacks simulados
        AuthCallbacks.OnSuccess onSuccess = mock(AuthCallbacks.OnSuccess.class);
        AuthCallbacks.OnInvalidCredentials onInvalidCredentials = mock(AuthCallbacks.OnInvalidCredentials.class);
        AuthCallbacks.OnError onError = mock(AuthCallbacks.OnError.class);

        // DTO de prueba
        NexusAuthenticationRequestDto dto = new NexusAuthenticationRequestDto("velas.blas.vrsa@gmail.com", "123456789");

        // Ejecutar la autenticación
        authClient.authenticate(dto, onSuccess, onInvalidCredentials, onError);

        // Verificar que se llamó al callback de error
        verify(onError).execute(any(IOException.class));
        verify(onSuccess, never()).execute(anyString());
        verify(onInvalidCredentials, never()).execute(anyString());
    }
}
