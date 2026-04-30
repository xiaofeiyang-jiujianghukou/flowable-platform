package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
@Tag(name = "流程模型", description = "BPMN / CMMN / DMN 模型设计、保存与部署")
public class ModelController {

    private final RepositoryService repositoryService;
    private final CmmnRepositoryService cmmnRepositoryService;
    private final DmnRepositoryService dmnRepositoryService;

    public ModelController(RepositoryService repositoryService,
                           CmmnRepositoryService cmmnRepositoryService,
                           DmnRepositoryService dmnRepositoryService) {
        this.repositoryService = repositoryService;
        this.cmmnRepositoryService = cmmnRepositoryService;
        this.dmnRepositoryService = dmnRepositoryService;
    }

    @GetMapping
    @Operation(summary = "查询模型列表")
    public List<Map<String, Object>> list(
            @Parameter(description = "模型类型: bpmn/cmmn/dmn") @RequestParam(required = false) String type) {
        var query = repositoryService.createModelQuery().orderByLastUpdateTime().desc();
        if (type != null) query.modelCategory(type);
        return query.list().stream().map(this::toMap).toList();
    }

    @PostMapping
    @Operation(summary = "创建模型")
    public Map<String, Object> create(
            @Parameter(description = "模型名称") @RequestParam String name,
            @Parameter(description = "模型Key") @RequestParam String key,
            @Parameter(description = "模型类型") @RequestParam(defaultValue = "bpmn") ModelType type,
            @Parameter(description = "描述") @RequestParam(required = false) String description) {
        Model model = repositoryService.newModel();
        model.setName(name);
        model.setKey(key);
        model.setCategory(type.name().toLowerCase());
        if (description != null) model.setMetaInfo(description);
        repositoryService.saveModel(model);
        Map<String, Object> r = toMap(model);
        r.put("type", type.name().toLowerCase());
        return r;
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "获取模型详情")
    public Map<String, Object> getById(@PathVariable String modelId) {
        Model m = repositoryService.getModel(modelId);
        return m != null ? toMap(m) : Map.of();
    }

    @PutMapping("/{modelId}")
    @Operation(summary = "更新模型信息")
    public Map<String, Object> update(
            @PathVariable String modelId,
            @Parameter(description = "模型名称") @RequestParam(required = false) String name,
            @Parameter(description = "模型Key") @RequestParam(required = false) String key) {
        Model m = repositoryService.getModel(modelId);
        if (m == null) return Map.of("error", "not found");
        if (name != null) m.setName(name);
        if (key != null) m.setKey(key);
        repositoryService.saveModel(m);
        return toMap(m);
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "删除模型（级联删除部署）")
    public Map<String, String> delete(@PathVariable String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (model != null && model.getDeploymentId() != null) {
            repositoryService.deleteDeployment(model.getDeploymentId(), true);
        }
        repositoryService.deleteModel(modelId);
        return Map.of("result", "deleted");
    }

    @GetMapping("/{modelId}/source")
    @Operation(summary = "获取模型 XML 源码")
    public Map<String, Object> getSource(@PathVariable String modelId) {
        byte[] source = repositoryService.getModelEditorSource(modelId);
        return Map.of("xml", source != null ? new String(source, StandardCharsets.UTF_8) : "");
    }

    @PutMapping("/{modelId}/source")
    @Operation(summary = "保存模型 XML 源码")
    public Map<String, String> saveSource(
            @PathVariable String modelId,
            @Parameter(description = "XML 内容") @RequestBody String xml) {
        repositoryService.addModelEditorSource(modelId, xml.getBytes(StandardCharsets.UTF_8));
        return Map.of("result", "saved");
    }

    @GetMapping("/{modelId}/source-extra")
    @Operation(summary = "获取模型图 SVG")
    public Map<String, Object> getSourceExtra(@PathVariable String modelId) {
        byte[] extra = repositoryService.getModelEditorSourceExtra(modelId);
        return Map.of("svg", extra != null ? new String(extra, StandardCharsets.UTF_8) : "");
    }

    @PutMapping("/{modelId}/source-extra")
    @Operation(summary = "保存模型图 SVG")
    public Map<String, String> saveSourceExtra(
            @PathVariable String modelId,
            @Parameter(description = "SVG 内容") @RequestBody String svg) {
        repositoryService.addModelEditorSourceExtra(modelId, svg.getBytes(StandardCharsets.UTF_8));
        return Map.of("result", "saved");
    }

