package frc.robot.simulator.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import frc.robot.Robot;
import frc.robot.simulator.physics.bodies.DriveBaseSide;
import frc.robot.subsystem.drive.DriveSubsystem;

/**
 * A simple screen displaying the drive base in a side view
 */
public class DriveBaseSideScreen extends AbstractPhysicsSimulationScreen {

    private Robot robot;
    private Stage stage;
    private PhysicsSimulation physicsSimulation;
    private World world;
    private DriveBaseSide driveBaseLeftSide;
    private DriveBaseSide driveBaseRightSide;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    // no gravity, let stuff float
    private Vector2 gravity = new Vector2(0, 0);

    public DriveBaseSideScreen(PhysicsSimulation physicsSimulation, Robot robot) {
        this.physicsSimulation = physicsSimulation;
        this.robot = robot;

        // make our camera a 5x5 meter space
        float worldWidth = 5;
        float worldHeight = 5;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight * ((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth()));
        camera.update();
        Viewport viewport = new FitViewport(worldWidth, worldHeight * ((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth()), camera);

        stage = new Stage(viewport);
        debugRenderer = new Box2DDebugRenderer();

        // create a world to simulate the physics in
        world = new World(gravity, true);

        // create a couple actors
        driveBaseLeftSide = new DriveBaseSide(world, 2f, 2f);
        driveBaseRightSide = new DriveBaseSide(world, 4f, 2f);
        stage.addActor(driveBaseLeftSide);
        stage.addActor(driveBaseRightSide);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // wheels move at 20000 ticks / 100ms
        // or 2.44140625 revolutions per 100ms
        // or 24.4 revolutions per second
        // motor speed is in radians per second
        // 2 pi radians is a full revolution so
        // 24.4 rev/s = 2(pi)*24.4 radians / sec so about 50
        float motorSpeed = 50f;

        driveBaseLeftSide.setFrontMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getLeftFrontMotor().getMotorOutputPercent()));
        driveBaseLeftSide.setRearMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getLeftRearMotor().getMotorOutputPercent()));

        driveBaseRightSide.setFrontMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getRightFrontMotor().getMotorOutputPercent()));
        driveBaseRightSide.setRearMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getRightRearMotor().getMotorOutputPercent()));

        camera.update();

        stage.act();
        stage.draw();
        debugRenderer.render(world, stage.getCamera().combined);
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


}