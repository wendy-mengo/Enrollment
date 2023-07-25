package com.meng.school_classes.repos;

import com.meng.school_classes.domain.Course;
import com.meng.school_classes.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByCourses(Course course);

    Student findFirstByCourses(Course course);

}
