package ba.unsa.etf.rpr;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

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

    public static void main(String[] args) {
        petlja:
        for (; ; ) {
            System.out.println("Odaberite: \n --1 za ispisivanje glavnog grada unesene drzave \n --" +
                    "2 za ispisivanje svih gradova u bazi podataka \n --3 za kraj");
            Scanner ulaz = new Scanner(System.in);
            int izbor = ulaz.nextInt();
            switch (izbor) {
                case 1:
                    glavniGrad();
                    break;
                case 2:
                    System.out.println(ispisiGradove());
                    break;
                case 3:
                    break petlja;
            }
        }
    }
}