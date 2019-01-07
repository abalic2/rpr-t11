package ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;
    private PreparedStatement upit;
    private int brojDrzava = 0; //redni broj drzave = njen id
    private int brojGradova = 0;

    public static Connection getConnection() {
        return conn;
    }

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) initialize();
        return instance;
    }

    private void ubaciUListe(ArrayList<Grad> gradovi, ArrayList<Drzava> drzave) {
        Grad pariz = new Grad(1, "Pariz", 2206488, null);
        Grad london = new Grad(2, "London", 8825000, null);
        Grad bec = new Grad(3, "Beč", 1899055, null);
        Grad manchester = new Grad(4, "Manchester", 545500, null);
        Grad graz = new Grad(5, "Graz", 280200, null);
        Drzava francuska = new Drzava(1, "Francuska", pariz);
        Drzava engleska = new Drzava(2, "Velika Britanija", london);
        Drzava austrija = new Drzava(3, "Austrija", bec);
        drzave.add(francuska);
        drzave.add(engleska);
        drzave.add(austrija);
        pariz.setDrzava(francuska);
        london.setDrzava(engleska);
        bec.setDrzava(austrija);
        manchester.setDrzava(engleska);
        graz.setDrzava(austrija);
        gradovi.add(pariz);
        gradovi.add(london);
        gradovi.add(bec);
        gradovi.add(manchester);
        gradovi.add(graz);
    }

    private GeografijaDAO() {
        File db = new File("baza.db");
        boolean bazaPostoji = db.exists();
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
            if (!bazaPostoji) {
                ArrayList<Grad> gradovi = new ArrayList<>();
                ArrayList<Drzava> drzave = new ArrayList<>();
                ubaciUListe(gradovi, drzave);

                PreparedStatement napraviGrad = conn.prepareStatement("CREATE TABLE grad (id integer primary key, naziv text, broj_stanovnika integer, drzava integer)");
                PreparedStatement napraviDrzavu = conn.prepareStatement("CREATE TABLE drzava (id integer primary key, naziv text, glavni_grad integer references grad)");
                napraviGrad.execute();
                napraviDrzavu.execute();

                PreparedStatement napuni = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
                for (Grad grad : gradovi) {
                    napuni.setInt(1, grad.getId());
                    napuni.setString(2, grad.getNaziv());
                    napuni.setInt(3, grad.getBrojStanovnika());
                    napuni.executeUpdate();
                }
                napuni = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
                for (Drzava drzava : drzave) {
                    napuni.setInt(1, drzava.getId());
                    napuni.setString(2, drzava.getNaziv());
                    napuni.setInt(3, drzava.getGlavniGrad().getId());
                    napuni.executeUpdate();
                }
                napuni = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
                for (Grad grad : gradovi) {
                    napuni.setInt(1, grad.getDrzava().getId());
                    napuni.setInt(2, grad.getId());
                    napuni.executeUpdate();
                }
                brojDrzava = 3;
                brojGradova = 5;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeInstance() {
        instance = null;
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    Grad glavniGrad(String drzava) {
        Drzava d = nadjiDrzavu(drzava);
        Grad g = null;
        if (d != null) g = d.getGlavniGrad();
        return g;
    }


    void obrisiDrzavu(String drzava) {
        try {
            Drzava d = nadjiDrzavu(drzava);
            if (d != null) {
                upit = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
                upit.setInt(1, d.getId());
                upit.execute();
                upit = conn.prepareStatement("DELETE FROM drzava WHERE id=?");
                upit.setInt(1, d.getId());
                upit.execute();
            }

        } catch (SQLException ignored) {
            System.out.println("Ne postoji drzava!");
        }
    }

    ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradoviUBazi = new ArrayList<>();
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.id = g.drzava ORDER BY g.broj_stanovnika DESC");
            ResultSet result = upit.executeQuery();
            while (result.next()) {
                Grad grad = new Grad();
                grad = nadjiGrad(result.getString(2));
                gradoviUBazi.add(grad);
            }
        } catch (SQLException ignored) {
            return null;
        }
        return gradoviUBazi;
    }

    void dodajGrad(Grad grad) {
        try {
            brojGradova++;
            Drzava d = nadjiDrzavu(grad.getDrzava().getNaziv());
            int idDrzave = brojDrzava + 1; //neka nepostojeca drzava
            if (d != null) idDrzave = d.getId();
            upit = conn.prepareStatement("INSERT INTO grad VALUES(?, ?, ?, ?)");
            upit.setInt(1, brojGradova); //ne njegov id nego ovaj da bi se lakse dodavalo
            upit.setString(2, grad.getNaziv());
            upit.setInt(3, grad.getBrojStanovnika());
            upit.setInt(4, idDrzave);
            upit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            brojDrzava++;
            Grad g = nadjiGrad(drzava.getGlavniGrad().getNaziv());
            int idGrada = brojGradova + 1; //neka nepostojeca drzava
            if (g != null) idGrada = g.getId();
            upit = conn.prepareStatement("INSERT INTO drzava VALUES(?, ?, ?)");
            upit.setInt(1, brojDrzava); //ne njegov id nego ovaj da bi se lakse dodavalo
            upit.setString(2, drzava.getNaziv());
            upit.setInt(3, idGrada);
            upit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            upit = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
            upit.setString(1, grad.getNaziv());
            upit.setInt(2, grad.getBrojStanovnika());
            upit.setInt(3, grad.getDrzava().getId());
            upit.setInt(4, grad.getId());
            upit.executeUpdate();
        } catch (SQLException ignored) {
            System.out.println("Dati grad ne postoji");
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            if (result.next()) { //ako ima rezultata
                Grad grad = new Grad(result.getInt(1), result.getString(2), result.getInt(3), null);
                Drzava d = new Drzava(result.getInt(4), result.getString(5), null);
                grad.setDrzava(d);
                d.setGlavniGrad(grad);
                return d;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public Grad nadjiGrad(String grad) {
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.naziv FROM grad g, drzava d WHERE d.id = g.drzava AND g.naziv = ?");
            upit.setString(1, grad);
            ResultSet result = upit.executeQuery();
            if (result.next()) { //ako ima rezultata
                Drzava d = nadjiDrzavu(result.getString(4)); //nadje drzavu grada
                Grad g = new Grad(result.getInt(1), result.getString(2), result.getInt(3), d);
                return g;
            }
        } catch (SQLException e) {
        }
        return null;
    }
}