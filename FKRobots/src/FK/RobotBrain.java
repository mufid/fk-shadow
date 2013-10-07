package FK;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.logging.Level;

import FK.Internal.Etc;
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
import robocode.WinEvent;

public class RobotBrain
{
    private static RobotBrain instance;
    private static MyLogger logger = MyLogger.getLogger( RobotBrain.class.getName() );
    static
    {
        logger.setEnabled( false );
        logger.setLevel( Level.FINE );
    }
    private UnderDog robot;
    private Navigator navigator;
    private TacticalAdvisor tacticalAdvisor;
    private boolean iWon;

    public static final synchronized RobotBrain getInstance( UnderDog r )
    {
        if ( instance == null )
        {
            instance = new RobotBrain( r );
        }
        return instance;
    }
    
    public void forceToPosition( Point2D pos ) {
    	this.navigator.determineHeadingChangeToGoToPoint(pos);
    }

    private RobotBrain( UnderDog r )
    {
        this.robot = r;
        this.navigator = new Navigator( this.robot );
        this.tacticalAdvisor = new TacticalAdvisor( this.robot );
    }

    public void reset()
    {
        this.iWon = false;
    }

    public void makeDecision()
    {
        if ( ( !this.iWon ) && ( this.robot.doOtherOpponentsExist() ) )
        {
            this.tacticalAdvisor.makeDecision();
            this.navigator.makeDecision();
        }
        else if ( this.iWon )
        {
            this.robot.clearAllEvents();
            this.robot.doNothing();
        }
        this.robot.execute();
    }

    public void processEvent( ScannedRobotEvent event )
    {
        double enemyBearing = this.robot.getHeading() + event.getBearing();

        double enemyX = event.getDistance() * Math.sin( Math.toRadians( enemyBearing ) );
        double enemyY = event.getDistance() * Math.cos( Math.toRadians( enemyBearing ) );
        Point2D enemyLocation = new Point2D.Double( this.robot.getX() + enemyX, this.robot.getY() + enemyY );
        logger.log( Level.FINEST, "\nprocessEvent(ScannedRobotEvent event)" );
        logger.log( Level.FINEST, "enemyX: " + enemyX + " enemyY: " + enemyY + "  enemyBearing: " + enemyBearing );
        logger.log( Level.FINEST, "enemyLocation: " + enemyLocation );
        logger.log( Level.FINEST, "robot.getLocation(): " + this.robot.getLocation() );
        logger.log( Level.FINEST, "robot.getHeading(): " + this.robot.getHeading() + "  event.getBearing(): " + event.getBearing() );

        EnemyManager.getInstance().processEvent( event, enemyLocation );
        this.navigator.processEvent( event );
        this.tacticalAdvisor.processEvent( event );
        this.robot.execute();
    }

    public void processEvent( HitRobotEvent event )
    {
        this.tacticalAdvisor.processEvent( event );
        this.navigator.processEvent( event );
        this.robot.execute();
    }
    
    public void processEvent2( HitRobotEvent e )
    {
    	if (e.getBearing() > -90 && e.getBearing() < 90) {
    		this.robot.setTurnRight(e.getBearing());
    		this.robot.back(50);
			
		} // else he's in back of us, so set ahead a bit.
		else {
			
			this.robot.setTurnLeft(e.getBearing());
    		this.robot.ahead(50);
		}
    }

    public void processEvent( HitWallEvent event )
    {
        this.navigator.processEvent( event );
        this.robot.execute();
    }

    public void processEvent( BulletMissedEvent event )
    {
        EnemyManager.getInstance().processEvent( event );
        this.tacticalAdvisor.processEvent( event );
    }

    public void processEvent( BulletHitEvent event )
    {
    	EnemyManager.getInstance().processEvent( event );
    }

    public void processEvent( HitByBulletEvent event )
    {
        this.navigator.processEvent( event );
        this.tacticalAdvisor.processEvent( event );
        this.robot.execute();
    }

    public void processEvent( CustomEvent event )
    {
//        this.navigator.processEvent( event );
        this.tacticalAdvisor.processEvent( event );
    }

    public void processEvent( RobotDeathEvent event )
    {
        EnemyManager.getInstance().processEvent( event );
        this.tacticalAdvisor.processEvent( event );
        this.robot.execute();
    }

    public void processEvent( DeathEvent event )
    {
        this.tacticalAdvisor.processEvent( event );
        this.navigator.processEvent( event );
        EnemyManager.getInstance().processEvent( event );
        this.robot.execute();
    }

    public void processEvent( WinEvent event )
    {
        this.iWon = true;
        this.tacticalAdvisor.processEvent( event );
        this.navigator.processEvent( event );
        EnemyManager.getInstance().processEvent( event );
        this.robot.execute();
    }

    public void onPaint( Graphics2D g )
    {
        this.tacticalAdvisor.onPaint( g );
        this.navigator.onPaint( g );
    }

    public void findNextNearestMove() {
    	Point2D target = null;// = null;
    	// DO NOT MOVE NEAR WALL. DO RANDOM POSITION IF NEAR WALL!
    	boolean nearWall = this.nearWall();
    	if (nearWall) {
    		target = new Point(400, 300);
    	}
    	// If there is no current locked target, run randomly
    	if (EnemyManager.getInstance().getAllEnemies().size() == 0) {
    		if (target == null) target = Etc.getRandomPoint(800, 600);
    	} else {
    		// TODO: FLAW WARNING: Using closest enemy may not accurate
    		if (target == null) target = EnemyManager.getInstance().getClosestEnemy().getLocation();
    	}
    	// Only when in not "ENEMY PERIMETER"
    	// Used only when want to come nearer to enemy
    	// Use DODGEMOVE if in Enemy Perimeter
    	Point currentPos = new Point((int) this.robot.getX(), (int) this.robot.getY());
    	
    	// Get the angle between currentPos and target
    	System.out.println("Comparing: " + currentPos.toString() + " and " + target.toString());
    	double angle_rad = Math.atan2(target.getY() - currentPos.getY(), target.getX() - currentPos.getX());
    	
    	System.out.println("Now and target: Going " + Math.toDegrees(angle_rad) + " degree");
    	double RANDOM_MAX = 2.3f;
    	double RADIUS_RANDOM = 20.f;
    	double MGAX_RADIUS = 140.f + Math.random() * RADIUS_RANDOM;
    	angle_rad += (Math.random() * RANDOM_MAX * 2) - RANDOM_MAX;
    	
    	// TODO: FLAW WARNING: MAY BE INFINITY!
    	Point nextPoint = new Point((int) (MGAX_RADIUS * Math.cos(angle_rad) + currentPos.getX()), (int) (MGAX_RADIUS * Math.sin(angle_rad) + currentPos.getY()));
    	System.out.println("FIXED: " + nextPoint.toString());
    	this.forceToPosition(nearWall ? target : nextPoint);
    }

	private boolean nearWall() {
		int SENSITIVITY = 120;
		System.out.println("WARNING: NEAR WALL");
		boolean xNearWall = this.robot.getX() < SENSITIVITY || this.robot.getX() > 800 - SENSITIVITY;
		boolean yNearWall = this.robot.getY() < SENSITIVITY || this.robot.getY() > 600 - SENSITIVITY;
		return xNearWall || yNearWall;
	}
    
}

 