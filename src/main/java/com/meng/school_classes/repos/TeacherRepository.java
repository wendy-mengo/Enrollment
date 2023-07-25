package com.meng.school_classes.repos;

import com.meng.school_classes.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
