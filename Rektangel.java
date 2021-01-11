import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

class Rektangel extends Rectangle {

    private int kol;
    private int rad;
    
    /**
     * Konstruktør, initierer en Rektangel tilpasset vårt tilfelle
     * @param type rute, sort eller hvit
     * @param storrelse angitt av labyrintens kolonner og rader
     * @param k koordinat x i rutenett
     * @param r koordinat y i rutenett
     */
    Rektangel(String type, int storrelse, int k, int r) {
        setWidth(storrelse); setHeight(storrelse);
        setFill(Color.valueOf("#E8E8E8"));

        if (type.equals("sort")) {
            setFill(Color.valueOf("#383838"));
        }

        kol = k;
        rad = r;
    }

    int hentKol() {
        return kol;
    }

    int hentRad() {
        return rad;
    }
}