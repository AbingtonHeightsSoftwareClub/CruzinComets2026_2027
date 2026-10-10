package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMemberMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests verifying AprilTag cluster detection lookups in {@link Camera#get(String)}.
 * Tests coverage for:
 * 1. Long-name lookup
 * 2. Short-name lookup
 * 3. Backward compatibility with existing cluster configurations
 * 4. Unmatched / null name queries
 * 5. Other detection types (e.g., single AprilTags)
 * 6. Matching precedence
 * 7. Resilience to null metadata
 */
public class CameraTest {

    private static AprilTagClusterMetadata createClusterMeta(String longName, String shortName) {
        ArrayList<AprilTagClusterMemberMetadata> members = new ArrayList<>();
        members.add(new AprilTagClusterMemberMetadata(1, new VectorF(0, 0, 0), 2.0));
        members.add(new AprilTagClusterMemberMetadata(2, new VectorF(1, 0, 0), 2.0));
        return new AprilTagClusterMetadata(members, longName, shortName, null, null, null);
    }

    private static AprilTagClusterDetection createClusterDetection(String longName, String shortName) {
        AprilTagClusterMetadata meta = createClusterMeta(longName, shortName);
        return new AprilTagClusterDetection(100, meta, null, null, null, null, 0L);
    }

    private static void setDetections(List<AprilTagDetection> detections) throws Exception {
        AprilTagProcessor mockProcessor = new AprilTagProcessor() {
            @Override
            public void setDecimation(float decimation) {}
            @Override
            public void setPoseSolver(PoseSolver poseSolver) {}
            @Override
            public int getPerTagAvgPoseSolveTime() { return 0; }
            @Override
            public ArrayList<AprilTagDetection> getDetections() {
                return new ArrayList<>(detections);
            }
            @Override
            public ArrayList<AprilTagDetection> getFreshDetections() {
                return new ArrayList<>(detections);
            }
            @Override
            public void init(int width, int height, org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration calibration) {}
            @Override
            public Object processFrame(org.opencv.core.Mat frame, long captureTimeNanos) { return null; }
            @Override
            public void onDrawFrame(android.graphics.Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {}
        };

        Field field = Camera.class.getDeclaredField("aprilTag");
        field.setAccessible(true);
        field.set(null, mockProcessor);
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("Assertion failed: " + message + " [expected: " + expected + ", actual: " + actual + "]");
    }

    // TEST 1: LONG-NAME LOOKUP
    public static void testLongNameLookup() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagClusterDetection cluster = createClusterDetection("Audience Hive Cluster", "Hive");
        detections.add(cluster);
        setDetections(detections);

        Optional<AprilTagClusterDetection> result = Camera.get("Audience Hive Cluster");
        assertTrue("Cluster should be found by long name", result.isPresent());
        assertEquals("Returned cluster should match long name", "Audience Hive Cluster", result.get().metadata.name);
        System.out.println("TEST 1 PASSED: Long-name lookup succeeds");
    }

    // TEST 2: SHORT-NAME LOOKUP
    public static void testShortNameLookup() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagClusterDetection cluster = createClusterDetection("Audience Hive Cluster", "Hive");
        detections.add(cluster);
        setDetections(detections);

        Optional<AprilTagClusterDetection> result = Camera.get("Hive");
        assertTrue("Cluster should be found by short name", result.isPresent());
        assertEquals("Returned cluster short name should match", "Hive", result.get().metadata.shortName);
        assertEquals("Returned cluster long name should match", "Audience Hive Cluster", result.get().metadata.name);
        System.out.println("TEST 2 PASSED: Short-name lookup succeeds");
    }

    // TEST 3: BACKWARD COMPATIBILITY
    public static void testBackwardCompatibility() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagClusterDetection cluster1 = createClusterDetection("RED AUDIENCE", "RED AUDIENCE");
        AprilTagClusterDetection cluster2 = createClusterDetection("Center Goal", "GOAL");
        detections.add(cluster1);
        detections.add(cluster2);
        setDetections(detections);

        Optional<AprilTagClusterDetection> result1 = Camera.get("RED AUDIENCE");
        assertTrue("Existing cluster with identical long/short name found", result1.isPresent());
        assertEquals("Matched RED AUDIENCE", "RED AUDIENCE", result1.get().metadata.name);

        Optional<AprilTagClusterDetection> result2 = Camera.get("Center Goal");
        assertTrue("Existing cluster found by long name", result2.isPresent());
        assertEquals("Matched Center Goal", "Center Goal", result2.get().metadata.name);
        System.out.println("TEST 3 PASSED: Backward compatibility verified");
    }

    // TEST 4: UNMATCHED NAME
    public static void testUnmatchedName() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        detections.add(createClusterDetection("Audience Hive Cluster", "Hive"));
        setDetections(detections);

        Optional<AprilTagClusterDetection> notFound = Camera.get("NonexistentCluster");
        assertTrue("Unmatched name should return empty Optional", !notFound.isPresent());

        Optional<AprilTagClusterDetection> nullQuery = Camera.get(null);
        assertTrue("Null name query should return empty Optional", !nullQuery.isPresent());

        setDetections(new ArrayList<>());
        Optional<AprilTagClusterDetection> emptyDetections = Camera.get("Hive");
        assertTrue("Empty detections list should return empty Optional", !emptyDetections.isPresent());
        System.out.println("TEST 4 PASSED: Unmatched name returns empty Optional");
    }

    // TEST 5: OTHER DETECTION TYPES
    public static void testOtherDetectionTypes() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagMetadata singleMeta = new AprilTagMetadata(583, "Robbie", 2.0, null, null, null);
        AprilTagSingleDetection single = new AprilTagSingleDetection(583, 0, 0.0f, null, null, singleMeta, null, null, null, 0L, null);
        detections.add(single);

        AprilTagClusterDetection cluster = createClusterDetection("Audience Hive Cluster", "Hive");
        detections.add(cluster);
        setDetections(detections);

        Optional<AprilTagClusterDetection> singleResult = Camera.get("Robbie");
        assertTrue("Single detections should not match Camera.get", !singleResult.isPresent());

        Optional<AprilTagClusterDetection> clusterResult = Camera.get("Hive");
        assertTrue("Cluster in mixed list should still be found", clusterResult.isPresent());
        assertEquals("Cluster in mixed list matches", "Audience Hive Cluster", clusterResult.get().metadata.name);
        System.out.println("TEST 5 PASSED: Other detection types handled correctly");
    }

    // TEST 6: MATCHING PRECEDENCE
    public static void testMatchingPrecedence() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagClusterDetection first = createClusterDetection("Cluster 1 Long", "Target");
        AprilTagClusterDetection second = createClusterDetection("Target", "Cluster 2 Short");
        detections.add(first);
        detections.add(second);
        setDetections(detections);

        // 'Target' matches first detection's shortName and second detection's longName.
        // Sequential scan must return the first matching detection encountered.
        Optional<AprilTagClusterDetection> result = Camera.get("Target");
        assertTrue("Result should be present", result.isPresent());
        assertEquals("First encountered matching detection should be returned", "Cluster 1 Long", result.get().metadata.name);
        System.out.println("TEST 6 PASSED: Matching precedence preserved");
    }

    // TEST 7: RESILIENCE TO NULL METADATA
    public static void testNullMetadataResilience() throws Exception {
        List<AprilTagDetection> detections = new ArrayList<>();
        AprilTagClusterDetection nullMetaDet = new AprilTagClusterDetection(100, null, null, null, null, null, 0L);
        detections.add(nullMetaDet);
        AprilTagClusterDetection validCluster = createClusterDetection("Audience Hive Cluster", "Hive");
        detections.add(validCluster);
        setDetections(detections);

        Optional<AprilTagClusterDetection> result = Camera.get("Hive");
        assertTrue("Should not crash on null metadata and find valid cluster", result.isPresent());
        assertEquals("Found valid cluster", "Audience Hive Cluster", result.get().metadata.name);
        System.out.println("TEST 7 PASSED: Null metadata resilience verified");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running CameraTest suite...");
        testLongNameLookup();
        testShortNameLookup();
        testBackwardCompatibility();
        testUnmatchedName();
        testOtherDetectionTypes();
        testMatchingPrecedence();
        testNullMetadataResilience();
        System.out.println("ALL 7 TESTS PASSED SUCCESSFULLY!");
    }
}
