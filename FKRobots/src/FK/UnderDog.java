package FK;

import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;

import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.CustomEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;
import FK.interfaces.AllyRobotPositionMessage;
import FK.logging.MyLogger;

public class UnderDog extends FKrado
{
	
	
    private static MyLogger logger = MyLogger.getLogger( UnderDog.class.getName() );
    private final RobotBrain myBrain;
    private Battlefield battlefield;
    private boolean hasPlayedClip = false;
    private static final int EXTERNAL_BUFFER_SIZE = 128000;
    public int total_tick=0;
    public int id = 0;
    
    public UnderDog()
    {
    	this.myBrain = RobotBrain.getInstance( this );
    }

    public void run()
    {
    	
        // can't be called in the constructor
        this.battlefield = new Battlefield( this );
        
        this.myBrain.reset();
        if ( ( getOthers() == 1 ) && ( !this.hasPlayedClip ) )
        {
            this.hasPlayedClip = true;
        }

        setRadarColor( Color.red );
        setGunColor( Color.yellow );
        setBodyColor( Color.yellow );
        setScanColor( Color.yellow );
        setBulletColor( Color.yellow );
        setAdjustGunForRobotTurn( true );
        
        id=Integer.parseInt(this.getName().replaceAll("[^0-9]*", ""));
        
        
        //move to certain position first
        System.out.println("move First");
        Point[] positionInit={new Point(20, 20),new Point(700, 20),new Point(20, 500),new Point(700, 500)};
        Point thisInitialPosition = positionInit[id-1];
        this.myBrain.forceToPosition(thisInitialPosition);
        int firsttick = 100;
        Point lastPosition = null;
        while ( true )
        {
            this.myBrain.makeDecision();
            
            firsttick--;
            if (thisInitialPosition.distance(this.getX(), this.getY()) < 20 || 
                this.getVelocity()<0.1) {
                firsttick = 0;
            }
            lastPosition = new Point((int) this.getX(), (int) this.getY());
            try {
                this.broadcastMessage(new AllyRobotPositionMessage(getName(), lastPosition));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (firsttick > 0) {
                continue;
            }
            
            
            if(total_tick>4){
            	total_tick=0;
            	this.myBrain.findNextNearestMove();
            }
            total_tick++;
            
            
        	
        }
    }
    
    @Override
    public void onMessageReceived(MessageEvent event) {
        Serializable raw = event.getMessage();
        if (raw instanceof AllyRobotPositionMessage) {
            AllyRobotPositionMessage msg = (AllyRobotPositionMessage) raw;
            AllyManager.getInstance().update(msg.getSender(), msg.getPosition());
        }
    }

    public boolean isGoingForward()
    {
        return getDistanceRemaining() >= 0.0D;
    }

    public void onScannedRobot( ScannedRobotEvent event )
    {
    	if (isTeammate(event.getName())) {
			return;
		}
        this.myBrain.processEvent( event );
    }

    public void onHitByBullet( HitByBulletEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onHitRobot( HitRobotEvent event )
    {
    	if(!isTeammate(event.getName())) this.myBrain.processEvent( event );
    	else this.myBrain.processEvent2(event);
    	//else total_tick=10;
    }

    public void onHitWall( HitWallEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onBulletMissed( BulletMissedEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onBulletHit( BulletHitEvent event )
    {
    	if(!isTeammate(event.getName()))this.myBrain.processEvent( event );
    	else{
    		this.ahead(10);
    	}
    }

    public void onCustomEvent( CustomEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onRobotDeath( RobotDeathEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onDeath( DeathEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onWin( WinEvent event )
    {
        this.myBrain.processEvent( event );
    }

    public void onSkippedTurn( SkippedTurnEvent event )
    {
        System.out.println( "WHOOPSTakinto   LONG!" );
    }

    public boolean doOtherOpponentsExist()
    {
        return 0 < getOthers();
    }

    public Point2D getLocation()
    {
        return new Point2D.Double( getX(), getY() );
    }

    public Battlefield getBattlefield()
    {
        return battlefield;
    }

    public void onPaint( Graphics2D g )
    {
        this.myBrain.onPaint( g );
    }
    
	
	@Override
	public void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			// Define the movement
			//this.myBrain.forceToPosition(new Point(100, 00));
			
			this.myBrain.findNextNearestMove();
			break;
		}
	}
}
