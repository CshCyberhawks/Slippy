package frc.robot.constants

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object TrunkConstants {
    // someone rename these
    const val MAX_PIVOT_HEIGHT_M = 0.8
    const val MIN_PIVOT_HEIGHT_M = 0.0
    const val THING_LENGTH_M = 0.6133
    const val MOVER_GEAR_RADIUS_M = 0.0127
    const val MOVER_GEAR_CIRCUMFERENCE_M = MOVER_GEAR_RADIUS_M * 2.0 * PI
    const val ELEVATOR_ANGLE = 28.8309683
    val d2y = sin(ELEVATOR_ANGLE * PI / 180.0)
    val d2x = cos(ELEVATOR_ANGLE * PI / 180.0)

    var MIN_SAFE_ANGLE: Double = -2.0
    var TARGET_SAFE_ANGLE: Double = 0.0
    var MAX_ANGLE: Double = 0.0

    var SPEAKER_POSITION: Double = .6

    var AMP_POSITION: Double = 0.7
    var AMP_ANGLE: Double = 120.0

    var INTAKE_POSITION: Double = 0.0
    var INTAKE_ANGLE: Double = -30.0

    var CROSSBAR_BOTTOM: Double = 0.1
    var CROSSBAR_TOP: Double = .15

    var STOW_POSITION: Double = 0.0
    var STOW_ANGLE: Double = 0.0

    var TRAP_POSITION: Double = 0.0
    var TRAP_ANGLE: Double = 0.0

    var TOP_BREAK_BEAM_POSITION: Double = 0.75
    var BOTTOM_BREAK_BEAM_POSITION: Double = 0.0

    val MIN_ANGLE_BELOW_CROSSBAR = -10.0
    val MIN_ANGLE_ABOVE_CROSSBAR = -20.0

    var rotationOffset: Double = 0.0

    const val SMART_MOTION_SLOT = 0

    var positionKP = .9
    var positionKI = 0.0
    var positionKD = 0.0
    var positionIz = 0.0
    var positionFF = 0.0
    var positionMax = 1.0
    var positionMin = -1.0
    var positionMinRPM = 10.0
    var positionMaxRPM = 5700.0
    var positionMaxAcceleration = 1500.0
    var positionMaxError = 5.0

    var rotationKP = .075
    var rotationKI = 0.0
    var rotationKD = 0.0
    var rotationIz = 0.0
    var rotationFF = 0.0
    var rotationMin = -1.0
    var rotationMax = 1.0
    var rotationMinRPM = 10.0
    var rotationMaxRPM = 5700.0
    var rotationMaxAcceleration = 1500.0
    var rotationMaxError = 5.0
}