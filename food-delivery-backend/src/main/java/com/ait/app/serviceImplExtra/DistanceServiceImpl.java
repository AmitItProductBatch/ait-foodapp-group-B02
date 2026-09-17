package com.ait.app.serviceImplExtra;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ait.app.service.DistanceService;

@Service
public class DistanceServiceImpl implements DistanceService {

    private final RestClient restClient;

    @Value("${ors.api.key:default_key}")
    private String apiKey;

    public DistanceServiceImpl(RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("https://api.heigit.org")
                .build();
    }

    @Override
    public double getDistance(
            double restaurantLongitude,
            double restaurantLatitude,
            double customerLongitude,
            double customerLatitude) {

        Map<String, Object> requestBody = Map.of(
                "coordinates",
                new double[][] {
                        {
                                restaurantLongitude,
                                restaurantLatitude
                        },
                        {
                                customerLongitude,
                                customerLatitude
                        }
                }
        );

        Map<String, Object> response =
                restClient.post()
                        .uri(
                                "/openrouteservice/v2/directions/driving-car"
                        )
                        .header("Authorization", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

        if (response == null) {
            throw new RuntimeException(
                    "Distance response is empty"
            );
        }

        List<Map<String, Object>> routes =
                (List<Map<String, Object>>)
                        response.get("routes");

        if (routes == null || routes.isEmpty()) {
            throw new RuntimeException(
                    "Route not found"
            );
        }

        Map<String, Object> route =
                routes.get(0);

        Map<String, Object> summary =
                (Map<String, Object>)
                        route.get("summary");

        if (summary == null) {
            throw new RuntimeException(
                    "Route summary not found"
            );
        }

        Number distance =
                (Number) summary.get("distance");

        if (distance == null) {
            throw new RuntimeException(
                    "Distance not found"
            );
        }

        // ORS returns meters
        return distance.doubleValue() / 1000.0;
    }
}