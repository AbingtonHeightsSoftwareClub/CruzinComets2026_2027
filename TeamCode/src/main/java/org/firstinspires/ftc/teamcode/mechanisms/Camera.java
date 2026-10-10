package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;



/*
This mechanism will be used for detecting April Tag clusters

April Tag clusters are several April Tags next to each other in a specific location.
Look at the Hive for an example.

There are no single April Tags this year, but incomplete detections may lead to single detections.

Detecting a cluster allows for accurate localization.


 */


public class Camera {

    private static AprilTagProcessor aprilTag;
    private static VisionPortal visionPortal;

    private static boolean useTelemetry;
    private static Telemetry telemetry;

    public static void init(String cameraName, HardwareMap hardwareMap, Telemetry telemetry, boolean useTelemetry) {

        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, cameraName)
        );
        useTelemetry = useTelemetry;
        telemetry = telemetry;


    }

    // We don't always know if we will detect AprilTags
    // So, we use Optional
    // It allows use to return either the list or nothing
    public static Optional<List<String>> detectedTags() {


        // We will put the integer numbers of the detected tags here
        // We don't need all of the information for every tag
        List<String> detected_tags = new ArrayList<String>();

        // Get a list of detected tags
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

        // If there is no April Tag detected, we return "nothing"
        if (currentDetections.isEmpty()) {
            return Optional.empty();
        }

        for (AprilTagDetection detection : currentDetections) {

            // This checks whether the detection is a single April tag
            if (detection instanceof AprilTagSingleDetection) {

                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                if (singleDet.metadata != null) { // If the metadata is null, the tag is not for BioBuzz
                    detected_tags.add(String.valueOf(singleDet.id));
                }
            } else {
                // Cluster tags are two clusters side by side
                // Look under the hive for an example
                // By detecting two known tags at once, the program can calculate
                // the exact position of the robot on the field
                AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                detected_tags.add(clusterDet.metadata.name);

                // Adding the cluster name to
                if (useTelemetry) {
                    // Tag clusters have specific names, not id's.
                    telemetry.addData("====== Tag Cluster", clusterDet.metadata.name);
                    // Percent tags found is to determine if anything is blocking the tags
                    telemetry.addData("Percent tags found", clusterDet.percentClusterFound);
                    telemetry.addData("Range", detection.ftcPose.range);
                    telemetry.addData("Bearing", detection.ftcPose.bearing);
                    telemetry.addData("Elevation", detection.ftcPose.elevation);
                }

            }
        }


        return Optional.of(detected_tags);
    }

    public static Optional<AprilTagClusterDetection> get(String name) {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster_detection = (AprilTagClusterDetection) detection;
                // first_string.equals(other_string) checks if the values of both string match
                // String == other_string checks if they are both the same variable
                // So we must use .equals for String comparisons
                if (name.equals(cluster_detection.metadata.name)) {
                    return Optional.of(cluster_detection);
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isScorable() {

        // TODO Use this link to check the orientation of the hive https://ftc-docs.firstinspires.org/en/latest/tech_tips/tech-tips/tech-tip-apriltag-clusters/tech-tip-apriltag-clusters.html
        return false;
    }

}
