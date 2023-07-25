package com.meng.school_classes.repos;

import com.meng.school_classes.domain.Course;
import com.meng.school_classes.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findFirstByTeacher(Teacher teacher);

}
