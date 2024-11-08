package frc.robot.commands.trunk

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.RobotContainer
import frc.robot.TrunkPose
import frc.robot.constants.TrunkConstants

class GoToClimbPoseTrunk(val desiredPose: TrunkPose) : Command() {
    var currentTargetAngle: Double = TrunkConstants.SAFE_TRAVEL_ANGLE
    var currentTargetPosition: Double = TrunkConstants.LEGAL_PIVOT_POSITION_TARGET

    val isAngleSafe: Boolean
        get() = RobotContainer.trunkSystem.getThroughboreRotation() >= TrunkConstants.SAFE_TRAVEL_ANGLE

    val isPivotPositionLegal: Boolean
        get() = RobotContainer.trunkSystem.getPosition() >= TrunkConstants.LEGAL_PIVOT_POSITION

    val isPositionAlwaysSafe: Boolean
        get() = RobotContainer.trunkSystem.getPosition() >= TrunkConstants.SAFE_PIVOT_POSITION && desiredPose.position >= TrunkConstants.SAFE_PIVOT_POSITION

    override fun initialize() {
        RobotContainer.trunkSystem.isAtPose = false
        RobotContainer.trunkSystem.setDesiredRotation(currentTargetAngle)
        RobotContainer.trunkSystem.setDesiredPosition(currentTargetPosition)

        RobotContainer.trunkSystem.io.rotationBrake = isPivotPositionLegal
    }

    override fun execute() {
        SmartDashboard.putNumber("Current Target Angle", currentTargetAngle)

        if (isPivotPositionLegal) {
            RobotContainer.trunkSystem.io.rotationBrake = true
            currentTargetPosition = RobotContainer.trunkSystem.getPosition()
            RobotContainer.trunkSystem.setDesiredPosition(currentTargetPosition)
            RobotContainer.trunkSystem.setDesiredRotation(currentTargetAngle)

            val rotationVolts = RobotContainer.trunkSystem.calculateRotationOut(currentTargetAngle, true)
            RobotContainer.trunkSystem.io.setRotationVoltage(rotationVolts)
        }

        if (isAngleSafe || isPositionAlwaysSafe) {
            currentTargetAngle = desiredPose.angle
            currentTargetPosition = desiredPose.position
            RobotContainer.trunkSystem.setDesiredRotation(currentTargetAngle)
            RobotContainer.trunkSystem.setDesiredPosition(currentTargetPosition)
        }

        val elevatorPercent = RobotContainer.trunkSystem.calculatePositionOut(currentTargetPosition)
        RobotContainer.trunkSystem.io.setElevatorSpeed(elevatorPercent)
    }

    override fun isFinished(): Boolean {
        return RobotContainer.trunkSystem.checkAtClimbPose(
            RobotContainer.trunkSystem.trunkDesiredRotation,
            currentTargetPosition
        )

    }

    override fun end(interrupted: Boolean) {
        if (!interrupted) {
            RobotContainer.trunkSystem.isAtPose = true
        }
    }
}
