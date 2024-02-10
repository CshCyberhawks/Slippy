package frc.robot

import com.pathplanner.lib.auto.AutoBuilder
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.Filesystem
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import frc.robot.commands.ResetSwerveFieldForward
import frc.robot.subsystems.GUNSystem
//import frc.robot.constants.YAGSLConfig
import frc.robot.subsystems.SwerveSystem
import java.io.File

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the [Robot]
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
object RobotContainer {
    val swerveSystem: SwerveSystem = SwerveSystem(File(Filesystem.getDeployDirectory(), "yagsl_configs/good_news_goose"), 4.5)
//    val swerveSystem: SwerveSystem = SwerveSystem(YAGSLConfig)
    val gunSystem = GUNSystem()

    private val leftJoystick: CommandJoystick = CommandJoystick(0)
    private val rightJoystick: CommandJoystick = CommandJoystick(1)
    private val xboxController: CommandXboxController = CommandXboxController(2)

    lateinit var teleopSwerveCommand: Command
    lateinit var teleopElevateCommand: Command
    val autoChooser: SendableChooser<Command> = AutoBuilder.buildAutoChooser()

    /**
     * The container for the robot.  Contains subsystems, IO devices, and commands.
     */
    init {
        when (Constants.currentMode) {
            Constants.Mode.REAL -> {

            }

            Constants.Mode.SIM -> {

            }

            Constants.Mode.REPLAY -> {


            }
        }
        // Configure the button bindings
        configureButtonBindings()

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Use this method to define your button->command mappings.  Buttons can be created by
     * instantiating a [GenericHID] or one of its subclasses ([ ] or [XboxController]), and then passing it to a
     * [edu.wpi.first.wpilibj2.command.button.JoystickButton].
     */
    private fun configureButtonBindings() {
        rightJoystick.button(2).onTrue(ResetSwerveFieldForward())
        teleopSwerveCommand = Commands.run({
            swerveSystem.drive(
                Translation2d(rightJoystick.x, rightJoystick.y),
                rightJoystick.twist,
                true
            )
        })
        teleopElevateCommand = Commands.run({
            gunSystem.elevate(xboxController.leftY)
        })
    }

    /**
     * Use this to pass the autonomous command to the main [Robot] class.
     *
     * @return the command to run in autonomous
     */
}
