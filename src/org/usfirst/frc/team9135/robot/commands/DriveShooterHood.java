package org.usfirst.frc.team9135.robot.commands;

import org.usfirst.frc.team9135.robot.CommandBase;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveShooterHood extends Command {
	private boolean driveUpwards = false;
	private static final double SHOOTER_HOOD_MOTOR_POWER = 1.0;

	private boolean hoodEncoderPresent = false;

    public DriveShooterHood() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(CommandBase.shooterHood);
    	this.driveUpwards = driveUpwards;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	CommandBase.shooterHood.CheckIfHoodHitsLimitSwitch();

    	if (this.driveUpwards) {
    		CommandBase.shooterHood.DriveShooterHoodMotor(SHOOTER_HOOD_MOTOR_POWER);
    	}
    	else if (this.driveUpwards == false) {
    		CommandBase.shooterHood.DriveShooterHoodMotor(-SHOOTER_HOOD_MOTOR_POWER);
    	}
    	else {
    		CommandBase.shooterHood.DriveShooterHoodMotor(0.0);
    	}

    	/*hoodEncoderPresent = CommandBase.shooterHood.HoodEncoderPluggedIn();

    	if (hoodEncoderPresent == false) {
    		std::cout << "Shooter Hood Encoder Not Connected" << std::endl;
    	} */
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	CommandBase.shooterHood.DriveShooterHoodMotor(0.0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
