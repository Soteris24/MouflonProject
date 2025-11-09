package org.firstinspires.ftc.teamcode.mouflonproject;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@TeleOp(name = "mouflonproject")
public class mouflonproject extends LinearOpMode {
    private List<DcMotor> armMotors = new ArrayList<>();
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor armleft;
    private DcMotor armright;
    private DcMotor railmotor;
    private DcMotor chairmotor;
    private IMU imu;

    @Override
    public void runOpMode() {
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");

        armleft = hardwareMap.get(DcMotor.class, "armleft");
        armright = hardwareMap.get(DcMotor.class, "armright");
        armright.setDirection(DcMotorSimple.Direction.REVERSE);
        armMotors.addAll(Arrays.asList(armleft, armright));

        railmotor = hardwareMap.get(DcMotor.class, "railmotor");
        chairmotor = hardwareMap.get(DcMotor.class, "chairmotor");

        // ✅ IMU setup: Logo UP, USB FORWARD (side)
        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters imuParams = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        );
        imu.initialize(imuParams);

        resetARMencoders();
        resetRAILencoders();
        resetCHAIRencoders();

        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                drivetrain();
                arm();
                rail();
                chair();
            }
        }
    }

    private void resetARMencoders() {
        for (DcMotor motor : armMotors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setTargetPosition(0);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    private void resetRAILencoders() {
        railmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        railmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        railmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        railmotor.setTargetPosition(0);
        railmotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void resetCHAIRencoders() {
        chairmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        chairmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        chairmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        chairmotor.setTargetPosition(0);
        chairmotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void drivetrain() {
        backLeft.setPower(gamepad1.left_stick_y);
        backRight.setPower(gamepad1.right_stick_y);

        frontLeft.setPower(gamepad1.left_stick_y);
        frontRight.setPower(gamepad1.right_stick_y);
    }

    private void arm() {
        for (DcMotor motor : armMotors) {
            if (gamepad1.y) {
                motor.setTargetPosition(-1000);
                motor.setPower(1);
            } else if (gamepad1.a) {
                motor.setTargetPosition(0);
                motor.setPower(1);
            } else {
                motor.setPower(0);
            }
        }
    }

    private void rail() {
        if (gamepad1.dpad_up) {
            railmotor.setTargetPosition(-10000);
            railmotor.setPower(1);
        } else if (gamepad1.dpad_down) {
            railmotor.setTargetPosition(10000);
            railmotor.setPower(1);
        } else {
            railmotor.setPower(0);
        }
    }

    private void chair() {
        // ✅ Chair motor moves based on pitch angle
        YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
        double pitch = angles.getPitch(AngleUnit.DEGREES);

        // Tune this multiplier based on how much the motor should move per degree
        int targetPosition = (int)(-pitch * 10);

        chairmotor.setTargetPosition(targetPosition);
        chairmotor.setPower(0.5); // Adjust power if needed
    }
}
