package com.chilibytes.mystify.core.feature.collage.ui;

import com.chilibytes.mystify.core.feature.BaseFeatureDialog;
import com.chilibytes.mystify.core.feature.collage.service.CollageEventHandlerService;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class CollageDialog extends BaseFeatureDialog<CollageEventHandlerService, CollageEventHandlerService.CollageDialogControls> {

    private final String createButtonText = "Create Collage";
    private final String fileNameLabel = "Collage Name";
    private final String dialogTitle = "Collage Maker";
    private final String selectButtonText = "Select source images folder";

    public CollageDialog(CollageEventHandlerService collageEventHandlerService) {
        super(collageEventHandlerService);
    }

    @Override
    protected CollageEventHandlerService.CollageDialogControls createControls() {
        return new CollageEventHandlerService.CollageDialogControls(
                btnSelectInputFolder,
                btnSelectOutputFolder,
                txtInputFolderPath,
                txtOutputFolderPath,
                lblFileName,
                txtFileName,
                btnCreate,
                btnCancel
        );
    }

    @Override
    protected void configureEventHandlers(Stage collageStage) {
        CollageEventHandlerService.CollageDialogControls collageControls = createControls();
        eventHandlerService.setupEventHandlers(collageStage, collageControls);
    }
}
