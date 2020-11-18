package dad.javafx.listas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	
	private ListProperty<String> nombres = new SimpleListProperty<String>(FXCollections.observableArrayList());
	private StringProperty seleccionado = new SimpleStringProperty();
	
	// view
	
	private ListView<String> nombresListView;
	private Button anadirButton, quitarButton; 
	
	
	@Override
	public void init() throws Exception {	
		
		InputStream nombresStream = getClass().getResourceAsStream("/nombres.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(nombresStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String [] nombre = line.split(",");
			nombres.add(nombre[0] + " " + nombre[1]);
		}
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		nombresListView = new ListView<String>();
		nombresListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
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
		
		// sin ordenar
//		nombresListView.itemsProperty().bind(nombres);
		
		// ordenado (a sorted le pasamos el comparador, que debe devolver 0 si o1 == o2, -1 si o1 > o2, y +1 si o1 < o2
		nombresListView.itemsProperty().bind(new SimpleListProperty<>(nombres.sorted((o1, o2) -> o2.compareTo(o1)))); 
		
		seleccionado.bind(nombresListView.getSelectionModel().selectedItemProperty());
		
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
			
			System.out.println("---> ha habido cambios:");
			
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
		if (result.isPresent() && !nombres.contains(result.get())) {
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
