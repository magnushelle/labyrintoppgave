import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.collections.ObservableList;

class Rutenett extends GridPane {
    
    /**
     * Metode som itererer gjennom Rutenettet og returnerer Rektangelet 
     * på oppgitt posisjon
     * @param kol kolonnenummer
     * @param rad radnummer
     * @return Rektangel som ligger på oppgitt posisjon
     */
    Rektangel hentInnhold(int kol, int rad) {

        ObservableList<Node> innhold = this.getChildren();

        for (Node n : innhold) {
            Rektangel r = (Rektangel) n;
            if (r.hentKol() == kol && r.hentRad() == rad) {
                return r;
            }
        }
        
        return null;
    }
}