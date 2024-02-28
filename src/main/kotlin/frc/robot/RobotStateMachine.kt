package frc.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import frc.robot.constants.CannonConstants
import frc.robot.constants.FieldPositions


//This represents the desired shooter state
enum class ShooterState(val leftVel: Double, val rightVel: Double) {
    Stopped(0.0, 0.0),
    Prepped(CannonConstants.LEFT_SHOOTER_PREP_VELOCITY, CannonConstants.RIGHT_SHOOTER_PREP_VELOCITY),
    Shooting(CannonConstants.LEFT_SHOOTER_SHOOT_VELOCITY, CannonConstants.RIGHT_SHOOTER_SHOOT_VELOCITY)
}

//this represents the CURRENT note state
enum class NoteState {
    Empty,
    Intaking,
    Stored
}

//this represents the DESIRED intake state (functionally current since intake immediately spins up)
enum class IntakeState(val innerPercent: Double, val outerPercent: Double) {
    Stopped(0.0, 0.0),
    Intaking(CannonConstants.INNER_INTAKE_PERCENT, CannonConstants.OUTER_INTAKE_PERCENT),
    Feeding(CannonConstants.INNER_FEED_PERCENT, CannonConstants.OUTER_FEED_PERCENT),
    Spitting(CannonConstants.INNER_SPIT_PERCENT, CannonConstants.OUTER_SPIT_PERCENT)
}

//this represents the DESIRED trunk state
enum class TrunkState {
    Speaker,
    SpeakerFromStage,
    Amp,
    Stow,
    Trap,
    Floor,
    Source,
    Manual
}


//CURRENT states
enum class GlobalZones(val range: Pair<Translation2d, Translation2d>) {
    Wing(Pair(FieldPositions.WING_START, FieldPositions.WING_END)),
    Stage(Pair(FieldPositions.STAGE_START, FieldPositions.STAGE_END)),
    NO(Pair(FieldPositions.NO_START, FieldPositions.NO_END)),
}

//DESIRED states
enum class GlobalSpecificPosition(val pos: Translation2d) {
    Source(FieldPositions.SOURCE),
    Amp(FieldPositions.AMP),
    Speaker(FieldPositions.SPEAKER)
}


//What do we want to do with our current note (or lack of note)?
enum class RobotAction() {
    Amp,
    Speaker,
    Trap,
    FloorIntake,
    SourceIntake,
    Chill
}

enum class DriveState() {
    Teleop,
    TranslationTeleop,
    Auto
}

class RobotStateMachine {

    var trunkState: TrunkState = TrunkState.Stow;
    var intakeState: IntakeState = IntakeState.Stopped;
    var shooterState: ShooterState = ShooterState.Stopped;
    var noteState: NoteState = NoteState.Stored;

    var currentRobotZone: GlobalZones = GlobalZones.Wing
    var prevRobotZone: GlobalZones = GlobalZones.Wing

    var robotAction: RobotAction = RobotAction.Chill
    var driveState: DriveState = DriveState.Teleop

    //Is the trunk at the desired position?
    var trunkReady: Boolean = false
        get() = TODO("Not yet implemented")
        private set

    //Is the shooter at the desired velocity?
    var shooterReady: Boolean = true
        get() = RobotContainer.cannonSystem.shooterReady()
        private set


    //Should be called in teleop periodic
    fun TeleopAutomaticStateManagement() {
        if (currentRobotZone != prevRobotZone) {
            when (currentRobotZone) {
                //when in NO set to stow and sping down shooter (if not overridden)
                GlobalZones.NO -> {
                    if (noteState == NoteState.Empty) {
                        trunkState = TrunkState.Stow
                        shooterState = ShooterState.Stopped
                    }
                }
                //when in wing set to wing shooting position and enable the auto (pivot) aiming and prep shooter (if not manual override)
                GlobalZones.Wing -> {
                    if (noteState != NoteState.Empty) {
                        if (robotAction == RobotAction.Speaker) {
                            trunkState = TrunkState.Speaker
                            shooterState = ShooterState.Prepped
                        }
                    }
                }
                //when in stage, set to stage shooting position and enable the auto (pivot) aiming and prep shooter (if not manual override)
                GlobalZones.Stage -> {
                    if (noteState != NoteState.Empty) {
                        if (robotAction == RobotAction.Speaker) {
                            trunkState = TrunkState.SpeakerFromStage
                            shooterState = ShooterState.Prepped
                        }
                    }
                }
            }
        }
    }
}