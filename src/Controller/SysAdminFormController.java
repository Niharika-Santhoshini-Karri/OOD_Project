package Controller;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import db.DBConnection;
import javafx.animation.TranslateTransition;
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

public class SysAdminFormController {

	
	public TextField txt_username;
	public TextField txt_password;

	public Button btn_sbt;

	public AnchorPane root;
	private Connection connection;
	private PreparedStatement selectall;
	private PreparedStatement selectPswd;

	public void btn_sbt() throws IOException, SQLException {

		connection = DBConnection.getInstance().getConnection();
		selectall = connection.prepareStatement("SELECT * from memberdetail");
		selectPswd = connection.prepareStatement("select password from memberdetail where email=?");



		if (txt_username.getText().isEmpty() || txt_password.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
			Optional<ButtonType> buttonType = alert.showAndWait();
			return;
		} else if (txt_username.getText().equals("sysadmin") && txt_password.getText().equals("sysadmin")) {

			Parent root = null;
			System.out.println(txt_username.getText() + "  ... " + txt_password.getText());
			root = FXMLLoader.load(this.getClass().getResource("/View/HomeFormView.fxml"));

			System.out.println(root + " set ");

			if (root != null) {
				System.out.println("to go");
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

		else {


			selectPswd.setString(1, txt_username.getText());
			;
			ResultSet rst = selectPswd.executeQuery();
			if (rst.next()) {
				System.out.println("psws " + rst.getString(1));
				if (txt_password.getText().equals(rst.getString(1))) {

					Parent root = null;
					System.out.println(txt_username.getText() + "  ... " + txt_password.getText());
					root = FXMLLoader.load(this.getClass().getResource("/View/CustomerFunction.fxml"));
					if (root != null) {
						System.out.println("set");
						Scene subScene = new Scene(root);
						Stage primaryStage = (Stage) this.root.getScene().getWindow();
						primaryStage.setScene(subScene);
						primaryStage.centerOnScreen();

						TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
						tt.setFromX(-subScene.getWidth());
						tt.setToX(0);
						tt.play();
					}
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Entered Login details are Invalid.", ButtonType.OK);
					Optional<ButtonType> buttonType = alert.showAndWait();
					return;
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid User Name", ButtonType.OK);
				Optional<ButtonType> buttonType = alert.showAndWait();
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