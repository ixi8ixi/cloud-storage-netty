package com.ix8oio8xi.client;

import com.ix8oio8xi.client.network.Network;
import com.ix8oio8xi.client.ui.LoginController;
import com.ix8oio8xi.config.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Network network = new Network();
        try {
            if (network.sync(Config.CONNECTION_TIMEOUT_SECONDS)) {
                loadScene(stage, network);
            } else {
                shutdown(network);
            }
        } catch (InterruptedException e) {
            shutdown(network);
        }
    }

    private static void loadScene(Stage stage, Network network) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("hello-view.fxml"));
        LoginController controller = new LoginController(network);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> network.shutdown());
        stage.show();
    }

    private void shutdown(Network network) {
        network.shutdown();
        Platform.exit();
    }
}