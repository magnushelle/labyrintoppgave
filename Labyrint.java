import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Labyrint {

    private static Rute[][] rutenett;
    private static int antKolonner;
    private static int antRader;

    private Lenkeliste<String> utveier;

    private Labyrint.Monitor monitor;

    /**
     * Konstruktør
     * @param r to-dimensjonalt rutenett av Ruter
     * @param ak antall kolonner
     * @param ar antall rader
     */
    private Labyrint(Rute[][] r, int ak, int ar) {
        rutenett = r;
        antKolonner = ak;
        antRader = ar;

        utveier = new Lenkeliste<>();

        monitor = new Monitor();
    }

    class Monitor {

        Lock laas;

        public Monitor() {
            laas = new ReentrantLock();
        }

        void leggTilString(String streng) {
            laas.lock();
            try {
                utveier.leggTil(streng);
            } finally {
                laas.unlock();
            }
        }
    }

    Lenkeliste<String> finnUtveiFra(int kol, int rad) {

        rutenett[kol][rad].finnUtvei(); 
        
        Lenkeliste<String> tmp = new Lenkeliste<>();

        for (String vei : utveier) {
            tmp.leggTil(vei);
            utveier.fjern();
        }

        return tmp;
    }

    static Labyrint lesFraFil(File fil) throws FileNotFoundException {

        // Variable som brukes til filinnlesing
        Scanner scanner = new Scanner(fil);
        String linje;

        // Variable som brukes til å opprette Labyrint
        antRader = scanner.nextInt();
        antKolonner = scanner.nextInt();
        rutenett = new Rute[antKolonner][antRader];

        Labyrint labyrint = new Labyrint(rutenett, antKolonner, antRader);

        // Følger antagelsen gitt i oppgavetekst om at alle filer er gyldige -
        // bruker derfor ikke 'while (scanner.hasNext()) {...}'
        for (int i = 0; i < antRader; i++) {
            
            // Leser inn hver rad i tekstfilen
            linje = scanner.next();

            for (int j = 0; j < antKolonner; j++) {

                // Parse hvert tegn til String
                String tegn = Character.toString(linje.charAt(j));

                boolean hvit = false;
                boolean aapning = false;

                // Identifiserer type rute
                if (tegn.equals(".")) {
                    hvit = true;

                    if (i == 0 || j == 0 || i + 1 == antRader || j + 1 == 
                            antKolonner) {
                        aapning = true;
                    }
                }

                // Bestemmer type rute vi oppretter på posisjon
                if (hvit && aapning) {
                    rutenett[j][i] = new Aapning(j, i, labyrint.monitor);
                } else if (hvit) {
                    rutenett[j][i] = new HvitRute(j, i, labyrint.monitor);
                } else {
                    rutenett[j][i] = new SortRute(j, i, labyrint.monitor);
                }      
            }
        }

        scanner.close();        

        // Gir alle ruter en peker til sine naboer og labyrinten
        for (int i = 0; i < rutenett.length; i++) {
            for (int j = 0; j < rutenett[i].length; j++) {
                settNabopeker(i, j);
                rutenett[i][j].settLabyrint(labyrint);
            }
        }

        return labyrint;
    }

    private static void settNabopeker(int kol, int rad) {

        Rute[] naboer = new Rute[4];
        
        // Peker til nabo nord
        if (rad == 0) {
            naboer[0] = null;
        } else {
            naboer[0] = rutenett[kol][rad - 1];
        } 

        // Peker til nabo sør
        if (rad + 1 == antRader) {
            naboer[1] = null;
        } else {
            naboer[1] = rutenett[kol][rad + 1];
        }

        // Peker til nabo vest
        if (kol == 0) {
            naboer[2] = null;
        } else {
            naboer[2] = rutenett[kol - 1][rad];
        }

        // Peker til nabo øst
        if (kol + 1 == antKolonner) {
            naboer[3] = null;  
        }  else {
            naboer[3] = rutenett[kol + 1][rad];
        }

        // Bruker metode i rute som angir naboer
        rutenett[kol][rad].settNabo(naboer);
    }

    Rute[][] hentRutenett() {
        return rutenett;
    }

    Lenkeliste<String> hentUtveiliste() {
        return utveier;
    }

    int hentAntKol() {
        return antKolonner;
    }

    int hentAntRad() {
        return antRader;
    }

    /**
     * Funksjon som gir responsiv størrelse på rektangler avhengig av 
     * labyrintstørrelse
     * @return størrelse på rektanglene
     */
    int ruteStorrelse() {
        int areal = antKolonner * antRader;
        // Labyrint 1, 2, 7
        if (areal < 100) {
            return 35;
        // Labyrint 3
        } else if (areal < 500) {
            return 25;
        // Labyrint 4, 6
        } else if (areal < 1000) {
            return 15;
        // Labyrint 5
        } else {
            return 10;
        }
    }
}