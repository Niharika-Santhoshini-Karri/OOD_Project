package Model;

public class CarReturnTM {
    private String id;
    private String issuedDate;
    private String returnedDate;
    private float penalty;



    private String issueId;

    public CarReturnTM(String id, String issuedDate, String returnedDate, float penalty, String issueId) {
        this.id = id;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.penalty = penalty;
        this.issueId = issueId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

    public float getPenalty() {
        return penalty;
    }

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
}
