package com.meng.school_classes.controller;

import com.meng.school_classes.model.TeacherDTO;
import com.meng.school_classes.service.TeacherService;
import com.meng.school_classes.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(final TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "teacher/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("teacher") final TeacherDTO teacherDTO) {
        return "teacher/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("teacher") @Valid final TeacherDTO teacherDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "teacher/add";
        }
        teacherService.create(teacherDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("teacher.create.success"));
        return "redirect:/teachers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("teacher", teacherService.get(id));
        return "teacher/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("teacher") @Valid final TeacherDTO teacherDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "teacher/edit";
        }
        teacherService.update(id, teacherDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("teacher.update.success"));
        return "redirect:/teachers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = teacherService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            teacherService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("teacher.delete.success"));
        }
        return "redirect:/teachers";
    }

}
