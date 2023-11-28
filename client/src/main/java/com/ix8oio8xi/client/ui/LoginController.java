package com.ix8oio8xi.client.ui;

import com.ix8oio8xi.client.ClientApplication;
import com.ix8oio8xi.client.commands.processors.ClientLoginProcessor;
import com.ix8oio8xi.client.network.Network;
import com.ix8oio8xi.config.Config;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final Network network;

    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorLabel;

    private final UiCallback callback = new UiCallback() {
        @Override
        public void postMessage(boolean success, String message) {
            Platform.runLater(() -> {
                String color = success ? "green" : "red";
                errorLabel.setStyle("-fx-text-fill: " + color + ";");
                errorLabel.setText(message);

                if (success) {
                    loadProfileScene();
                }
            });
        }
    };

    public LoginController(Network network) {
        this.network = network;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network.setCallback(callback);
    }

    public void loginButtonClicked(ActionEvent ignoredActionEvent) {
        String login = usernameField.getText();
        String password = passwordField.getText();
        network.sendMessage(ClientLoginProcessor.makeMessage(login, password));
        passwordField.requestFocus();
    }

    public void enterLogin(ActionEvent ignoredActionEvent) {
        passwordField.requestFocus();
    }

    private void loadProfileScene() {
        FXMLLoader loader = new FXMLLoader(ClientApplication.class.getResource("profile.fxml"));
        ProfileController controller = new ProfileController(network);
        loader.setController(controller);
        try {
            Parent profileScene = loader.load();
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            stage.setScene(new Scene(profileScene, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
            stage.show();
        } catch (IOException e) {
            System.err.println("Unable to load profile fxml");
        }
    }
}