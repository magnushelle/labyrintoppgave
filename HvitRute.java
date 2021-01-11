class HvitRute extends Rute {

    public HvitRute(int k, int r, Labyrint.Monitor m) {
        super(k, r, m);
    }

    @Override
    void gaa(Rute forrige, String vei, Lenkeliste<Rute> sjekket) {

        // Funksjonalitet for sykliske labyrinter
        for (Rute r : sjekket) {
            if (this.equals(r)) {
                return;
            }
        }
        sjekket.leggTil(this);
        
        // Legger til sine egne koordinater i vei-strengen
        vei += (toString() + " --> ");

        // Finner den siste hvite ruten som gammelTråd skal gå til
        int sisteHvite = -1;
        for (int i = 0; i < naboer.length; i++) {
            if (skalGaasTil(naboer[i], forrige)) {
                sisteHvite = i;
            }
        }

        // Holder på trådene som blir lagret for senere å kalle på join() på dem
        Thread[] traader = new Thread[2];
        int t = 0;

        // SVAR PÅ SPØRSMÅL: Hva skjer om den gamle tråden først går videre 
        // til neste rute og så etterpå starter opp nye tråder?

        // Dersom det rekursive kallet ligger før vi starter opp nye tråder,
        // vil rekursiviteten sørge for at vi finner alle utveier med 
        // kun den første tråden, og vi har dermed ikke bruk for flere tråder.

        // Starter opp nye tråder på hvite ruter (utenfor oppstart 0-2 stk.) 
        for (int i = 0; i < sisteHvite; i++) {
            if (skalGaasTil(naboer[i], forrige)) {

                // Opprette og starter ny tråd, legges i beholder
                Thread traad = new Thread(new Oppgave(naboer[i], this, vei, sjekket));
                traader[t] = traad;
                t++;
                traad.start();  

            }
        }

        // Rekursivt kall
        if (sisteHvite > -1) {
            naboer[sisteHvite].gaa(this, vei, sjekket);
        }
        
        for (int i = 0; i < traader.length; i++) {
            if (traader[i] != null) {
                try { traader[i].join();
                } catch (InterruptedException e) {}
            }
        }
    }

    // Sjekker om ruten er hvit og ikke lik ruten den kom fra
    private boolean skalGaasTil(Rute rute, Rute forrige) {
        return (rute instanceof HvitRute && !rute.equals(forrige));
    }

    
}