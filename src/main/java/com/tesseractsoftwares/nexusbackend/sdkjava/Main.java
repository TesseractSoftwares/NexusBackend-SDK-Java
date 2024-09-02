package com.tesseractsoftwares.nexusbackend.sdkjava;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String graphqlEndpoint = "http://localhost:5286/graphql";  // Cambia esta URL si es diferente

        // Define la consulta GraphQL
        String query = "query { userInfo(email: \"velas.blas.vrsa@gmail.com\") { coins email level userName } }";

        // Ejecuta la consulta y obtiene la respuesta
        String response = executeGraphQLQuery(graphqlEndpoint, query);

        // Imprime la respuesta de GraphQL
        System.out.println("GraphQL Response: " + response);
    }

    public static String executeGraphQLQuery(String graphqlEndpoint, String query) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(graphqlEndpoint);

            // Crear el cuerpo de la petición JSON
            String json = "{\"query\":\"" + query.replace("\"", "\\\"") + "\"}";
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            // Ejecutar la petición y obtener la respuesta
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
