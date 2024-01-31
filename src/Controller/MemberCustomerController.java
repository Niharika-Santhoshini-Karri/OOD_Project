package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Model.MemberTM;
import db.DB;
import db.DBConnection;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MemberCustomerController {

	public TextField mem_id;
	public TextField txt_enterEmail;
	public TextField mem_email;
	public TextField mem_nme;
	public TextField mem_addss;
	public TextField mem_num;
	public TextField mem_password;

	public ImageView img_bk;
	public AnchorPane root;
	public Button btn_add;


	private Connection connection;
	private PreparedStatement selectall;
	private PreparedStatement newIdQuery;
	private PreparedStatement addToTable;
	private PreparedStatement updateQuarary;
	private PreparedStatement deleteQuarary;
	private PreparedStatement slectmemID;
	private PreparedStatement slectmemEmail;

	public void initialize() throws ClassNotFoundException {
	
		mem_id.setDisable(true);
		Class.forName("com.mysql.jdbc.Driver");
		mem_email.setEditable(false);
		mem_email.setDisable(true);

	
		try {
			connection = DBConnection.getInstance().getConnection();

			selectall = connection.prepareStatement("select * from memberdetail");
			slectmemID = connection.prepareStatement("select * from memberdetail where id=?");
			slectmemEmail = connection.prepareStatement("select * from memberdetail where email=?");
			newIdQuery = connection.prepareStatement("SELECT id FROM memberdetail");

			addToTable = connection.prepareStatement("INSERT INTO memberdetail() values(?,?,?,?)");
			updateQuarary = connection.prepareStatement(
					"UPDATE memberdetail SET email=?, name=? , address=? , contact=?, password=? where id=?");
			deleteQuarary = connection.prepareStatement("DELETE from memberdetail where id=?");

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}


	public void btn_email_sbt(ActionEvent actionEvent) throws SQLException {
		if (!txt_enterEmail.getText().isEmpty()) {
			ObservableList<MemberTM> members = FXCollections.observableList(DB.members);
			slectmemEmail.setString(1, txt_enterEmail.getText());
			ResultSet rst = slectmemEmail.executeQuery();
			if (!rst.next()) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "please enter valid Email to update your details",
						ButtonType.OK);
				Optional<ButtonType> buttonType = alert.showAndWait();
				return;
			} else {
				System.out.println("btn update member");
				mem_id.setText(rst.getString(1));
				mem_email.setText(rst.getString(2));
				mem_nme.setText(rst.getString(3));
				mem_addss.setText(rst.getString(4));
				mem_num.setText(rst.getString(5));
				mem_password.setText(rst.getString(6));
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "please enter Email to update your details", ButtonType.OK);
			Optional<ButtonType> buttonType = alert.showAndWait();
		}
	}

	public void btn_update(ActionEvent actionEvent) throws SQLException {
		mem_email.setEditable(false);
		mem_email.setDisable(true);
		if (mem_id.getText().isEmpty() || mem_email.getText().isEmpty() || mem_nme.getText().isEmpty()
				|| mem_addss.getText().isEmpty() || mem_num.getText().isEmpty() || mem_password.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter your details to update", ButtonType.OK);
			Optional<ButtonType> buttonType = alert.showAndWait();
			return;
		}

		if (!mem_nme.getText().matches("\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+")) {
			new Alert(Alert.AlertType.ERROR, "Entered name is Invalid").show();
			return;
		}
		if (!mem_addss.getText().matches("^\\b[A-Za-z0-9/,\\s]+.$")) {
			new Alert(Alert.AlertType.ERROR, "Entered address is Invalid").show();
			return;
		}
		if (!mem_num.getText().matches("\\d{10}")) {
			new Alert(Alert.AlertType.ERROR, "Entered phone is Invalid").show();
			return;
		}
		if (!mem_password.getText().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$")) {
			new Alert(Alert.AlertType.ERROR, "Entered password is Invalid").show();
			return;
		}

		try {
			updateQuarary.setString(1, mem_email.getText());
			updateQuarary.setString(2, mem_nme.getText());
			updateQuarary.setString(3, mem_addss.getText());
			updateQuarary.setString(4, mem_num.getText());
			updateQuarary.setString(5, mem_password.getText());
			updateQuarary.setString(6, mem_id.getText());
			int affected = updateQuarary.executeUpdate();

			if (affected > 0) {

				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Record updated successfully!!", ButtonType.OK);
				System.out.println("clear fields");
//                                clearFields();

				Optional<ButtonType> buttonType = alert.showAndWait();
				clearFields();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Update error!", ButtonType.OK);
				Optional<ButtonType> buttonType = alert.showAndWait();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}




	public void clearFields() {
		mem_id.clear();
		mem_email.clear();
		mem_nme.clear();
		mem_num.clear();
		mem_addss.clear();
		mem_password.clear();
	}

	public void img_back(MouseEvent event) throws IOException {
		URL resource = this.getClass().getResource("/View/CustomerFunction.fxml");
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
