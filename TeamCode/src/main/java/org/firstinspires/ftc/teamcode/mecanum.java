package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;

@TeleOp(name = "Example TeleOp")
public class mecanum extends OpMode {

    private Follower follower;

    boolean motorRunning = false;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        Intake.init(hardwareMap, gamepad1, telemetry, "intake");
    }

    @Override
    public void loop() {
        follower.manual(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x
        );
        Intake.on();
        follower.update();
    }
}