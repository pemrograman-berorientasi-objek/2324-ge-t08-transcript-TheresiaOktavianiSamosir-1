package academic.model;

/**
 * @author 12S24055 Theresia Oktaviani Samosir
 */

import java.util.List;

public class CourseOpening {
    private String courseCode;
    private String academicYear;
    private String semester;
    private List<Lecturer> lecturers; 
    private String initial; // Ini menyimpan inisial dosen pengampu, bisa berupa multiple, ex: "DAP,RMM"
    
    public CourseOpening(String courseCode, String academicYear, String semester, List<Lecturer> lecturers, String initial) {
        this.courseCode = courseCode;
        this.academicYear = academicYear;
        this.semester = semester;
        this.lecturers = lecturers;
        this.initial = initial;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }
}
