package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Teacher;
import model.services.TeacherService;

public class TeacherListController implements Initializable, DataChangeListener {
	
	private TeacherService service;
	
	@FXML
	private TableView<Teacher> tableViewTeacher;
	
	@FXML
	private TableColumn<Teacher, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Teacher, String> tableColumnName;
	
	@FXML
	private TableColumn<Teacher, String> tableColumnCpf;
	
	@FXML
	private TableColumn<Teacher, String> tableColumnPhone;
	
	@FXML
	private TableColumn<Teacher, Date> tableColumnAdmissionDate;
	
	@FXML
	private TableColumn<Teacher, Double> tableColumnSalary;
	
	@FXML
	private TableColumn<Teacher, String> tableColumnChief;
	
	@FXML
	private TableColumn<Teacher, String> tableColumnCoordinator;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Teacher> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Teacher obj = new Teacher();
		createDialogForm(obj, "/gui/TeacherForm.fxml", parentStage);
	}
	
	public void setTeacherService(TeacherService service) {
		this.service = service;		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
		tableColumnAdmissionDate.setCellValueFactory(new PropertyValueFactory<>("admissionDate"));
		tableColumnSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
		tableColumnChief.setCellValueFactory(new PropertyValueFactory<>("chief"));
		tableColumnCoordinator.setCellValueFactory(new PropertyValueFactory<>("coordinator"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewTeacher.prefHeightProperty().bind(stage.heightProperty());		
	}
	
	public void updateTableView() {
		if (service == null)
			throw new IllegalStateException("Service was null");
		List<Teacher> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewTeacher.setItems(obsList);
	}
	
	private void createDialogForm(Teacher obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();			
			TeacherFormController controller = loader.getController();
			controller.subscribeDataChengeListener(this);
			controller.setTeacher(obj);
			
			controller.setTeacherService(new TeacherService());
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter teacher data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();		
	}
}
