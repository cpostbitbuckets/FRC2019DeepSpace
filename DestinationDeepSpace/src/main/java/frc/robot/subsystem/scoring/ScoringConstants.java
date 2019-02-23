package frc.robot.subsystem.scoring;

public class ScoringConstants {

	public final static double MAX_ARM_MOTOR_CURRENT_AMPS = 60.0;
	public final static int ARM_BIAS_TICKS = 3737 - 57; // subtracted from absolute encoder value
	public final static double ANGLE_TOLERANCE_DEG = 4.0; // If we made it to within this angle then call it good

	public enum ScoringLevel {
		INVALID              (0),       // multiple levels are selected
		NONE                 (0),       // no levels are selected
        TOP_DEAD_CENTER      (0), //deg
		GROUND               (113), //deg vs inches -->(5.0),       // hatch panel ground pickup
		
		HP                   (83.0), //deg vs inches -->(19.0),    // cargo scoring

		BALL_CARGO           (63.0), // deg vs inches -->(31.5),    // cargo scoring
		BALL_LOADING_STATION (49.0), // deg vs inches -->(37.0),    // loading station pickup
		BALL_ROCKET_1        (72.0); // deg vs inches -->(27.5);    // rocket 1 scoring



		private final double HEIGHT_INCH;
		private final double ANGLE_RAD;

		ScoringLevel(double angle_deg) {
			HEIGHT_INCH = 0; //height_inch;
			// tip of arm is given by (height off floor) + (length) * cos(angle)
			//ANGLE_RAD = Math.acos((height_inch - ARM_AXIS_HEIGHT_OFF_FLOOR_INCH) / ARM_LENGTH_INCH);
			ANGLE_RAD = Math.toRadians(angle_deg);
		}

		public double getAngle_rad() {
			return ANGLE_RAD;
		}
	}


	public static final double ARM_LENGTH_INCH = 27.5;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR_INCH = 19;



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 4096;



	// TODO: actual value
	public static final int ROTATION_MOTOR_ERROR_DEADBAND_TICKS = 50;



	public static final double LEVEL_CHANGE_TIMEOUT_SEC = 3.0;
}