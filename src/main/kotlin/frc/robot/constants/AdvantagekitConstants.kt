package frc.robot

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. inside the companion object).  Do not put anything functional in this class.
 *
 *
 * It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
object Constants {
    val currentMode: Mode = Mode.REAL

    enum class Mode {
        /** Running on a real robot.  */
        REAL,

        /** Running a physics simulator.  */
        SIM,

        /** Replaying from a log file.  */
        REPLAY
    }
}
