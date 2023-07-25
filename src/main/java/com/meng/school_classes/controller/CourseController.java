package com.meng.school_classes.controller;

import com.meng.school_classes.domain.Teacher;
import com.meng.school_classes.model.CourseDTO;
import com.meng.school_classes.repos.TeacherRepository;
import com.meng.school_classes.service.CourseService;
import com.meng.school_classes.util.CustomCollectors;
import com.meng.school_classes.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final TeacherRepository teacherRepository;

    public CourseController(final CourseService courseService,
            final TeacherRepository teacherRepository) {
        this.courseService = courseService;
        this.teacherRepository = teacherRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("teacherValues", teacherRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Teacher::getId, Teacher::getFirstName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("course") final CourseDTO courseDTO) {
        return "course/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("course") @Valid final CourseDTO courseDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "course/add";
        }
        courseService.create(courseDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("course.create.success"));
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("course", courseService.get(id));
        return "course/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("course") @Valid final CourseDTO courseDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "course/edit";
        }
        courseService.update(id, courseDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("course.update.success"));
        return "redirect:/courses";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = courseService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            courseService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("course.delete.success"));
        }
        return "redirect:/courses";
    }

}
