package com.meng.school_classes.service;

import com.meng.school_classes.domain.Course;
import com.meng.school_classes.domain.Teacher;
import com.meng.school_classes.model.TeacherDTO;
import com.meng.school_classes.repos.CourseRepository;
import com.meng.school_classes.repos.TeacherRepository;
import com.meng.school_classes.util.NotFoundException;
import com.meng.school_classes.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public TeacherService(final TeacherRepository teacherRepository,
            final CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    public List<TeacherDTO> findAll() {
        final List<Teacher> teachers = teacherRepository.findAll(Sort.by("id"));
        return teachers.stream()
                .map(teacher -> mapToDTO(teacher, new TeacherDTO()))
                .toList();
    }

    public TeacherDTO get(final Long id) {
        return teacherRepository.findById(id)
                .map(teacher -> mapToDTO(teacher, new TeacherDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TeacherDTO teacherDTO) {
        final Teacher teacher = new Teacher();
        mapToEntity(teacherDTO, teacher);
        return teacherRepository.save(teacher).getId();
    }

    public void update(final Long id, final TeacherDTO teacherDTO) {
        final Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(teacherDTO, teacher);
        teacherRepository.save(teacher);
    }

    public void delete(final Long id) {
        teacherRepository.deleteById(id);
    }

    private TeacherDTO mapToDTO(final Teacher teacher, final TeacherDTO teacherDTO) {
        teacherDTO.setId(teacher.getId());
        teacherDTO.setFirstName(teacher.getFirstName());
        teacherDTO.setLastName(teacher.getLastName());
        return teacherDTO;
    }

    private Teacher mapToEntity(final TeacherDTO teacherDTO, final Teacher teacher) {
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        return teacher;
    }

    public String getReferencedWarning(final Long id) {
        final Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Course teacherCourse = courseRepository.findFirstByTeacher(teacher);
        if (teacherCourse != null) {
            return WebUtils.getMessage("teacher.course.teacher.referenced", teacherCourse.getId());
        }
        return null;
    }

}
