package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DBConnection;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;



public class CustomerHomeController {

    public ImageView img_bk;

    public AnchorPane root;


    public TextField txt_email;
    public TextField  txt_name;
    public TextField txt_addrs;
    public TextField txt_phnum;
    public TextField txt_password;
    public TextField txt_confirm_password;
    public TextField txt_passcode;

    private Connection connection;
    private PreparedStatement selectall;
    private PreparedStatement newIdQuery;
    private PreparedStatement addToTable;
    private PreparedStatement userPresent;



    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DBConnection.getInstance().getConnection();
            addToTable = connection.prepareStatement("INSERT INTO memberdetail(email,name,address, contact, password) values(?,?,?,?,?)");
            userPresent = connection.prepareStatement("select * from memberdetail where email=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void btn_sbt() throws SQLException, IOException {
        System.out.println("ffff");
        Parent root = null;
        System.out.println("hii "+ txt_password.getText());
        if(!txt_email.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
        {
            new Alert(Alert.AlertType.ERROR, "Entered Email is Invalid").show();
            return;
        }
        if(!txt_name.getText().matches("\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+"))
        {
            new Alert(Alert.AlertType.ERROR, "Entered name is Invalid").show();
            return;
        }
        if(!txt_addrs.getText().matches("^\\b[A-Za-z0-9/,\\s]+.$"))
        {
            new Alert(Alert.AlertType.ERROR, "Entered address is Invalid").show();
            return;
        }
        if(!txt_phnum.getText().matches("\\d{10}"))
        {
            new Alert(Alert.AlertType.ERROR, "Entered phone is Invalid").show();
            return;
        }
        if(!txt_password.getText().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"))
        {
            new Alert(Alert.AlertType.ERROR, "Entered password is Invalid").show();
            return;
        }
        if(txt_confirm_password.getText().isEmpty())
        {
            new Alert(Alert.AlertType.ERROR, "Enter Conformation password").show();
            return;
        }

        if(txt_password.getText().equals( txt_confirm_password.getText())){
            System.out.println("hello  "+ txt_password.getText());
            userPresent.setString(1,txt_email.getText());
            ResultSet rst = userPresent.executeQuery();
            if(rst.next()){
                new Alert(Alert.AlertType.ERROR, "User Already exist").show();
                return;
            }

            addToTable.setString(1, txt_email.getText());
            addToTable.setString(2, txt_name.getText());
            addToTable.setString(3, txt_addrs.getText());
            addToTable.setString(4, txt_phnum.getText());
            addToTable.setString(5, txt_password.getText());
            int affectedRows = addToTable.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data loaded");
            } else {
                System.out.println("Something went wrong");
            }


            root = FXMLLoader.load(this.getClass().getResource("/View/CustomerFunction.fxml"));
            if (root != null) {
                System.out.println("ffff");
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();
            }
        }else{
            new Alert(Alert.AlertType.ERROR, "Password Mismatches").show();

        }

    }
    public void img_bk(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/CustomerFormView.fxml");
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
        if (mouseEvent.getSource() instanceof Button) {
            Button icon = (Button) mouseEvent.getSource();
            icon.setCursor(Cursor.HAND);
        }
    }

    public void setDefaultCursor(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            icon.setCursor(Cursor.DEFAULT);
        }
        if (mouseEvent.getSource() instanceof Button) {
            Button icon = (Button) mouseEvent.getSource();
            icon.setCursor(Cursor.DEFAULT);
        }
    }
}