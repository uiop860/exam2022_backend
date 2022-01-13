package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Basic(optional = false)
    @NotNull
    @Column(name = "time")
    @Temporal(TemporalType.TIME)
    private Date time;

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
    @Column(name = "packingList")
    private String packingList;

    @ManyToMany(mappedBy = "trips")
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private Guide guide;

    public Trip() {
    }

    public Trip(String name, Date date, Date time, String location, String duration, String packingList) {
        Name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.duration = duration;
        this.packingList = packingList;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void addGuide(Guide guide){
        this.guide = guide;
        guide.addTrip(this);
    }

}
