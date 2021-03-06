package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trips")
@NamedQuery(name = "Trip.deleteAllRows", query = "delete from Trip ")
@NamedNativeQuery(name = "Trip.resetAutoIncrement", query = "ALTER TABLE trips AUTO_INCREMENT = 1;")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    private String Name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "time")
//    @Temporal(TemporalType.TIME)
//    private Date time;

    @Basic(optional = false)
    @NotNull
    @Column(name = "location")
    private String location;

    @Basic(optional = false)
    @NotNull
    @Column(name = "duration")
    private String duration;

    @Basic(optional = false)
    @NotNull
    @Column(name = "packing_list")
    private String packingList;

    @ManyToMany(mappedBy = "trips")
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private Guide guide;

    public Trip() {
    }

    public Trip(String name, Date date, String location, String duration, String packingList) {
        Name = name;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.packingList = packingList;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        if (this.users != null && !this.users.isEmpty()) {
            users.remove(user);
        }
    }

    public void addGuide(Guide guide) {
        this.guide = guide;
        guide.addTrip(this);
    }

    public void removeGuide(Guide guide) {
        this.guide = null;
        guide.removeTrip(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return Name;
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

    public List<User> getUsers() {
        return users;
    }

    public Guide getGuide() {
        return guide;
    }
}
