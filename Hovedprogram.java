// Importer
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileNotFoundException;

import javafx.event.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;

// Grafikk-klasse
public class Hovedprogram extends Application {

    private Labyrint labyrint = null;
    private Rutenett rutenett;
    private Text infotekst;
    private Text instruks;
    
    private HBox tekstinput;
    private HBox resultat;
    private Label kolonne;
    private Label rad;
    private TextField k;
    private TextField r;
    private Button finnUtveier;
    private Button forrige;
    private Button neste;
    private Text losnNr;

    private Button nyFil;
    private Button avslutt;
    private HBox alternativer;

    private VBox venstreBoks;
    private VBox hoyreBoks;
    private Pane pane;
    private Scene scene;

    private Lenkeliste<String> utveier;
    private boolean[][] denneUtveien;

    static int losningSomSkalVises = 0;

    // EventHandler, trykk på hvit rute
    class RuteKlikk implements EventHandler<MouseEvent> {

        /**
         * Metode som håndterer museklikk på rute. Setter igang med å 
         * finne utveiene på labyrinten dersom en klikker på en rute.
         * @param m 
         */
        @Override
        public void handle(MouseEvent m) {
            Rektangel rektangel = (Rektangel) m.getSource();
            int kol = rektangel.hentKol();
            int rad = rektangel.hentRad();

            if (labyrint.hentRutenett()[kol][rad] instanceof HvitRute) {
                k.setText("" + kol);
                r.setText("" + rad);
                resultat.setSpacing(9);
                finnUtvei(kol, rad);

            } else {
                k.setText("");
                r.setText("");
                losnNr.setText("");
                resultat.setSpacing(25);
            }
        }
    }

    // EventHandler, hover over Rektangel
    class RuteHover implements EventHandler<MouseEvent> {

        /**
         * Metode som kalles når bruker hovrer over en hvit rute
         * @param m
         */
        @Override
        public void handle(MouseEvent m) {
            Rektangel rektangel = (Rektangel) m.getSource();
            int kol = rektangel.hentKol();
            int rad = rektangel.hentRad();
            
            if (labyrint.hentRutenett()[kol][rad] instanceof HvitRute) {
                rektangel.setStyle("-fx-cursor: hand");
                rektangel.setFill(Color.valueOf("#7CAFC2"));
                k.setText("" + kol);
                r.setText("" + rad);

            } else {
                rektangel.setFill(Color.valueOf("#AB4642"));
            }
        }
    }

    // EventHandler, stopp å hover over Rektangel
    class RuteHoverExit implements EventHandler<MouseEvent> {
        /**
         * Metode som kalles når bruker slutter å hovre over hvit
         * @param m
         */
        @Override
        public void handle(MouseEvent m) {
            Rektangel rektangel = (Rektangel) m.getSource();
            int kol = rektangel.hentKol();
            int rad = rektangel.hentRad();

            if (labyrint.hentRutenett()[kol][rad] instanceof HvitRute) {
                rektangel.setFill(Color.valueOf("#E8E8E8"));
            } else {
                rektangel.setFill(Color.valueOf("#383838"));
            }
        }
    }
    
    // EventHandler, avslutt program
    class AvsluttBehandler implements EventHandler<ActionEvent> {

        /**
         * Metode som avslutter programmet
         * @param e
         */
        @Override
        public void handle(ActionEvent e) {
            Platform.exit();
        }
    }

    // EventHandler, velg ny labyrint-fil
    class NyFilBehandler implements EventHandler<ActionEvent> {

        /**
         * Metode som åpner et nytt vindu. Dette er sannsynligvis ikke riktig
         * måte å gjøre dette på.
         * @param e
         */
        @Override
        public void handle(ActionEvent e) {
            startProgram(new Stage());
        }
    }
 
    // EventHandler, finn utveier til koordinater
    class FinnUtveier implements EventHandler<ActionEvent> {

        /**
         * Metode som finner utveier når bruker velger å bruke koordinater
         * @param e
         */
        @Override
        public void handle(ActionEvent e) {

            losningSomSkalVises = 0;
            
            try {
                int kol = Integer.parseInt(k.getText());
                int rad = Integer.parseInt(r.getText());
                finnUtvei(kol, rad);

            } catch (NumberFormatException | ArrayIndexOutOfBoundsException n) {
                losnNr.setText("Tast inn gyldige tall");
            }
            
        }
    }

    // EventHandler, trykk på neste
    class NesteBehandler implements EventHandler<ActionEvent> {

