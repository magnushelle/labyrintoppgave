class Oppgave implements Runnable {

    Rute denne;
    Rute forrige;
    String vei;
    Lenkeliste<Rute> sjekket;

    /**
     * Konstruktør til tråden
     * @param rute Ruten som tråden starter fra
     * @param forrige Ruten som tråden ble opprettet fra
     * @param vei Den påbegynte veien
     */
    public Oppgave(Rute denne, Rute forrige, String vei, Lenkeliste<Rute> forrigeListe) {
        this.denne = denne;
        this.forrige = forrige;
        this.vei = vei;

        // Lager en kopi den forrige trådens sjekkede ruter
        sjekket = new Lenkeliste<Rute>();
        for (Rute r : forrigeListe) {
            sjekket.leggTil(r);
        }
    }

    public void run() {
        denne.gaa(forrige, vei, sjekket);
    }
}