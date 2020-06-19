package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));


        window.setTitle("Flashcards");
        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            if(!ConfirmBox.display("Exit", "Are you sure you want to quit?"))
                e.consume();
            else {
                //TO DO: SAVE PROGRESS?
                window.close();
            }
        });

        window.setScene(new Scene(root));
        window.show();
    }


    public static void main(String[] args) {
        /// INITIALISING ONE TABLE
        SQLHandler test = new SQLHandler();
        try {
            if(!test.getSets().next()) {
                test.createSet("English");
                test.addContent("English", "Hello", "way to greet people");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /// END
        launch(args);

    }
}
