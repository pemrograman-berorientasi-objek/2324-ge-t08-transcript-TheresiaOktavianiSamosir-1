package academic.model;

/**
 * @author 12S24055 Theresia Oktaviani Samosir
 */

public class Enrollment {

    private String code;  
    private String id;
    private String academicYear;
    private String semester;
    private String grade = "None"; 
    // remedialGrade dihapus karena previousGrade kini digunakan untuk menyimpan riwayat nilai remedial
    private String previousGrade; // Digunakan untuk menyimpan grade sebelumnya (termasuk riwayat remedial)
    private int total ; // Counter untuk jumlah percobaan remedial

    public Enrollment(String _code, String _id, String _academicYear, String _semester) {
        code = _code;
        id = _id;
        academicYear = _academicYear; 
        semester = _semester;
        previousGrade = ""; // Inisialisasi kosong
        total = 0;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String _grade) {
        grade = _grade;
    }

    // getRemedialGrade() dan setRemedialGrade() dihapus

    public int getTotal(){
        return total;
    }

    public void setTotal(){
        this.total += 1;
    }

    /**
     * Menukar grade saat ini dengan previousGrade.
     * Grade yang sedang aktif menjadi previous, dan previous menjadi grade aktif baru.
     */
    public void swapGrade() {
        String temp = grade;
        grade = previousGrade;
        previousGrade = temp;
    }

    public String getPreviousGrade() {
        return previousGrade;
    }

    public void setPreviousGrade(String previousGrade) {
        this.previousGrade = previousGrade;
    }

    @Override
    public String toString() {
        // Output disesuaikan agar konsisten dengan format cetak di Driver1
        String gradeOutput = grade;
        if (!previousGrade.isEmpty() && !previousGrade.equals("null")) {
            gradeOutput += "(" + previousGrade + ")";
        }
        return code + "|" + id + "|" + academicYear + "|" + semester + "|" + gradeOutput;
    }

    // remedialToString() dihapus karena tidak digunakan secara eksplisit di Driver1
}

