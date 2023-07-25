package com.meng.school_classes.service;

import com.meng.school_classes.domain.Course;
import com.meng.school_classes.domain.Student;
import com.meng.school_classes.domain.Teacher;
import com.meng.school_classes.model.CourseDTO;
import com.meng.school_classes.repos.CourseRepository;
import com.meng.school_classes.repos.StudentRepository;
import com.meng.school_classes.repos.TeacherRepository;
import com.meng.school_classes.util.NotFoundException;
import com.meng.school_classes.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public CourseService(final CourseRepository courseRepository,
            final TeacherRepository teacherRepository, final StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public List<CourseDTO> findAll() {
        final List<Course> courses = courseRepository.findAll(Sort.by("id"));
        return courses.stream()
                .map(course -> mapToDTO(course, new CourseDTO()))
                .toList();
    }

    public CourseDTO get(final Long id) {
        return courseRepository.findById(id)
                .map(course -> mapToDTO(course, new CourseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CourseDTO courseDTO) {
        final Course course = new Course();
        mapToEntity(courseDTO, course);
        return courseRepository.save(course).getId();
    }

    public void update(final Long id, final CourseDTO courseDTO) {
        final Course course = courseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(courseDTO, course);
        courseRepository.save(course);
    }

    public void delete(final Long id) {
        final Course course = courseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        studentRepository.findAllByCourses(course)
                .forEach(student -> student.getCourses().remove(course));
        courseRepository.delete(course);
    }

    private CourseDTO mapToDTO(final Course course, final CourseDTO courseDTO) {
        courseDTO.setId(course.getId());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setTeacher(course.getTeacher() == null ? null : course.getTeacher().getId());
        return courseDTO;
    }

    private Course mapToEntity(final CourseDTO courseDTO, final Course course) {
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        final Teacher teacher = courseDTO.getTeacher() == null ? null : teacherRepository.findById(courseDTO.getTeacher())
                .orElseThrow(() -> new NotFoundException("teacher not found"));
        course.setTeacher(teacher);
        return course;
    }

    public String getReferencedWarning(final Long id) {
        final Course course = courseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Student coursesStudent = studentRepository.findFirstByCourses(course);
        if (coursesStudent != null) {
            return WebUtils.getMessage("course.student.courses.referenced", coursesStudent.getId());
        }
        return null;
    }

}
