package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class Main extends Application {

    static String ispisiGradove() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        GeografijaDAO geo = GeografijaDAO.getInstance();
        ArrayList<Grad> gradovi = geo.gradovi();
        String s = "";
        if (gradovi != null) {
            for (Grad grad : gradovi)
                s += grad.toString();
        } else {
            System.out.println("Nema gradova u bazi!");
        }
        return s;
    }

    static void glavniGrad() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        GeografijaDAO geo = GeografijaDAO.getInstance();
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        Grad grad = geo.glavniGrad(drzava);
        if (grad != null)
            System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
        else
            System.out.println("Nepostojeća država");
    }

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale.setDefault(new Locale("bs", "BA"));
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader( getClass().getResource("gui.fxml" ), bundle);
        loader.setController(new GuiController());
        Parent root = loader.load();
        primaryStage.setTitle("Gradovi i drzave");
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.show();
    }

}