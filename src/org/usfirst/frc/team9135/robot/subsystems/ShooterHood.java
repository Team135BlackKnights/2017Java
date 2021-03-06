package org.usfirst.frc.team9135.robot.subsystems;

import org.usfirst.frc.team9135.robot.RobotMap;
import org.usfirst.frc.team9135.robot.commands.ReadHoodEncoderValue;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ShooterHood extends Subsystem {
	CANTalon shooterHoodMotor;
	
	private static final int SHOOTER_HOOD_ENCODER_COUNTS = 497;
	private static final int SHOOTER_HOOD_QUADRATURE_ENCODER_COUNTS = (SHOOTER_HOOD_ENCODER_COUNTS * 4);

	private static final boolean REVERSE_SHOOTER_HOOD_ENCODER_DIRECTION = true;

	private static final int MIN_ENCODER_VALUE = 0;
	//  15000 for Practice Bot
	private static final int MAX_ENCODER_VALUE = 15000;
	private static final int RANGE_OF_ENCODER_VALUES = (MAX_ENCODER_VALUE - MIN_ENCODER_VALUE);

	private static final double MIN_ANGLE_VALUE = 50;
	private static final double MAX_ANGLE_VALUE = 75;
	private static final int RANGE_OF_ANGLE_VALUES = (int) (MAX_ANGLE_VALUE - MIN_ANGLE_VALUE);

	private static final double ENCODER_POSITION_TO_ANGLE_OF_SHOOTER_HOOD = (((double)RANGE_OF_ANGLE_VALUES)/((double)RANGE_OF_ENCODER_VALUES));
	private static final double ANGLE_TO_ENCODER_POSITION_OF_SHOOTER_HOOD = (((double)RANGE_OF_ENCODER_VALUES)/((double)RANGE_OF_ANGLE_VALUES));

	//  Variables for GetAngleOfShooterHoodGivenEncoderPosition()
	private double angleBelowMaxHoodAngle = 0.0;
	private double actualAngle = 0.0;

	//  Variables for GetEncoderPositionOfShooterHoodGivenAngle()
	private double calculatedAngleBelowMaxHoodAngle = 0.0;
	private double doubleEncoderPosition = 0.0;
	private int intEncoderPosition = 0;

	//  Variables for DriveShooterHoodMotorToDesiredAngle()
	private int currentEncoderPosition = (int) 0.0;
	private double currentAngle = 0.0;
	private int desiredEncoderPosition = (int) 0.0;
	private int differenceBetweenCurrentAndDesiredEncoderPositions = 0;
	private static final int THRESHOLD_ENCODER_RANGE_TO_START_SLOWING_MOTOR_POWER = 1500;
	private static final double APPROACHING_DESIRED_ENCODER_POSITION_MOTOR_POWER = .5;
	private double desiredMotorPowerToRunAt = 0.0;

	private boolean initializeDirectionOfHoodToMove = false;
	private boolean driveHoodToIncreaseAngle = false;
	private boolean drivenToAngle = false;

	//  Variables for Calculating Angle for Shooter Hood Given Lidar Value
	private static final double ACCELERATION_OF_GRAVITY = 9.81;
	private static final double ADDED_X_DISTANCE_MIN_INSIDE_BOILER_IN = 17.5;  //  Directly in front of boiler
	private static final double ADDED_X_DISTANCE_MIN_INSIDE_BOILER_CM = (ADDED_X_DISTANCE_MIN_INSIDE_BOILER_IN * 2.54);
	private static final double ADDED_X_DISTANCE_MIN_INSIDE_BOILER_M = (ADDED_X_DISTANCE_MIN_INSIDE_BOILER_CM/100.0);
	private static final double ADDED_X_DISTANCE_MAX_INSIDE_BOILER_IN = 25.0;  //  Shooting from the side of the field //  Hypotenuse of Triangle is 27.3in.
	private static final double ADDED_X_DISTANCE_MAX_INSIDE_BOILER_CM = (ADDED_X_DISTANCE_MAX_INSIDE_BOILER_IN * 2.54);
	private static final double ADDED_X_DISTANCE_MAX_INSIDE_BOILER_M = (ADDED_X_DISTANCE_MAX_INSIDE_BOILER_CM/100.0);
	private static final double SHOOTER_CLOSE_SHOT_M_PER_SEC = 5.0;  //  To Be Determined
	private static final double SHOOTER_FAR_SHOT_M_PER_SEC = 6.5;  //  To Be Determined
	private static final double BOILER_HEIGHT_IN = 97.0;
	private static final double BOILER_HEIGHT_CM = (BOILER_HEIGHT_IN * 2.54);
	private static final double BOILER_HEIGHT_M = (BOILER_HEIGHT_CM/100.0);
	private static final double SHOOTER_HEIGHT_OFF_GROUND_IN = 10.0;
	private static final double SHOOTER_HEIGHT_OFF_GROUND_CM = (SHOOTER_HEIGHT_OFF_GROUND_IN * 2.54);
	private static final double SHOOTER_HEIGHT_OFF_GROUND_M = (SHOOTER_HEIGHT_OFF_GROUND_CM/100.0);
	private static final double Y_DISTANCE_M = (BOILER_HEIGHT_M - SHOOTER_HEIGHT_OFF_GROUND_M);

	private double xDistanceFromLidar_M = 0.0;
	private double totalXDistance_M = 0.0;
	private double chosenVelocityOfShooter = 0.0;
	private double valueAngleForLoopHasToEqual = 0.0;
	private double valueWithInputtedAngle = 0.0;
	private double radiansAngleValue = 0.0;
	private boolean completeFirstForLoop = false;
	private boolean firstAngleValueReceived = false;
	private double currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
	private double pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
	private double calculatedAngle = 0.0;
	private double maxPossibleAngleFirstRound = 0.0;
	private double minPossibleAngleFirstRound = 0.0;
	private int secondAngleCounter = 0;
	private boolean incrementCounterWhenAngleIsClose = false;
	private boolean startSecondForLoop = false;
	private boolean secondLoopCompleted = false;
	private double desiredAngleValueFromSecondForLoop = 0.0;

	//  Variables for TimeOfShotGreaterThanTimeOfMaxHeight()
	private double timeOfMaxHeight = 0.0;
	private double timeOfShot = 0.0;
	private double initialYVelocity = 0.0;
	private double initialXVelocity = 0.0;
	private double radiansDesiredAngleValue = 0.0;
	private double lidarValue_Meters = 0.0;
	private boolean timeOfShotGreaterThanTimeOfMaxHeight = false;

	//  finalant for ConvertDegreesToRadians()
	private static final double DEGREES_TO_RADIANS_CONSTANT = (Math.PI/180.0);

	private int currentShooterHoodEncoderValue = 0;
	private int differenceBetweenDesiredAndCurrentShooterHoodEncoderValue = 0;
	private static final double SHOOTER_HOOD_MOTOR_POWER = 1.0;
	private static final double SLOWER_SHOOTER_HOOD_MOTOR_POWER = .55;
	private boolean initializeDirectionOfShooterHood = false;
	private boolean drivingShooterHoodForward = false;
	private boolean hoodAtDesiredEncoderValue = false;
	private static final int THRESHOLD_SHOOTER_HOOD_ENCODER_VALUE_TO_DECREASE_MOTOR_POWER = 1800;

	private int maxLimitSwitchValue = 0;
	private int minLimitSwitchValue = 0;

	private final CANTalon.FeedbackDeviceStatus UNKNOWN_CONNECTED = CANTalon.FeedbackDeviceStatus.FeedbackStatusUnknown;
	private final CANTalon.FeedbackDeviceStatus RECOGNIZED_CONNECTED = CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent;
	private final CANTalon.FeedbackDeviceStatus DISCONNECTED = CANTalon.FeedbackDeviceStatus.FeedbackStatusNotPresent;

	CANTalon.FeedbackDeviceStatus hoodEncoderPresent;

	private boolean hoodEncoderPluggedIn = false;
	
	
	public int desiredHoodEncoderValueGivenLidarValue = 0;
	public static final double LIDAR_DISTANCE_TO_DESIRED_HOOD_ENCODER_VALUE = 1.0;


	public static final int NUM_OF_SHOOTER_ANGLED_POSITIONS = 2;
	public static final int STRAIGHT_ON = 0;
	public static final int FURTHEST_POINT_FROM_STRAIGHT_ON = 1;

	public static final int[] SHOOTER_ANGLED_POSITION_ARRAY = {STRAIGHT_ON, FURTHEST_POINT_FROM_STRAIGHT_ON};


    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    
    protected void initDefaultCommand() {
    	// Set the default command for a subsystem here.
    	// SetDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new ReadHoodEncoderValue());
    }

    public void InitializeShooterHoodMotor(boolean competitionBot) {
    	if (competitionBot) {
    		shooterHoodMotor = new CANTalon(RobotMap.CB_HOOD_MOTOR_TALON_ID);
    		shooterHoodMotor.setInverted(RobotMap.CB_SHOOTER_HOOD_MOTOR_INVERTED);
    	}
    	else if (competitionBot == false) {
    		shooterHoodMotor = new CANTalon(RobotMap.PB_HOOD_MOTOR_TALON_ID);
    		shooterHoodMotor.setInverted(RobotMap.PB_SHOOTER_HOOD_MOTOR_INVERTED);
    	}
    }

    public void DriveShooterHoodMotor(double motorPower) {
    	if (this.GetMinAngleLimitSwitch() == 1) {
    		this.SetShooterHoodEncoder(MAX_ENCODER_VALUE);
    	}
    	else if (this.GetMaxAngleLimitSwitch() == 1) {
    		this.ZeroShooterHoodEncoder();
    	}

    	shooterHoodMotor.set(motorPower);
    }

    public void ConfigureShooterHoodEncoder() {
    	shooterHoodMotor.configEncoderCodesPerRev(SHOOTER_HOOD_ENCODER_COUNTS);
    	shooterHoodMotor.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
    	shooterHoodMotor.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 10);
    	shooterHoodMotor.reverseSensor(REVERSE_SHOOTER_HOOD_ENCODER_DIRECTION);
    }


    public boolean HoodEncoderPluggedIn() {
    	hoodEncoderPresent = shooterHoodMotor.isSensorPresent(CANTalon.FeedbackDevice.QuadEncoder);
    	if (hoodEncoderPresent == DISCONNECTED) {
    		hoodEncoderPluggedIn = false;
    		//std.cout << "Disconnected" << std.endl;
    	}
    	else if (hoodEncoderPresent == UNKNOWN_CONNECTED || hoodEncoderPresent == RECOGNIZED_CONNECTED) {
    		/*if (hoodEncoderPresent == UNKNOWN_CONNECTED) {
    			std.cout << "Unknown" << std.endl;
    		}
    		else if (hoodEncoderPresent == RECOGNIZED_CONNECTED) {
    			std.cout << "Present" << std.endl;
    		} */
    		hoodEncoderPluggedIn = true;
    	}
    	return hoodEncoderPluggedIn;
    }

    public int GetShooterHoodEncoderPosition() {
    	return (-1 * shooterHoodMotor.getEncPosition());
    }

    public void SetShooterHoodEncoder(int encoderPosition) {
    	shooterHoodMotor.setEncPosition(encoderPosition);
    }

    public void ZeroShooterHoodEncoder() {
    	shooterHoodMotor.setEncPosition(0);
    }

    public double GetAngleOfShooterHoodGivenEncoderPosition(int encoderPosition) {
    	angleBelowMaxHoodAngle = (((double)encoderPosition) * ENCODER_POSITION_TO_ANGLE_OF_SHOOTER_HOOD);
    	actualAngle = (MAX_ANGLE_VALUE - angleBelowMaxHoodAngle);
    	return actualAngle;
    }

    public int GetEncoderPositionOfShooterHoodGivenAngle(double angle) {
    	calculatedAngleBelowMaxHoodAngle = (MAX_ANGLE_VALUE - angle);
    	doubleEncoderPosition = (calculatedAngleBelowMaxHoodAngle * ANGLE_TO_ENCODER_POSITION_OF_SHOOTER_HOOD);
    	intEncoderPosition = ((int)(Math.round((doubleEncoderPosition))));
    	return intEncoderPosition;
    }

    public boolean DriveShooterHoodMotorToDesiredAngle(double desiredAngle, double motorPower) {
    	currentEncoderPosition = this.GetShooterHoodEncoderPosition();
    	currentAngle = this.GetAngleOfShooterHoodGivenEncoderPosition(currentEncoderPosition);
    	desiredEncoderPosition = this.GetEncoderPositionOfShooterHoodGivenAngle(desiredAngle);
    	differenceBetweenCurrentAndDesiredEncoderPositions = (Math.abs(currentEncoderPosition - desiredEncoderPosition));

    	if (initializeDirectionOfHoodToMove == false) {
    		if (desiredAngle >= currentAngle) {
    			driveHoodToIncreaseAngle = true;
    		}
    		else if (currentAngle > desiredAngle) {
    			driveHoodToIncreaseAngle = false;
    		}
    		drivenToAngle = false;
    		initializeDirectionOfHoodToMove = true;
    	}
    	else if (initializeDirectionOfHoodToMove) {
    		if (driveHoodToIncreaseAngle) {
    			if (currentAngle >= desiredAngle) {
    				this.DriveShooterHoodMotor(0.0);
    				initializeDirectionOfHoodToMove = false;
    				drivenToAngle = true;
    			}
    			else {
    				if (differenceBetweenCurrentAndDesiredEncoderPositions > THRESHOLD_ENCODER_RANGE_TO_START_SLOWING_MOTOR_POWER) {
    					desiredMotorPowerToRunAt = motorPower;
    				}
    				else if (differenceBetweenCurrentAndDesiredEncoderPositions <= THRESHOLD_ENCODER_RANGE_TO_START_SLOWING_MOTOR_POWER) {
    					desiredMotorPowerToRunAt = APPROACHING_DESIRED_ENCODER_POSITION_MOTOR_POWER;
    				}
    				this.DriveShooterHoodMotor(desiredMotorPowerToRunAt);
    			}
    		}
    		else if (driveHoodToIncreaseAngle == false) {
    			if (currentAngle <= desiredAngle) {
    				this.DriveShooterHoodMotor(0.0);
    				initializeDirectionOfHoodToMove = false;
    				drivenToAngle = true;
    			}
    			else {
    				if (differenceBetweenCurrentAndDesiredEncoderPositions > THRESHOLD_ENCODER_RANGE_TO_START_SLOWING_MOTOR_POWER) {
    					desiredMotorPowerToRunAt = ((-1) * motorPower);
    				}
    				else if (differenceBetweenCurrentAndDesiredEncoderPositions <= THRESHOLD_ENCODER_RANGE_TO_START_SLOWING_MOTOR_POWER) {
    					desiredMotorPowerToRunAt = ((-1) * APPROACHING_DESIRED_ENCODER_POSITION_MOTOR_POWER);
    				}
    				this.DriveShooterHoodMotor(-desiredMotorPowerToRunAt);
    			}
    		}
    	}
    	return drivenToAngle;
    }

    public boolean DriveShooterHoodToDesiredEncoderValue(int desiredShooterHoodEncoderValue) {
    	currentShooterHoodEncoderValue = this.GetShooterHoodEncoderPosition();
    	differenceBetweenDesiredAndCurrentShooterHoodEncoderValue = Math.abs(desiredShooterHoodEncoderValue - currentShooterHoodEncoderValue);

    	if (initializeDirectionOfShooterHood == false) {
    		if (desiredShooterHoodEncoderValue > currentShooterHoodEncoderValue) {
    			this.DriveShooterHoodMotor(-SHOOTER_HOOD_MOTOR_POWER);
    			drivingShooterHoodForward = false;
    		}
    		else if (currentShooterHoodEncoderValue > desiredShooterHoodEncoderValue) {
    			this.DriveShooterHoodMotor(SHOOTER_HOOD_MOTOR_POWER);
    			drivingShooterHoodForward = true;
    		}
    		hoodAtDesiredEncoderValue = false;
    		initializeDirectionOfShooterHood = true;
    	}
    	else if (initializeDirectionOfShooterHood) {
    		if (drivingShooterHoodForward) {
    			if (desiredShooterHoodEncoderValue == 0) {
    				if (GetMaxAngleLimitSwitch() == 1) {
    					this.DriveShooterHoodMotor(0.0);
    					initializeDirectionOfShooterHood = false;
    					hoodAtDesiredEncoderValue = true;
    				}
    				else {
    					this.DriveShooterHoodMotor(SHOOTER_HOOD_MOTOR_POWER);
    				}
    			}
    			else if (currentShooterHoodEncoderValue <= desiredShooterHoodEncoderValue) {
    				this.DriveShooterHoodMotor(0.0);
    				initializeDirectionOfShooterHood = false;
    				hoodAtDesiredEncoderValue = true;
    			}
    			else {
    				if (differenceBetweenDesiredAndCurrentShooterHoodEncoderValue <= THRESHOLD_SHOOTER_HOOD_ENCODER_VALUE_TO_DECREASE_MOTOR_POWER) {
    					this.DriveShooterHoodMotor(SLOWER_SHOOTER_HOOD_MOTOR_POWER);
    				}
    				else {
    					this.DriveShooterHoodMotor(SHOOTER_HOOD_MOTOR_POWER);
    				}
    			}
    		}
    		else if (drivingShooterHoodForward == false) {
    			if (currentShooterHoodEncoderValue >= desiredShooterHoodEncoderValue) {
    				this.DriveShooterHoodMotor(0.0);
    				initializeDirectionOfShooterHood = false;
    				hoodAtDesiredEncoderValue = true;
    			}
    			else {
    				if (differenceBetweenDesiredAndCurrentShooterHoodEncoderValue <= THRESHOLD_SHOOTER_HOOD_ENCODER_VALUE_TO_DECREASE_MOTOR_POWER) {
    					this.DriveShooterHoodMotor(-SLOWER_SHOOTER_HOOD_MOTOR_POWER);
    				}
    				else {
    					this.DriveShooterHoodMotor(-SHOOTER_HOOD_MOTOR_POWER);
    				}
    			}
    		}
    	}
    	return hoodAtDesiredEncoderValue;
    }

    public void ResetDesiredAngleOfShooterHoodFunctionVariables() {
    	completeFirstForLoop = false;
    	incrementCounterWhenAngleIsClose = false;
    	firstAngleValueReceived = false;
    	secondAngleCounter = 0;
    	pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
    	currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
    	calculatedAngle = 0.0;
    	startSecondForLoop = false;
    	secondLoopCompleted = false;
    	maxPossibleAngleFirstRound = 0.0;
    	minPossibleAngleFirstRound = 0.0;
    	desiredAngleValueFromSecondForLoop = 0.0;
    }

    public double GetDesiredAngleOfShooterHood(double xDistanceFromLidar_CM, boolean closeShot, int shooterAngledPosition) {
    	if (shooterAngledPosition == SHOOTER_ANGLED_POSITION_ARRAY[FURTHEST_POINT_FROM_STRAIGHT_ON]) {
    		xDistanceFromLidar_M = (xDistanceFromLidar_CM/100.0);
    		totalXDistance_M = (xDistanceFromLidar_M + ADDED_X_DISTANCE_MAX_INSIDE_BOILER_M);
    	}
    	else if (shooterAngledPosition == SHOOTER_ANGLED_POSITION_ARRAY[STRAIGHT_ON]) {
    		xDistanceFromLidar_M = (xDistanceFromLidar_CM/100.0);
    		totalXDistance_M = (xDistanceFromLidar_M + ADDED_X_DISTANCE_MIN_INSIDE_BOILER_M);
    	}

    	if (closeShot) {
    		chosenVelocityOfShooter = SHOOTER_CLOSE_SHOT_M_PER_SEC;
    	}
    	else if (closeShot == false) {
    		chosenVelocityOfShooter = SHOOTER_FAR_SHOT_M_PER_SEC;
    	}

    	valueAngleForLoopHasToEqual = ((((-1.0) * ACCELERATION_OF_GRAVITY * totalXDistance_M)/(Math.pow(chosenVelocityOfShooter, 2.0))) - (Y_DISTANCE_M/totalXDistance_M));


    	if (completeFirstForLoop == false) {
    		for (int i = 0; i <= 90; i++) {
    			radiansAngleValue = ConvertDegreesToRadians(((double)i));
    			valueWithInputtedAngle = (((Y_DISTANCE_M/totalXDistance_M) * Math.cos((2.0 * radiansAngleValue))) - (Math.sin((2.0 * radiansAngleValue))));
    			currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = (Math.abs(valueAngleForLoopHasToEqual - valueWithInputtedAngle));
    			if (currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue < .2) {
    				if (incrementCounterWhenAngleIsClose == false) {
    					secondAngleCounter++;
    					incrementCounterWhenAngleIsClose = true;
    				}

    				if (secondAngleCounter == 2) {
    					firstAngleValueReceived = false;
    					secondAngleCounter++;
    				}

    				if (firstAngleValueReceived == false) {
    					pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue;
    					calculatedAngle = ((double)i);
    					firstAngleValueReceived = true;
    				}
    				else if (firstAngleValueReceived && currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue < pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue) {
    					pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue;
    					calculatedAngle = ((double)i);
    				}
    			}
    			else {
    				incrementCounterWhenAngleIsClose = false;
    			}

    			if (i == 90) {
    				completeFirstForLoop = true;
    			}
    		}
    	}
    	else if (completeFirstForLoop && startSecondForLoop == false) {
    		maxPossibleAngleFirstRound = (calculatedAngle + 1.0);
    		minPossibleAngleFirstRound = (calculatedAngle - 1.0);

    		currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
    		pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = 0.0;
    		calculatedAngle = 0.0;
    		firstAngleValueReceived = false;
    		startSecondForLoop = true;
    	}
    	else if (completeFirstForLoop && startSecondForLoop) {
    		for (double i = minPossibleAngleFirstRound; i <= maxPossibleAngleFirstRound; i = i + .1) {
    			radiansAngleValue = ConvertDegreesToRadians(((double)i));
    			valueWithInputtedAngle = (((Y_DISTANCE_M/totalXDistance_M) *  Math.cos((2.0 * radiansAngleValue))) - (Math.sin((2.0 * radiansAngleValue))));
    			currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = (Math.abs(valueAngleForLoopHasToEqual - valueWithInputtedAngle));
    			if (firstAngleValueReceived == false) {
    				pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue;
    				calculatedAngle = ((double)i);
    				firstAngleValueReceived = true;
    			}
    			else if (firstAngleValueReceived && currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue < pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue) {
    				pastDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue = currentDifferenceBetweenCalculatedAngleValueAndDesiredAngleValue;
    				calculatedAngle = ((double)i);
    			}

    			if (i == maxPossibleAngleFirstRound) {
    				secondLoopCompleted = true;
    			}
    		}
    	}
    	else if (completeFirstForLoop && startSecondForLoop && secondLoopCompleted) {
    		desiredAngleValueFromSecondForLoop = calculatedAngle;
    	}

    	return desiredAngleValueFromSecondForLoop;
    }

    public boolean TimeOfShotGreaterThanTimeOfMaxHeight(double desiredAngle, boolean closeShot, double lidarValue_CM, int shooterAngledPosition) {
    	if (shooterAngledPosition == SHOOTER_ANGLED_POSITION_ARRAY[FURTHEST_POINT_FROM_STRAIGHT_ON]) {
    		lidarValue_Meters = (lidarValue_CM/100.0);
    		totalXDistance_M = (lidarValue_Meters + ADDED_X_DISTANCE_MAX_INSIDE_BOILER_M);
    	}
    	else if (shooterAngledPosition == SHOOTER_ANGLED_POSITION_ARRAY[STRAIGHT_ON]) {
    		lidarValue_Meters = (lidarValue_CM/100.0);
    		totalXDistance_M = (lidarValue_Meters + ADDED_X_DISTANCE_MIN_INSIDE_BOILER_M);
    	}

    	radiansDesiredAngleValue = this.ConvertDegreesToRadians(desiredAngle);
    	if (closeShot) {
    		initialYVelocity = (SHOOTER_CLOSE_SHOT_M_PER_SEC * (Math.sin(radiansDesiredAngleValue)));
    		initialXVelocity = (SHOOTER_CLOSE_SHOT_M_PER_SEC * (Math.cos(radiansDesiredAngleValue)));
    	}
    	else if (closeShot == false) {
    		initialYVelocity = (SHOOTER_FAR_SHOT_M_PER_SEC * (Math.sin(radiansDesiredAngleValue)));
    		initialXVelocity = (SHOOTER_FAR_SHOT_M_PER_SEC * (Math.cos(radiansDesiredAngleValue)));
    	}

    	timeOfMaxHeight = (initialYVelocity/ACCELERATION_OF_GRAVITY);
    	timeOfShot = (totalXDistance_M/initialXVelocity);

    	if (timeOfShot > timeOfMaxHeight) {
    		timeOfShotGreaterThanTimeOfMaxHeight = true;
    	}
    	else if (timeOfMaxHeight >= timeOfShot) {
    		timeOfShotGreaterThanTimeOfMaxHeight = false;
    	}

    	return timeOfShotGreaterThanTimeOfMaxHeight;
    }

    public void CheckIfHoodHitsLimitSwitch() {
    	if (this.GetMaxAngleLimitSwitch() == 1) {
    		this.ZeroShooterHoodEncoder();
    	}
    	else if (this.GetMinAngleLimitSwitch() == 1) {
    		this.SetShooterHoodEncoder(MAX_ENCODER_VALUE);
    	}
    }

    public int GetMaxAngleLimitSwitch() {
    	if (RobotMap.COMPETITION_BOT) {
    		maxLimitSwitchValue = shooterHoodMotor.isRevLimitSwitchClosed() ? 1 : 0;
    	}
    	else if (RobotMap.COMPETITION_BOT == false) {
    		maxLimitSwitchValue = shooterHoodMotor.isFwdLimitSwitchClosed() ? 1 : 0;
    	}
    	return maxLimitSwitchValue;
    }

    public int GetMinAngleLimitSwitch() {
    	if (RobotMap.COMPETITION_BOT) {
    		minLimitSwitchValue = shooterHoodMotor.isFwdLimitSwitchClosed() ? 1 : 0;
    	}
    	else if (RobotMap.COMPETITION_BOT == false) {
    		minLimitSwitchValue = shooterHoodMotor.isRevLimitSwitchClosed() ? 1 : 0;
    	}
    	return minLimitSwitchValue;
    }

    public int GetDesiredHoodEncoderPositionGivenLidarValue(double lidarValueIN) {
    	return desiredHoodEncoderValueGivenLidarValue;
    }

    public double ConvertDegreesToRadians(double degrees) {
    	return (degrees * DEGREES_TO_RADIANS_CONSTANT);
    }
}

