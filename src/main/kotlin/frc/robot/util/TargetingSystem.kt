package frc.robot.util;

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.units.Velocity
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import frc.robot.RobotContainer
import frc.robot.constants.CannonConstants
import frc.robot.constants.FieldConstants
import frc.robot.constants.TargetingConstants
import frc.robot.constants.TrunkConstants
import kotlin.math.*

data class ShotSetup(var robotAngle: Double, var shooterAngle: Double)
private data class TargetingVariables(val shooterVelocity: Double) {
    val x: Double
    val y: Double
    val vx: Double
    val vy: Double
    val r: Double
    init {
        val robotPose = RobotContainer.swerveSystem.getSwervePose()
        val robotVelocity = RobotContainer.swerveSystem.driveTrain.currentRobotChassisSpeeds
//        val robotAngle = robotPose.rotation.angle


//        val shootingOffset = TrunkConstants.SHOOTING_OFFSET
//        val xOffset =
//        + cos(robotAngle) * shootingOffset
//        val yOffset = FieldConstants.Speaker.centerSpeakerOpening.y
//        + sin(robotAngle) * shootingOffset

        x = robotPose.x - TargetingConstants.speakerX - TargetingConstants.endpointX
        y = robotPose.y - TargetingConstants.speakerY - TargetingConstants.endpointY
        vx = robotVelocity.vxMetersPerSecond
        vy = robotVelocity.vyMetersPerSecond
        r = sqrt(x * x + y * y)
    }
}

class TargetingSystem {
//    private val g = 9.81
    // overaccount for gravity
    private val g = 11.0
    private val rad2deg = 180.0 / PI

    val shootingVelocity = CannonConstants.LEFT_SHOOTER_SHOOT_VELOCITY / 60 * PI * 3

//    init {
//        if (CannonConstants.LEFT_SHOOTER_SHOOT_VELOCITY != CannonConstants.RIGHT_SHOOTER_SHOOT_VELOCITY) {
//            println("Shooting with spin, shooter velocity needs to be recalculated")
//        }
//    }

// dumb scaling dont think about it
    private val shootingVelocityScaling = 1.3

    // if prep then use ideal velocity else use actual velocity
    fun calculateShot(actualVelocity: Boolean): ShotSetup {
        val shooterVars = TargetingVariables(shootingVelocity)

        val targetRobotAngle = robotAngleFunction(shooterVars)
        val targetShooterAngle = shooterAngleFunction(shooterVars)

        return ShotSetup(targetRobotAngle, targetShooterAngle)
    }

    fun calculateShootingAngle(actualVelocity: Boolean) = shooterAngleFunction(TargetingVariables(shootingVelocity))

    fun calculateRobotAngle() = robotAngleFunction(TargetingVariables(shootingVelocity))

    private fun robotAngleFunction(vars: TargetingVariables): Double {
        return acos(
                vars.x * ((vars.x * vars.x - vars.y * vars.y) * vars.vx + 2 * vars.x * vars.y * vars.vy) /
                        (vars.r.pow(1.5) * sqrt(vars.vx * vars.vx + vars.vy * vars.vy))
        ) * rad2deg
    }

    private fun shooterAngleFunction(vars: TargetingVariables): Double {
        val inverseR = 1.0 / vars.r
        val rDot = inverseR * (vars.x * vars.vx + vars.y * vars.vy)
        val h = FieldConstants.Speaker.centerSpeakerOpening.z - TrunkConstants.SHOOTING_HEIGHT

        return atan(
            h * inverseR + g * vars.r /
                    ((shootingVelocityScaling + .15 * rDot) * shootingVelocity * vars.r / (sqrt(vars.r.pow(2) + h.pow(2))) + rDot).pow(
                        2
                    )
        ) * rad2deg +
                (40.0 * rDot) / (shootingVelocityScaling * shootingVelocity)
    }

    fun getShotNoVelocity(actualVelocity: Boolean): ShotSetup {
        val vars = TargetingVariables(shootingVelocity * TargetingConstants.velocityMultiplier)
        SmartDashboard.putNumber("robot speaker rel pos x", vars.x)
        SmartDashboard.putNumber("robot speaker rel pos y", vars.y)
        SmartDashboard.putNumber("robot distance to speaker", vars.r)

        val z = TargetingConstants.endpointZ - TargetingConstants.shooterZ

        val targetRobotAngle = acos(vars.x / vars.r) * rad2deg
//        val targetShooterAngle = atan2(g * (vars.rSquared + h * h) + 2 * h * shootingVelocity, vars.r) * rad2deg
        val targetShooterAngle = atan((z + .5 * g * (vars.r.pow(2) + z.pow(2)) / vars.shooterVelocity.pow(2)) / vars.r) * rad2deg

        return ShotSetup(targetRobotAngle, targetShooterAngle)
    }
}
