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

package downfall.fx;

import downfall.fx.fetcher.Fetcher;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *  JavaFX GUI element that contains a table, content of which can be edited with add and remove actions.
 * @param <T> type of item that is edited in
 */
public class SimpleTableEditor<T> extends VBox {
    TableView<T> tableView = new TableView<>();
    ToolBar toolBar = new ToolBar();
    Button addButton = new Button("Add");
    Button removeButton = new Button("Remove");
    Fetcher<T> fetcher;

    /**
     * Default constructor sets items and fetcher and columns to null
     */
    public SimpleTableEditor() {
        this(null, null);
    }

    /**
     * Constructor that sets columns to null
     * @param items items to be displayed by the TableView
     * @param fetcher a fetcher class that will be used when adding new items to the list
     */
    SimpleTableEditor(ObservableList<T> items, Fetcher<T> fetcher) {
        this(items, fetcher, null);
    }

    /**
     *
     * @param items items to be displayed by the TableView
     * @param fetcher a fetcher class that will be used when adding new items to the list
     * @param columns columns that will be added to the TableView
     */
    SimpleTableEditor(ObservableList<T> items, Fetcher<T> fetcher, TableColumn<T, ?>... columns) {
        //Add Error Checking;
        this.fetcher = fetcher;

        tableView.setEditable(true);

        VBox.setVgrow(tableView, Priority.ALWAYS);

        toolBar.getItems().addAll(addButton, removeButton);
        getChildren().addAll(toolBar, tableView);
        tableView.setItems(items);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addButton.setOnAction(event -> tableView.getItems().add(this.fetcher.retrieve()));

        removeButton.setOnAction(event -> {
            if(tableView.getSelectionModel().getSelectedItem() != null)
                tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
        });

        if(columns != null)
            tableView.getColumns().addAll(columns);
    }

    /**
     * Lightweight mutator. Sets items directly into the TableView
     * @param items items to be set
     */
    public void setItems(ObservableList<T> items) {
        tableView.setItems(items);
    }

    /**
     * Lightweight mutator.
     * @param fetcher fetcher whose retrieve method will be used to generate new items when adding them to the TableView
     */
    public void setFetcher(Fetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    /**
     * lightweight accessor
     * @return tableView that is used inside
     */
    public TableView<T> getTableView() {
        return tableView;
    }
}
