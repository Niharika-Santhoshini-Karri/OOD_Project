package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import Model.CarIssueTM;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CarIssueController {
    public TextField txt_issid;
    public DatePicker txt_isu_date;
    public TextField txt_name;
    public TextField txt_title;
    public ComboBox mem_is_id;
    public ComboBox car_id;
    public TableView<CarIssueTM> bk_ssue_tbl;
    public AnchorPane bk_iss;
    private Connection connection;

    //JDBC
    private PreparedStatement selectALl;
    private PreparedStatement selectmemID;
    private PreparedStatement selectbkdtl;
    private PreparedStatement table;
    private PreparedStatement delete;

    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        bk_ssue_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueId"));
        bk_ssue_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        bk_ssue_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bk_ssue_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("carId"));
        txt_isu_date.setValue(LocalDate.now());
        txt_isu_date.setEditable(false);
        txt_isu_date.setDisable(true);
        txt_issid.setDisable(true);
        txt_isu_date.getEditor().setDisable(true);
        txt_isu_date.getEditor().setStyle("-fx-opacity: 1;");
        txt_name.setDisable(true);
        txt_title.setEditable(false);
        txt_title.setDisable(true);

        try {
            connection = DBConnection.getInstance().getConnection();
            ObservableList<CarIssueTM> issue = bk_ssue_tbl.getItems();

            selectALl = connection.prepareStatement("SELECT * FROM issuetb");
            selectmemID = connection.prepareStatement("select name from memberdetail where id=?");
            selectbkdtl = connection.prepareStatement("select model,status from cardetail where id=?");
            table = connection.prepareStatement("INSERT INTO issuetb values(?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM issuetb WHERE issueId=?");
            ResultSet rst = selectALl.executeQuery();

            while (rst.next()) {
                System.out.println("display");
                issue.add(new CarIssueTM(rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)));
            }

            bk_ssue_tbl.setItems(issue);
            mem_is_id.getItems().clear();
            ObservableList cmbmembers = mem_is_id.getItems();
            String sql2 = "select id from memberdetail";
            PreparedStatement pstm1 = connection.prepareStatement(sql2);
            ResultSet rst1 = pstm1.executeQuery();

            while (rst1.next()) {
                cmbmembers.add(rst1.getString(1));
            }

            car_id.getItems().clear();
            ObservableList cmbcars = car_id.getItems();
            String sql3 = "select id from cardetail";
            PreparedStatement pstm2 = connection.prepareStatement(sql3);
            ResultSet rst2 = pstm2.executeQuery();
            while (rst2.next()) {
                cmbcars.add(rst2.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mem_is_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                if (mem_is_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = mem_is_id.getSelectionModel().getSelectedItem();
                    if (selectedItem.equals(null) || mem_is_id.getSelectionModel().isEmpty()) {
                        return;
                    }
                    try {
                        selectmemID.setString(1, selectedItem.toString());
                        ResultSet rst = selectmemID.executeQuery();

                        if (rst.next()) {
                            txt_name.setText(rst.getString(1));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        car_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (car_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = car_id.getSelectionModel().getSelectedItem();

                    try {
                        txt_title.clear();
                        selectbkdtl.setString(1, selectedItem.toString());
                        ResultSet rst = selectbkdtl.executeQuery();

                        if (rst.next()) {
                            if (rst.getString(2).equals("Available")) {
                                txt_title.setText(rst.getString(1));
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR,
                                        "This car isn't available!",
                                        ButtonType.OK);
                                car_id.setValue("");
//                                car_id.getItems().clear();
                                Optional<ButtonType> buttonType = alert.showAndWait();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //button new action
    public void new_action(ActionEvent actionEvent) throws SQLException {
        txt_title.clear();
        txt_name.clear();
        txt_issid.setDisable(false);
        txt_issid.setEditable(false);
        mem_is_id.getSelectionModel().clearSelection();
        car_id.getSelectionModel().clearSelection();
        txt_isu_date.setPromptText("Issue Date");

        String sql = "Select max(issueId) from issuetb";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        String ids = null;
        int maxId = 0;

        if (rst.next()) {
            maxId = rst.getInt(1) +1;
            ids = Integer.toString(maxId);
            System.out.println("id issue "+maxId);
            // do something with the id
        } else {
            System.out.println(" no issue");
            // handle case where the ResultSet is empty
        }
//        maxId = maxId + 1;
//        String id = "";
//        if (maxId < 10) {
//            id = "I00" + maxId;
//        } else if (maxId < 100) {
//            id = "I0" + maxId;
//        } else {
//            id = "I" + maxId;
//        }

        txt_issid.setText(ids);

    }

    //button add action
    public void add_Action(ActionEvent actionEvent) throws SQLException {

        ObservableList<CarIssueTM> issued = FXCollections.observableList(DB.issued);
        ObservableList<CarsTM> cars = FXCollections.observableList(DB.cars);

        if (txt_issid.getText().isEmpty() ||
//                car_id.getItems().isEmpty() || mem_is_id.getItems().isEmpty()
                car_id.getSelectionModel().isEmpty() ||
                mem_is_id.getSelectionModel().isEmpty()
                || txt_isu_date.getValue().toString().equals(null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill your details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            String memberId = (String) mem_is_id.getSelectionModel().getSelectedItem();
            String carId = (String) car_id.getSelectionModel().getSelectedItem();
            issued.add(new CarIssueTM(txt_issid.getText(), txt_isu_date.getValue().toString(), memberId, carId));

            try {
                table.setString(1, txt_issid.getText());
                table.setString(2, txt_isu_date.getValue().toString());
                table.setString(3, (String) mem_is_id.getSelectionModel().getSelectedItem());
                table.setString(4, (String) car_id.getSelectionModel().getSelectedItem());
                int affectedRows = table.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Data insertion complete");
                    String sql2 = "Update cardetail SET status=? where id=?";
                    PreparedStatement pstm2 = connection.prepareStatement(sql2);
                    String id = (String) car_id.getSelectionModel().getSelectedItem();
                    pstm2.setString(1, "Unavailable");
                    pstm2.setString(2, id);
                    int affected = pstm2.executeUpdate();

                    if (affected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Issued Successfully.",
                                ButtonType.OK);
                        txt_name.setText("");
                        txt_title.setText("");
                        Optional<ButtonType> buttonType = alert.showAndWait();
                    } else {
                        System.out.println("ERROR");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            bk_ssue_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //button delete action
    public void delete_Action(ActionEvent actionEvent) throws SQLException {
        
        CarIssueTM selectedItem = bk_ssue_tbl.getSelectionModel().getSelectedItem();
        if (bk_ssue_tbl.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a raw.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            try {
                delete.setString(1, selectedItem.getIssueId());
                delete.executeUpdate();

                String sql2 = "Update cardetail SET status=? where id=?";
                PreparedStatement pstm2 = connection.prepareStatement(sql2);
                String id = (String) car_id.getSelectionModel().getSelectedItem();
                pstm2.setString(1, "Available");
                pstm2.setString(2, id);
                pstm2.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        try {
            bk_ssue_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void back_click(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.bk_iss.getScene().getWindow();
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
        if (mouseEvent.getSource() instanceof ComboBox) {
            ComboBox icon = (ComboBox) mouseEvent.getSource();
            icon.setCursor(Cursor.HAND);
        }
        if (mouseEvent.getSource() instanceof DatePicker) {
            DatePicker icon = (DatePicker) mouseEvent.getSource();
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
        if (mouseEvent.getSource() instanceof ComboBox) {
            ComboBox icon = (ComboBox) mouseEvent.getSource();
            icon.setCursor(Cursor.HAND);
        }
        if (mouseEvent.getSource() instanceof DatePicker) {
            DatePicker icon = (DatePicker) mouseEvent.getSource();
            icon.setCursor(Cursor.HAND);
        }
    }
}
