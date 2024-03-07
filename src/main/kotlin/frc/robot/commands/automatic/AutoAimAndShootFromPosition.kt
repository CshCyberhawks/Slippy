package frc.robot.commands.automatic


import edu.wpi.first.math.MathUtil.clamp
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.*
import frc.robot.commands.cannon.AutoShootCommand
import frc.robot.constants.TrunkConstants


class AutoAimAndShootFromPosition(val position: Pose2d) : Command() {
    private val autoShoot: AutoShootCommand = AutoShootCommand()

    override fun initialize() {
        RobotContainer.stateMachine.shooterState = ShooterState.Shooting

        RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.SPEAKER

//        if (RobotContainer.stateMachine.targetTrunkPose != TrunkPosition.SPEAKER && RobotContainer.stateMachine.targetTrunkPose != TrunkPosition.SPEAKER_FROM_STAGE) {
//            if (RobotContainer.stateMachine.currentRobotZone == GlobalZones.Stage) {
//                RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.SPEAKER_FROM_STAGE
//            } else {
//                RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.SPEAKER
//            }
//        }
    }

    override fun execute() {
        val shotSetup = RobotContainer.targetingSystem.getShotNoVelocityFromPosition(position)

        //Handle the cannon aiming component
        val shooterAngle = clamp(shotSetup.shooterAngle, TrunkConstants.MIN_SHOOT_ANGLE, TrunkConstants.MAX_SHOOT_ANGLE)
//        SmartDashboard.putBoolean("shot is possible?", shooterAngle == shotSetup.shooterAngle)
//        RobotContainer.trunkSystem.setShootingAngle(shooterAngle)
//        RobotContainer.trunkSystem.goToCustom()
        //Handle the cannon aiming component
        println("shooting angle " + shooterAngle)
        RobotContainer.trunkSystem.setShootingAngle(shooterAngle)

        //Can we shoot?
        if (RobotContainer.stateMachine.trunkReady) {
            autoShoot.schedule()
        }
//        if (RobotContainer.leftJoystick.button(2).asBoolean) {
//            autoShoot.schedule()
//            println("scheduling auto shoot")
//        }
    }

    override fun isFinished(): Boolean {
        return (autoShoot.isFinished) || RobotContainer.stateMachine.noteState == NoteState.Empty
    }

    override fun end(interrupted: Boolean) {
        println("Shootyboi Done")
        RobotContainer.stateMachine.shooterState = ShooterState.Stopped
        RobotContainer.stateMachine.driveState = DriveState.Teleop
        RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.STOW
    }
}