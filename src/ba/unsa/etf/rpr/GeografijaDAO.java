package ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;  /* i ostalo što treba za bazu */
    private PreparedStatement upit;
    private ArrayList<Grad> gradovi = new ArrayList<>();
    private ArrayList<Drzava> drzave = new ArrayList<>();

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    public static GeografijaDAO getInstance() {
        initialize();
        return instance;
    }

    private void ubaciUListe() {
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
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
            //ima li tabela

            upit = conn.prepareStatement("CREATE TABLE IF NOT EXISTS grad (id integer primary key, naziv text, broj_stanovnika int, drzava integer references drzava)");
            upit.executeUpdate();
            upit = conn.prepareStatement("CREATE TABLE IF NOT EXISTS drzava (id integer primary key, naziv text, glavni_grad integer references grad)");
            upit.executeUpdate();
            ubaciUListe();
            upit = conn.prepareStatement("DELETE FROM grad");
            upit.executeUpdate();
            upit = conn.prepareStatement("DELETE FROM drzava");
            upit.executeUpdate();
            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
            for (Grad grad : gradovi) {
                upit.setInt(1, grad.getId());
                upit.setString(2, grad.getNaziv());
                upit.setInt(3, grad.getBrojStanovnika());
                upit.executeUpdate();
            }
            upit = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
            for (Drzava drzava : drzave) {
                upit.setInt(1, drzava.getId());
                upit.setString(2, drzava.getNaziv());
                upit.setInt(3, drzava.getGlavniGrad().getId());
                upit.executeUpdate();
            }
            upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
            for (Grad grad : gradovi) {
                upit.setInt(1, grad.getDrzava().getId());
                upit.setInt(2, grad.getId());
                upit.executeUpdate();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removeInstance() {
        instance = null;
    }

    Grad glavniGrad(String drzava) {
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Grad grad = new Grad();
            Drzava nadjenaDrzava = new Drzava();
            grad.setDrzava(nadjenaDrzava);
            nadjenaDrzava.setGlavniGrad(grad);
            boolean imaDrzave = false;
            while (result.next()) {
                imaDrzave = true;
                int idGrada = result.getInt(1);
                String nazivGrada = result.getString(2);
                int brojStanovnika = result.getInt(3);
                int idDrzave = result.getInt(4);
                String nazivDrzave = result.getString(5);
                grad.setId(idGrada);
                grad.setNaziv(nazivGrada);
                grad.setBrojStanovnika(brojStanovnika);
                nadjenaDrzava.setId(idDrzave);
                nadjenaDrzava.setNaziv(nazivDrzave);

            }
            if (imaDrzave) {
                return grad;
            }
        } catch (SQLException e) {
        }
        return null;
    }


    void obrisiDrzavu(String drzava) {
        try {
            int idDrzave = 0;
            upit = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            boolean imaDrzave = false;
            while (result.next()) {
                imaDrzave = true;
                idDrzave = result.getInt(1);
            }
            if (!imaDrzave) return;
            upit = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
            upit.setInt(1, idDrzave);
            upit.execute();

            upit = conn.prepareStatement("DELETE FROM drzava WHERE id=?");
            upit.setInt(1, idDrzave);
            upit.execute();
        } catch (SQLException ignored) {
            System.out.println("Ne postoji drzava!");
        }
    }

    ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradoviUBazi = new ArrayList<>();
        try {
            upit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet result = upit.executeQuery();
            while (result.next()) {
                Grad grad = new Grad();
                Drzava d = new Drzava();
                int idGrada = result.getInt(1);
                String nazivGrada = result.getString(2);
                int brojStanovnika = result.getInt(3);
                int idDrzave = result.getInt(4);
                grad.setId(idGrada);
                grad.setNaziv(nazivGrada);
                grad.setBrojStanovnika(brojStanovnika);
                d.setId(idDrzave); //ostali podaci za drzavu su nebitni sad
                grad.setDrzava(d);
                gradoviUBazi.add(grad);
            }
            upit = conn.prepareStatement("SELECT * FROM drzava");

            result = upit.executeQuery();
            while (result.next()) {
                Drzava d = new Drzava();
                int idDrzave = result.getInt(1);
                String nazivDrzave = result.getString(2);
                d.setId(idDrzave);
                d.setNaziv(nazivDrzave);
                int idGlavnogGrada = result.getInt(3);
                for (Grad grad : gradoviUBazi) {
                    if (grad.getDrzava().getId() == d.getId()) {
                        grad.setDrzava(d);
                    }
                    if (idGlavnogGrada == grad.getId()) {
                        d.setGlavniGrad(grad);
                    }
                }
            }
        } catch (SQLException ignored) {
            return null;
        }
        return gradoviUBazi;
    }

    void dodajGrad(Grad grad) {
        try {
            upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ? AND broj_stanovnika IS NULL");
            upit.setString(1, grad.getNaziv());
            ResultSet result = upit.executeQuery();
            int id = -1;
            while (result.next())
                id = result.getInt(1);
            if (id != -1) {
                grad.setId(id);
                upit = conn.prepareStatement("SELECT id FROM drzava WHERE glavni_grad = ?");
                upit.setInt(1, id);
                result = upit.executeQuery();
                id = -1;
                while (result.next())
                    id = result.getInt(1);
                Drzava temp = new Drzava();
                temp.setId(id);
                grad.setDrzava(temp);
                izmijeniGrad(grad);
                return;
            }
            //ima li drzave od tog grada
            upit = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            upit.setString(1, grad.getDrzava().getNaziv());
            result = upit.executeQuery();
            boolean imaDrzave = false;
            int idDrzave = 0;
            while (result.next()) {
                idDrzave = result.getInt(1);
                imaDrzave = true;
            }

            upit = conn.prepareStatement("SELECT id FROM grad ORDER BY id DESC");
            result = upit.executeQuery();
            int idGrada = 0;
            while (result.next()) {
                result.getInt(1);
                idGrada++;
            }
            idGrada++;
            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            upit.setInt(1, idGrada);
            upit.setString(2, grad.getNaziv());
            upit.setInt(3, grad.getBrojStanovnika());

            if (!imaDrzave)
                upit.setNull(4, Types.INTEGER);
            else
                upit.setInt(4, idDrzave);
            upit.executeUpdate();


            if (!imaDrzave) {
                upit = conn.prepareStatement("SELECT id FROM drzava ORDER BY id DESC");
                result = upit.executeQuery();
                idDrzave = 0;
                while (result.next()) {
                    result.getInt(1);
                    idDrzave++;
                }
                idDrzave++;
                upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
                upit.setInt(1, idDrzave);
                upit.setString(2, grad.getDrzava().getNaziv());
                upit.setInt(3, idGrada); //ne mora biti glavni grad
                upit.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            //ima li glavnog grada
            upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            upit.setString(1, drzava.getGlavniGrad().getNaziv());
            ResultSet result = upit.executeQuery();
            boolean imaGlGrada = false;
            int idGrada = 0;
            while (result.next()) {
                idGrada = result.getInt(1);
                imaGlGrada = true;
            }
            upit = conn.prepareStatement("SELECT id FROM drzava ORDER BY id DESC");
            result = upit.executeQuery();
            int idDrzave = 0;
            while (result.next()) {
                result.getInt(1);
                idDrzave++;
            }
            idDrzave++;
            //Unos nove drzave
            upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
            upit.setInt(1, idDrzave);
            upit.setString(2, drzava.getNaziv());
            if (!imaGlGrada)
                upit.setNull(3, Types.INTEGER);
            else
                upit.setInt(3, idGrada);
            upit.executeUpdate();

            if (!imaGlGrada) {
                //Dodaj novi grad
                upit = conn.prepareStatement("SELECT id FROM grad ORDER BY id DESC");
                result = upit.executeQuery();
                idGrada = 0;
                while (result.next()) {
                    result.getInt(1);
                    idGrada++;
                }
                idGrada++;
                upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, NULL, NULL)");
                upit.setInt(1, idGrada);
                upit.setString(2, drzava.getGlavniGrad().getNaziv());
                //upit.setInt(3, idGrada);
                upit.executeUpdate();

                upit = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
                upit.setInt(1, idGrada);
                upit.setInt(2, idDrzave);
                upit.executeUpdate();
            }
        } catch (SQLException ignored) {
            System.out.println("Greska");
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
        Drzava d = new Drzava();
        try {
            upit = conn.prepareStatement("SELECT d.id, d.naziv, g.id, g.naziv, g.broj_stanovnika FROM drzava d, grad g WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Grad glavniGrad = new Grad();
            d.setGlavniGrad(glavniGrad);
            glavniGrad.setDrzava(d);
            while (result.next()) {
                int idDrzava = result.getInt(1);
                d.setId(idDrzava);
                String nazivDrzave = result.getString(2);
                d.setNaziv(nazivDrzave);
                int idGrad = result.getInt(3);
                glavniGrad.setId(idGrad);
                String nazivGrad = result.getString(4);
                glavniGrad.setNaziv(nazivGrad);
                int brojStanovnika = result.getInt(5);
                glavniGrad.setBrojStanovnika(brojStanovnika);
            }
        } catch (SQLException ignored) {
            System.out.println("Drzava ne postoji");
            return null;
        }
        return d;

    }
}
