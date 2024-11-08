package frc.robot.util

import MiscCalculations
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import frc.robot.RobotContainer
import frc.robot.constants.CannonConstants
import frc.robot.constants.FieldConstants
import frc.robot.constants.TargetingConstants
import frc.robot.constants.TrunkConstants
import java.lang.Math
import kotlin.math.*

data class ShotSetup(val robotAngle: Double, var shooterAngle: Double) {
    init {
        shooterAngle = -shooterAngle + 90
    }
}

class TargetingVariables(
    robotPose: Pose2d = AllianceFlip.apply(RobotContainer.swerveSystem.getSwervePose()),
    robotVelocity: ChassisSpeeds = RobotContainer.swerveSystem.currentRobotChassisSpeeds
) {
    private val flippedRobotPose = AllianceFlip.apply(robotPose)

    private val red =
        DriverStation.getAlliance().isPresent && DriverStation.getAlliance().get() == DriverStation.Alliance.Red

    val vx: Double = robotVelocity.vxMetersPerSecond * if (red) -1 else 1
    val vy: Double = robotVelocity.vyMetersPerSecond

    val x: Double =
        (TargetingConstants.speakerX + TargetingConstants.endpointX - flippedRobotPose.x - TargetingConstants.leadTime * vx) * if (red) -1 else 1
    val y: Double =
        (TargetingConstants.speakerY + TargetingConstants.endpointY - flippedRobotPose.y - TargetingConstants.leadTime * vy)
    val z = TargetingConstants.endpointZ - TargetingConstants.shooterZ
    // + .02 * r.pow(1.5)

    val r: Double = sqrt(x * x + y * y)

    // estimate based on things
    val t = .05882 * r + .06886
}

class TargetingSystem {
    //    private val g = 9.81
    // overaccount for gravity
    private var g = 10.0

    private val real_g = 9.8066

    private val rad2deg = 180.0 / PI

    private val shootingVelocity =
        TargetingConstants.velocityMultiplier * CannonConstants.SHOOTER_SHOOT_VELOCITY * MiscCalculations.rpm2ups(
            Units.inchesToMeters(1.5)
        )

    fun getVelocityShot(
        robotPose: Pose2d = RobotContainer.swerveSystem.getSwervePose(),
        robotVelocity: ChassisSpeeds = RobotContainer.swerveSystem.currentRobotChassisSpeeds
    ): ShotSetup {
        val vars = TargetingVariables(robotPose, robotVelocity)
        Telemetry.putNumber("robot speaker rel pos x", vars.x, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("robot speaker rel pos y", vars.y, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("robot distance to speaker", vars.r, RobotContainer.telemetry.trunkTelemetry)
        return ShotSetup(velocityRobotAngleFunction(vars), velocityShooterAngleFunction(vars))
    }

    fun velocityShooterAngle() = velocityShooterAngleFunction(TargetingVariables())

    fun velocityRobotAngle(vars: TargetingVariables) = velocityRobotAngleFunction(vars)

    private fun velocityRobotAngleFunction(vars: TargetingVariables) =
        atan2(vars.y - vars.vy * vars.t, vars.x - vars.vx * vars.t) * rad2deg

    private fun velocityShooterAngleFunction(vars: TargetingVariables): Double {
        g = SmartDashboard.getNumber("g", 11.0)
        val rDot = (vars.x * vars.vx + vars.y * vars.vy) / vars.r
        Telemetry.putNumber("robot r Dot", rDot, RobotContainer.telemetry.trunkTelemetry)
        val k1 = 1.0 / sqrt(vars.r.pow(2) + vars.z.pow(2))
        val k2 = 2.0 * (shootingVelocity * vars.r * k1 + rDot).pow(2)
        return atan(
            1.0 / (
                    vars.r * k2 /
                            (vars.z * k2 + g * vars.r.pow(2)) -
                            rDot /
                            ((1.1 + .03 * vars.r) * shootingVelocity * vars.z * k1)
                    )
        ) * rad2deg
    }

    fun getShotNoVelocity(
        robotPose: Pose2d = RobotContainer.swerveSystem.getSwervePose(),
        robotVelocity: ChassisSpeeds = RobotContainer.swerveSystem.currentRobotChassisSpeeds
    ): ShotSetup {
        val vars = TargetingVariables(robotPose, robotVelocity)
        return ShotSetup(noVelocityRobotAngle(vars), noVelocityShooterAngle(vars))
    }

    fun noVelocityRobotAngle(vars: TargetingVariables) = atan2(vars.y, vars.x) * rad2deg

    fun noVelocityShooterAngle(vars: TargetingVariables) = Math.toDegrees(
        atan2(
            shootingVelocity.pow(2) - sqrt(
                shootingVelocity.pow(4) - 2 * real_g * vars.z * shootingVelocity.pow(2) - real_g.pow(
                    2
                ) * vars.r.pow(2)
            ), real_g * vars.r
        )
    )

    fun getMortarShot(
        robotPose: Pose2d = RobotContainer.swerveSystem.getSwervePose(),
        robotVelocity: ChassisSpeeds = RobotContainer.swerveSystem.currentRobotChassisSpeeds
    ): ShotSetup {
        val vars = TargetingVariables(robotPose, robotVelocity)
        val shooterAngle = Math.toDegrees(
            atan2(
                shootingVelocity.pow(2) + sqrt(
                    shootingVelocity.pow(4) - 2 * real_g * vars.z * shootingVelocity.pow(2) - real_g.pow(
                        2
                    ) * vars.r.pow(2)
                ), real_g * vars.r
            )
        )
        return ShotSetup(velocityRobotAngle(vars), shooterAngle)
    }

    fun getShotVelocityRobotNoVelocityShooter(
        robotPose: Pose2d = RobotContainer.swerveSystem.getSwervePose(),
        robotVelocity: ChassisSpeeds = RobotContainer.swerveSystem.currentRobotChassisSpeeds,
    ): ShotSetup {
        val vars = TargetingVariables(robotPose, robotVelocity)

        SmartDashboard.putNumber("Robot Speaker X:", vars.x)
        SmartDashboard.putNumber("Robot Speaker Y:", vars.y)
        SmartDashboard.putNumber("Robot Speaker Z:", vars.z)

        return ShotSetup(noVelocityRobotAngle(vars), noVelocityShooterAngle(vars))
    }

    fun test(robotPose: Pose2d, robotVelocity: ChassisSpeeds) {
        val vars = TargetingVariables(robotPose, robotVelocity)
        val noVelShot = getShotNoVelocity(robotPose, robotVelocity)
        val velShot = getVelocityShot(robotPose, robotVelocity)
        println("vars:")
        println("x=" + vars.x + " y=" + vars.y + " r=" + vars.r + " vx=" + vars.vx + " vy=" + vars.vy + " r=" + vars.r + " z=" + vars.z + " t=" + vars.t)
        println("no velocity:")
        println(noVelShot)
        println("velocity:")
        println(velShot)
    }
}
