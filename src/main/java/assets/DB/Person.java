package assets.DB;

public class Person {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String birthdate;
    private String phoneNO;
    private String job;

    public Person(String fname, String lname, String email, String password, String birthdate, String phoneNO, String job) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.phoneNO = phoneNO;
        this.job = job;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
