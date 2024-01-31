package Controller;

 

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

 

import javafx.scene.control.*;
import Model.CarReturnTM;
import db.DBConnection;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

 

public class CarReturnController {
    public AnchorPane Returnroot;
    public TextField txt_issu_date;
    public TextField txt_fine;
    public DatePicker txt_rt_date;
    public TableView<CarReturnTM> rt_tbl;
    public ComboBox cmb_issue_id;
    private Connection connection;
    private PreparedStatement insertPstm;
    private static int counter = 1;
    private String returnCounterId = "R00";

 

    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

 

        rt_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        rt_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        rt_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        rt_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("penalty"));
        rt_tbl.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("issueId"));
        txt_issu_date.setEditable(false);
        txt_issu_date.setDisable(true);
        txt_rt_date.setEditable(false);

 

        try {
            connection = DBConnection.getInstance().getConnection();
            ObservableList<CarReturnTM> returns = rt_tbl.getItems();

 

            String sql = "SELECT * from returndetail";
            PreparedStatement pstm = connection.prepareStatement(sql);

 

            insertPstm = connection.prepareStatement("INSERT INTO returndetail(issuedDate,returnedDate,penalty,issueid) VALUES (?,?,?,?)");

 

            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                float penalty = Float.parseFloat(rst.getString(4));
                returns.add(new CarReturnTM(rst.getString(1), rst.getString(2), rst.getString(3), penalty, rst.getString(5)));
            }
            rt_tbl.setItems(returns);

 

            cmb_issue_id.getItems().clear();
            ObservableList cmbIssue = cmb_issue_id.getItems();
            String sql2 = "select issueId from issuetb where issueId not in (select issueId from returndetail)";
            PreparedStatement pstm1 = connection.prepareStatement(sql2);
            ResultSet rst1 = pstm1.executeQuery();

 

            while (rst1.next()) {
                cmbIssue.add(rst1.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

 

        cmb_issue_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (cmb_issue_id.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                try {
                    String sql = "select date from issuetb where issueId =?";
                    PreparedStatement pstm = connection.prepareStatement(sql);
                    pstm.setString(1, (String) cmb_issue_id.getSelectionModel().getSelectedItem());
                    ResultSet rst = pstm.executeQuery();
                    if (rst.next()) {
                        txt_issu_date.setText(rst.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        txt_rt_date.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {

 

                if (txt_rt_date.getValue() == null) {
                    return;
                }
                LocalDate returned = txt_rt_date.getValue();
                LocalDate issued = LocalDate.parse(txt_issu_date.getText());

 

                if (returned != null && issued != null && returned.isBefore(issued)) {
                    // if the selected date is before the issue date, show an error message and reset the value to null
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Please Select return date greater than issued date.",
                            ButtonType.OK);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    System.out.println("Fill empty fields!");
                    return;
                }

 

                Date date1 = Date.valueOf(issued);
                Date date2 = Date.valueOf(returned);

 

                long diff = date2.getTime() - date1.getTime();

 

                System.out.println(TimeUnit.MILLISECONDS.toDays(diff));
                int dateCount = (int) TimeUnit.MILLISECONDS.toDays(diff);
                float penalty = 0;

 

                if (dateCount > 14) {
                    penalty = dateCount * 15;
                }
                txt_fine.setText(Float.toString(penalty));
            }
        });
    }

 

    //btn new action
    public void btn_new(ActionEvent actionEvent) {
        txt_fine.clear();
        txt_issu_date.setPromptText("Issue date");
        cmb_issue_id.getSelectionModel().clearSelection();
        txt_rt_date.setPromptText("Returned date");
    }

 

    //btn add action
    public void btn_add_inveb(ActionEvent actionEvent) throws SQLException {
        if (cmb_issue_id.getSelectionModel().isEmpty() ||
                txt_issu_date.getText().isEmpty() ||
                txt_rt_date.getValue() == null ||
                txt_fine.getText().isEmpty()
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            System.out.println("Fill empty fields!");
            return;
        }

 

 

        String issueID = (String) cmb_issue_id.getSelectionModel().getSelectedItem();
//        String sql = "INSERT INTO returndetail VALUES (?,?,?,?)";
//        PreparedStatement pstm = connection.prepareStatement(sql);
        String maxIdSql = "select id from returndetail";
        PreparedStatement newIdQuery = connection.prepareStatement(maxIdSql);
        ResultSet rst = newIdQuery.executeQuery();

 

//        String ids = null;
//        int maxId = 0;
//
//        while (rst.next()) {
//            ids = rst.getString(1);
//
//            int id = Integer.parseInt(ids.replace("R", ""));
//            if (id > maxId) {
//                maxId = id;
//            }
//        }
//        maxId = maxId + 1;
//        String ID = "";
//        if (maxId < 10) {
//            ID = "R00" + maxId;
//        } else if (maxId < 100) {
//            ID = "R0" + maxId;
//        } else {
//            ID = "R" + maxId;
//        }

 

//        insertPstm.setString(1, ID);

 

        insertPstm.setString(1, txt_issu_date.getText());
        insertPstm.setString(2, txt_rt_date.getValue().toString());
        insertPstm.setString(3, txt_fine.getText());
        insertPstm.setString(4, (String) cmb_issue_id.getSelectionModel().getSelectedItem());
        int affectedRows = insertPstm.executeUpdate();

 

        if (affectedRows > 0) {
            System.out.println("done");
            String sql4 = "Update cardetail SET status=? where id=?";
            PreparedStatement pstm2 = connection.prepareStatement(sql4);

 

            String sql3 = "select carId from issuetb where issueId=?";
            PreparedStatement pstm3 = connection.prepareStatement(sql3);
            pstm3.setString(1, (String) cmb_issue_id.getSelectionModel().getSelectedItem());
            ResultSet rst3 = pstm3.executeQuery();
            String id = null;

 

            if (rst3.next()) {
                id = rst3.getString(1);
                System.out.println(id + "done");
            }
            
      
      
          
           
            
            
          
            
            pstm2.setString(1, "Available");
            pstm2.setString(2, id);
            int affected = pstm2.executeUpdate();
            if (affected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Status updated.",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            }
        } else {
            System.out.println("Something went wrong");
        }
        try {
            rt_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmb_issue_id.getItems().remove(issueID);
        cmb_issue_id.getSelectionModel().clearSelection();
        txt_issu_date.clear();
        txt_fine.clear();
        txt_rt_date.getEditor().clear();
    }

 

    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.Returnroot.getScene().getWindow();
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
            icon.setCursor(Cursor.DEFAULT);
        }
        if (mouseEvent.getSource() instanceof DatePicker) {
            DatePicker icon = (DatePicker) mouseEvent.getSource();
            icon.setCursor(Cursor.DEFAULT);
        }
    }
}