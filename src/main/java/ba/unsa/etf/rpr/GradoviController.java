package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class GradoviController {
    GeografijaDAO geo = GeografijaDAO.getInstance();
    public ListView sviGradovi;

    @FXML
    public void initialize(){
        ObservableList<Grad> gradovi= FXCollections.observableArrayList(geo.gradovi());
        sviGradovi.setItems(gradovi);
    }
}
