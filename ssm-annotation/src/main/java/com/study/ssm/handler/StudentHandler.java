package com.study.ssm.handler;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.ssm.bean.Student;
import com.study.ssm.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentHandler {

    @Autowired
    private StudentService studentService;


//     查询所有学生
    @GetMapping("/list")
    public String listStudents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        List<Student> students = studentService.getAllStudents();
        // 创建PageInfo对象，包含分页相关数据
        PageInfo<Student> pageInfo = new PageInfo<>(students);

        model.addAttribute("students", students);
        model.addAttribute("pageInfo", pageInfo);
        return "student/list";
    }

    // 跳转到添加页面
    @GetMapping("/add")
    public String toAddPage(Model model) {
        model.addAttribute("student", new Student());
        return "student/add";
    }

    // 添加学生
    @PostMapping("/add")
    public String addStudent(Student student) {
        studentService.addStudent(student);
        return "redirect:/student/list";
    }

    // 跳转到编辑页面
    @GetMapping("/edit/{id}")
    public String toEditPage(@PathVariable("id") String id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "student/edit";
    }

    // 更新学生信息
    @PutMapping("/update")
    public String updateStudent(Student student) {
        studentService.updateStudent(student);
        return "redirect:/student/list";
    }

    // 删除学生
    @DeleteMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") String id) {
        studentService.deleteStudentById(id);
        return "redirect:/student/list";
    }
}