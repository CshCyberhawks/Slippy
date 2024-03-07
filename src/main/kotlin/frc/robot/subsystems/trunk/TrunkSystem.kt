package frc.robot.subsystems.trunk

import MiscCalculations
import com.revrobotics.CANSparkBase
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.ElevatorFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj.util.Color8Bit
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.RobotContainer
import frc.robot.TrunkPosition
import frc.robot.TrunkState
import frc.robot.constants.TrunkConstants
import frc.robot.util.Telemetry
import frc.robot.util.visualization.Mechanism2d
import frc.robot.util.visualization.MechanismLigament2d


class TrunkSystem(val io: TrunkIO) : SubsystemBase() {
//
//    private val positionLimits = false
//    private val rotationLimits = false

    private val positionPID: PIDController =
        PIDController(TrunkConstants.positionKP, TrunkConstants.positionKI, TrunkConstants.positionKD)
    private val positionFF: ElevatorFeedforward = ElevatorFeedforward(0.0001, 0.27, 3.07, 0.09)

    private var rotationOffset = TrunkConstants.rotationOffset
    private val rotationFF = ArmFeedforward(
        TrunkConstants.rotationFFkS,
        TrunkConstants.rotationFFkG,
        TrunkConstants.rotationFFkV,
        TrunkConstants.rotationFFkA
    )
    private val rotationPID =
        PIDController(TrunkConstants.rotationKP, TrunkConstants.rotationKI, TrunkConstants.rotationKD)


    private var isPIDing = true

    val isAtAngle: Boolean
        get() =
            MiscCalculations.appxEqual(rotationPID.setpoint, getRotation(), TrunkConstants.ANGLE_DEADZONE)


    var currentState = TrunkState.CALIBRATING
//    var RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.STOW
//        set(value) =
//            if (!this.isMoving) {
//                field = value
//            } else {
//                field = field
//            }


    private var prevTargetPose = TrunkPosition.SPEAKER

    var currentPosition: Double = 0.0
    var currentRotation: Double = getRotation()

    var isRotationSafe = false
    var hasElevatorMoved = false

    var isMoving = false

    var isAnglePID = false

    val superstructureMechanism = Mechanism2d(
        TrunkConstants.TOP_BREAK_BEAM_POSITION * TrunkConstants.d2x + 1.0,
        TrunkConstants.TOP_BREAK_BEAM_POSITION * TrunkConstants.d2y + 1.0
    )
    val elevatorMechanismRoot = superstructureMechanism.getRoot("Elevator Root", 0.25, 0.25)
    val trunkMechanismRoot = superstructureMechanism.getRoot(
        "Trunk Root",
        currentPosition * TrunkConstants.d2x + .25,
        currentPosition * TrunkConstants.d2y + .25
    )
    val trunkMechanism =
        trunkMechanismRoot.append(MechanismLigament2d("Trunk", -.25, currentRotation, color = Color8Bit(0, 0, 255)))
    val crossbarRoot = superstructureMechanism.getRoot(
        "Crossbar Root",
        (TrunkConstants.CROSSBAR_BOTTOM + .02) * TrunkConstants.d2x + .25,
        (TrunkConstants.CROSSBAR_BOTTOM - .02) * TrunkConstants.d2y + 0.25
    )


    init {
        elevatorMechanismRoot.append(MechanismLigament2d("Elevator", .8, TrunkConstants.ELEVATOR_ANGLE))
        crossbarRoot.append(
            MechanismLigament2d(
                "Crossbar",
                TrunkConstants.CROSSBAR_TOP - TrunkConstants.CROSSBAR_BOTTOM - .25,
                TrunkConstants.ELEVATOR_ANGLE,
                color = Color8Bit(0, 255, 0)
            )
        )
        setDesiredRotation(TrunkConstants.SAFE_TRAVEL_ANGLE)
    }

    fun goManual() {
        io.setElevatorSpeed(0.0)
        io.setRotationSpeed(0.0)
        setPID(false)
        currentState = TrunkState.MANUAL
        brakeMotors()
        io.setPositionLimits(true)
    }

    fun calibrate() {
        io.setElevatorSpeed(0.0)
        io.setRotationSpeed(0.0)
        setPID(false)
        currentState = TrunkState.CALIBRATING
        io.setElevatorSpeed(0.2)
        io.setPositionLimits(false)
    }

    fun rotate(speed: Double) {
        if (currentState == TrunkState.MANUAL) {
            io.setRotationSpeed(speed)
//            SmartDashboard.putNumber("rotation percent output", speed)
        }
    }

