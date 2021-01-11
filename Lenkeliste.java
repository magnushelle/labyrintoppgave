import java.util.Iterator;
import java.util.NoSuchElementException;

class Lenkeliste<T> implements Liste<T> {

    // Forste node i lenken
    protected Node node = null;

    // Holder styr pa sluttindex som inkrementerer ved add-metoder
    // og dekrementerer ved remove-metoder
    protected int sluttindex = -1;

    // Indre klasse
    protected class Node {
        
        // Indre klasse som beskriver hvert element i lenkelisten.
        // Klassen inneholder en referanse til objektet av klasse T
        // og en referanse til det neste objektet av Node-klassen
        
        T objekt;
        Node nesteNode = null;

        public Node(T x) {
            objekt = x;
        }
    }

    // Hjelpemetode 1
    protected void sjekkPosisjon(int pos) {
        // Metode som sjekker om posisjon er utenfor det 
        // vanlige omradet. Gyldig omrade er 0 < x <= sluttindex.

        if (pos < 0 || pos > sluttindex) {
            throw new UgyldigListeIndeks(pos);
        }
    }

    // Hjelpemetode 2
    protected Node hentNode(int pos) {
        // Metode som itererer gjennom lenken til angitt posisjon.
        // Denne metode sparer oss for redundant kode, da 
        // den gjentas i mange metoder lenger nede.
        
        sjekkPosisjon(pos);
        Node tellerNode = node;

        for (int i = 0; i < pos; i++) {
            tellerNode = tellerNode.nesteNode;
        }

        return tellerNode;
    }

    // Hjelpemetode 3
    protected boolean erTom() {
        // Metode som sjekker om lenken er tom og som returner
        // true eller false. 
        return node == null;
    }

    public void leggTil(T x) {
        // Metode som legger til i slutten lenken. Tar hoyde for at:
        //  1. lenken er tom
        //  2. lenken ikke er tom

        Node nyNode = new Node(x);
        
        // 1.
        if (erTom()) {
            node = nyNode;

        // 2.
        } else {
            hentNode(sluttindex).nesteNode = nyNode;
        }

        sluttindex++;   
    }

    public T fjern() throws UgyldigListeIndeks {
        // Metode som fjerner forste node og returnerer objektet 
        // tilhorende den forste noden i lenken. Tar hoyde for at:
        //  1. lenken er tom
        //  2. vi fjerner det eneste elementet i lenken
        //  3. lenken bestar av flere elementer

        //  1. 
        if (erTom()) {
            throw new UgyldigListeIndeks(-1);
        }

        T returverdi = node.objekt;

        //  2. 
        if (sluttindex == 0) {
            node = null;

        //  3.
        } else {
            node = node.nesteNode;
        }

        sluttindex--;
        return returverdi;
    }

    public void sett(int pos, T x) {
        // Metode som tar inn en posisjon og overskriver objektet
        // til noden pa gitt posisjon

        hentNode(pos).objekt = x;
    }

    public void leggTil(int pos, T x) {
        // Metode som tar inn et objekt og plasserer det i en node
        // pa angitt posisjon. Tar hoyde for at vi setter inn i:
        //  1. starten av lenken
        //  2. slutten av lenken, rett utenfor sluttindex
        //  3. en posisjon omgitt av andre noder

        Node nyNode = new Node(x);

        //  1.
        if (pos == 0) {
            nyNode.nesteNode = node;
            node = nyNode;

        //  2.
        } else if (pos > sluttindex) {
            hentNode(pos - 1).nesteNode = nyNode;

        //  3.
        } else {
            nyNode.nesteNode = hentNode(pos);
            hentNode(pos - 1).nesteNode = nyNode;
        }

        sluttindex++;
    }

    public T fjern(int pos) {
        // Metode som fjerner noder pa oppgitt posisjon. Tar hoyde
        // for at vi fjerner fra:
        //  1. begynnelsen av lenken
        //  2. slutten av lenken
        //  3. en posisjon omgitt av andre noder

        T returverdi = hentNode(pos).objekt;

        //  1.
        if (pos == 0) {
            node = node.nesteNode;

        //  2.
        } else if (pos == sluttindex) {
            hentNode(pos - 1).nesteNode = null;

        //  3.
        } else {
            hentNode(pos - 1).nesteNode = hentNode(pos + 1);
        }

        sluttindex--;
        return returverdi;
    }

    public int stoerrelse() {
        // Metode som returnerer antall noder i lenken
        return sluttindex + 1;
    }

    public T hent(int pos) {
        // Metode som returnerer objektet tilhorende noden
        // i angitt posisjon

        return hentNode(pos).objekt;
    }

    public Iterator<T> iterator(){
        return new ListeIterator();
    }
    private class ListeIterator implements Iterator<T>{
        Node tellerNode = node;

        @Override
        public boolean hasNext(){
            return tellerNode != null;
        }

        @Override
        public T next() throws NoSuchElementException{

            if (tellerNode == null){
                throw new NoSuchElementException("Noden har ikke en node som er lenket til seg. ");
            }

            T tmp = tellerNode.objekt;
            tellerNode = tellerNode.nesteNode;
            return tmp;
        }
    }
}