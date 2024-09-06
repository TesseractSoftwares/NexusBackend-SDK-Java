package com.tesseractsoftwares.nexusbackend.sdkjava;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GraphQLClient {
    private final String baseUrl;

    public GraphQLClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String executeGraphQLQuery(String query) {
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost httpPost = new HttpPost(baseUrl + "/graphql");

            String json = "{\"query\":\"" + query + "\"}";
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return responseString;
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
