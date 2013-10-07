package FK.Internal;

import java.awt.Point;

/**
 * Efkarado Utility Class
 * @author Mufid
 *
 */
public class Etc {
	public static Point getRandomPoint(int maxX, int maxY) {
		int x = (int)(Math.random() * maxX);
		int y = (int)(Math.random() * maxY);
		return new Point(x, y);
	}
}
