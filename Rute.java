abstract class Rute {

    protected Rute[] naboer = new Rute[4];

    protected Labyrint labyrint;
    protected int kolonne;
    protected int rad;

    Labyrint.Monitor monitor;

    public Rute(int k, int r, Labyrint.Monitor m) {
        kolonne = k;
        rad = r;
        monitor = m;
    }

    abstract void gaa(Rute forrige, String vei, Lenkeliste<Rute> sjekket);

    /**
     * Metode som oppretter første tråd
     * @param visueltKall boolean satt ved kall av program
     */
    void finnUtvei() {

        // Starter første tråd.
        Thread gammelTraad = new Thread(new Oppgave(this, null, "", new Lenkeliste<Rute>()));
        gammelTraad.start();

        // Kaller på .join() for at main-tråden ikke skal fortsette før den 
        // første tråden er ferdig. 
        try { gammelTraad.join();
        } catch (InterruptedException e) {}
    }

    @Override
    public String toString() {
        return "(" + this.kolonne + ", " + this.rad + ")";
    }

    protected void settNabo(Rute[] n) {
        for (int i = 0; i < n.length; i++) {
            naboer[i] = n[i];
        }
    }

    void settLabyrint(Labyrint l) {
        labyrint = l;
    }

    int hentKolonne() {
        return kolonne;
    }

    int hentRad() {
        return rad;
    }

    Rute[] hentNaboer() {
        return naboer;
    }
}