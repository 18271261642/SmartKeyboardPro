package com.app.smartkeyboard.bean;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherBean {


    @JsonProperty("location")
    private Object location;
    @JsonProperty("tempMax")
    private String tempMax;
    @JsonProperty("tempMin")
    private String tempMin;
    @JsonProperty("temp")
    private String temp;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("airAqi")
    private String airAqi;
    @JsonProperty("airLevel")
    private String airLevel;
    @JsonProperty("airStatus")
    private Object airStatus;
    @JsonProperty("sunrise")
    private String sunrise;
    @JsonProperty("sunset")
    private String sunset;
    @JsonProperty("humidity")
    private String humidity;
    @JsonProperty("uvIndex")
    private String uvIndex;
    @JsonProperty("tomorrow")
    private FutureWeatherBean tomorrow;
    @JsonProperty("dayAfterTomorrow")
    private FutureWeatherBean dayAfterTomorrow;
    @JsonProperty("threeDaysFromNow")
    private FutureWeatherBean threeDaysFromNow;
    @JsonProperty("dateTimeStamp")
    private long dateTimeStamp;
    @JsonProperty("windKph")
    private String windKph;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("district")
    private String district;

    private String address;
    @JsonProperty("hourly")
    private List<HourlyDTO> hourly;

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getAirAqi() {
        return TextUtils.isEmpty(airAqi)  ? "50" : airAqi;
    }

    public void setAirAqi(String airAqi) {
        this.airAqi = airAqi;
    }

    public String getAirLevel() {
        return airLevel;
    }

    public void setAirLevel(String airLevel) {
        this.airLevel = airLevel;
    }

    public Object getAirStatus() {
        return airStatus;
    }

    public void setAirStatus(Object airStatus) {
        this.airStatus = airStatus;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public FutureWeatherBean getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(FutureWeatherBean tomorrow) {
        this.tomorrow = tomorrow;
    }

    public FutureWeatherBean getDayAfterTomorrow() {
        return dayAfterTomorrow;
    }

    public void setDayAfterTomorrow(FutureWeatherBean dayAfterTomorrow) {
        this.dayAfterTomorrow = dayAfterTomorrow;
    }

    public FutureWeatherBean getThreeDaysFromNow() {
        return threeDaysFromNow;
    }

    public void setThreeDaysFromNow(FutureWeatherBean threeDaysFromNow) {
        this.threeDaysFromNow = threeDaysFromNow;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getWindKph() {
        return windKph;
    }

    public void setWindKph(String windKph) {
        this.windKph = windKph;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public List<HourlyDTO> getHourly() {
        return hourly;
    }

    public void setHourly(List<HourlyDTO> hourly) {
        this.hourly = hourly;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TomorrowDTO {
        @JsonProperty("tempMax")
        private String tempMax;
        @JsonProperty("sunrise")
        private String sunrise;
        @JsonProperty("sunset")
        private String sunset;
        @JsonProperty("humidity")
        private String humidity;
        @JsonProperty("dateTimeStamp")
        private Integer dateTimeStamp;
        @JsonProperty("uvIndex")
        private String uvIndex;
        @JsonProperty("airAqi")
        private String airAqi;
        @JsonProperty("airLevel")
        private String airLevel;
        @JsonProperty("tempMin")
        private String tempMin;
        @JsonProperty("airStatus")
        private String airStatus;
        @JsonProperty("status")
        private String status;
        @JsonProperty("statusCode")
        private Integer statusCode;

        public String getTempMax() {
            return tempMax;
        }

        public void setTempMax(String tempMax) {
            this.tempMax = tempMax;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public Integer getDateTimeStamp() {
            return dateTimeStamp;
        }

        public void setDateTimeStamp(Integer dateTimeStamp) {
            this.dateTimeStamp = dateTimeStamp;
        }

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public String getAirAqi() {
            return airAqi;
        }

        public void setAirAqi(String airAqi) {
            this.airAqi = airAqi;
        }

        public String getAirLevel() {
            return airLevel;
        }

        public void setAirLevel(String airLevel) {
            this.airLevel = airLevel;
        }

        public String getTempMin() {
            return tempMin;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

        public String getAirStatus() {
            return airStatus;
        }

        public void setAirStatus(String airStatus) {
            this.airStatus = airStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DayAfterTomorrowDTO {
        @JsonProperty("tempMax")
        private String tempMax;
        @JsonProperty("sunrise")
        private String sunrise;
        @JsonProperty("sunset")
        private String sunset;
        @JsonProperty("humidity")
        private String humidity;
        @JsonProperty("dateTimeStamp")
        private Integer dateTimeStamp;
        @JsonProperty("uvIndex")
        private String uvIndex;
        @JsonProperty("airAqi")
        private String airAqi;
        @JsonProperty("airLevel")
        private String airLevel;
        @JsonProperty("tempMin")
        private String tempMin;
        @JsonProperty("airStatus")
        private String airStatus;
        @JsonProperty("status")
        private String status;
        @JsonProperty("statusCode")
        private Integer statusCode;

        public String getTempMax() {
            return tempMax;
        }

        public void setTempMax(String tempMax) {
            this.tempMax = tempMax;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public Integer getDateTimeStamp() {
            return dateTimeStamp;
        }

        public void setDateTimeStamp(Integer dateTimeStamp) {
            this.dateTimeStamp = dateTimeStamp;
        }

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public String getAirAqi() {
            return airAqi;
        }

        public void setAirAqi(String airAqi) {
            this.airAqi = airAqi;
        }

        public String getAirLevel() {
            return airLevel;
        }

        public void setAirLevel(String airLevel) {
            this.airLevel = airLevel;
        }

        public String getTempMin() {
            return tempMin;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

        public String getAirStatus() {
            return airStatus;
        }

        public void setAirStatus(String airStatus) {
            this.airStatus = airStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreeDaysFromNowDTO {
        @JsonProperty("tempMax")
        private String tempMax;
        @JsonProperty("sunrise")
        private String sunrise;
        @JsonProperty("sunset")
        private String sunset;
        @JsonProperty("humidity")
        private String humidity;
        @JsonProperty("dateTimeStamp")
        private Integer dateTimeStamp;
        @JsonProperty("uvIndex")
        private String uvIndex;
        @JsonProperty("airAqi")
        private String airAqi;
        @JsonProperty("airLevel")
        private String airLevel;
        @JsonProperty("tempMin")
        private String tempMin;
        @JsonProperty("airStatus")
        private String airStatus;
        @JsonProperty("status")
        private String status;
        @JsonProperty("statusCode")
        private Integer statusCode;

        public String getTempMax() {
            return tempMax;
        }

        public void setTempMax(String tempMax) {
            this.tempMax = tempMax;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public Integer getDateTimeStamp() {
            return dateTimeStamp;
        }

        public void setDateTimeStamp(Integer dateTimeStamp) {
            this.dateTimeStamp = dateTimeStamp;
        }

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public String getAirAqi() {
            return airAqi;
        }

        public void setAirAqi(String airAqi) {
            this.airAqi = airAqi;
        }

        public String getAirLevel() {
            return airLevel;
        }

        public void setAirLevel(String airLevel) {
            this.airLevel = airLevel;
        }

        public String getTempMin() {
            return tempMin;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

        public String getAirStatus() {
            return airStatus;
        }

        public void setAirStatus(String airStatus) {
            this.airStatus = airStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyDTO {
        @JsonProperty("statusCode")
        private Integer statusCode;
        @JsonProperty("temp")
        private String temp;
        @JsonProperty("icon")
        private String icon;
        @JsonProperty("dateTime")
        private String dateTime;
        @JsonProperty("status")
        private String status;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }



    public static class FutureWeatherBean{
        @JsonProperty("tempMax")
        private String tempMax;
        @JsonProperty("sunrise")
        private String sunrise;
        @JsonProperty("sunset")
        private String sunset;
        @JsonProperty("humidity")
        private String humidity;
        @JsonProperty("dateTimeStamp")
        private Long dateTimeStamp;
        @JsonProperty("uvIndex")
        private String uvIndex;
        @JsonProperty("airAqi")
        private String airAqi;
        @JsonProperty("airLevel")
        private String airLevel;
        @JsonProperty("tempMin")
        private String tempMin;
        @JsonProperty("airStatus")
        private String airStatus;
        @JsonProperty("status")
        private String status;
        @JsonProperty("statusCode")
        private Integer statusCode;

        public String getTempMax() {
            return tempMax;
        }

        public void setTempMax(String tempMax) {
            this.tempMax = tempMax;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public Long getDateTimeStamp() {
            return dateTimeStamp;
        }

        public void setDateTimeStamp(Long dateTimeStamp) {
            this.dateTimeStamp = dateTimeStamp;
        }

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public String getAirAqi() {
            return airAqi;
        }

        public void setAirAqi(String airAqi) {
            this.airAqi = airAqi;
        }

        public String getAirLevel() {
            return airLevel;
        }

        public void setAirLevel(String airLevel) {
            this.airLevel = airLevel;
        }

        public String getTempMin() {
            return tempMin;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

        public String getAirStatus() {
            return airStatus;
        }

        public void setAirStatus(String airStatus) {
            this.airStatus = airStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }
    }
}