    fun elevate(speed: Double) {
        if (currentState == TrunkState.MANUAL) {
            io.setElevatorSpeed(speed)
        }
    }

    fun goToCustom() {
        io.setElevatorSpeed(0.0)
        setPID(true)
        currentState = TrunkState.CUSTOM
        io.setPositionLimits(true)
    }

    fun getRotation(): Double {
        return frc.robot.util.Math.wrapAroundAngles((-io.getRawRotation() * 360.0) - rotationOffset)
//        return rotationEncoder.position
    }

    fun getPosition(): Double {
        return io.getRawPosition() * TrunkConstants.ELEVATOR2M
    }

    private fun setDesiredPosition(position: Double) {
        positionPID.setpoint = position
    }

    private fun setDesiredRotation(angle: Double) {
        rotationPID.setpoint = (Math.toRadians(angle))
    }

    fun setShootingAngle(angle: Double) {
        //If we aren't in a shooting position, dont to this
        if (RobotContainer.stateMachine.targetTrunkPose != TrunkPosition.SPEAKER && RobotContainer.stateMachine.targetTrunkPose != TrunkPosition.SPEAKER_FROM_STAGE) {
            return
        }
        //If we are still moving, dont do this
        if (isMoving) {
            return
        }
        //If we aren't in IK mode, don't do this
        if (RobotContainer.stateMachine.trunkState != TrunkState.CUSTOM) {
            return
        }
        setDesiredRotation(angle)
    }

    fun setPID(on: Boolean) {
        isPIDing = on
    }


    fun STOP() {
        setPID(false)
        currentState = TrunkState.STOP
        io.setElevatorSpeed(0.0)
        io.setRotationSpeed(0.0)
        io.setPositionLimits(true)
    }

