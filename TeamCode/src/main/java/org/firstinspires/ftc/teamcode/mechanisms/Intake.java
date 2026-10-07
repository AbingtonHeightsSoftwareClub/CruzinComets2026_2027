package org.firstinspires.ftc.teamcode.mechanisms;


import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Intake {

    static boolean motorRunning = false;
    private static DcMotorEx intake_motor;
    private static Gamepad gamepad1;
    private static Telemetry telemetry;

    public static void init(HardwareMap hwMap, Gamepad gamepad1, Telemetry telemetry, String deviceName) {
        intake_motor = hwMap.get(DcMotorEx.class, deviceName);
        intake_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        telemetry=telemetry;
        gamepad1 = gamepad1;

    }

    public static void on() {
        intake_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (gamepad1.rightBumperWasPressed()) {
            motorRunning = !motorRunning;
            if (motorRunning){
                intake_motor.setVelocity(500);
            }
            else{
                intake_motor.setVelocity(0);
            }
        }
    }

    public Command goTo(int target_position, int velocity) {

        return Command.build()
                .setStart(() -> {
                    intake_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    intake_motor.setTargetPosition(target_position);
                    intake_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                }).setExecute(() -> {
                    intake_motor.setVelocity(velocity);
                }).setDone(() ->
                        !intake_motor.isBusy()
                ).setEnd(endCondition -> {
                    intake_motor.setPower(0);
                });

    }

}