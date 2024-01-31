package Controller;

 

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

 

import Model.CarsTM;
import db.DB;
import db.DBConnection;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

 

public class CarController {
    public TextField txt_bk_id;
    public TextField txt_bk_title;
    public TextField txt_bk_auth;
    public TextField txt_bk_st;
    public TableView<CarsTM> tbl_bk;
    public AnchorPane bk_root;
    public Button btn_add;
    private Connection connection;

 

    //JDBC
    private PreparedStatement selectall;
    private PreparedStatement selectID;
    private PreparedStatement newIdQuery;
    private PreparedStatement addToTable;
    private PreparedStatement updateQuarary;
    private PreparedStatement deleteQuarary;
    private PreparedStatement nextID;
    private PreparedStatement issueidtable;

    public void initialize() throws ClassNotFoundException {
        //disable id field
        txt_bk_id.setDisable(true);

 

        //load table
        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("model"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("owner"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

 

        Class.forName("com.mysql.jdbc.Driver");

 

        try {
            connection = DBConnection.getInstance().getConnection();
            selectall = connection.prepareStatement("SELECT * from cardetail");
            updateQuarary = connection.prepareStatement("UPDATE cardetail SET model=? , owner=? , status=? where id=?");
            selectID = connection.prepareStatement("select * from cardetail where id=?");
            addToTable = connection.prepareStatement("INSERT INTO cardetail values(?,?,?,?)");
            newIdQuery = connection.prepareStatement("SELECT id from cardetail");
            deleteQuarary = connection.prepareStatement("DELETE from cardetail where id=?");
            issueidtable=connection.prepareStatement("select  memberid from issuetb where carId=?");
            
//            nextID = connection.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME ='cardetail'");
            ObservableList<CarsTM> members = tbl_bk.getItems();
            ResultSet rst = selectall.executeQuery();
            while (rst.next()) {
                System.out.println("display");
                members.add(new CarsTM(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
            tbl_bk.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }

 

        tbl_bk.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CarsTM>() {
            @Override
            public void changed(ObservableValue<? extends CarsTM> observable, CarsTM oldValue, CarsTM newValue) {
                CarsTM selectedItem = tbl_bk.getSelectionModel().getSelectedItem();
                try {
                    connection = null;
                    try {
                        selectID.setString(1, selectedItem.getId());
                        ResultSet rst = selectID.executeQuery();

 

                        if (rst.next()) {
                            txt_bk_id.setText(rst.getString(1));
                            txt_bk_title.setText(rst.getString(2));
                            txt_bk_auth.setText(rst.getString(3));
                            txt_bk_st.setText(rst.getString(4));
                            txt_bk_id.setDisable(true);
                            btn_add.setText("Update");
                        }
                        btn_add.setText("Update");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException n) {
                    return;
                }
            }
        });
    }

 

    //button new action
    public void btn_new(ActionEvent actionEvent) throws SQLException {
        btn_add.setText("Add");
        txt_bk_st.setText("Available");
        txt_bk_st.setDisable(true);
        txt_bk_id.setDisable(false);
        txt_bk_id.setEditable(false);
        txt_bk_id.clear();
        txt_bk_auth.clear();
        txt_bk_title.clear();
        txt_bk_title.requestFocus();

 

//        ResultSet rst = newIdQuery.executeQuery();
        nextID = connection.prepareStatement("SELECT MAX(id) FROM cardetail");
        ResultSet rst = nextID.executeQuery();
        int  id=100;
        if (rst.next()) {
            id = rst.getInt(1) +1;
            System.out.println("id display "+id);
            // do something with the id
        } else {
            System.out.println(" no car");

 

        }

 

        String ids = Integer.toString(id);
        txt_bk_id.setText(ids);
    }

 

    //button add action
    public void btn_Add(ActionEvent actionEvent) throws SQLException {
        ObservableList<CarsTM> cars = FXCollections.observableList(DB.cars);

 

        if (txt_bk_id.getText().isEmpty() || txt_bk_title.getText().isEmpty() || txt_bk_auth.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill your details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        }

 

        //reg ex
        if (!(txt_bk_title.getText().matches("^\\b([A-Za-z.]+\\s?)+$") && txt_bk_auth.getText().matches("^\\b([A-Za-z.]+\\s?)+$"))) {
            new Alert(Alert.AlertType.ERROR, "Enter Valid Name").show();
            return;
        }
        if (btn_add.getText().equals("Add")) {
            addToTable.setString(1, txt_bk_id.getText());
            addToTable.setString(2, txt_bk_title.getText());
            addToTable.setString(3, txt_bk_auth.getText());
            addToTable.setString(4, txt_bk_st.getText());

 

            int affectedRows = addToTable.executeUpdate();

 

            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "car is add successfully.",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                System.out.println("Data insertion complete");
                clearFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Entered details are invalid.",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                return;
            }
        } else {
            if (btn_add.getText().equals("Update")) {
                for (int i = 0; i < cars.size(); i++) {
                    if (txt_bk_id.getText().equals(cars.get(i).getId())) {
                        updateQuarary.setString(1, txt_bk_title.getText());
                        updateQuarary.setString(2, txt_bk_auth.getText());
                        updateQuarary.setString(3, txt_bk_st.getText());
                        updateQuarary.setString(4, txt_bk_id.getText());
                        int affected = updateQuarary.executeUpdate();

 

                        if (affected > 0) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                    "Record updated!",
                                    ButtonType.OK);
                            Optional<ButtonType> buttonType = alert.showAndWait();

 

                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Update error!",
                                    ButtonType.OK);
                            Optional<ButtonType> buttonType = alert.showAndWait();
                        }
                    }
                }
                tbl_bk.setItems(cars);
            }
        }
        try {
            tbl_bk.getItems().clear();
//            clearFields();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

 

    //button delete action
    public void btn_dlt(ActionEvent actionEvent) throws SQLException {
    	System.out.println("It's me");
        CarsTM selectedItem = tbl_bk.getSelectionModel().getSelectedItem();
        if (tbl_bk.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a member.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
        	
        	 issueidtable.setString(1,selectedItem.getId());
             ResultSet rst1 = issueidtable.executeQuery();
             
        	if(rst1.next()==true) {
        		Alert alert = new Alert(Alert.AlertType.ERROR,
                        "This car is issued to a member.",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                return;
        		
        	}
        	else {
        		
        	
        	
            deleteQuarary.setString(1, selectedItem.getId());
            int affected = deleteQuarary.executeUpdate();

 

            if (affected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted successfully!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                clearFields();
            }
        	}
        }
        try {
            tbl_bk.getItems().clear();
//            clearFields();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

 

    public void clearFields(){
        System.out.println("clear car fields");
     txt_bk_id.clear();
     txt_bk_title.clear();
     txt_bk_auth.clear();
    }

 

    public void img_back(MouseEvent event) throws IOException {

 

        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.bk_root.getScene().getWindow();
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