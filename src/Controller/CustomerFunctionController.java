package Controller;

import java.io.IOException;
import java.net.URL;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomerFunctionController {
    public AnchorPane root;


    public void navigate(MouseEvent event) throws IOException {

        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            Parent root = null;

            switch (icon.getId()) {
                case "search":
                    System.out.println("admin form");
                    root = FXMLLoader.load(this.getClass().getResource("/View/CarSearchCustomerView.fxml"));
                    break;
                case "members":
                    root = FXMLLoader.load(this.getClass().getResource("/View/MembersCustomerView.fxml"));

                    break;
            }

            if (root != null) {
                System.out.println(root + "login");
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();
            }
        }
    }

    public void btn_back(MouseEvent event) throws IOException {

        URL resource = this.getClass().getResource("/View/Login.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void setHoverCursor(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            icon.setCursor(Cursor.HAND);
        }
    }

    public void setDefaultCursor(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            icon.setCursor(Cursor.DEFAULT);
        }
    }
}
