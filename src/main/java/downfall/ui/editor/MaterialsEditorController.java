// Copyright 2023 Prokhor Kalinin
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package downfall.ui.editor;

import downfall.fx.ImageChooserButton;
import downfall.fx.LogoTableColumn;
import downfall.fx.SimpleTableEditor;
import downfall.fx.fetcher.Fetcher;
import downfall.fx.fetcher.SimpleMaterialTemplateFetcher;
import downfall.realm.template.VisualMaterialTemplate;
import downfall.ui.StageController;
import downfall.util.Configurator;
import downfall.util.DownfallUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Controller class for the Materials Editor.
 *  Controls /fxml/MaterialsEditor.fxml and is annotated with @FXML where it references that FXML file.
 */
public class MaterialsEditorController implements StageController {
    @FXML
    private TextField exportPriceTextField;

    @FXML
    private TextField importPriceTextField;

    @FXML
    private SimpleTableEditor<VisualMaterialTemplate> materialTemplateEditor;

    @FXML
    private CheckBox isExportableCheckBox;

    @FXML
    private AnchorPane leftAnchor;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button okButton;

    @FXML
    private TextField pathToGFXTextField;

    @FXML
    private ImageChooserButton fileChooserButton;

    @FXML
    private SplitPane rootPane;

    ObservableList<VisualMaterialTemplate> materials = FXCollections.emptyObservableList();

    Stage stage;

    /**
     * Initialize method that is called automatically after the FXML has finished loading. Initializes all UI elements before they are displayed
     */
    @FXML
    public void initialize() {
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add(DownfallUtil.MAIN_CSS_RESOURCE);

        //retrieving the list of material templates in current rules.
        materials = FXCollections.observableList(Configurator.getInstance().getRules().getMaterialTemplates());

        Fetcher fetcher = new SimpleMaterialTemplateFetcher();
        //fetcher.initialize(stage, materials);
        materialTemplateEditor.setFetcher(fetcher);
        materialTemplateEditor.getTableView().setItems(materials);
        materialTemplateEditor.getTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<VisualMaterialTemplate, String> materialNameColumn = new TableColumn<>();

        //Creating Table Columns for the editor to display
        materialNameColumn.setText("Material");
        materialNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());

        LogoTableColumn<VisualMaterialTemplate> materialImageColumn = new LogoTableColumn<>();
        materialImageColumn.setDefaultSizePolicy();

        materialImageColumn.setCellValueFactory(param -> param.getValue().pathToGFXProperty());

        materialTemplateEditor.getTableView().getColumns().addAll(materialImageColumn, materialNameColumn);
        fileChooserButton.setOutput(pathToGFXTextField);

        //listening for changes in selection made by the user in materials table view to update data displayed.
        materialTemplateEditor.getTableView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue == null || validateMaterial(oldValue)) {
                if(oldValue != null)
                    unbindMaterial(oldValue);
                displayMaterial(newValue);
            }
        });

        disableTradable();

        isExportableCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                enableTradable();
            else
                disableTradable();
        });

        okButton.setOnAction(e-> stage.close());

    }

    /**
     * Lightweight mutator method.
     * Always should be called before the editor is displayed to the user.
     * @param stage Stage on which this controller is displayed.
     */
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Disables the exportPriceTextField as you should not set a price for non-tradeable materials.
     */
    private void disableTradable() {
        exportPriceTextField.setDisable(true);
    }

    /**
     * Enables the exportPriceTextField as you should be abel to set a price for tradeable materials.
     */
    private void enableTradable() {
        exportPriceTextField.setDisable(false);
    }

    /**
     * Unbinds the properties of a given materials from all TextFields and CheckBoxes.
     * @param template template to be unbound.
     */
    private void unbindMaterial(VisualMaterialTemplate template) {
        nameTextField.textProperty().unbindBidirectional(template.nameProperty());
        isExportableCheckBox.selectedProperty().unbindBidirectional(template.isExportableProperty());
        pathToGFXTextField.textProperty().unbindBidirectional(template.pathToGFXProperty());
        exportPriceTextField.textProperty().unbindBidirectional(template.defExportPriceProperty());
        importPriceTextField.textProperty().unbindBidirectional(template.defImportPriceProperty());
    }

    /**
     * Binds the properties of a given material to all TextFields and CheckBoxes.
     * @param materialTemplate template to be displayed
     */
    private void displayMaterial(VisualMaterialTemplate materialTemplate) {
        nameTextField.textProperty().bindBidirectional(materialTemplate.nameProperty());
        isExportableCheckBox.selectedProperty().bindBidirectional(materialTemplate.isExportableProperty());
        pathToGFXTextField.textProperty().bindBidirectional(materialTemplate.pathToGFXProperty());
        exportPriceTextField.textProperty().bindBidirectional(materialTemplate.defExportPriceProperty(), new NumberStringConverter());
        importPriceTextField.textProperty().bindBidirectional(materialTemplate.defImportPriceProperty(), new NumberStringConverter());
    }

    /**
     * Checks that it can read a file that is set as a pathToGFX.
     * @param materialTemplate VisualMaterialTemplate to be validated.
     * @return true if file can be read. False if it cannot be read.
     */
    private boolean validateMaterial(VisualMaterialTemplate materialTemplate) {
        File checkFile = new File(pathToGFXTextField.getText());
        if(checkFile.canRead()) {
            return true;
        } else {
            Logger.getLogger(DownfallUtil.DEFAULT_LOGGER).log(Level.WARNING, "Could not Find file: "+pathToGFXTextField.getText()+" when trying to check integrity during material template save.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not find file: "+pathToGFXTextField.getText());
            alert.setContentText(pathToGFXTextField.getText()+" must be a path to a valid readable file");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Removes the old Material Templates from the current Ruleset, adds all materialTemplates in materials field, and then force saves the rules at a lastLoadedRules location.
     */
    @Deprecated
    private void saveChanges() {
        Configurator.getInstance().getRules().getMaterialTemplates().clear();
        Configurator.getInstance().getRules().getMaterialTemplates().addAll(materials);
        Configurator.getInstance().saveRules();
    }
}
