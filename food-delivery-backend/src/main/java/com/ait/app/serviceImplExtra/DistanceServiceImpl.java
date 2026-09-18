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

        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("default_key")) {
            try {
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

                if (response != null) {
                    List<Map<String, Object>> routes =
                            (List<Map<String, Object>>)
                                    response.get("routes");

                    if (routes != null && !routes.isEmpty()) {
                        Map<String, Object> route = routes.get(0);
                        Map<String, Object> summary = (Map<String, Object>) route.get("summary");
                        if (summary != null) {
                            Number distance = (Number) summary.get("distance");
                            if (distance != null) {
                                return distance.doubleValue() / 1000.0;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Fall back to Haversine distance calculation
            }
        }

        // Fallback: Haversine distance with road curvature factor (1.2)
        return calculateHaversine(restaurantLatitude, restaurantLongitude, customerLatitude, customerLongitude);
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Radius of the Earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = R * c;
        return Math.max(1.0, Math.round(distanceKm * 1.2 * 100.0) / 100.0);
    }
}