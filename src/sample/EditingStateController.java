package sample;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditingStateController implements Initializable {
    @FXML
    private ListView<String> setList;
    @FXML
    private TableView currentSet;
    @FXML
    private TableColumn keywordColumn;
    @FXML
    private TableColumn explanationColumn;
    @FXML
    private TextField newKeywordText;
    @FXML
    private TextField newExplanationText;

    private ObservableList<Flashcard> data;

    private SQLHandler sqlHandler;

    Integer tempItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tempItem = -1;
        setList.setEditable(true);
        setList.setCellFactory(TextFieldListCell.forListView());

        currentSet.setEditable(true);
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };


        keywordColumn.setEditable(true);
        keywordColumn.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("keyWord"));
        keywordColumn.setCellFactory(cellFactory);
        keywordColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Flashcard, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Flashcard, String> e) {
                String oldWord = e.getRowValue().getKeyWord();
                ((Flashcard) e.getTableView().getItems().get(e.getTablePosition().getRow())).setKeyWord(e.getNewValue());
                String tableName = setList.getSelectionModel().getSelectedItem().toString();
                String oldExplanation = e.getTableView().getItems().get(e.getTablePosition().getRow()).getExplanation();
                int dbId = -1;

                //FINDING THE ID OF THE ROW
                try {
                    dbId = sqlHandler.getId(tableName, oldWord, oldExplanation);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }

                //EDITING THE ROW IN DATABASE
                try {
                    sqlHandler.editRow(tableName, dbId, e.getNewValue(), oldExplanation);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
        });
        explanationColumn.setEditable(true);
        explanationColumn.setCellValueFactory(new PropertyValueFactory<Flashcard, String>("explanation"));
        explanationColumn.setCellFactory(cellFactory);
        explanationColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Flashcard, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Flashcard, String> e) {
                String oldWord = e.getRowValue().getKeyWord();
                String oldExplanation = e.getRowValue().getExplanation();
                ((Flashcard) e.getTableView().getItems().get(e.getTablePosition().getRow())).setExplanation(e.getNewValue());
                String tableName = setList.getSelectionModel().getSelectedItem().toString();
                int dbId = -1;

                //FINDING THE ID OF THE ROW

                try {
                    dbId = sqlHandler.getId(tableName, oldWord, oldExplanation);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }

                //EDITING THE ROW IN DATABASE
                try {
                    sqlHandler.editRow(tableName, dbId, oldWord, e.getNewValue());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
        });

        //READING SET TITLE FROM DATABASE AND ADDING THEM TO THE  LISTVIEW
        ObservableList<String> items = FXCollections.observableArrayList();
        sqlHandler = new SQLHandler();
        try {
            ResultSet sets = sqlHandler.getSets();

            while(sets.next()){
                //System.out.println(sets.getString("TABLE_NAME"));
                items.add(sets.getString("TABLE_NAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        setList.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> event) {
                String oldValue = setList.getSelectionModel().getSelectedItem();
                String newValue = event.getNewValue();
                if(!newValue.contains(" ")) {
                    setList.getItems().set(event.getIndex(), newValue);

                    //CHANGING THE NAME OF THE SET IN DATABASE
                    try {
                        if (oldValue != newValue)
                            sqlHandler.editSetName(oldValue, newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    setList.getSelectionModel().clearSelection();
                }
                else{
                    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                    Popup p = new Popup();
                    Label label = new Label("Name of the set can't contain spaces!");
                    label.setStyle(" -fx-background-color: red;");
                    label.setMinWidth(200);
                    label.setMinHeight(50);
                    label.setTextAlignment(TextAlignment.CENTER);
                    label.setFont(new Font("Arial", 30));
                    p.getContent().add(label);
                    p.setAutoHide(true);
                    p.show(window);
                }
            }
        });

        setList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previousSelection, String selection) {

                //LOADING AND DISPLAYING THE WORDS FROM THE SELECTED SET
                data = FXCollections.observableArrayList();
                try {
                    ResultSet words = sqlHandler.getWords(selection);
                    if(words != null) {
                        while (words.next()) {
                            data.add(new Flashcard(words.getString("word"), words.getString("explanation")));
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                currentSet.setItems(data);

                //CLOSING THE DATABASE
                try {
                    SQLHandler.con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        setList.setItems(items);

    }

    public void backToMainMenu(ActionEvent event) throws IOException {

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));

        window.setScene(new Scene(root));
        window.show();
    }

    public void deleteSet(){
        //DELETING THE SET FROM LISTVIEW AND DATABASE
        if(ConfirmBox.display("Delete set?", "Are you sure you want to permanently delete this set?")) {
            int id = setList.getSelectionModel().getSelectedIndex();
            try {
                sqlHandler.deleteSet(setList.getItems().get(id));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            setList.getItems().remove(id);
        }
    }

    public void deleteWord(){
        //DELETING THE WORD FROM THE TABLEVIEW AND THE DATABASE

        String tableName = setList.getSelectionModel().getSelectedItem().toString();
        if(!currentSet.getSelectionModel().isEmpty()) {
            int id = currentSet.getSelectionModel().getSelectedIndex();
            int dbId = -1;
            try {
                dbId = sqlHandler.getId(tableName, ((Flashcard) currentSet.getItems().get(id)).getKeyWord(), ((Flashcard) currentSet.getItems().get(id)).getExplanation());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                sqlHandler.removeContent(tableName, dbId);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            currentSet.getItems().remove(id);
        }
    }

    public void addSet(ActionEvent event){
        if(!setList.getItems().contains("NewSet")) {
            setList.getItems().add("NewSet");
            //ADDING NEW SET TO THE DATABASE
            try {
                sqlHandler.createSet("NewSet");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Popup p = new Popup();
            Label label = new Label("Rename NewSet first!");
            label.setStyle(" -fx-background-color: #ff0000;");
            label.setMinWidth(200);
            label.setMinHeight(50);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setFont(new Font("Arial", 30));
            p.getContent().add(label);
            //p.setX(window.getX()+window.getScene().getX()+window.getScene().getWidth()/2-200);
            //p.setY(window.getY()+window.getScene().getY()+window.getScene().getHeight()/2-50);
            p.setAutoHide(true);
            p.show(window);
        }
    }

    public void addButtonPress(ActionEvent event){

        //CHECKING IF THE INPUT IS CORRECT
        if(!newKeywordText.getText().isBlank() && !newExplanationText.getText().isBlank()) {
            String tableName = setList.getSelectionModel().getSelectedItem().toString();
            data.add(new Flashcard(newKeywordText.getText(), newExplanationText.getText()));
            //ADDING THE WORD TO DATABASE
            try {
                sqlHandler.addContent(tableName, newKeywordText.getText(), newExplanationText.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            newExplanationText.clear();
            newKeywordText.clear();
        }
        else{
            //POPUP FOR INCORRECT INPUT
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Popup p = new Popup();
            Label label = new Label("Fields cannot be empty!");
            label.setStyle(" -fx-background-color: #ff0000;");
            label.setMinWidth(200);
            label.setMinHeight(50);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setFont(new Font("Arial", 30));
            p.getContent().add(label);
            //p.setX(window.getX()+window.getScene().getX()+window.getScene().getWidth()/2-200);
            //p.setY(window.getY()+window.getScene().getY()+window.getScene().getHeight()/2-50);
            p.setAutoHide(true);
            p.show(window);
        }
    }
}
