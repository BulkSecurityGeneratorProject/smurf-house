package com.smurfhouse.web.rest.dto;

public class StatsSincronyzeDTO {

	public int houseAdded = 0;
	public int houseEnded = 0;
	public int houseNewPrice = 0;


    public String toString() {
        return "Stats {" +
            "Houses added =" + houseAdded +
            ", Houses ended='" + houseEnded + "'" +
            ", House Update price='" + houseNewPrice + "'" +
            '}';

    }

    public int getHouseEnded() {
        return houseEnded;
    }

    public void setHouseEnded(int houseEnded) {
        this.houseEnded = houseEnded;
    }

    public int getHouseNewPrice() {
        return houseNewPrice;
    }

    public void setHouseNewPrice(int houseNewPrice) {
        this.houseNewPrice = houseNewPrice;
    }

    public int getHouseAdded() {

        return houseAdded;
    }

    public void setHouseAdded(int houseAdded) {
        this.houseAdded = houseAdded;
    }




}