    override fun periodic() {
        RobotContainer.telemetry.trunkTelemetry = SmartDashboard.getBoolean("Trunk Telemetry", RobotContainer.telemetry.trunkTelemetry)
        SmartDashboard.putBoolean("Trunk Telemetry", RobotContainer.telemetry.trunkTelemetry)

//        if(positionLimits && (getPosition() < positionBottomLimit || getPosition() > positionTopLimit) || rotationLimits &&  (getRotation() < rotationBottomLimit || getRotation() > rotationTopLimit))
//            goManual()
//
        if(io.atTopLimit()) {
            io.setZeroPosition(top = true)
        }

        Telemetry.putString("Angle Idle Mode", io.getAngleIdleMode().name, RobotContainer.telemetry.trunkTelemetry)

//        if(io.atBottomLimit()) {
//            io.setZeroPosition(top = false)
//        }

        Telemetry.putBoolean("is moving", isMoving, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putBoolean("is rotation safe?", isRotationSafe, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putBoolean("has elevator moved?", hasElevatorMoved, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("Angle val", getRotation(), RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("Angle val no offset", frc.robot.util.Math.wrapAroundAngles((-io.getRawRotation() * 360.0)), RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("position val", getPosition(), RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("target position", RobotContainer.stateMachine.targetTrunkPose.position, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putString("trunk state", RobotContainer.stateMachine.trunkState.name, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("target angle", RobotContainer.trunkSystem.rotationPID.setpoint, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putString("prevTargetPosition name", prevTargetPose.name, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putString("targetPosition name", RobotContainer.stateMachine.targetTrunkPose.name, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putBoolean("is angle PID?", isAnglePID, RobotContainer.telemetry.trunkTelemetry)

        if (currentState == TrunkState.CUSTOM) {
//            io.setDesiredPosition(TrunkConstants.STOW_POSITION)
            //Changed position
            if (RobotContainer.stateMachine.targetTrunkPose != prevTargetPose) {
                setPID(true)

                //Not safe rotation
                if (!isRotationSafe && !hasElevatorMoved) {
                    isMoving = true
                    setDesiredPosition(prevTargetPose.position)
                    setDesiredRotation(TrunkConstants.SAFE_TRAVEL_ANGLE)
                }
                //Safe rotation but still need to start
                else if (isRotationSafe && !hasElevatorMoved) {
                    isMoving = true
                    println("set the desired position to target position")
                    setDesiredRotation(TrunkConstants.SAFE_TRAVEL_ANGLE)
                    setDesiredPosition(RobotContainer.stateMachine.targetTrunkPose.position)
                }

                if (MiscCalculations.appxEqual(
                        getPosition(),
                        RobotContainer.stateMachine.targetTrunkPose.position,
                        .05
                    )
                ) {
                    hasElevatorMoved = true
                }

                //Elevator has moved
                if (hasElevatorMoved) {
                    setDesiredRotation(RobotContainer.stateMachine.targetTrunkPose.angle)
                }

                //Elevator has moved and the angle is good (finished)
                if (hasElevatorMoved && MiscCalculations.appxEqual(
                        getRotation(),
                        RobotContainer.stateMachine.targetTrunkPose.angle,
                        TrunkConstants.ANGLE_DEADZONE
                    )
                ) {
                    prevTargetPose = RobotContainer.stateMachine.targetTrunkPose
                    hasElevatorMoved = false
                    isMoving = false
                }
                //More handling for the stupid intaking (stupid intaking finished)
                else if (hasElevatorMoved && !isAnglePID && RobotContainer.stateMachine.targetTrunkPose == TrunkPosition.INTAKE) {
                    prevTargetPose = RobotContainer.stateMachine.targetTrunkPose
                    hasElevatorMoved = false
                    isMoving = false
                    io.setAngleIdleMode(CANSparkBase.IdleMode.kCoast)
                }
            }
            //Is rotation safe?
            if (getRotation() > TrunkConstants.SAFE_TRAVEL_ANGLE || MiscCalculations.appxEqual(
                    getRotation(),
                    TrunkConstants.SAFE_TRAVEL_ANGLE,
                    TrunkConstants.ANGLE_DEADZONE
                )
            ) {
                isRotationSafe = true
            }


            //Handling for the garbage intake logic bc the build team poorly designed the intake and didn't tell us
            if (RobotContainer.stateMachine.targetTrunkPose == TrunkPosition.INTAKE && getPosition() < TrunkConstants.SAFE_TO_DROP_INTAKE_POSITION) {
                isAnglePID = false
                io.setAngleIdleMode(CANSparkBase.IdleMode.kCoast)
                io.setRotationVoltage(0.0)
//                println("for stupid intake coasted the motors")
            } else {
                isAnglePID = true
            }
        } else if (currentState == TrunkState.CALIBRATING) {
            calibratePeriodic()
        }


        //Do the trunk PIDs
        Telemetry.putNumber("position pid setpoint", positionPID.setpoint, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("angle pid setpoint", rotationPID.setpoint, RobotContainer.telemetry.trunkTelemetry)

        val positionPIDOut = positionPID.calculate(getPosition())


//        SmartDashboard.putNumber("leader voltage", mainRotationMotor.appliedOutput)
        val posFF = TrunkConstants.positionFF

        Telemetry.putNumber("position pid out", positionPIDOut, RobotContainer.telemetry.trunkTelemetry)
        Telemetry.putNumber("position pid + FF", positionPIDOut + posFF, RobotContainer.telemetry.trunkTelemetry)

        Telemetry.putBoolean("Is PIDing", isPIDing, RobotContainer.telemetry.trunkTelemetry)
        if (isPIDing) {

            io.setElevatorSpeed(posFF + positionPIDOut)

            if (isAnglePID) {
                val pidVal: Double = MathUtil.clamp(rotationPID.calculate(Math.toRadians(getRotation())), -5.0, 5.0)
                Telemetry.putNumber("rotation pid out", pidVal, RobotContainer.telemetry.trunkTelemetry)
                Telemetry.putNumber(
                    "rotation PID + FF", pidVal
                            + rotationFF.calculate(Math.toRadians(getRotation() - 90), 0.0), RobotContainer.telemetry.trunkTelemetry
                )

                io.setRotationVoltage(
                    (pidVal
                            + rotationFF.calculate(Math.toRadians(getRotation() - 90), 0.0))
                )
            }
        }
        io.periodic()
    }


    fun freeMotors() {
        io.setAngleIdleMode(CANSparkBase.IdleMode.kCoast)
        io.setPositionIdleMode(CANSparkBase.IdleMode.kCoast)
        io.setRotationSpeed(0.0)
    }

   fun brakeMotors() {
       io.setPositionIdleMode(CANSparkBase.IdleMode.kBrake)
       io.setAngleIdleMode(CANSparkBase.IdleMode.kBrake)
   }

    private fun calibratePeriodic() {
        io.setRotationSpeed(0.0)
        val topLimit = io.atTopLimit()
        if (topLimit) {
            io.setElevatorSpeed(0.0)
            io.setZeroPosition(true)
            RobotContainer.stateMachine.targetTrunkPose = TrunkPosition.STOW
            prevTargetPose = TrunkPosition.SPEAKER
            goToCustom()
        }
    }
}
