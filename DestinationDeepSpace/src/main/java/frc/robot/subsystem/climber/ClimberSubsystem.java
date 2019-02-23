/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.climber;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.ServoId;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.utils.talonutils.TalonUtils;
import frc.robot.utils.talonutils.TalonUtils;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.utils.talonutils.TalonUtils;
/**
 * Add your docs here.
 */
public class ClimberSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	// here. Call these from Commands.

	// Singleton method; use ClimberSubsystem.instance() to get the ClimberSubsystem instance.
	Servo climbServo;
	WPI_TalonSRX  climbMotor1;
	WPI_TalonSRX climbMotor2;


	// TODO: Set proper values for angles and motors
	double highClimbAngle = 40;
	double highClimbSpeed = 0.25;

	double lowClimbAngle = 10;

	double start;

	public enum eState {
	IDLE,
	ARMED,
	HIGH_CLIMB,
	LOW_CLIMB;
	}

	eState state = eState.IDLE;

	private final OI oi = OI.instance();

	private ClimberSubsystem() {
		setName("ClimberSubsystem");
		climbServo  = new Servo(ServoId.CLIMB_SERVO_ID);
		climbMotor1 = TalonUtils.createMotorFromConfig(config.motors.climb.climb1);
		climbMotor2 = TalonUtils.createMotorFromConfig(config.motors.climb.climb2);

		climbMotor1.overrideLimitSwitchesEnable(true);
		climbMotor2.follow(climbMotor1);
	}

	public static ClimberSubsystem instance() {
		if(inst == null)
			inst = new ClimberSubsystem();
		return inst;
	}
	private static ClimberSubsystem inst;
	

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void periodic() {
		clearDiagnosticsEnabled();
		updateBaseDashboard();
		SmartDashboard.putBoolean(getName()+"/END Limit Switch", climbMotor1.getSensorCollection().isFwdLimitSwitchClosed());
		SmartDashboard.putNumber(getName() + "/ManualJoystickCommand", oi.manualClimbControl());
		double climbMotor1current = climbMotor1.getOutputCurrent();
		double climbMotor2current = climbMotor2.getOutputCurrent();
		SmartDashboard.putNumber(getName() + "/climbMotor1Current", climbMotor1current);
		SmartDashboard.putNumber(getName() + "/climbMotor2Current", climbMotor2current);
		switch (state) {
			case IDLE:{
				climbMotor1.set(0);
				if (oi.armClimber()){
					state = eState.ARMED;	
				}
			}
				break;
			case ARMED:{
				if (oi.highClimb()){
					state = eState.HIGH_CLIMB;
					start = Timer.getFPGATimestamp();
				}
				else if (oi.lowClimb()){
					state = eState.LOW_CLIMB;
				}
			}
				break;
			case HIGH_CLIMB: {
				highClimbManual();
				//highClimb();
				if (climbMotor1.getSensorCollection().isFwdLimitSwitchClosed()||climbMotor1current>200||climbMotor2current>200){
					state=eState.IDLE;
				}
			}	
				break;
			case LOW_CLIMB: {
				lowClimb();
				}	
					break;
			default:{

			}
				break;
		}

		if (getTelemetryEnabled())
		{
			updateDashboard();
		}

	}

	@Override
	public void diagnosticsPeriodic() {
		updateBaseDashboard();
		if (getDiagnosticsEnabled())
		{
			double angle = SmartDashboard.getNumber(getName()+"/ServoTestAngle(deg)", 0.0);
			climbServo.setAngle(angle);
		}
		updateDashboard();
	}

	public void updateDashboard()
	{
		SmartDashboard.putNumber(getName()+"/CurrentServoAngle(deg)",climbServo.getAngle());
		SmartDashboard.putString(getName()+"/CurrentState()",state.name());
	}

	public void initialize() {
		initializeBaseDashboard();

		initializeDashboard();
	}

	void initializeDashboard()
	{
		SmartDashboard.putNumber(getName()+"/ServoTestAngle(deg)", 0.0);
	}

	@Override
	public void diagnosticsInitialize() {

	}

	@Override
	public void diagnosticsCheck() {

	}

	private int highClimb() {
		int err = 0;
		climbServo.setAngle(highClimbAngle);
		if (Timer.getFPGATimestamp() - start > 1.0) {
			climbMotor1.set(highClimbSpeed);
		}
		return (err);
	}

	private void highClimbManual()
	{
		climbServo.setAngle(highClimbAngle);
		if (Timer.getFPGATimestamp() - start > 1.0) {
			climbMotor1.set(oi.manualClimbControl());
		}
	}


	private int lowClimb() {
		int err = 0;
		climbServo.setAngle(lowClimbAngle);
		return (err);
	}

	public void startIdle() {
		state = eState.IDLE;
		climbServo.setAngle(0);
	}
}
