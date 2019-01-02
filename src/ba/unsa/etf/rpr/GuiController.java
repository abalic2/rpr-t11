package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class GuiController {
    private GeografijaDAO geo = GeografijaDAO.getInstance();
    public TextField drzavaZaBrisanje;
    public TextField drzavaZaTrazenje;

    public void prikaziGradove(ActionEvent actionEvent) {
        Parent root = null;
        try {
            Stage myStage = new Stage();
            root = FXMLLoader.load(getClass().getResource("gradovi.fxml"));
            myStage.setTitle("Gradovi");
            myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void obrisiDrzavu(ActionEvent actionEvent) {
        Drzava d = geo.nadjiDrzavu(drzavaZaBrisanje.getText());
        if(d==null){ //nema drzave - alert
            prikaziAlertDaNemaDrzave();
        }
        else{
            geo.obrisiDrzavu(d.getNaziv());
            //information da je brisanje uspjesno
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Drzava uspjesno obrisana!");
            alert.showAndWait();
        }
        drzavaZaBrisanje.setText("");
    }

    public void nadjiDrzavu(ActionEvent actionEvent) {
        Drzava d = geo.nadjiDrzavu(drzavaZaTrazenje.getText());
        if(d == null){
            prikaziAlertDaNemaDrzave();
        }
        else{
            System.out.println(d.getNaziv()+ " (" + d.getGlavniGrad().getNaziv() + ")");
        }
    }

    public void mijenjajGrad(ActionEvent actionEvent) {
        Parent root = null;
        try {
            Stage myStage = new Stage();
            root = FXMLLoader.load(getClass().getResource("grad.fxml"));
            myStage.setTitle("Izmjena grada");
            myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prikaziAlertDaNemaDrzave(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Greska");
        alert.setContentText("Unesena drzava ne postoji!");
        alert.showAndWait();
    }
}
