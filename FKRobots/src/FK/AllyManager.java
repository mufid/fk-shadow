package FK;

import java.awt.Point;
import java.util.HashMap;

public class AllyManager {
    static
    {
        instance = new AllyManager();
    }
    
    private HashMap<String, Point> allyPositions;
    private static final AllyManager instance;

    public AllyManager()
    {
        this.allyPositions = new HashMap<String, Point>();
    }

    public static final AllyManager getInstance()
    {
        return instance;
    }
    
    public synchronized void update(String name, Point position) {
        if (allyPositions.containsKey(name)) {
            allyPositions.remove(name);
        }
        allyPositions.put(name, position);
    }
    
    public synchronized Point get(String name) {
        return allyPositions.get(name);
    }
    
    public synchronized boolean existInlineAllies(Point from, Point target, double radius) {
        double dist = from.distance(target);
        for (String name : allyPositions.keySet()) {
            Point pos = allyPositions.get(name);
            double distToLine = FK.Internal.Etc.pointToLineDistance(from, target, pos);
            if (distToLine < radius && from.distance(pos) < dist) {
                return true;
            }
        }
        return false;
    }
}
