package com.smurfhouse.scratch.model;

import java.util.List;

/**
 * Created by fmunozse on 6/7/16.
 */
public class PageParsed {

    private String nextUrl;

    private List<ScratchHouse> houses;

    public PageParsed(List<ScratchHouse> houses, String nextUrl) {
        this.houses = houses;
        this.nextUrl = nextUrl;
    }

    public List<ScratchHouse> getHousesCurrentPage() {
        return houses;
    }

    public String getNextUrl() {
        return nextUrl;
    }
}
