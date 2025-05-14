package com.study.ssm.handler;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.ssm.bean.Fileses;
import com.study.ssm.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileHandler {

    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);
    @Autowired
    private FileService fileService;

    @GetMapping("/list")
    public String listFiles(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            Model model
    ) {
        try {
            // 使用PageHelper进行分页
            PageHelper.startPage(page, pageSize);

            List<Fileses> files;
            if (category == null || category.isEmpty() || "查询全部".equals(category)) {
                files = fileService.getAllFiles();
            } else {
                files = fileService.getFilesByCategory(category);
            }

            // 使用PageInfo包装查询结果
            PageInfo<Fileses> pageInfo = new PageInfo<>(files);

            // 获取文件类型列表用于过滤
            List<String> fileTypes = fileService.getAllFileTypes();

            model.addAttribute("files", files);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("category", category);
            model.addAttribute("fileTypes", fileTypes != null ? fileTypes : new ArrayList<>());

            // 添加额外的分页参数
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalPages", pageInfo.getPages());
            model.addAttribute("totalItems", pageInfo.getTotal());
            model.addAttribute("hasNext", pageInfo.isHasNextPage());
            model.addAttribute("hasPrevious", pageInfo.isHasPreviousPage());

        } catch (Exception e) {
            logger.error("Error fetching files", e);
            model.addAttribute("errorMessage", "获取文件列表时发生错误: " + e.getMessage());
        }

        return "files/list";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "files/upload";
    }

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploader") String uploader
    ) {
        fileService.uploadFile(file, uploader);
        return "redirect:/files/list";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") int id) throws IOException {
        Fileses file = fileService.getFileById(id);
        Path path = Paths.get(file.getPath()); // 这里现在获取的是本地文件系统路径

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        fileService.download(file);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}