    @PostMapping("/{modelId}/deploy")
    @Operation(summary = "部署模型")
    public Map<String, Object> deploy(@PathVariable String modelId) {
        Model model = repositoryService.getModel(modelId);
        byte[] source = repositoryService.getModelEditorSource(modelId);
        if (source == null || source.length == 0) {
            throw new IllegalArgumentException("模型没有设计内容，请先保存 BPMN 流程");
        }
        String xml = new String(source, StandardCharsets.UTF_8);
        String type = model.getCategory() != null ? model.getCategory() : "bpmn";

        // bpmn-js 默认 isExecutable=false、id=Process_1、没有 name，需要修正
        if ("bpmn".equals(type)) {
            xml = fixBpmnXml(xml, model.getKey(), model.getName());
        }

        try {
            Map<String, Object> r = new HashMap<>();
            switch (type) {
                case "cmmn" -> {
                    var deployment = cmmnRepositoryService.createDeployment()
                            .name(model.getName())
                            .addString(model.getKey() + ".cmmn.xml", xml)
                            .deploy();
                    r.put("deploymentId", deployment.getId());
                }
                case "dmn" -> {
                    var deployment = dmnRepositoryService.createDeployment()
                            .name(model.getName())
                            .addString(model.getKey() + ".dmn.xml", xml)
                            .deploy();
                    r.put("deploymentId", deployment.getId());
                }
                default -> {
                    var deployment = repositoryService.createDeployment()
                            .name(model.getName())
                            .key(model.getKey())
                            .addString(model.getKey() + ".bpmn20.xml", xml)
                            .deploy();
                    r.put("deploymentId", deployment.getId());
                }
            }
            model.setDeploymentId((String) r.get("deploymentId"));
            repositoryService.saveModel(model);
            r.put("type", type);
            r.put("name", model.getName());
            return r;
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) cause = cause.getCause();
            String detail = cause.getMessage();
            if (detail != null && detail.contains("Errors while parsing:")) {
                detail = detail.substring(detail.indexOf("Errors while parsing:"));
                if (detail.length() > 500) detail = detail.substring(0, 500);
            }
            throw new RuntimeException(detail, e);
        }
    }

    private String fixBpmnXml(String xml, String processKey, String processName) {
        // 提取原始 process id（兼容 <process 和 <bpmn:process）
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
            "<(?:\\w+:)?process\\s+([^>]*)>").matcher(xml);
        if (!m.find()) return xml;

        String oldAttrs = m.group(1);
        String oldId = "Process_1";
        java.util.regex.Matcher idM = java.util.regex.Pattern.compile("id=\"([^\"]+)\"").matcher(oldAttrs);
        if (idM.find()) oldId = idM.group(1);

        // 重建 process 标签：注入 key/name/isExecutable
        String newAttrs = "id=\"" + processKey + "\" name=\"" + processName + "\" isExecutable=\"true\"";
        // 保留原有的其它属性（除了 id/name/isExecutable）
        String keep = oldAttrs.replaceAll("id=\"[^\"]*\"", "")
                              .replaceAll("name=\"[^\"]*\"", "")
                              .replaceAll("isExecutable=\"[^\"]*\"", "")
                              .replaceAll("\\s+", " ").trim();
        if (!keep.isEmpty()) newAttrs += " " + keep;

        String prefix = m.group(0).substring(0, m.group(0).indexOf(oldAttrs));
        xml = xml.replace(m.group(0), prefix + newAttrs + ">");

        // 替换所有 bpmnElement 引用
        xml = xml.replace("bpmnElement=\"" + oldId + "\"", "bpmnElement=\"" + processKey + "\"");

        return xml;
    }

    private Map<String, Object> toMap(Model m) {
        Map<String, Object> r = new HashMap<>();
        r.put("id", m.getId());
        r.put("name", m.getName());
        r.put("key", m.getKey());
        r.put("category", m.getCategory());
        r.put("version", m.getVersion());
        r.put("deploymentId", m.getDeploymentId());
        r.put("metaInfo", m.getMetaInfo());
        r.put("createTime", m.getCreateTime());
        r.put("lastUpdateTime", m.getLastUpdateTime());
        r.put("hasEditorSource", m.hasEditorSource());
        return r;
    }

    public enum ModelType {
        bpmn, cmmn, dmn
    }
}
