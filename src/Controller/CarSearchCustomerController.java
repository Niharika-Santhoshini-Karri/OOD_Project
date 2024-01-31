package Controller;

 

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

 

import Model.CarsTM;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

 

public class CarSearchCustomerController {
    public TextField bk_sch;
    public TableView<CarsTM> tbl_bk;
    public AnchorPane sch_root;
    private Connection connection;

 

    public void initialize() {

 

        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("model"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("owner"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

 

        try {
            connection = DBConnection.getInstance().getConnection();

            ObservableList<CarsTM> cars = tbl_bk.getItems();

 

            String sql = "SELECT * from cardetail";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                System.out.println("Start");
                cars.add(new CarsTM(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
            }
            tbl_bk.setItems(cars);
 
        } catch (SQLException e) {
            e.printStackTrace();
        }

 

 

        bk_sch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String searchText = bk_sch.getText();

 

                try {
                    tbl_bk.getItems().clear();

 

 

                    try {
                        connection = DBConnection.getInstance().getConnection();

                        String sql = "Select * FROM cardetail where id like ? OR model like ? OR owner like ? OR status like ?";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        String like = "%" + searchText + "%";
                        pstm.setString(1, like);
                        pstm.setString(2, like);
                        pstm.setString(3, like);
                        String like2 = searchText + "%";
                        pstm.setString(4, like2);

 

                        ResultSet rst = pstm.executeQuery();

 

                        ObservableList tbl = tbl_bk.getItems();
                        tbl.clear();

 

                        while (rst.next()) {
                            tbl.add(new CarsTM(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
                        }

 

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

 

 

                } catch (NullPointerException n) {
                    return;
                }

 

            }
        });
    }

 

    public void img_bk(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/CustomerFunction.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.sch_root.getScene().getWindow();
        primaryStage.setScene(scene);

 

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

 

    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

 

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

 

            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
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

 