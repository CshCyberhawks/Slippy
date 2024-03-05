package frc.robot.subsystems.trunk

import com.revrobotics.CANSparkBase

interface TrunkIO {
    // TODO: I have no wifi, so I can't see how to actually advantagekit this
    fun getRawPosition(): Double
    fun getRawRotation(): Double
    fun setElevatorSpeed(speed: Double)
    fun setRotationSpeed(speed: Double)
    fun setZeroPosition(top: Boolean)
    fun setTopPositionLimit(position: Double)
    fun setBottomPositionLimit(position: Double)
    fun setTopRotationLimit(angle: Double)
    fun setBottomRotationLimit(angle: Double)

    fun setAngleIdleMode(mode: CANSparkBase.IdleMode)
    fun getAngleIdleMode(): CANSparkBase.IdleMode
    fun setPositionIdleMode(mode: CANSparkBase.IdleMode)
    fun setRotationVoltage(volts: Double)
    fun atTopLimit(): Boolean
    fun atBottomLimit(): Boolean
    fun setPositionLimits(on: Boolean)
    fun periodic()
}