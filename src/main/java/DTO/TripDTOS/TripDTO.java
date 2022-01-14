package DTO.TripDTOS;

import DTO.GuideDTOS.GuideDTO;
import DTO.UserDTOS.UserDTO;
import entities.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripDTO {

    private Long id;
    private String name;
    private Date date;
    private String location;
    private String duration;
    private String packingList;
    private List<UserDTO> users;
    private GuideDTO guide;
    private List<TripDTO> trips;

    public TripDTO() {
    }

    public TripDTO(String name, Date date, String location, String duration, String packingList) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.packingList = packingList;
    }

    public TripDTO(Trip trip){
        this.id = trip.getId();
        this.name = trip.getName();
        this.date = trip.getDate();
        this.location = trip.getLocation();
        this.duration = trip.getDuration();
        this.packingList = trip.getPackingList();
        if(trip.getUsers() != null && !trip.getUsers().isEmpty()){
            if(this.users == null){
                this.users = new ArrayList<>();
            }
            this.users = new UserDTO(trip.getUsers()).getUsers();
        }
        if(trip.getGuide() != null){
            this.guide = new GuideDTO(trip.getGuide());
        }
    }

    public TripDTO(List<Trip> trips){
        if(trips != null && !trips.isEmpty()){
            this.trips = new ArrayList<>();
            for(Trip trip: trips){
                this.trips.add(new TripDTO(trip));
            }
        }
    }

    public List<TripDTO> getTrips() {
        return trips;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getDuration() {
        return duration;
    }

    public String getPackingList() {
        return packingList;
    }

    @Override
    public String toString() {
        return "TripDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", packingList='" + packingList + '\'' +
                ", users=" + users +
                ", guide=" + guide +
                ", trips=" + trips +
                '}';
    }
}
