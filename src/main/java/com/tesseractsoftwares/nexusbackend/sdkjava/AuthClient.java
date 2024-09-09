package com.tesseractsoftwares.nexusbackend.sdkjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesseractsoftwares.nexusbackend.sdkjava.callbacks.AuthCallbacks;
import com.tesseractsoftwares.nexusbackend.sdkjava.dtos.NexusAuthenticationRequestDto;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthClient {
    private final String baseUrl;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public AuthClient(String baseUrl, String secretKey) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.objectMapper = new ObjectMapper();
    }

    public void authenticate(
            NexusAuthenticationRequestDto dto,
            AuthCallbacks.OnSuccess onSuccess,
            AuthCallbacks.OnInvalidCredentials onInvalidCredentials,
            AuthCallbacks.OnError onError) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/api/auth/login");

            // Create JSON solicitude
            String json = objectMapper.writeValueAsString(dto);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (response.getCode() == 200) {
                    onSuccess.execute("Authentication success");
                } else {
                    onInvalidCredentials.execute("Invalid Credentials");
                }
            } catch (IOException | ParseException e) {
                onError.execute(e);
            }
        } catch (IOException e) {
            onError.execute(e);
        }
    }
}
