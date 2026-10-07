package org.firstinspires.ftc.teamcode.mechanisms;


import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {


    // Each follower variable must be turned into the class, because those classes are implemented in the type needed
    // We need to create the object with its specific configuration
    //    com.pedropathing.localization.Localizer localizer,
    //    com.pedropathing.drivetrain.Drivetrain drivetrain,
    //    com.pedropathing.algorithm.Algorithm algorithm
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("front_left_drive");
        c.frontRightName.set("front_right_drive");
        c.backLeftName.set("back_left_drive");
        c.backRightName.set("back_right_drive");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("imu");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set((11.5-20.75)/2.54);
        c.yPodOffset.set(-(1/2.54)*(3.6+17));
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3302030112308159);
                Controller secondaryTranslationalForward = Controller.proportional(0.12200118946282745);
                Controller primaryTranslationalLateral = Controller.proportional(0.7209043218795887);
                Controller secondaryTranslationalLateral = Controller.proportional(0.2663548840162572);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018249561737091018));
                c.brake.set(Controller.proportionalFeedforward(0.015512127476527365));

                c.headingFeedback.set(Controller.proportional(5.855764427427831));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04510637144666724, 0.006039055359977932));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06144136210835548, 0.060871558127271484));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0018968993054121303, 0.0015499265959235697));

                c.maxAchievableForwardVelocity.set(59.21429278376949);
                c.maxAchievableStrafeVelocity.set(41.28441934233979);
                c.naturalForwardDeceleration.set(31.805458286920544);
                c.naturalStrafeDeceleration.set(67.00815378239326);
            }
    );


}
