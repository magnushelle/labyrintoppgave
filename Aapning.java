class Aapning extends HvitRute {

    public Aapning(int k, int r, Labyrint.Monitor m) {
        super(k, r, m);
    }

    @Override
    void gaa(Rute forrige, String vei, Lenkeliste<Rute> sjekket) {
        monitor.leggTilString(vei + toString());   
    }
}