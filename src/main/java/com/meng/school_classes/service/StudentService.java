package com.meng.school_classes.service;

import com.meng.school_classes.domain.Course;
import com.meng.school_classes.domain.Student;
import com.meng.school_classes.model.StudentDTO;
import com.meng.school_classes.repos.CourseRepository;
import com.meng.school_classes.repos.StudentRepository;
import com.meng.school_classes.util.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(final StudentRepository studentRepository,
            final CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<StudentDTO> findAll() {
        final List<Student> students = studentRepository.findAll(Sort.by("id"));
        return students.stream()
                .map(student -> mapToDTO(student, new StudentDTO()))
                .toList();
    }

    public StudentDTO get(final Long id) {
        return studentRepository.findById(id)
                .map(student -> mapToDTO(student, new StudentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final StudentDTO studentDTO) {
        final Student student = new Student();
        mapToEntity(studentDTO, student);
        return studentRepository.save(student).getId();
    }

    public void update(final Long id, final StudentDTO studentDTO) {
        final Student student = studentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(studentDTO, student);
        studentRepository.save(student);
    }

    public void delete(final Long id) {
        studentRepository.deleteById(id);
    }

    private StudentDTO mapToDTO(final Student student, final StudentDTO studentDTO) {
        studentDTO.setId(student.getId());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setCourses(student.getCourses().stream()
                .map(course -> course.getId())
                .toList());
        return studentDTO;
    }

    private Student mapToEntity(final StudentDTO studentDTO, final Student student) {
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        final List<Course> courses = courseRepository.findAllById(
                studentDTO.getCourses() == null ? Collections.emptyList() : studentDTO.getCourses());
        if (courses.size() != (studentDTO.getCourses() == null ? 0 : studentDTO.getCourses().size())) {
            throw new NotFoundException("one of courses not found");
        }
        student.setCourses(courses.stream().collect(Collectors.toSet()));
        return student;
    }

}
