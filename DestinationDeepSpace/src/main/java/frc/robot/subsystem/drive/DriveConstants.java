/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.MotorId;
/**
 * Add your docs here.
 */
public class DriveConstants {
    public static final double JOYSTICK_DEADBAND = 0.15;

    // Set velocity follower type to false when independent gear boxes are being used
    // Set to true of all wheels on one side are physically linked
    public static final boolean CLOSED_LOOP_FOLLOWER = false;
    public static final double DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS = 8976.0;	// per 100 ms, average of 10 samples


    public static final double MAX_ALLOWED_SPEED_IPS = 5.0*12.0;
    public static final double MAX_ALLOWED_TURN_DPS  = 180.0;
    public static final double MAX_ALLOWED_TURN_RADPS = Math.toRadians(MAX_ALLOWED_TURN_DPS);
    public static final double STANDARD_G_FTPSPS = 32.1740;
    public static final double MAX_LAT_ACCELERATION_IPSPS = STANDARD_G_FTPSPS * 12.0;
    public static final double LOCK_DEADBAND_IPS = 12.0;  // ignore button command changes above this speed
    public static final double ALIGN_DEADBAND_DPS = 45.0; // ignore button command changes above this turn rate

    public static final double WHEEL_TRACK_INCHES = 23.5;
    public static final double WHEEL_DIAMETER_INCHES = 6.0;
    public static final double WHEEL_CIRCUMFERENCE_INCHES = Math.PI*WHEEL_DIAMETER_INCHES;
    public static final double TRACK_TO_CIRCUMFERENCE_RATIO = WHEEL_TRACK_INCHES / WHEEL_DIAMETER_INCHES;
    public static final double WHEEL_ROTATION_PER_FRAME_DEGREES = TRACK_TO_CIRCUMFERENCE_RATIO / 360.0;

    // Speed constants
    public static final double DRIVE_MOTOR_NATIVE_TICKS_PER_REV=8192; // AMT-201 at 2048 pulses per rev
    public static final double DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC = WHEEL_CIRCUMFERENCE_INCHES * 
         (DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS /
          DRIVE_MOTOR_NATIVE_TICKS_PER_REV) * 10;
    public static final double DRIVE_MAXIMUM_NO_LOAD_SPEED_FT_PER_SEC = DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC / 12.0;

    public static final double MAX_ALLOWED_PERCENT_SPEED = MAX_ALLOWED_SPEED_IPS/DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC;
    public static final double DRIVE_MAXIMUM_NO_LOAD_TURN_RATE_RAD_PER_SEC = DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC / WHEEL_TRACK_INCHES / 2.0;
    public static final double DRIVE_MAXIMUM_NO_LOAD_TURN_RATE_DEG_PER_SEC = Math.toDegrees(DRIVE_MAXIMUM_NO_LOAD_TURN_RATE_RAD_PER_SEC);
    public static final double MAX_ALLOWED_PERCENT_TURN = MAX_ALLOWED_TURN_DPS / DRIVE_MAXIMUM_NO_LOAD_TURN_RATE_DEG_PER_SEC;
    
    // The motor controllers we use (TalonSRX) return velocity in terms of native ticks per 100 ms 
    // and expect commands to be similarly dimensioned.
    // Even though this is a constants package, we provide the convenient conversion to/from
    // inches per second to the native ticks per 100 ms.
    // NOTE: Integer truncation is assumed for a maximum reduction of 10 ticks per second.
    // For 8192 ticks per rev that is error of < 0.07 RPM
    public static int ipsToTicksP100(double ips)
    {
        double rps = ips / WHEEL_CIRCUMFERENCE_INCHES;
        return (int)(DRIVE_MOTOR_NATIVE_TICKS_PER_REV * rps / 10.0);
    }
    public static double ticksP100ToIps(int ticksP100)
    {
        double rps = ticksP100 * 10.0 / DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
        return rps * WHEEL_CIRCUMFERENCE_INCHES;
    }

    
	public static final int CONTROLLER_TIMEOUT_MS = 100; // Default timeout to wait for configuration response
    
    public static final int SUPER_HIGH_STATUS_FRAME_PERIOD_MS  =   5;	// CAUTION!
	public static final int HIGH_STATUS_FRAME_PERIOD_MS        =  10;	
	public static final int MEDIUM_HIGH_STATUS_FRAME_PERIOD_MS =  20;
	public static final int MEDIUM_STATUS_FRAME_PERIOD_MS      =  50;
    public static final int LOW_STATUS_FRAME_PERIOD_MS         = 100;

    public static final double DRIVE_MOTOR_NATIVE_TICKS_PER_FRAME_DEGREES = DRIVE_MOTOR_NATIVE_TICKS_PER_REV * WHEEL_ROTATION_PER_FRAME_DEGREES;
    public static final double MAXIMUM_MOTION_ERROR_INCHES = 0.125;	// Convert into native ticks later
    public static final double MAXIMUM_ROTATION_ERROR_INCHES = 0.50;

    // These Motion Magic values defined the shape of the trapezoidal profile for speed
    // The cruise speed is the maximum speed during the profile and is chosen to keep   		// below the maximum (which varies with battery voltage). The acceleration is the
        // slope allowed to reach the cruise speed or zero (hence, a trapezoid)
    //
    // Setting this to 80% of maximum is a reasonable place to start;
    // However, the acceleration is currently default to reach cruising speed within 1 second 
    // and may need to be increased or decreased depending on static friction limits of tires
        
    public static final int DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS = (int)(0.75 * 
         DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS);
    public static final int DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS = (int) (4346/1.5); // 0.26 g on wood//DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS;

    public static final int DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS = (int) (MAXIMUM_MOTION_ERROR_INCHES * DRIVE_MOTOR_NATIVE_TICKS_PER_REV / WHEEL_CIRCUMFERENCE_INCHES);
    public static final int DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION = (int) (MAXIMUM_ROTATION_ERROR_INCHES * DRIVE_MOTOR_NATIVE_TICKS_PER_REV / WHEEL_CIRCUMFERENCE_INCHES);
        
    public static final double MOTOR_TEST_PERCENT = 0.5;

	public static final double TURN_SIGN = 1.0;
}
