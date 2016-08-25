package com.v2tech.service;

/**
 * Created by 28851274 on 8/25/16.
 */
public interface PersonalSetting {

    public enum PersonalGender {
        MALE,
        FEMALE;
    }

    public void updateNickname(String name);


    public void updateGender(PersonalGender gender);

    public void updateLocation(String country, String prov, String city, String district);


    public void updateNotes(String word);
}
