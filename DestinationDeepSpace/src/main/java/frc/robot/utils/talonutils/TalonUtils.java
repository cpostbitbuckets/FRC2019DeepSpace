package frc.robot.utils.talonutils;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;
import frc.robot.config.Config.MotorConfig;
import frc.robot.config.Config.MotorSlotConfig;

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
        int pidIdx = 0;
        initializeMotorFPID(motor, kF, kP, kI, kD, iZone, slotIdx, pidIdx);
    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int iZone, int slotIdx, int pidIdx){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function too.
         */

        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        motor.selectProfileSlot(slotIdx, pidIdx);
        motor.config_kF(slotIdx, kF, timeout);
        motor.config_kP(slotIdx, kP, timeout);
        motor.config_kI(slotIdx, kI, timeout);
        motor.config_kD(slotIdx, kD, timeout);
        motor.config_IntegralZone(slotIdx, iZone, RobotMap.CONTROLLER_TIMEOUT_MS);
    }

    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor) {
        initializeQuadEncoderMotor(motor, MAX_STATUS_FRAME_PERIOD, RobotMap.PRIMARY_PID_LOOP);
    }
    public static void initializeMagEncoderRelativeMotor(WPI_TalonSRX motor) {
        initializeMagEncoderRelativeMotor(motor, MAX_STATUS_FRAME_PERIOD, RobotMap.PRIMARY_PID_LOOP);
    }
    public static void initializeMagEncoderAbsoluteMotor(WPI_TalonSRX motor) {
        initializeMagEncoderAbsoluteMotor(motor, MAX_STATUS_FRAME_PERIOD, RobotMap.PRIMARY_PID_LOOP);
    }

    /**
     * Initializes the quad encoder motor, whatever that means.
     */
    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor, int statusFramePeriod, int pidLoop) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;


        motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, statusFramePeriod, timeout);

    }

    /**
     * Initializes the motor controller to have a relative mag encoder.
     */
    public static void initializeMagEncoderRelativeMotor(WPI_TalonSRX motor, int statusFramePeriod, int pidLoop) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;


        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, statusFramePeriod, timeout);

    }

    /**
     * Initializes the motor controller to have a relative mag encoder.
     */
    public static void initializeMagEncoderAbsoluteMotor(WPI_TalonSRX motor, int statusFramePeriod, int pidLoop) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;


        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, statusFramePeriod, timeout);

    }

    public static WPI_TalonSRX createMotorFromConfig(MotorConfig config, MotorSlotConfig slots) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        WPI_TalonSRX motor = new WPI_TalonSRX(config.id);

        // zero it out
        TalonUtils.initializeMotorDefaults(motor);

        // FIRST !!! get the sensor phase correct.
        // If positive input to motor controller (green LED) makes the sensor
        // return positive increasing counts then the sensor phase is set correctly.
        // E.g., start with false, if the counts go the correct direction you are good
        // to go; if not, set the flag to true (indicating the sensor inverted from the
        // positive input).
        motor.setSensorPhase(config.sensorPhase);

        // SECOND !!! if you need the motor to move in the opposite direction when
        // positive is commanded, set the appropriate inversion flag true
        //
        // NOTE: If you are using a WPI drive class that performs inversion arithmetically
        // then look for a function to disable it; in order to use both the WPI class
        // and other control modes in the robot, the motor the inversions (if needed) must be in
        // the physical controller firmware, not the software.
        motor.setInverted(config.inverted);

        if (config.neutralMode != null) {
            motor.setNeutralMode(config.neutralMode);
        }

        // Motion Magic Control Constant (JUNIOR)
        // see documentation (2018 SRM Section 12.6)
        // The gains are determined empirically following the Software Reference Manual
        // Summary:
        //	Run drive side at full speed, no-load, forward and initiate SelfTest on System Configuration web page
        //  Observe the number of encoder ticks per 100 ms, the % output, and voltage
        //  Collect data in both forward and backwards (e.g., 5 fwd, 5 back)
        //  Average the absolute value of that number, adjust as measured_ticks / percentage_factor
        //  Compute Kf = 1023 / adjusted_tick_average
        //  The using that value, run the Motion Magic forward 10 revolutions at the encoder scale
        //  Note the error (in ticks)
        //  Compute Kp = 0.1 * 1023 / error as a starting point
        //  Command any position through Motion Magic and attempt to turn the motor by hand while holding the command
        //  If the axle turns, keep doubling the Kp until it stops turning (or at leasts resists vigorously without
        //  oscillation); if it oscillates, you must drop the gain.
        //  Run the Motion Magic for at least 10 rotations in each direction
        //  Make not of any misses or overshoot.
        //  If there is unacceptable overshoot then set Kd = 10 * Kp as a starting point and re-test
        //
        //  Put drive train on ground with weight and re-test to see if position is as commanded.
        //  If not, then add SMALL amounts of I-zone and Ki until final error is removed.    
        if (config.motionPID != null) {
            TalonUtils.initializeMotorFPID(motor, config.motionPID.kF, config.motionPID.kP, config.motionPID.kI, config.motionPID.kD, config.motionPID.iZone, slots.motionMagicSlot, slots.primaryPIDLoop);
        }

        // Motion Magic likes to specify a trapezoidal speed profile
        // These two settings provide the top speed and acceleration (slope) of profile
        motor.configMotionAcceleration(config.motionAcceleration, timeout);
		motor.configMotionCruiseVelocity(config.motionCruise, timeout);

        // velocity is always slot 1
        if (config.velocityPID != null) {
            TalonUtils.initializeMotorFPID(motor, config.velocityPID.kF, config.velocityPID.kP, config.velocityPID.kI, config.velocityPID.kD, config.velocityPID.iZone, slots.velocitySlot, slots.primaryPIDLoop);
        }

        // Identify what type of feedback device we will use on this drive base
        // Assume that all feedback devices are the same type on all axels that
        // need to be measured.
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

        // Closed loop ramp rates are tricky; too much and the PID can become unstable as
        // if there was a lot of system lag; we must be cautious!
        // An alternative is to use an alpha filter on the inputs to prevent the user
        // from changing the command too rapidly
        motor.configOpenloopRamp(config.openLoopRampSeconds, timeout);
        motor.configClosedloopRamp(config.closedLoopRampSeconds, timeout);

        // The left and right sides may not be precisely balanced in terms of
        // friction at really low speeds. We would like fine control to be balanced
        // so the neutral deadband is adjusted to determine when the motors start
        // moving on each side. This also prevents the motor from moving when
        // really small commands are passed through.
        //
        // The values are determined empirically by simply driving the motors slowly
        // until they first start to move on one side and not the other. Increase the
        // values until the desired response is achieved.
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
