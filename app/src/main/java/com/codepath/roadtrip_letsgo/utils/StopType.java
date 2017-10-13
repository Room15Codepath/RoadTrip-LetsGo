package com.codepath.roadtrip_letsgo.utils;

/**
 * Created by tessavoon on 10/12/17.
 */

public enum StopType {
    CAFE("cafe"),
    ATM("atm"),
    RESTAURANT("restaurant"),
    MALL("mall"),
    GAS("gas station");

    private String term;

    StopType(String term) {
        this.term = term;
    }

    public String term() {
        return term;
    }
}
