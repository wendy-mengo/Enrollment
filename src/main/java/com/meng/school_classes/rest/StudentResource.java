package com.meng.school_classes.rest;


import com.meng.school_classes.model.CourseDTO;
import com.meng.school_classes.model.CourseVO;
import com.meng.school_classes.model.StudentDTO;
import com.meng.school_classes.model.StudentVO;
import com.meng.school_classes.model.TeacherDTO;
import com.meng.school_classes.model.TeacherVO;
import com.meng.school_classes.service.CourseService;
import com.meng.school_classes.service.StudentService;
import com.meng.school_classes.service.TeacherService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/api/students", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentResource {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;


    public StudentResource(final StudentService studentService, TeacherService teacherService, CourseService courseService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(studentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createStudent(@RequestBody @Valid final StudentDTO studentDTO) {
        final Long createdId = studentService.create(studentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateStudent(@PathVariable(name = "id") final Long id,
                                              @RequestBody @Valid final StudentDTO studentDTO) {
        studentService.update(id, studentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteStudent(@PathVariable(name = "id") final Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enroll")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> enroll(@RequestBody @Valid final StudentVO studentVO) {
        StudentDTO studentDTO = new StudentDTO();
        List<Long> courses = new ArrayList<Long>();
        // create teacher,

        for (CourseVO courseVO : studentVO.getCourses()) {
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setFirstName(courseVO.getTeacher().getFirstName());
            teacherDTO.setLastName(courseVO.getTeacher().getLastName());
            final Long teacherId = teacherService.create(teacherDTO);

            // add teacher to course and create course
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseName(courseVO.getCourseName());
            courseDTO.setDescription(courseVO.getDescription());
            courseDTO.setTeacher(teacherId);
            final Long courseId = courseService.create(courseDTO);


            courses.add(courseId);
        }
        //add course to student
        studentDTO.setCourses(courses);
        // create student
        studentDTO.setFirstName(studentVO.getFirstName());
        studentDTO.setLastName(studentVO.getLastName());
        final Long studentId = studentService.create(studentDTO);
        return new ResponseEntity<>(studentId, HttpStatus.CREATED);
    }

    @GetMapping("/listAll/{id}")
    public ResponseEntity<StudentVO> listAll(@PathVariable(name = "id") final Long id) {
        StudentDTO studentDTO = studentService.get(id);
        StudentVO studentVO = new StudentVO();

        studentVO.setFirstName(studentDTO.getFirstName());
        studentVO.setLastName(studentDTO.getLastName());

        List<CourseVO> courses = new ArrayList<>();


        for (Long courseId : studentDTO.getCourses()) {
            CourseDTO courseDTO = courseService.get(courseId);

            CourseVO courseVO = new CourseVO();
            courseVO.setCourseName(courseDTO.getCourseName());
            courseVO.setDescription(courseDTO.getDescription());

            TeacherDTO teacherDTO = teacherService.get(courseDTO.getTeacher());
            TeacherVO teacherVO = new TeacherVO();
            teacherVO.setFirstName(teacherDTO.getFirstName());
            teacherVO.setLastName(teacherDTO.getLastName());

            courseVO.setTeacher(teacherVO);

            courses.add(courseVO);
        }

        studentVO.setCourses(courses);
        return ResponseEntity.ok(studentVO);
    }
    //  StudentVO studentVO = new StudentVO();
    //    studentVO.setFirstName(studentDTO.getFirstName());
    //    studentVO.setLastName(studentDTO.getLastName());

    //     for(CourseDTO courseDTO : studentDTO.getCourses()) {

    //   courseVO.getCourseName();
    //   courseVO.getDescription();

    // TeacherVO teacherVO = new TeacherVO();
    //  teacherVO.getFirstName();
    //  teacherVO.getLastName();
    // }
}
