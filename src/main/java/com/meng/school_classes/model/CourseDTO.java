package com.meng.school_classes.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CourseDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String courseName;

    @Size(max = 255)
    private String description;

    @NotNull
    private Long teacher;

}
