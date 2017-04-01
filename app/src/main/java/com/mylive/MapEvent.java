package com.mylive;

/**
 * Created by Sunny on 3/30/2017.
 */

public class MapEvent {

    public String colorType;
    public String endDate;
    public String image;
    public double latitude;
    public double longitude;
    public String startDate;
    public String title;
    public String type;
    public String venue;

    public MapEvent()
    {

    }
    /*
    public MapEvent(String colorType, Date endDate, String imageURl, double latitude,
                    double longitude, Date startDate, String title, String type,
                    String venue)
    {
        setColorType(colorType);
        setEndDate(endDate);
        setImageURL(imageURl);
        setLatitude(latitude);
        setLongitude(longitude);
        setStartDate(startDate);
        setTitle(title);
        setType(type);
        setVenue(venue);
    }*/
    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
