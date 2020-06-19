package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class numberBox {
    public static int howMany;
    public static int display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        label.setAlignment(Pos.CENTER);

        TextField input = new TextField("0");
        input.setAlignment(Pos.CENTER);
        input.setMaxWidth(50);

        Button confirmButton = new Button("Confirm");
        confirmButton.setAlignment(Pos.CENTER);

        confirmButton.setOnAction(e -> {
            try {
                howMany = Integer.parseInt(input.getText());
                window.close();
            }
            catch(NumberFormatException exception){
                Popup p = new Popup();
                Label label1 = new Label("This has to be a number.");
                label1.setStyle(" -fx-background-color: #ff0000;");
                label1.setMinWidth(50);
                label1.setMinHeight(20);
                label1.setTextAlignment(TextAlignment.CENTER);
                label1.setFont(new Font("Arial", 15));
                p.getContent().add(label1);
                p.setX(window.getX()+window.getScene().getX()+window.getScene().getWidth()/2-90);
                p.setY(window.getY()+window.getScene().getY()+window.getScene().getHeight()/2-20);
                p.setAutoHide(true);
                p.show(window);
            }
        });


        VBox layout = new VBox(50);
        layout.getChildren().addAll(label, input, confirmButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return howMany;
    }
}
