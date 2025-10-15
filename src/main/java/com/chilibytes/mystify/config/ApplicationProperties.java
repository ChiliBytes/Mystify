package com.chilibytes.mystify.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@ToString
@Component
@PropertySource("classpath:application.properties")
public class ApplicationProperties {
    @Value("${app.version.code:1.0.0}")
    private String versionCode;

    @Value("${app.version.name:1.0.0}")
    private String versionName;

    @Value("${app.name:Mystify App}")
    private String name;

    @Value("${app.control.left-panel.button.size:24px}")
    private String appControlLeftPanelButtonSize;

    @Value("${app.control.left-panel.button.style}")
    private String appControlLeftPanelButtonStyle;

    @Value("${app.scripts.python.version}")
    private String appScriptsPythonVersion;

    @Value("${app.scripts.python.base.path}")
    private String appScriptsPythonBasePath;

    @Value("${app.workspace.default.input:/opt/mystify/}")
    private String appWorkspaceDefaultInput;

    @Value("${app.workspace.default.output:/opt/mystify/}")
    private String appWorkspaceDefaultOutput;

    @Value("${app.core.images.allowed.extensions:jpg,jpeg,png,svg,JPG,JPEG,PNG,SVG}")
    private String[] allowedExtensions;

    @Value("${dev.enable.test.button:false}")
    private boolean testsEnabled;

    public List<String> getAppImagesAllowedExtensions() {
        return Arrays.asList(getAllowedExtensions());
    }

    public List<String> getAppImagesAllowedWildCards(List<String> allowedExtensions) {
        return allowedExtensions.stream()
                .map(extension -> "*." + extension)
                .toList();
    }
}
