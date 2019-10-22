package dad.javafx.listas;

import java.util.Optional;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ListasApp extends Application {

	// model
	
	private ListProperty<String> nombres = new SimpleListProperty<String>(
				FXCollections.observableArrayList()
			);
	private IntegerProperty seleccionado = new SimpleIntegerProperty();
	
	// view
	
	private ListView<String> nombresListView;
	private Button anadirButton, quitarButton; 
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		nombresListView = new ListView<String>();
		nombresListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		anadirButton = new Button("Añadir");

		quitarButton = new Button("Quitar");
		
		HBox botonesBox = new HBox(5, anadirButton, quitarButton);
		botonesBox.setAlignment(Pos.BASELINE_RIGHT);
		botonesBox.setPadding(new Insets(5, 0, 0, 0));
		
		BorderPane root = new BorderPane();
		root.setCenter(nombresListView);
		root.setBottom(botonesBox);
		root.setPadding(new Insets(5));
		
		Scene scene = new Scene(root, 640, 480);
		
		primaryStage.setTitle("Listas");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// listeners y bindeos
		
		quitarButton.disableProperty().bind(nombresListView.getSelectionModel().selectedItemProperty().isNull());
		
		nombresListView.itemsProperty().bind(nombres);
		
		seleccionado.bind(nombresListView.getSelectionModel().selectedIndexProperty());
		
		anadirButton.setOnAction(e -> onAnadirButton(e));
		quitarButton.setOnAction(e -> onQuitarButton(e));
		
		nombres.addListener(new ListChangeListener<String>() {
			public void onChanged(Change<? extends String> c) {
				onNombresChanged(c);
			}
		});
		
	}
	
	private void onNombresChanged(Change<? extends String> c) {
		while (c.next()) {
			
			for (String nuevo : c.getAddedSubList()) {
				System.out.println("se ha añadido " + nuevo);
			}

			for (String quitado : c.getRemoved()) {
				System.out.println("se ha quitado " + quitado);
			}
			
		}
	}

	private void onAnadirButton(ActionEvent e) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Nuevo nombre");
		dialog.setHeaderText("Añadir un nuevo nombre a la lista");
		dialog.setContentText("Nombre:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			nombres.add(result.get());
		}
	}

	private void onQuitarButton(ActionEvent e) {
		nombres.remove(seleccionado.get());
	}

	public static void main(String[] args) {
		launch(args);
	}

}
