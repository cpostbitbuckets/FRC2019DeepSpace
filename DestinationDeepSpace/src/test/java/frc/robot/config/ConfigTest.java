package frc.robot.config;

import static org.junit.Assert.assertEquals;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {
    
    @Test
    public void testReadConfig() throws Exception {
        // this call loads in the config
        Config config = Config.instance();

        // check the eval, note, this test will break if the constants change
        assertEquals(9, config.motors.scoring.arm1.id);
        assertEquals(0.146143*2*2*2  *2*1.5, config.motors.scoring.arm1.motionPID.kP, 0);

        // check the templated config        
        assertEquals(3, config.motors.drive.left[0].id);
        
        // check the climber, make sure it loaded some stuff
        assertEquals(8, config.motors.climb.climb1.id);

        // check that merging works
        // the RightFront motor defines motion and velocity pid constants
        // The other right side motors should pick them up
        assertEquals(config.motors.drive.right[0].velocityPID.kF, 
        config.motors.drive.right[1].velocityPID.kF, 0);
        assertEquals(config.motors.drive.right[0].velocityPID.kF, 
        config.motors.drive.right[1].velocityPID.kF, 0);

        // Same with left
        assertEquals(config.motors.drive.left[0].velocityPID.kF, 
        config.motors.drive.left[1].velocityPID.kF, 0);
        assertEquals(config.motors.drive.left[0].velocityPID.kF, 
        config.motors.drive.left[1].velocityPID.kF, 0);

        // scoring arm2 is simple, does not inherit
        assertEquals(null, config.motors.scoring.arm2.motionPID);

        // test enums and climber limit switches
        assertEquals(NeutralMode.Brake, config.motors.climb.climb1.neutralMode);
        assertEquals(true, config.motors.climb.climb1.forwardLimitSwitch.enabled);

    }
}