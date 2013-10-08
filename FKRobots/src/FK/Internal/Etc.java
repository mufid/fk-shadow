package FK.Internal;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Efkarado Utility Class
 * 
 * @author Mufid
 * 
 */
public class Etc {
    public static Point getRandomPoint(int maxX, int maxY) {
        int x = (int) (Math.random() * maxX);
        int y = (int) (Math.random() * maxY);
        return new Point(x, y);
    }

    /**
     * FKRado special function for winning 
     * @param A Titik A
     * @param B Titik B
     * @param P Titik P
     * @return Mengembalikan jarak titik P ke garis AB
     */
    public static double pointToLineDistance(Point A, Point B, Point P) {
        double normalLength = Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y)
                * (B.y - A.y));
        return Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x))
                / normalLength;
    }

    public static Point toPoint(Point2D p) {
        return new Point((int) p.getX(), (int) p.getY());
    }
}
