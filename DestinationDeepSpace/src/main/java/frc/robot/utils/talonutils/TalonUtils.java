package frc.robot.utils.talonutils;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;
import frc.robot.config.Config.MotorConfig;

public class TalonUtils {

    public static int MAX_STATUS_FRAME_PERIOD = 160;
    /**
     * initializeMotor - set all of the motor configuration states to a known value
     * This is important when we are not sure if the motor is in a factory state
     * @param motor
     *
     * The following T O D O is done.
     * T O D O: Move this to a separate package?
     */
    public static void initializeMotorDefaults(WPI_TalonSRX motor)
    {
        // TODO: Check ErrorCode?
        motor.configFactoryDefault(RobotMap.CONTROLLER_TIMEOUT_MS);

        motor.stopMotor();

    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int iZone){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function.
         */

        int slotIdx = 0;
        initializeMotorFPID(motor, kF, kP, kI, kD, iZone, slotIdx);
    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int iZone, int slotIdx){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function too.
         */

        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        motor.selectProfileSlot(slotIdx, RobotMap.PRIMARY_PID_LOOP);
        motor.config_kF(slotIdx, kF, timeout);
        motor.config_kP(slotIdx, kP, timeout);
        motor.config_kI(slotIdx, kI, timeout);
        motor.config_kD(slotIdx, kD, timeout);
        motor.config_IntegralZone(slotIdx, iZone, RobotMap.CONTROLLER_TIMEOUT_MS);
    }

    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor) {
        initializeQuadEncoderMotor(motor, MAX_STATUS_FRAME_PERIOD);
    }
    public static void initializeMagEncoderRelativeMotor(WPI_TalonSRX motor) {
        initializeMagEncoderRelativeMotor(motor, MAX_STATUS_FRAME_PERIOD);
    }
    public static void initializeMagEncoderAbsoluteMotor(WPI_TalonSRX motor) {
        initializeMagEncoderAbsoluteMotor(motor, MAX_STATUS_FRAME_PERIOD);
    }

    /**
     * Initializes the quad encoder motor, whatever that means.
     */
    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor, int statusFramePeriod) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;
        int pidLoop = RobotMap.PRIMARY_PID_LOOP;


        motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, statusFramePeriod, timeout);

    }

    /**
     * Initializes the motor controller to have a relative mag encoder.
     */
    public static void initializeMagEncoderRelativeMotor(WPI_TalonSRX motor, int statusFramePeriod) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;
        int pidLoop = RobotMap.PRIMARY_PID_LOOP;


        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, statusFramePeriod, timeout);

    }

    /**
     * Initializes the motor controller to have a relative mag encoder.
     */
    public static void initializeMagEncoderAbsoluteMotor(WPI_TalonSRX motor, int statusFramePeriod) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;
        int pidLoop = RobotMap.PRIMARY_PID_LOOP;


        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, statusFramePeriod, timeout);

    }

    public static WPI_TalonSRX createMotorFromConfig(MotorConfig config) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        WPI_TalonSRX motor = new WPI_TalonSRX(config.id);

        // zero it out
        TalonUtils.initializeMotorDefaults(motor);

        motor.setInverted(config.inverted);
        motor.setSensorPhase(config.sensorPhase);
        if (config.neutralMode != null) {
            motor.setNeutralMode(config.neutralMode);
        }

        // motion is always slot0
        if (config.motionPID != null) {
            TalonUtils.initializeMotorFPID(motor, config.motionPID.kF, config.motionPID.kP, config.motionPID.kI, config.motionPID.kD, config.motionPID.iZone, 0);
        }
		motor.configMotionAcceleration(config.motionAcceleration, timeout);
		motor.configMotionCruiseVelocity(config.motionCruise, timeout);

        // velocity is always slot 1
        if (config.velocityPID != null) {
            TalonUtils.initializeMotorFPID(motor, config.velocityPID.kF, config.velocityPID.kP, config.velocityPID.kI, config.velocityPID.kD, config.velocityPID.iZone, 1);
        }

        if (config.encoder != null && config.encoder.enabled) {
            switch (config.encoder.type) {
                case QuadEncoder:
                    motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, config.encoder.pidLoop, timeout);
                    motor.setSelectedSensorPosition(config.encoder.startingPosition, config.encoder.pidLoop, timeout);
                    motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.encoder.pidLoop, timeout);        
                    break;
                case CTRE_MagEncoder_Relative:
                    motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, config.encoder.pidLoop, timeout);
                    motor.setSelectedSensorPosition(config.encoder.startingPosition, config.encoder.pidLoop, timeout);
                    motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.encoder.pidLoop, timeout);        
                    break;
                case CTRE_MagEncoder_Absolute:
                    motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, config.encoder.pidLoop, timeout);
                    motor.setSelectedSensorPosition(config.encoder.startingPosition, config.encoder.pidLoop, timeout);
                    motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, config.encoder.pidLoop, timeout);        
                    break;
                default:
                    System.err.println("Encoder type: " + config.encoder.type + " is not supported in config");
                    break;            
            }
        }

        motor.configOpenloopRamp(config.openLoopRampSeconds, timeout);
        motor.configClosedloopRamp(config.closedLoopRampSeconds, timeout);
        motor.configNeutralDeadband(config.neutralDeadband, timeout);

        if (config.forwardLimitSwitch != null && config.forwardLimitSwitch.enabled) {
            motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.forwardLimitSwitch.normalOpenOrClose);
        }
        if (config.reverseLimitSwitch != null && config.reverseLimitSwitch.enabled) {
            motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.reverseLimitSwitch.normalOpenOrClose);
        }

        return motor;
    }

}
