package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class GuiController {
    private GeografijaDAO geo = GeografijaDAO.getInstance();
    public TextField drzavaZaBrisanje;
    public TextField drzavaZaTrazenje;
    public TextField drzavaZaIzvjestaj;

    public void bosanski(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("bs", "BA"));
        Stage myStage = (Stage) drzavaZaBrisanje.getScene().getWindow();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation_bs");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"), bundle);
        loader.setController(new GuiController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }

    public void njemacki(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("de", "DE"));
        Stage myStage = (Stage) drzavaZaBrisanje.getScene().getWindow();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation_de");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"), bundle);
        loader.setController(new GuiController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }

    public void engleski(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("en", "US"));
        Stage myStage = (Stage) drzavaZaBrisanje.getScene().getWindow();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation_en_US");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"), bundle);
        loader.setController(new GuiController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }

    public void francuski(ActionEvent actionEvent) {
        Locale.setDefault(new Locale("fr", "FR"));
        Stage myStage = (Stage) drzavaZaBrisanje.getScene().getWindow();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation_fr");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"), bundle);
        loader.setController(new GuiController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }


    public void prikaziGradove(ActionEvent actionEvent) {
        Stage myStage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gradovi.fxml"), bundle);
        loader.setController(new GradoviController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Gradovi");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();

    }


    public void obrisiDrzavu(ActionEvent actionEvent) {
        Drzava d = geo.nadjiDrzavu(drzavaZaBrisanje.getText());
        if (d == null) { //nema drzave - alert
            prikaziAlertDaNemaDrzave();
        } else {
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
        if (d == null) {
            prikaziAlertDaNemaDrzave();
        } else {
            System.out.println(d.getNaziv() + " (" + d.getGlavniGrad().getNaziv() + ")");
        }
    }

    public void mijenjajGrad(ActionEvent actionEvent) {
        Stage myStage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("grad.fxml"), bundle);
        loader.setController(new GradController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Izmjena grada");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }

    public void prikaziAlertDaNemaDrzave() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Greska");
        alert.setContentText("Unesena drzava ne postoji!");
        alert.showAndWait();
    }

    public void stampajGradove(ActionEvent actionEvent) {
        try {
            new GradoviReport().showReport(GeografijaDAO.getInstance().getConnection());
        } catch (JRException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stampajNekeGradove(ActionEvent actionEvent) {
        Drzava d = geo.nadjiDrzavu(drzavaZaIzvjestaj.getText());
        try {
            if (d == null) { //nema drzave - alert
                prikaziAlertDaNemaDrzave();
            } else {
                new GradoviReport().showReportNovi(GeografijaDAO.getInstance().getConnection(),drzavaZaIzvjestaj.getText());
            }
        } catch (JRException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void spasi(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("DOCX", "*.docx"),
                new FileChooser.ExtensionFilter("XSLX", "*.xslx"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            new GradoviReport().saveAs(GeografijaDAO.getConnection(), file.getAbsolutePath());
        }
    }

}
