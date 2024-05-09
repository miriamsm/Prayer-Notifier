
package com.praytimes.MobileProject.prayernotifier;

public class UserConfig {

    private String country;//user country
    private String city;//user city
    private String longitude;//user city longitude
    private String latitude;//user city latitude
    private String timezone;//user city time zone


    private static UserConfig userConfig;

    private UserConfig() {
    }

    public static UserConfig getSingleton() {
        if (userConfig == null) {
            userConfig = new UserConfig();
        }
        return userConfig;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public static UserConfig getUserConfig() {
        return userConfig;
    }

    public static void setUserConfig(UserConfig userConfig) {
        UserConfig.userConfig = userConfig;
    }
}
