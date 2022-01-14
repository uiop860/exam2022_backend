package DTO.GuideDTOS;

import entities.Guide;

import java.util.ArrayList;
import java.util.List;

public class GuideDTO {

    private Long id;
    private String name;
    private String gender;
    private String birthYear;
    private String profile;
    private String imageUrl;
    private List<GuideDTO> guides;

    public GuideDTO() {
    }

    public GuideDTO(String name, String gender, String birthYear, String profile, String imageUrl) {
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.profile = profile;
        this.imageUrl = imageUrl;
    }

    public GuideDTO(Guide guide){
        this.id = guide.getId();
        this.name = guide.getName();
        this.gender = guide.getGender();
        this.birthYear = guide.getBirthYear();
        this.profile = guide.getProfile();
        this.imageUrl = guide.getImageUrl();
    }

    public GuideDTO(List<Guide> guides){
        if(guides != null && !guides.isEmpty()){
            this.guides = new ArrayList<>();
            for(Guide trip: guides){
                this.guides.add(new GuideDTO(trip));
            }
        }
    }

    public List<GuideDTO> getGuides() {
        return guides;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getProfile() {
        return profile;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