        /**
         * Metode som tillater bruker å bla gjennom løsninger
         * @param e
         */
        @Override
        public void handle(ActionEvent e) {
            losningSomSkalVises++;
            if (losningSomSkalVises > utveier.stoerrelse() - 1) {
                losningSomSkalVises = 0;
            }

            visUtvei(losningSomSkalVises);
        }
    }

    // EventHandler, trykk på forrige
    class ForrigeBehandler implements EventHandler<ActionEvent> {

        /**
         * Metode som tillater bruker å bla gjennom løsninger
         * @param e
         */
        @Override
        public void handle(ActionEvent e) {
            losningSomSkalVises--;
            if (losningSomSkalVises < 0) {
                losningSomSkalVises = utveier.stoerrelse() - 1;
            }

            visUtvei(losningSomSkalVises);
        }
    }


    /**
     * Metode som initerer alle GUI-elementer
     * @param stage
     */
    private void startProgram(Stage stage) {

        // Leser inn fra fil
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./inputs"));
        File fil = fileChooser.showOpenDialog(stage);
        try {
            labyrint = Labyrint.lesFraFil(fil);
        } catch (FileNotFoundException e) {
            Platform.exit();
        }

        // Oppretter klikkbehandlere
        RuteKlikk rkBehandler = new RuteKlikk();
        RuteHover rsBehandler = new RuteHover();
        RuteHoverExit hover = new RuteHoverExit();
        NyFilBehandler nyFilBehandler = new NyFilBehandler();
        AvsluttBehandler avsluttBehandler = new AvsluttBehandler();
        FinnUtveier finnUtveierBehandler = new FinnUtveier();
        NesteBehandler nesteBehandler = new NesteBehandler();
        ForrigeBehandler forrigeBehandler = new ForrigeBehandler();

        // Oppretter rutenett
        rutenett = nyttRutenett(labyrint, rkBehandler, rsBehandler, hover); 
        rutenett.setGridLinesVisible(true);

        // Oppretter tekst
        String lNr = fil.getName().substring(0, 1);
        infotekst = new Text("Labyrint " + lNr);
        infotekst.setFont(new Font(30));
        instruks = new Text("Skriv inn koordinater eller velg rute");
        instruks.setFont(new Font(18));

        // Funksjonalitet for å taste inn koordinater
        tekstinput = new HBox();
        tekstinput.setSpacing(5.0);
        kolonne = new Label("Kolonne");
        rad = new Label("Rad");
        kolonne.setFont(new Font(15)); rad.setFont(new Font(15));
        k = new TextField(); k.setPrefSize(40, 15);
        r = new TextField(); r.setPrefSize(40, 15);
        finnUtveier = new Knapp("Finn utveier");
        finnUtveier.setOnAction(finnUtveierBehandler);
        tekstinput.getChildren().addAll(kolonne, k, rad, r, finnUtveier);
        tekstinput.setStyle("-fx-padding: 50 0 00 0;");

        // Funksjonalitet for å se løsninger
        resultat = new HBox();
        forrige = new Knapp("<<"); forrige.setPrefWidth(40);
        neste = new Knapp(">>"); neste.setPrefWidth(40);
        neste.setOnAction(nesteBehandler); forrige.setOnAction(forrigeBehandler);
        losnNr = new Text("");
        resultat.setSpacing(25);
        resultat.setStyle("-fx-padding: 0 0 0 70;");
        resultat.getChildren().addAll(forrige, losnNr, neste);
        
        // Oppretter buttons
        nyFil = new Knapp("Velg ny fil...");
        nyFil.setOnAction(nyFilBehandler);
        avslutt = new Knapp("Avslutt");
        avslutt.setOnAction(avsluttBehandler);
        alternativer = new HBox();
        alternativer.setSpacing(10.0);
        alternativer.setStyle("-fx-padding: 100 0 0 0;");
        alternativer.getChildren().addAll(nyFil, avslutt);

        // To bokser, en til venstre og en til høyre
        venstreBoks = new VBox();
        venstreBoks.getChildren().addAll(infotekst, instruks, tekstinput, 
            resultat, alternativer);
        venstreBoks.setStyle("-fx-padding: 20, 0, 20, 20;");
        venstreBoks.setSpacing(10.0);

        hoyreBoks = new VBox();
        hoyreBoks.setStyle("-fx-padding: 25, 20, 20, 0;");
        hoyreBoks.setLayoutX(330);
        hoyreBoks.getChildren().addAll(rutenett);
        
        // Setter sammen panes, scene og stage
        pane = new Pane();
        pane.setStyle("-fx-background-color: #E8E8E8");
        pane.getChildren().addAll(venstreBoks, hoyreBoks);
        scene = new Scene(pane);
        stage.setScene(scene); stage.setTitle("Obligatorisk oppgave 7");
        stage.show();
    }

