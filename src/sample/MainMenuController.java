package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onExit(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        if(!ConfirmBox.display("Exit", "Are you sure you want to quit?"))
            event.consume();
        else {
            //Here we can save some progress maybe
            window.close();
        }
    }

    public void enterLearningState(ActionEvent event) throws IOException {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent learningStateFxml = FXMLLoader.load(getClass().getResource("learningState.fxml"));

        window.setScene(new Scene(learningStateFxml));
        window.show();
    }

    public void enterEditState(ActionEvent event) throws IOException{

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent editingStateFxml = FXMLLoader.load(getClass().getResource("editingState.fxml"));

        window.setScene(new Scene(editingStateFxml));
        window.show();
    }
}
