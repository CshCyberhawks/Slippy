package frc.robot.constants

import com.ctre.phoenix6.configs.Slot0Configs
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.ClosedLoopOutputType
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants.SteerFeedbackType
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstantsFactory
import edu.wpi.first.math.util.Units
import frc.robot.subsystems.swerve.CommandSwerveDrivetrain

// Generated by the Tuner X Swerve Project Generator
// https://v6.docs.ctr-electronics.com/en/stable/docs/tuner/tuner-swerve/index.html
object TunerConstants {
    // Both sets of gains need to be tuned to your individual robot.
    // The steer motor uses any SwerveModule.SteerRequestType control request with the
    // output type specified by SwerveModuleConstants.SteerMotorClosedLoopOutput
    private val steerGains: Slot0Configs = Slot0Configs()
        .withKP(100.0).withKI(0.0).withKD(0.2)
        .withKS(0.0).withKV(1.5).withKA(0.0)

    // When using closed-loop control, the drive motor uses the control
    // output type specified by SwerveModuleConstants.DriveMotorClosedLoopOutput
    private val driveGains: Slot0Configs = Slot0Configs()
        .withKP(3.0).withKI(0.0).withKD(0.0)
        .withKS(0.0).withKV(0.0).withKA(0.0)

    // The closed-loop output type to use for the steer motors;
    // This affects the PID/FF gains for the steer motors
    private val steerClosedLoopOutput = ClosedLoopOutputType.Voltage

    // The closed-loop output type to use for the drive motors;
    // This affects the PID/FF gains for the drive motors
    private val driveClosedLoopOutput = ClosedLoopOutputType.Voltage

    // The stator current at which the wheels start to slip;
    // This needs to be tuned to your individual robot
    private const val kSlipCurrentA = 300.0

    // Theoretical free speed (m/s) at 12v applied output;
    // This needs to be tuned to your individual robot
    const val kSpeedAt12VoltsMps: Double = 5.21

    // Every 1 rotation of the azimuth results in kCoupleRatio drive motor turns;
    // This may need to be tuned to your individual robot
    private const val kCoupleRatio = 3.5714285714285716

    private const val kDriveGearRatio = 6.122448979591837
    private const val kSteerGearRatio = 21.428571428571427
    private const val kWheelRadiusInches = 2.0

    private const val kSteerMotorReversed = true
    private const val kInvertLeftSide = false
    private const val kInvertRightSide = true

    private const val kCANbusName = ""
    private const val kPigeonId = 30


    // These are only used for simulation
    private const val kSteerInertia = 0.00001
    private const val kDriveInertia = 0.001

    // Simulated voltage necessary to overcome friction
    private const val kSteerFrictionVoltage = 0.25
    private const val kDriveFrictionVoltage = 0.25

    private val DrivetrainConstants: SwerveDrivetrainConstants = SwerveDrivetrainConstants()
        .withPigeon2Id(kPigeonId)
        .withCANbusName(kCANbusName)

    private val ConstantCreator: SwerveModuleConstantsFactory = SwerveModuleConstantsFactory()
        .withDriveMotorGearRatio(kDriveGearRatio)
        .withSteerMotorGearRatio(kSteerGearRatio)
        .withWheelRadius(kWheelRadiusInches)
        .withSlipCurrent(kSlipCurrentA)
        .withSteerMotorGains(steerGains)
        .withDriveMotorGains(driveGains)
        .withSteerMotorClosedLoopOutput(steerClosedLoopOutput)
        .withDriveMotorClosedLoopOutput(driveClosedLoopOutput)
        .withSpeedAt12VoltsMps(kSpeedAt12VoltsMps)
        .withSteerInertia(kSteerInertia)
        .withDriveInertia(kDriveInertia)
        .withSteerFrictionVoltage(kSteerFrictionVoltage)
        .withDriveFrictionVoltage(kDriveFrictionVoltage)
        .withFeedbackSource(SteerFeedbackType.FusedCANcoder)
        .withCouplingGearRatio(kCoupleRatio)
        .withSteerMotorInverted(kSteerMotorReversed)


    // Front Left
    private const val kFrontLeftDriveMotorId = 7
    private const val kFrontLeftSteerMotorId = 9
    private const val kFrontLeftEncoderId = 1
    private const val kFrontLeftEncoderOffset = -0.0703125

    private const val kFrontLeftXPosInches = 12.25
    private const val kFrontLeftYPosInches = 12.25

    // Front Right
    private const val kFrontRightDriveMotorId = 6
    private const val kFrontRightSteerMotorId = 10
    private const val kFrontRightEncoderId = 2
    private const val kFrontRightEncoderOffset = -0.55029296875

    private const val kFrontRightXPosInches = 12.25
    private const val kFrontRightYPosInches = -12.25

    // Back Left
    private const val kBackLeftDriveMotorId = 5
    private const val kBackLeftSteerMotorId = 11
    private const val kBackLeftEncoderId = 3
    private const val kBackLeftEncoderOffset = -0.13818359375

    private const val kBackLeftXPosInches = -12.25
    private const val kBackLeftYPosInches = 12.25

    // Back Right
    private const val kBackRightDriveMotorId = 8
    private const val kBackRightSteerMotorId = 12
    private const val kBackRightEncoderId = 4
    private const val kBackRightEncoderOffset = -0.310791015625

    private const val kBackRightXPosInches = -12.25
    private const val kBackRightYPosInches = -12.25


    private val FrontLeft: SwerveModuleConstants = ConstantCreator.createModuleConstants(
        kFrontLeftSteerMotorId,
        kFrontLeftDriveMotorId,
        kFrontLeftEncoderId,
        kFrontLeftEncoderOffset,
        Units.inchesToMeters(
            kFrontLeftXPosInches
        ),
        Units.inchesToMeters(kFrontLeftYPosInches),
        kInvertLeftSide
    )
    private val FrontRight: SwerveModuleConstants = ConstantCreator.createModuleConstants(
        kFrontRightSteerMotorId,
        kFrontRightDriveMotorId,
        kFrontRightEncoderId,
        kFrontRightEncoderOffset,
        Units.inchesToMeters(
            kFrontRightXPosInches
        ),
        Units.inchesToMeters(kFrontRightYPosInches),
        kInvertRightSide
    )
    private val BackLeft: SwerveModuleConstants = ConstantCreator.createModuleConstants(
        kBackLeftSteerMotorId, kBackLeftDriveMotorId, kBackLeftEncoderId, kBackLeftEncoderOffset, Units.inchesToMeters(
            kBackLeftXPosInches
        ), Units.inchesToMeters(kBackLeftYPosInches), kInvertLeftSide
    )
    private val BackRight: SwerveModuleConstants = ConstantCreator.createModuleConstants(
        kBackRightSteerMotorId,
        kBackRightDriveMotorId,
        kBackRightEncoderId,
        kBackRightEncoderOffset,
        Units.inchesToMeters(
            kBackRightXPosInches
        ),
        Units.inchesToMeters(kBackRightYPosInches),
        kInvertRightSide
    )

    val DriveTrain: CommandSwerveDrivetrain = CommandSwerveDrivetrain(
        DrivetrainConstants, FrontLeft,
        FrontRight, BackLeft, BackRight
    )
}
