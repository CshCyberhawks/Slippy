package frc.robot.commands

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand
import frc.robot.NoteState
import frc.robot.RobotContainer
import frc.robot.TrunkPose
import frc.robot.commands.cannon.HalfSpitCannon
import frc.robot.commands.cannon.IntakeCannon
import frc.robot.commands.trunk.CoastAngleMovePosition
import frc.robot.commands.trunk.GoToPoseAndHoldTrunk
import frc.robot.commands.trunk.GoToPoseTrunk
import frc.robot.commands.AutoIntakeCannon
import frc.robot.commands.AutoIntakeTrunk

class AutoIntake : Command() {

    var cannonIntake: AutoIntakeCannon = AutoIntakeCannon()
    var trunkIntake: AutoIntakeTrunk = AutoIntakeTrunk()

    override fun initialize() {
        cannonIntake = AutoIntakeCannon()
        trunkIntake = AutoIntakeTrunk()


        cannonIntake.schedule()
        trunkIntake.schedule()
    }

    override fun execute() {
    }

    override fun isFinished(): Boolean {
        return cannonIntake.isFinished && trunkIntake.isFinished
    }

    override fun end(interrupted: Boolean) {
        cannonIntake.cancel()
        trunkIntake.cancel()
    }
}
