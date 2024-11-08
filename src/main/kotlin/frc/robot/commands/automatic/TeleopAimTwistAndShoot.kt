package frc.robot.commands.automatic

import edu.wpi.first.math.MathUtil.clamp
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.DriveState
import frc.robot.NoteState
import frc.robot.RobotContainer
import frc.robot.ShooterState
import frc.robot.TrunkPose
import frc.robot.commands.cannon.AutoShootCommand
import frc.robot.commands.trunk.GoToPoseAndHoldTrunk
import frc.robot.commands.trunk.GoToPoseTrunk
import frc.robot.commands.trunk.HoldPositionGoToAngleTrunk
import frc.robot.constants.DriveConstants
import frc.robot.constants.TrunkConstants
import frc.robot.util.ControllerUtil
import frc.robot.util.ProfiledPID

class TeleopAimTwistAndShoot : Command() {
    var autoShoot: AutoShootCommand = AutoShootCommand()

    var trunkCommand: HoldPositionGoToAngleTrunk = HoldPositionGoToAngleTrunk(TrunkPose.SPEAKER)

    val twistPIDController = ProfiledPID(10.0, 0.0, 0.1, TrapezoidProfile.Constraints(100.0, 50.0))

    private var lastRobotAngle = 0.0

    override fun initialize() {
        trunkCommand = HoldPositionGoToAngleTrunk(TrunkPose.SPEAKER)
        autoShoot = AutoShootCommand()

        RobotContainer.stateMachine.driveState = DriveState.TranslationTeleop
        RobotContainer.stateMachine.shooterState = ShooterState.Shooting
        RobotContainer.stateMachine.currentTrunkCommand = GoToPoseTrunk(TrunkPose.SPEAKER).andThen(trunkCommand);
        RobotContainer.actuallyDoShoot = false

        twistPIDController.enableContinuousInput(0.0, 360.0);
        twistPIDController.goal = lastRobotAngle
    }

    override fun execute() {
        val shotSetup = RobotContainer.targetingSystem.getVelocityShot()

        //Handle the cannon aiming component
        val shooterAngle = clamp(shotSetup.shooterAngle, TrunkConstants.MIN_SHOOT_ANGLE, TrunkConstants.MAX_SHOOT_ANGLE)
//        SmartDashboard.putBoolean("shot is possible?", shooterAngle == shotSetup.shooterAngle)
//        RobotContainer.trunkSystem.setShootingAngle(shooterAngle)
        trunkCommand.desiredAngle = shooterAngle

        if (lastRobotAngle != shotSetup.robotAngle) {
            twistPIDController.goal = shotSetup.robotAngle
        }

        //Handle the twisting component
        val driveTwist = twistPIDController.calculate(
            RobotContainer.swerveSystem.getSwervePose().rotation.degrees,
        )

        val driveTranslation = ControllerUtil.calculateJoyTranslation(
            RobotContainer.rightJoystick.x, RobotContainer.rightJoystick.y,
            ControllerUtil.calculateJoyThrottle(RobotContainer.leftJoystick.throttle),
            DriveConstants.TELEOP_DEADZONE_X,
            DriveConstants.TELEOP_DEADZONE_Y
        )

        RobotContainer.swerveSystem.applyDriveRequest(
            driveTranslation.x,
            driveTranslation.y,
            Math.toRadians(driveTwist)
        ).execute()

        //Can we shoot?
//        if (RobotContainer.stateMachine.trunkReady && MiscCalculations.appxEqual(
//                        twistPIDController.setpoint,
//                        shotSetup.robotAngle,
//                        1.0
//                ) && !autoShoot.isScheduled
//        ) {
//            autoShoot.schedule()
//        }
        if (RobotContainer.actuallyDoShoot && !autoShoot.isScheduled) {
            autoShoot.schedule()
        }

        lastRobotAngle = shotSetup.robotAngle
    }

    override fun isFinished(): Boolean {
        return (autoShoot.isFinished) || RobotContainer.stateMachine.noteState == NoteState.Empty
    }

    override fun end(interrupted: Boolean) {
        RobotContainer.stateMachine.currentTrunkCommand = GoToPoseAndHoldTrunk(TrunkPose.STOW)
        RobotContainer.actuallyDoShoot = false
        RobotContainer.stateMachine.driveState = DriveState.Teleop
    }
}
