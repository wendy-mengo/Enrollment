package com.meng.school_classes.rest;

import com.meng.school_classes.model.TeacherDTO;
import com.meng.school_classes.service.TeacherService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
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


@RestController
@RequestMapping(value = "/api/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherResource {

    private final TeacherService teacherService;

    public TeacherResource(final TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(teacherService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTeacher(@RequestBody @Valid final TeacherDTO teacherDTO) {
        final Long createdId = teacherService.create(teacherDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTeacher(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TeacherDTO teacherDTO) {
        teacherService.update(id, teacherDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTeacher(@PathVariable(name = "id") final Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
