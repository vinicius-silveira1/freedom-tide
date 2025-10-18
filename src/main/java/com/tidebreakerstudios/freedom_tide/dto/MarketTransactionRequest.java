package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.MarketItem;
import lombok.Data;

@Data
public class MarketTransactionRequest {
    private MarketItem item;
    private int quantity;
}