    /**
     * Metode som kaller på Labyrint.finnUtveiFra(kol, rad)
     * @param kol rutens kolonne
     * @param rad rutens rad
     * @throws ArrayIndexOutOfBoundsException
     */
    private void finnUtvei(int kol, int rad) throws ArrayIndexOutOfBoundsException {
        utveier = labyrint.finnUtveiFra(kol, rad);
        visUtvei(losningSomSkalVises);
    }

    /**
     * Metode som endrer på GUIen når vi finner utveier
     * @param losning Løsningsnummer i Lenkeliste<String> utveier som skal vises
     */
    private void visUtvei(int losning) throws ArrayIndexOutOfBoundsException, 
        NullPointerException {

        // Viser bruker hvilke løsninger som finnes
        int antallLosn = utveier.stoerrelse();
        if (antallLosn == 0) {
            losnNr.setText("Ingen utveier");
            return;
        } else {
            losnNr.setText((losning+1) + " av " + utveier.stoerrelse());
        }

        // Bruker vedlagt metode for å oversette String utvei
        denneUtveien = losningStringTilTabell(utveier.hent(losning), 
            labyrint.hentAntKol(), labyrint.hentAntRad());

        // Endrer på GUIen 
        for (int i = 0; i < denneUtveien.length; i++) {
            for (int j = 0; j < denneUtveien[i].length; j++) {
                Rektangel r = rutenett.hentInnhold(j, i);

                if (labyrint.hentRutenett()[j][i] instanceof HvitRute) {
                    r.setFill(Color.valueOf("#E8E8E8"));
                }

                if (denneUtveien[i][j] == true) {
                    r.setFill(Color.valueOf("#7CAFC2"));

                    if (labyrint.hentRutenett()[j][i] instanceof Aapning) {
                        r.setFill(Color.valueOf("#F7CA88"));
                    }
                }
            }
        }
    }

    /**
     * Metode som initierer et GridPane
     * @param labyrint som velges som inn-fil
     * @return GridPane som representerer valgt labyrint
     */
    private static Rutenett nyttRutenett(Labyrint labyrint, RuteKlikk rk,
        RuteHover rs, RuteHoverExit h) {

        // Oppretter et rutenett
        Rutenett rutenett = new Rutenett();

        // Henter det todimensjonale rutenettet som er initiert i Labyrint.java
        Rute[][] lRutenett = labyrint.hentRutenett();

        // Henter størrelsen på hvert rektangel
        int rektangelStorrelse = labyrint.ruteStorrelse();

        for (int i = 0; i < lRutenett.length; i++) {
            for (int j = 0; j < lRutenett[i].length; j++) {

                Rektangel rektangel;
                Rute rute = lRutenett[i][j];
                
                // Oppretter et rektangel avhengig av klassetype
                if (rute instanceof SortRute) {
                    rektangel = new Rektangel("sort", rektangelStorrelse, i, j);
                } else {
                    rektangel = new Rektangel("hvit", rektangelStorrelse, i, j);
                }

                // Kobler klikk på rektangel til en EventHandler
                rektangel.setOnMousePressed(rk);

                // Kobler hovering over rektangel til en EventHandler
                rektangel.setOnMouseEntered(rs);
                rektangel.setOnMouseExited(h);

                // Legger rektangelen til i vårt GridPane og array
                rutenett.add(rektangel, i, j);
            }
        }

        return rutenett;
    }

    /**
     * Main-metode
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Metode som kalles i main
     * @param stage 
     */
    @Override
    public void start(Stage stage) {
        startProgram(stage);
    }

    /**
     * Konverterer losning-String fra oblig 5 til en boolean[][]-representasjon
     * av losningstien.
     * @param losningString String-representasjon av utveien
     * @param bredde        bredde til labyrinten
     * @param hoyde         hoyde til labyrinten
     * @return              2D-representasjon av rutene der true indikerer at
     *                      ruten er en del av utveien.
     */
    static boolean[][] losningStringTilTabell(String losningString, int bredde, int hoyde) {
        boolean[][] losning = new boolean[hoyde][bredde];
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\(([0-9]+),([0-9]+)\\)");
        java.util.regex.Matcher m = p.matcher(losningString.replaceAll("\\s",""));
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            losning[y][x] = true;
        }
        return losning;
    }
}
