package academic.model;

/**
 * @author 12S24055 Theresia Oktaviani Samosir
 */

public class Lecturer{
    private String id;
    private String name;
    private String initial;
    private String email;
    private String studyProgram;

    public Lecturer(String id, String name, String initial, String email, String studyProgram) {
        this.id = id;
        this.initial = initial;
        this.email = email;
        this.studyProgram = studyProgram;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getInitial() {
        return initial;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }
    
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + initial + "|" + email + "|" + studyProgram;
    }
}
