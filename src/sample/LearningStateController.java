package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class LearningStateController implements Initializable {

    @FXML
    ListView setList;
    @FXML
    Button knowButton;
    @FXML
    Button explainButton;
    @FXML
    Label flashcardLabel;
    @FXML
    Button nextButton;
    @FXML
    Rectangle rectangleShape;

    SQLHandler sqlHandler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flashcardLabel.setVisible(false);
        knowButton.setVisible(false);
        explainButton.setVisible(false);
        rectangleShape.setVisible(false);
        nextButton.setVisible(false);

        ObservableList<String> items = FXCollections.observableArrayList();
        sqlHandler = new SQLHandler();
        flashcardLabel.setWrapText(true);
        try {
            ResultSet sets = sqlHandler.getSets();

            while(sets.next()){
                items.add(sets.getString("TABLE_NAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setList.setItems(items);

        setList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previousSelection, String selection) {
                if(setList.getSelectionModel().getSelectedItem() != null) {
                    int howMany = numberBox.display("Choose learning sample", "How many words?");
                    String setName = setList.getSelectionModel().getSelectedItem().toString();
                    learningHandler(setName, howMany);
                }
            }
        });
    }

    public void backToMainMenu(ActionEvent event) throws IOException {

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));

        window.setScene(new Scene(root));
        window.show();
    }

    public void learningHandler(String tableName, int howMany){
        rectangleShape.setVisible(true);
        ArrayList<Pair<String, String>> wordList = new ArrayList();

        //SELECTING 'howMany' RANDOM WORDS FROM THE SELECTED SET
        try {
            ArrayList<Pair<String, String> > temp = new ArrayList();
            ResultSet words = sqlHandler.getWords(tableName);
            while(words.next()){
                temp.add(new Pair(words.getString("word"), words.getString("explanation")));
            }
            howMany = Math.min(howMany, temp.size());
            Collections.shuffle(temp);
            for(int i=0; i<howMany; i++){
                wordList.add(temp.get(i));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        flashcardLabel.setVisible(true);
        knowButton.setVisible(true);
        explainButton.setVisible(true);
        flashcardLabel.setText(wordList.get(0).getKey());

        //HANDLING THE LEARNING PART

        knowButton.setOnAction(e -> {
            wordList.remove(0);
            if(wordList.size() == 0){
                flashcardLabel.setText("GOOD JOB!");
                knowButton.setVisible(false);
                explainButton.setVisible(false);
                setList.getSelectionModel().clearSelection();
            }
            else{
                flashcardLabel.setText(wordList.get(0).getKey());
            }
        });
        explainButton.setOnAction(e -> {
            Pair<String,String> temp = new Pair(wordList.get(0).getKey(), wordList.get(0).getValue());
            flashcardLabel.setText(wordList.get(0).getValue());
            wordList.add(temp);
            knowButton.setVisible(false);
            explainButton.setVisible(false);
            nextButton.setVisible(true);
        });
        nextButton.setOnAction(e -> {
            knowButton.setVisible(true);
            explainButton.setVisible(true);
            nextButton.setVisible(false);
            wordList.remove(0);
            flashcardLabel.setText(wordList.get(0).getKey());
        });

    }
}
