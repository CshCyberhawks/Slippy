package frc.robot.constants

import edu.wpi.first.math.util.Units
import kotlin.math.PI

object TargetingConstants {
    // shooter velocity transfer proportion
    var velocityMultiplier = 1.0

    // coords of point we're aiming at relative to center of base of the speaker board (board with the fiducials)
    var endpointX = 0.125
    var endpointY = 0.0
    var endpointZ = 1.7 // Desmod says 1.75 change if time exists

    // coords of center of speaker backboard
    var speakerX = 0.0
    var speakerY = 5.544566 //Units.inchesToMeters(243.654)

    // height that we shoot from; technically varies a bit but lets just say it doesnt
    var shooterZ = Units.inchesToMeters(25.0)

    val ROBOT_ANGLE_DEADZONE = 5.0
}