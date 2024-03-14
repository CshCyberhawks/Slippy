package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.NoteState
import frc.robot.RobotContainer
import frc.robot.TrunkPose
import frc.robot.commands.trunk.GoToPoseTrunk

class AutoAmp : Command() {

    override fun initialize() {
        RobotContainer.cannonSystem.killShooter()
        RobotContainer.stateMachine.currentTrunkCommand = GoToPoseTrunk(TrunkPose.AMP)
    }

    override fun execute() {

        if (RobotContainer.xboxController.start().asBoolean) {
            RobotContainer.cannonSystem.ampSpit()
        }
        else {
            RobotContainer.cannonSystem.killIntake()
        }
    }

    override fun isFinished(): Boolean {
        return RobotContainer.stateMachine.noteState == NoteState.Empty
    }

    override fun end(interrupted: Boolean) {
        RobotContainer.cannonSystem.killIntake()
        RobotContainer.stateMachine.currentTrunkCommand = GoToPoseTrunk(TrunkPose.STOW)
    }
}