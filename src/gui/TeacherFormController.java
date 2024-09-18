package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Teacher;
import model.exception.ValidationException;
import model.services.TeacherService;

public class TeacherFormController implements Initializable{

	private Teacher entity;
	
	private TeacherService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtCpf;
	
	@FXML
	private TextField txtPhone;
	 
	@FXML
	private DatePicker dpAdmissionDate;
	
	@FXML
	private TextField txtSalary;
	
	@FXML
	private TextField txtChief;
	
	@FXML
	private TextField txtCoordinator;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorCpf;
	
	@FXML
	private Label labelErrorPhone;
	
	@FXML
	private Label labelErrorAdmissionDate;
	
	@FXML
	private Label labelErrorSalary;
	
	@FXML
	private Label labelErrorChief;
	
	@FXML
	private Label labelErrorCoordinator;
	
	@FXML
	private Button btSave;	

	@FXML
	private Button btCancel;
	
	public void setTeacher(Teacher entity) {
		this.entity = entity;
	}
	
	public void setTeacherService(TeacherService service) {
		this.service = service;
	}
	
	public void subscribeDataChengeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null)
			throw new IllegalStateException("Entity was null!");
		if(service == null)
			throw new IllegalStateException("Service was null!");
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException ex) {
			setErrorMessages(ex.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}		
	}

	private Teacher getFormData() {
		Teacher obj = new Teacher();
		
		ValidationException exception = new ValidationException("Validation eror"); 
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
		if (txtCpf.getText() == null || txtCpf.getText().trim().equals("")) 
			exception.addError("cpf", "Field can't be empty");
		obj.setName(txtCpf.getText());
		
		if (txtPhone.getText() == null || txtPhone.getText().trim().equals("")) 
			exception.addError("phone", "Field can't be empty");
		obj.setPhone(txtName.getText());
		
		if (dpAdmissionDate.getValue() == null) 
			exception.addError("admissionDate", "Field can't be empty");		
		else {
			Instant instant = Instant.from(dpAdmissionDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setAdmissionDate(Date.from(instant));
		}
		
		if (txtSalary.getText() == null || txtSalary.getText().trim().equals("")) 
			exception.addError("salary", "Field can't be empty");
		obj.setSalary(Utils.tryParseToDouble(txtSalary.getText()));
		
		if (txtChief.getText() == null || txtChief.getText().trim().equals("")) 
			exception.addError("chief", "Field can't be empty");
		obj.setChief(txtChief.getText());
		
		if (txtCoordinator.getText() == null || txtCoordinator.getText().trim().equals("")) 
			exception.addError("coordinator", "Field can't be empty");
		obj.setCoordinator(txtCoordinator.getText());
		
		if (exception.getErrors().size() > 0)
			throw exception;
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtCpf, 12);
		Constraints.setTextFieldMaxLength(txtPhone, 15);
		Utils.formatDatePicker(dpAdmissionDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtSalary);
		Constraints.setTextFieldMaxLength(txtChief, 3);
		Constraints.setTextFieldMaxLength(txtCoordinator, 3);		
	}
	
	public void updateFormData() {
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtCpf.setText(entity.getCpf());
		txtPhone.setText(entity.getPhone());
		if (entity.getAdmissionDate() != null) 
			dpAdmissionDate.setValue(LocalDate.ofInstant(entity.getAdmissionDate().toInstant(), ZoneId.systemDefault()));		
		Locale.setDefault(Locale.US);
		txtSalary.setText(String.format("%.2f", entity.getSalary()));
		txtChief.setText(entity.getChief());
		txtCoordinator.setText(entity.getCoordinator());		
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name"))
			labelErrorName.setText(errors.get("name"));
		if(fields.contains("cpf"))
			labelErrorCpf.setText(errors.get("cpf"));
		if(fields.contains("phone"))
			labelErrorPhone.setText(errors.get("phone"));
		if(fields.contains("admissionDate"))
			labelErrorAdmissionDate.setText(errors.get("admissionDate"));
		if(fields.contains("salary"))
			labelErrorSalary.setText(errors.get("salary"));
		if(fields.contains("chief"))
			labelErrorChief.setText(errors.get("chief"));
		if(fields.contains("coordinator"))
			labelErrorCoordinator.setText(errors.get("coordinator"));
	}	
}	
