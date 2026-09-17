package com.ait.app.serviceImplExtra;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.ait.app.exception.DeliveryFeeException;
import com.ait.app.service.GeocodingService;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private final RestClient restClient;

    public GeocodingServiceImpl(RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(
                        "User-Agent",
                        "FoodDeliverySystem/1.0 (ganeshvkure@gmail.com)"
                )
                .build();
    }

    @Override
    public double[] getCoordinates(String address) {

        String url = UriComponentsBuilder
                .fromUriString("/search")
                .queryParam("q", address)
                .queryParam("format", "jsonv2")
                .queryParam("countrycodes", "in")
                .queryParam("limit", 1)
                .build()
                .encode()
                .toUriString();

        try {

            ResponseEntity<List> response =
                    restClient.get()
                            .uri(url)
                            .retrieve()
                            .toEntity(List.class);

            List<Map<String, Object>> results =
                    response.getBody();

            if (results == null || results.isEmpty()) {

                throw new DeliveryFeeException(
                        "Address could not be geocoded: " + address,
                        HttpStatus.BAD_REQUEST
                );
            }

            Map<String, Object> result =
                    results.get(0);

            Object latObject = result.get("lat");
            Object lonObject = result.get("lon");

            if (latObject == null || lonObject == null) {

                throw new DeliveryFeeException(
                        "Latitude or longitude not found",
                        HttpStatus.BAD_REQUEST
                );
            }

            double latitude =
                    Double.parseDouble(latObject.toString());

            double longitude =
                    Double.parseDouble(lonObject.toString());

            return new double[] {
                    latitude,
                    longitude
            };

        } catch (DeliveryFeeException e) {

            throw e;

        } catch (Exception e) {

            throw new DeliveryFeeException(
                    "Geocoding failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}