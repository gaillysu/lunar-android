package com.medcorp.lunar.model;

import net.medcorp.library.worldclock.City;

public class ChooseCityViewModel {

    private final String displayName;

    private final int cityId;

    public ChooseCityViewModel(City city) {
        this.displayName = city.getName() + ", " + city.getCountry();
        this.cityId = city.getId();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getSortLetter() {
        String pinyin = displayName;
        String sortString = pinyin.substring(0, 1).toUpperCase();

        if (sortString.matches("[A-Z]")) {
            return sortString.toUpperCase();
        } else {
            return "#";
        }
    }
}
