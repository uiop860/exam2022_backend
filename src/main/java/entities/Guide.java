package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "guides")
@NamedQuery(name = "Guide.deleteAllRows", query = "delete from Guide ")
@NamedNativeQuery(name = "Guide.resetAutoIncrement", query = "ALTER TABLE guides AUTO_INCREMENT = 1;")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "gender")
    private String gender;

    @Basic(optional = false)
    @NotNull
    @Column(name = "birth_year")
    private String birthYear;

    @Basic(optional = false)
    @NotNull
    @Column(name = "profile")
    private String profile;

    @Basic(optional = false)
    @NotNull
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "guide")
    private List<Trip> trips = new ArrayList<>();

    public Guide() {
    }

    public Guide(String name, String gender, String birthYear, String profile, String imageUrl) {
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.profile = profile;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addTrip(Trip trip) {
        this.trips.add(trip);
    }
}
