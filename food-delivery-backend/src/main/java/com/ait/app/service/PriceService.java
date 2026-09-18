package com.ait.app.service;

import com.ait.app.dto.PriceResponse;

public interface PriceService {

    PriceResponse getPrice(int itemId);
}
