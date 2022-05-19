package com.example.bl_lab1.controllers;

import com.example.bl_lab1.dto.IdDto;
import com.example.bl_lab1.dto.SectionDto;
import com.example.bl_lab1.dto.VersionDto;
import com.example.bl_lab1.model.SectionEntity;
import com.example.bl_lab1.service.ArticleService;
import com.example.bl_lab1.service.SectionService;
import com.example.bl_lab1.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("version")
public class VersionController {
    private final VersionService service;
    private final SectionService sectionService;
    private final ArticleService articleService;

    public VersionController(VersionService service, SectionService sectionService, ArticleService articleService) {
        this.service = service;
        this.sectionService = sectionService;
        this.articleService = articleService;
    }

    @Operation(summary = "Сохранение новой версии ")
    @PostMapping("save")
    public void save(@RequestBody VersionDto data) throws Exception {
        Integer articleId = articleService.getIdByName(data.getArticleName());
        SectionEntity section = sectionService.getSectionByArticleIdAndIndex(articleId, data.getSectionIndex());
        if (data.getUsername() == null) {
            service.saveChangesByUnauthorizedUser(data.getNewText(), section);
        } else service.saveChangesByAuthorizedUser(data.getNewText(), data.getUsername(), section);
    }

    @Operation(summary = "Получение истории версий")
    @GetMapping("all")
    public ResponseEntity getAll() {
        return ResponseEntity.ok(service.getListOfWaitingUpdates());
    }

    @Operation(summary = "Получение последней версии текста")
    @PostMapping("last")
    public ResponseEntity getLast(@RequestBody SectionDto data) {
        Integer articleId = articleService.getIdByName(data.getArticleName());
        SectionEntity section = sectionService.getSectionByArticleIdAndIndex(articleId, data.getSectionIndex());
        return ResponseEntity.ok(service.getTextOfLastUpdateBySection(section));
    }

    @Operation(summary = "Получение предпоследней версии текста")
    @PostMapping("previous")
    public ResponseEntity getPrevious(@RequestBody SectionDto data) {
        Integer articleId = articleService.getIdByName(data.getArticleName());
        SectionEntity section = sectionService.getSectionByArticleIdAndIndex(articleId, data.getSectionIndex());
        return ResponseEntity.ok(service.getTextOfLastApprovedVersion(section));
    }

    @Operation(summary = "Подтверждение")
    @PostMapping("approve")
    public void approve(@RequestBody IdDto data) {
        service.approve(data.getId());
    }

    @Operation(summary = "Отмена")
    @PostMapping("decline")
    public void decline(@RequestBody IdDto data) throws Exception {
        service.decline(data.getId());
    }
}
