package frc.robot.config;

import java.io.IOException;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Config {

    static public final String DEFAULT_CONFIG_PATH = "config.robot.yaml";
    static public final String JUNIOR_CONFIG_PATH = "config.junior.yaml";

    /**
     * Singleton method; use Config.instance() to get the Config instance.
     * The first time this is called, it will load in the proper config for this
     * robot, either Junior, the default, the sim, etc.
     * @return
     */
	public static Config instance() {
        if(inst == null) {
            try {
                if (System.getProperty("junior", null) != null) {
                    inst = ConfigReader.readConfig(JUNIOR_CONFIG_PATH);
                } else {
                    inst = ConfigReader.readConfig(DEFAULT_CONFIG_PATH);
                }
            } catch (IOException e) {
                // if config load fails, we can't operate the robot, throw a runtime exception to force it to exit
                System.err.println("Failed to load config from resources.");
                throw new RuntimeException(e);
            }        
        }
		return inst;
	}
    
    private static Config inst;

    private Config() {}

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MotorsConfig {
        public DriveMotorSubsystemConfig drive = new DriveMotorSubsystemConfig();
        public ScoringMotorSubsystemConfig scoring = new ScoringMotorSubsystemConfig();
        public ClimbMotorSubsystemConfig climb = new ClimbMotorSubsystemConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DriveMotorSubsystemConfig {
        public MotorConfig[] left;
        public MotorConfig[] right;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScoringMotorSubsystemConfig {
        public MotorConfig arm1 = new MotorConfig();
        public MotorConfig arm2 = new MotorConfig();
        public MotorConfig intake = new MotorConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ClimbMotorSubsystemConfig {
        public MotorConfig climb1 = new MotorConfig();
        public MotorConfig climb2 = new MotorConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MotorConfig {
        public int id;
        public String name;
        public boolean inverted;
        public boolean sensorPhase;

        public NeutralMode neutralMode;

        public double openLoopRampSeconds;
        public double closedLoopRampSeconds;
        public double neutralDeadband;
        public double fullThrottleAverageSpeedNativeTicks;
        public double cruiseSpeedNativeTicks;

        public LimitSwitch forwardLimitSwitch;
        public LimitSwitch reverseLimitSwitch;

        public MotorPID motionPID;
        public int motionCruise;
        public int motionAcceleration;
        
        public MotorPID velocityPID;
        
        public Encoder encoder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LimitSwitch {
        public boolean enabled;
        public LimitSwitchNormal normalOpenOrClose;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Encoder {
        public boolean enabled;
        public int pidLoop;
        public FeedbackDevice type;
        public int startingPosition;
        public int statusFramePeriod;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MotorPID {
        public double kF;
        public double kP;
        public double kI;
        public double kD;
        public int iZone;
    }

    /**
     * Some easy to change constants for templating
     */
    public Map<String, Object> motorIds;

    /**
     * Some variables for templating
     */
    public Map<String, Object> vars;


    /**
     * This is the motors section of config
     */
    public MotorsConfig motors = new MotorsConfig();

}