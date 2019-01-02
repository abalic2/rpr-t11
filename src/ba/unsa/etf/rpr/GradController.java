package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class GradController {
    public TextField imeGrada;
    public TextField novoIme;
    public TextField noviBrSt;
    public TextField novaDrzava;
    private GeografijaDAO geo = GeografijaDAO.getInstance();

    public GradController(){}
    public void promijeni(ActionEvent actionEvent) {
        Grad g = geo.nadjiGrad(imeGrada.getText());
        if(g == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Greska");
            alert.setContentText("Uneseni grad ne postoji!");
            alert.showAndWait();
        }
        else{
            Grad novi = new Grad(g.getId(),novoIme.getText(),Integer.parseInt(noviBrSt.getText()),geo.nadjiDrzavu(novaDrzava.getText()));
            geo.izmijeniGrad(novi);
        }
    }

}
