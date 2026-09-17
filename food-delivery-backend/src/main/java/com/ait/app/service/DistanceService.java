package com.ait.app.service;

public interface DistanceService {

    double getDistance(
            double restaurantLongitude,
            double restaurantLatitude,
            double customerLongitude,
            double customerLatitude
    );
}