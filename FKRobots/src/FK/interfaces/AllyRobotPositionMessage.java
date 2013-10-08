package FK.interfaces;

import java.awt.Point;
import java.io.Serializable;

import FK.FKrado;

public class AllyRobotPositionMessage implements Serializable {
    /**
     * FKRado communication subsystem 
     */
    private static final long serialVersionUID = 1L;
    
    public String getSender() {
        return sender;
    }
    public Point getPosition() {
        return position;
    }
    public AllyRobotPositionMessage(String sender, Point position) {
        super();
        this.sender = sender;
        this.position = position;
    }
    public String sender;
    public Point position;
}
