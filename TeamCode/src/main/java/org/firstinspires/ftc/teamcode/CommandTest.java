package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


/*


Generic commands
Move forward x inches

Create sequential order of all commands

Schedule overall command

Execute command in loop



 */

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.robotcontroller.external.samples.ConceptNullOp;
import org.firstinspires.ftc.teamcode.mechanisms.Constants;

@Autonomous
public class CommandTest extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Poses
    private final Pose startPose = poseFactory.of(24, 24, 0);
    private final Pose scorePose = poseFactory.of(48, 48, 90);
    private final Pose parkPose = poseFactory.of(72, 48, 90);

    private DcMotorEx intakeMotor1;

    // Path methods
    private Path startToScore() {
        return line(startPose, scorePose).linear(startPose, scorePose);
    }

    private Path park() {
        return line(scorePose, parkPose).linear(scorePose, parkPose);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, startToScore()),
                // Add mechanism commands here.
                follow(follower, park())
        );
    }

    /*
        intakeMotor1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor1.setTargetPosition(100);
        intakeMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeMotor1.setVelocity(100);
     */

    private final Command intakeMotorSet = Command.build()
            .setStart(() -> {
                intakeMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                intakeMotor1.setTargetPosition(20000);
                intakeMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }).setExecute(() ->{
                intakeMotor1.setVelocity(2800);
            }).setDone(() ->
                !intakeMotor1.isBusy()
            ).setEnd(endCondition -> {
                intakeMotor1.setPower(0);
            });



    @Override
    public void init() {
        Scheduler.reset();
        intakeMotor1 = hardwareMap.get(DcMotorEx.class, "intake1");
        intakeMotor1.setDirection(DcMotorSimple.Direction.REVERSE);




        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();
    }

    @Override
    public void start() {
        schedule(intakeMotorSet);
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}
