package com.ix8oio8xi.client.ui;

import com.ix8oio8xi.client.commands.processors.ClientDownloadProcessor;
import com.ix8oio8xi.client.commands.processors.ClientMoveProcessor;
import com.ix8oio8xi.client.commands.processors.ClientUploadProcessor;
import com.ix8oio8xi.client.network.Network;
import io.netty.buffer.ByteBuf;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    private static final List<String> stateList = List.of("Download", "Upload", "Move");
    private static final String EMPTY_CHOICE_BOX = "Choose option";

    @FXML
    public TextField fromField;
    @FXML
    public TextField toField;
    @FXML
    public Label errorLabel;
    @FXML
    public ChoiceBox<String> operations;

    private final Network network;

    private final UiCallback callback = new UiCallback() {
        @Override
        public void postMessage(boolean success, String message) {
            Platform.runLater(() -> {
                String color = success ? "green" : "red";
                errorLabel.setStyle("-fx-text-fill: " + color + ";");
                errorLabel.setText(message);
            });
        }
    };

    public ProfileController(Network network) {
        this.network = network;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> options = FXCollections.observableArrayList(stateList);
        operations.setItems(options);
        network.setCallback(callback);
        operations.setValue(EMPTY_CHOICE_BOX);
    }

    public void enterFrom(ActionEvent actionEvent) {
        toField.requestFocus();
    }

    public void doAction(ActionEvent actionEvent) {
        String state = operations.getValue();
        if (!stateList.contains(state)) {
            callback.postMessage(false, "Illegal operation selected");
            return;
        }

        ByteBuf message = tryMakeMessage(state);
        if (message != null) {
            network.sendMessage(message);
        }
    }

    private ByteBuf tryMakeMessage(String state) {
        try {
            return switch (state) {
                case "Download" -> ClientDownloadProcessor.makeMessage(fromField.getText(), toField.getText());
                case "Upload" -> ClientUploadProcessor.makeMessage(fromField.getText(), toField.getText());
                case "Move" -> ClientMoveProcessor.makeMessage(fromField.getText(), toField.getText());
                default -> throw new AssertionError("State `"
                        + state + "` is not contained in the list of states.");
            };
        } catch (IOException | InvalidPathException e) {
            callback.postMessage(false, "Error while download: " + e.getMessage());
            return null;
        }
    }
}
