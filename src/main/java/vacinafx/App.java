package vacinafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vacinafx.controller.ControllerPrincipal;
import vacinafx.dao.ConexaoFactoryPostgreSQL;
import vacinafx.dao.core.ConnectionFactory;
import vacinafx.dao.core.DAOFactory;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private ControllerPrincipal controller;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vacinafx/JanelaMain.fxml"));
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Programa 14 - Tela 2");
            controller = loader.getController();

            ConnectionFactory connection = new ConexaoFactoryPostgreSQL("localhost:5432/poov", "postgres",
                    "Kokorozzdew1455");
            DAOFactory daoFactory = new DAOFactory(connection);

            controller.setDAOFactory(daoFactory);
            controller.atualizarTela();
            stage.show();
        } catch (IOException e) {
            System.out.println("[ERRO AO CRIAR TELA]");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }

}