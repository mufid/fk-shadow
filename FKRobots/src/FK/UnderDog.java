package FK;

import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import FK.logging.MyLogger;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.CustomEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;

public class UnderDog extends FKrado
{
	
	
    private static MyLogger logger = MyLogger.getLogger( UnderDog.class.getName() );
    private final RobotBrain myBrain;
    private Battlefield battlefield;
    private boolean hasPlayedClip = false;
    private static final int EXTERNAL_BUFFER_SIZE = 128000;
    public int total_tick=0;
    
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
        while ( true )
        {
            this.myBrain.makeDecision();
            
            if(total_tick>4){
            	total_tick=0;
            	this.myBrain.findNextNearestMove();
            }
            total_tick++;
        	
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
