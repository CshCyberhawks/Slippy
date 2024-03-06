package frc.robot

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import frc.robot.constants.LimelightConstants
import org.littletonrobotics.junction.LoggedRobot


class Robot : LoggedRobot() {
    private lateinit var m_autonomousCommand: Command

    private var lastRobotAction: RobotAction = RobotContainer.stateMachine.robotAction
    override fun robotInit() {
        SmartDashboard.putBoolean("arm motors free?", false)

        RobotContainer.swerveSystem.driveTrain.getDaqThread().setThreadPriority(99);
    }

    override fun robotPeriodic() {
        if (RobotContainer.robotActionSendable.selected != lastRobotAction && RobotContainer.robotActionSendable.selected != null) {
            RobotContainer.stateMachine.robotAction = RobotContainer.robotActionSendable.selected
            lastRobotAction = RobotContainer.robotActionSendable.selected
        }

        RobotContainer.swerveSystem.logger.telemeterize(RobotContainer.swerveSystem.driveTrain.state)

        CommandScheduler.getInstance().run()
        RobotContainer.stateMachine.logStates()

        if (RobotContainer.stateMachine.trunkState == TrunkState.MANUAL) {
            RobotContainer.trunkSystem.elevate(-RobotContainer.xboxController.leftY)
            RobotContainer.trunkSystem.rotate(-RobotContainer.xboxController.rightY * .1)
        } else {
//            if (!RobotContainer.trunkSystem.isMoving) {
//                RobotContainer.trunkSystem.io.setDesiredRotation(RobotContainer.trunkSystem.io.getRotation() + (-RobotContainer.xboxController.rightY * .1))
//            }
        }

        val armMotorsFree = SmartDashboard.getBoolean("arm motors free?", false)

        if (armMotorsFree && !DriverStation.isTeleopEnabled() && !DriverStation.isTestEnabled() && !DriverStation.isAutonomousEnabled()) {
            RobotContainer.trunkSystem.freeMotors()
        }
        else if (!armMotorsFree && !DriverStation.isTeleopEnabled() && !DriverStation.isTestEnabled() && !DriverStation.isAutonomousEnabled()){
            RobotContainer.trunkSystem.brakeMotors()
        }

        if (!DriverStation.isDisabled()) {
            RobotContainer.visionSystem.updateOdometry(2)
        }
        else {
            RobotContainer.visionSystem.updateOdometry(1)
        }


        SmartDashboard.putBoolean("shooter ready", RobotContainer.cannonSystem.shooterReady())
        SmartDashboard.putString("note state", RobotContainer.stateMachine.noteState.name)
        SmartDashboard.putString("intake state", RobotContainer.stateMachine.intakeState.name)
    }



    override fun disabledInit() {}

    override fun disabledPeriodic() {}

    override fun disabledExit() {}

    override fun autonomousInit() {
        RobotContainer.autonomousCommand.schedule()
    }

    override fun autonomousPeriodic() {}

    override fun autonomousExit() {}

    override fun teleopInit() {
        RobotContainer.autonomousCommand.cancel()
        RobotContainer.teleopSwerveCommand.schedule()
        RobotContainer.trunkSystem.calibrate()

//        RobotContainer.trunkSystem.STOP()

    }

    override fun teleopPeriodic() {
        RobotContainer.stateMachine.TeleopAutomaticStateManagement()




        //        SmartDashboard.putNumber("JoyX", RobotContainer.rightJoystick.x)
        //        SmartDashboard.putNumber("JoyY", RobotContainer.rightJoystick.y)
        //        SmartDashboard.putNumber("JoyTwist", RobotContainer.rightJoystick.twist)
        //        val desiredState = SwerveModuleState(0.0, Rotation2d(0.0, 0.0))
        //        if(RobotContainer.xboxController.b().asBoolean)
        //            RobotContainer.trunkSystem.goManual()
        //        if(RobotContainer.xboxController.x().asBoolean)
        //            RobotContainer.trunkSystem.STOP()
        //        if(RobotContainer.xboxController.a().asBoolean)
        //            RobotContainer.trunkSystem.calibrate()
        //        RobotContainer.trunkSystem.elevate(-RobotContainer.xboxController.leftY)
        //        RobotContainer.trunkSystem.rotate(-RobotContainer.xboxController.rightY*.3)

    }

    override fun teleopExit() {}

    override fun testInit() {
        CommandScheduler.getInstance().cancelAll()
    }

    override fun testPeriodic() {
        //        RobotContainer.swerveSystem.drive(Translation2d(0.25, 0.0), 0.0, true)
        //        val calibrator = ShooterCalibrator("/u/shooter_calibrator/test1.csv");
        //        calibrator.writeOut(shots)
        //        val readShots = calibrator.readCsv();
        //        readShots.forEach {
        //            println(it.toCSV())
        //        }
    }

    override fun testExit() {}

    override fun simulationPeriodic() {}
}
