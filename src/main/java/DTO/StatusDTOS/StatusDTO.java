package DTO.StatusDTOS;

public class StatusDTO {
    private String status;
    private String message;

    public StatusDTO(){}

    public StatusDTO(String status, String message){
        this.status = status;
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
