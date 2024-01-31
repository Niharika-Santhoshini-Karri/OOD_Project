package Model;

public class CarIssueTM {
    private String issueId;
    private String date;
    private String memberId;
    private String carId;

    public CarIssueTM(String issueId, String date, String memberId, String carId) {
        this.issueId = issueId;
        this.date = date;
        this.memberId = memberId;
        this.carId = carId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
