package academic.driver;

/**
 * @author 12S24055 Theresia Oktaviani Samosir
 */

import academic.model.Course;
import academic.model.CourseOpening;
import academic.model.Enrollment;
import academic.model.Lecturer;
import academic.model.Student;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Driver1 {

    final static Scanner scanner = new Scanner(System.in);

    // Static nested class untuk merepresentasikan satu entri mata kuliah di transkrip
    private static class TranscriptEntry {
        private Enrollment enrollment;
        private Course course;

        public TranscriptEntry(Enrollment enrollment, Course course) {
            this.enrollment = enrollment;
            this.course = course;
        }

        public Enrollment getEnrollment() { return enrollment; }
        public Course getCourse() { return course; } // Warning: "never used locally" - kept for design consistency
        public String getCourseCode() { return enrollment.getCode(); }
        public String getAcademicYear() { return enrollment.getAcademicYear(); }
        public String getSemester() { return enrollment.getSemester(); }
        public String getGrade() { return enrollment.getGrade(); }
        public String getPreviousGrade() { return enrollment.getPreviousGrade(); }
        public int getCredit() { return course.getCredit(); } // Warning: "never used locally" - kept for design consistency

        // Metode bantu untuk mengonversi semester menjadi nilai int untuk sorting
        public int getSemesterOrder() {
            if ("odd".equalsIgnoreCase(enrollment.getSemester())) {
                return 0; // Ganjil lebih dulu
            } else if ("even".equalsIgnoreCase(enrollment.getSemester())) {
                return 1; // Genap kedua
            }
            return 2; // Lainnya setelah itu (misal: "short")
        }
    }

    public static void main(String[] _args) {
        List<Course> courses = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();
        List<String> inputList = new ArrayList<>();
        List<Lecturer> lecturers = new ArrayList<>();
        List<CourseOpening> courseOpenings = new ArrayList<>();
        Map<String, String> lecturerEmails = new HashMap<>();

        do {
            String input = scanner.nextLine();
            if (input.equals("---")) {
                break;
            }
            inputList.add(input);
        } while (true);

        for (String input : inputList) {
            String[] command = input.split("#");

            switch (command[0]) {
                case "lecturer-add":
                    if (command.length == 6) {
                        Lecturer lecturer = new Lecturer(command[1], command[2], command[3], command[4], command[5]);
                        if (!isDuplicateLecturer(lecturer, lecturers)) {
                            lecturers.add(lecturer);
                            lecturerEmails.put(command[1], command[5]);
                        }
                    }
                    break;
                case "course-add":
                    if (command.length >= 5) {
                        Course course = new Course(command[1], command[2], Integer.parseInt(command[3]), command[4]);
                        if (!isDuplicateCourse(course, courses)) {
                            courses.add(course);
                        }
                    }
                    break;
                case "student-add":
                    if (command.length == 5) {
                        Student student = new Student(command[1], command[2], command[3], command[4]);
                        if (!isDuplicateStudent(student, students)) {
                            students.add(student);
                        }
                    }
                    break;
                case "enrollment-add":
                    if (command.length == 5) {
                        Enrollment enrollment = new Enrollment(command[1], command[2], command[3], command[4]);
                        if (!enrollments.contains(enrollment)) {
                            enrollments.add(enrollment);
                        }
                    }
                    break;
                case "enrollment-grade":
                    if (command.length == 6) {
                        String courseCode = command[1];
                        String studentId = command[2];
                        String academicYear = command[3];
                        String semester = command[4];
                        String grade = command[5];
                        
                        Enrollment targetEnrollment = findEnrollment(courseCode, studentId, academicYear, semester, enrollments);
                        
                        if (targetEnrollment != null) {
                            targetEnrollment.setGrade(grade);
                        }
                    }
                    break;
                case "student-details":
                    for (Student student : students) {
                        if (student.getId().equals(command[1])) {
                            Map<String, Enrollment> latestEnrollmentsForGPA = getLatestEnrollmentsForStudent(student.getId(), enrollments);
                            double gpa = calculateGPA(student.getId(), latestEnrollmentsForGPA.values().stream().collect(Collectors.toList()), courses);
                            int totalCredits = calculateTotalCredits(student.getId(), latestEnrollmentsForGPA.values().stream().collect(Collectors.toList()), courses);
                            
                            System.out.println(student.getId() + "|" + student.getName() + "|" + student.getYear() + "|" + student.getStudyProgram() + "|" + String.format("%.2f", gpa) + "|" + totalCredits);
                        }
                    }
                    break;
                case "course-open":
                    String courseCode = command[1];
                    String academicYear = command[2];
                    String semester = command[3];
                    String[] lecturerInitials = command[4].split(",");
                    List<Lecturer> lecturerList = new ArrayList<>();
                
                    for (String initial : lecturerInitials) {
                        for (Lecturer lecturer : lecturers) {
                            if (lecturer.getInitial().equals(initial.trim())) {
                                lecturerList.add(lecturer);
                            }
                        }
                    }
                
                    CourseOpening courseOpening = new CourseOpening(courseCode, academicYear, semester, lecturerList, String.join(",", lecturerInitials));
                    courseOpenings.add(courseOpening);
                    break;
                case "course-history":
                    String targetCourseCode = command[1];
                    List<CourseOpening> filteredOpenings = courseOpenings.stream()
                            .filter(opening -> opening.getCourseCode().equals(targetCourseCode))
                            // PERBAIKAN: Memperjelas tipe Comparator untuk menghindari error kompilasi
                            .sorted(Comparator.<CourseOpening>comparingInt(opening -> {
                                String s = opening.getSemester();
                                if ("odd".equalsIgnoreCase(s)) return 0;
                                if ("even".equalsIgnoreCase(s)) return 1;
                                return 2;
                            }).thenComparing(CourseOpening::getAcademicYear)) // Error thenComparing juga teratasi
                            .collect(Collectors.toList());
                    
                    for (CourseOpening opening : filteredOpenings) {
                        Course currentCourse = courses.stream()
                                .filter(c -> c.getCode().equals(opening.getCourseCode()))
                                .findFirst().orElse(null);

                        if (currentCourse == null) continue;

                        System.out.print(currentCourse.getCode() + "|" + currentCourse.getName() + "|" +
                                currentCourse.getCredit() + "|" + currentCourse.getPassingGrade() + "|" +
                                opening.getAcademicYear() + "|" + opening.getSemester() + "|");
                        
                        List<Lecturer> courseLecturers = opening.getLecturers();
                        String lecturerInfo = courseLecturers.stream()
                                .map(l -> l.getInitial() + " (" + l.getEmail() + ")")
                                .collect(Collectors.joining(";"));
                        System.out.print(lecturerInfo.isEmpty() ? "No lecturer assigned" : lecturerInfo);
                        System.out.println();
                        
                        List<Enrollment> enrollmentsForThisOpening = enrollments.stream()
                                .filter(enr -> enr.getCode().equals(opening.getCourseCode()) &&
                                        enr.getAcademicYear().equals(opening.getAcademicYear()) &&
                                        enr.getSemester().equals(opening.getSemester()))
                                .collect(Collectors.toList());

                        enrollmentsForThisOpening.sort(Comparator.comparing(Enrollment::getId));

                        for (Enrollment enr : enrollmentsForThisOpening) {
                            String gradeOutput = enr.getGrade();
                            if (!enr.getPreviousGrade().isEmpty() && !enr.getPreviousGrade().equals("null")) {
                                gradeOutput += "(" + enr.getPreviousGrade() + ")";
                            }
                            System.out.println(enr.getCode() + "|" + enr.getId() + "|" + enr.getAcademicYear() + "|" + enr.getSemester() + "|" + gradeOutput);
                        }
                    }
                    break;
                case "enrollment-remedial":
                    for (Enrollment enrollment : enrollments) {
                        if (enrollment.getCode().equals(command[1]) && 
                            enrollment.getId().equals(command[2]) &&
                            enrollment.getAcademicYear().equals(command[3]) &&
                            enrollment.getSemester().equals(command[4])) {
                            
                            if (enrollment.getGrade().equals("None")) {
                                break;
                            }
                            // PERBAIKAN: Logika remedial disederhanakan untuk menyimpan hanya grade terakhir sebagai "previousGrade"
                            enrollment.setPreviousGrade(enrollment.getGrade()); 
                            enrollment.setGrade(command[5]); // Grade baru menjadi grade aktif
                            enrollment.setTotal(); 
                            break; 
                        }
                    }
                    break;
                case "student-transcript":
                    String studentId = command[1];
                    Student targetStudent = students.stream()
                            .filter(s -> s.getId().equals(studentId))
                            .findFirst().orElse(null);

                    if (targetStudent == null) {
                        break;
                    }

                    Map<String, Enrollment> latestEnrollmentsMap = getLatestEnrollmentsForStudent(studentId, enrollments);

                    List<TranscriptEntry> transcriptEntries = new ArrayList<>();
                    for (Enrollment enr : latestEnrollmentsMap.values()) {
                        Course course = courses.stream()
                                .filter(c -> c.getCode().equals(enr.getCode()))
                                .findFirst().orElse(null);
                        
                        if (course == null) continue;
                        
                        transcriptEntries.add(new TranscriptEntry(enr, course)); 
                    }

                    transcriptEntries.sort(Comparator
                        .comparing(TranscriptEntry::getAcademicYear)
                        .thenComparingInt(TranscriptEntry::getSemesterOrder)
                        .thenComparing(TranscriptEntry::getCourseCode)
                    );

                    double cumulativeGpa = calculateGPA(studentId, latestEnrollmentsMap.values().stream().collect(Collectors.toList()), courses);
                    int totalCredits = calculateTotalCredits(studentId, latestEnrollmentsMap.values().stream().collect(Collectors.toList()), courses);
                    
                    System.out.println(targetStudent.getId() + "|" + targetStudent.getName() + "|" + targetStudent.getYear() + "|" + targetStudent.getStudyProgram() + "|" + String.format("%.2f", cumulativeGpa) + "|" + totalCredits);

                    for (TranscriptEntry entry : transcriptEntries) {
                        String gradeOutput = entry.getGrade();
                        if (!entry.getPreviousGrade().isEmpty() && !entry.getPreviousGrade().equals("null")) {
                            gradeOutput += "(" + entry.getPreviousGrade() + ")";
                        }

                        System.out.println(entry.getCourseCode() + "|" + entry.getEnrollment().getId() + "|" +
                                           entry.getAcademicYear() + "|" + entry.getSemester() + "|" + gradeOutput);
                    }
                    break;
            }
        }

        for (Lecturer lecturer : lecturers) {
            System.out.println(lecturer);
        }
        
        for (Course course : courses) {
            System.out.println(course.getCode() + "|" + course.getName() + "|" + course.getCredit() + "|" + course.getPassingGrade());
        }

        for (Student student : students) {
            System.out.println(student);
        }                  

        for (Enrollment enrollment : enrollments) {
            if(enrollment.getPreviousGrade().equals("") || enrollment.getPreviousGrade().equals("null")){
                System.out.println(enrollment.getCode() + "|" + enrollment.getId() + "|" + enrollment.getAcademicYear() + "|" + enrollment.getSemester() + "|" + enrollment.getGrade());
            } else {
                System.out.println(enrollment.getCode() + "|" + enrollment.getId() + "|" + enrollment.getAcademicYear() + "|" + enrollment.getSemester() + "|" + enrollment.getGrade()+"("+enrollment.getPreviousGrade()+")");
            }
        } 
    }

    private static boolean isDuplicateCourse(Course course, List<Course> courses) {
        return courses.stream().anyMatch(c -> c.getCode().equals(course.getCode()));
    }

    private static boolean isDuplicateStudent(Student student, List<Student> students) {
        return students.stream().anyMatch(s -> s.getId().equals(student.getId()));
    }

    private static boolean isDuplicateLecturer(Lecturer lecturer, List<Lecturer> lecturers) {
        return lecturers.stream().anyMatch(e -> e.getName().equals(lecturer.getName()));
    }

    private static Enrollment findEnrollment(String courseCode, String studentId, String academicYear, String semester, List<Enrollment> enrollments) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCode().equals(courseCode) && 
                enrollment.getId().equals(studentId) &&
                enrollment.getAcademicYear().equals(academicYear) &&
                enrollment.getSemester().equals(semester)) {
                return enrollment;
            }
        }
        return null;
    }

    private static Map<String, Enrollment> getLatestEnrollmentsForStudent(String studentId, List<Enrollment> allEnrollments) {
        Map<String, Enrollment> latestEnrollments = new HashMap<>();

        List<Enrollment> studentEnrollments = allEnrollments.stream()
                .filter(enr -> enr.getId().equals(studentId))
                .sorted(Comparator
                    .comparing(Enrollment::getAcademicYear)
                    .thenComparingInt(enr -> {
                        if ("odd".equalsIgnoreCase(enr.getSemester())) return 0;
                        if ("even".equalsIgnoreCase(enr.getSemester())) return 1;
                        return 2;
                    })
                )
                .collect(Collectors.toList());

        for (Enrollment enrollment : studentEnrollments) {
            latestEnrollments.put(enrollment.getCode(), enrollment);
        }
        return latestEnrollments;
    }


    private static double calculateGPA(String studentId, List<Enrollment> enrollmentsToConsider, List<Course> courses) {
        double totalScore = 0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollmentsToConsider) {
            if (enrollment.getGrade() != null && !enrollment.getGrade().equals("None")) {
                Course course = courses.stream()
                                    .filter(c -> c.getCode().equals(enrollment.getCode()))
                                    .findFirst().orElse(null);
                if (course != null) {
                    totalScore += convertGradeToScore(enrollment.getGrade()) * course.getCredit();
                    totalCredits += course.getCredit();
                }
            }
        }

        if (totalCredits == 0) {
            return 0;
        }

        return totalScore / totalCredits;
    }
        
      
    private static int calculateTotalCredits(String studentId, List<Enrollment> enrollmentsToConsider, List<Course> courses) {
        int totalCredits = 0;

        for (Enrollment enrollment : enrollmentsToConsider) {
            Course course = courses.stream()
                                .filter(c -> c.getCode().equals(enrollment.getCode()))
                                .findFirst().orElse(null);
            if (course != null) {
                totalCredits += course.getCredit();
            }
        }
        return totalCredits;
    }
       
    private static double convertGradeToScore(String grade) {
        switch (grade) {
            case "A":
                return 4.0;
            case "AB":
                return 3.5;
            case "B":
                return 3.0;
            case "BC":
                return 2.5;
            case "C":
                return 2.0;
            case "D":
                return 1.0;
            case "E":
                return 0.0;
            default:
                return 0.0;
        }
    }
}
