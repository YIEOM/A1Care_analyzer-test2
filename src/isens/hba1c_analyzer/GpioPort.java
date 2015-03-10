package isens.hba1c_analyzer;

import isens.hba1c_analyzer.SerialPort.CtrTarget;
import android.util.Log;

public class GpioPort {
	
	final static String DOOR_SENSOR 	   = "ID",
						CARTRIDGE_SENSOR   = "IC";
	
	private SerialPort mSerialPort;
	
	private enum SensorScan {InitialState, DebounceState, StableState, ReleaseState}
	
	private static SensorScan DoorSensorState = SensorScan.InitialState;
	private static SensorScan CartridgeSensorState = SensorScan.InitialState;
	
	public static boolean CartridgeActState = false;
	public static boolean DoorActState = false;
	
	private static byte DoorInitState;
	private static byte CartridgeInitState;
	
	public static boolean IsCheck = false;
	
	static	{
		
		System.loadLibrary("gpio_port");
	}
	
	public native int Open();
	public native int Close();
	public native int GpioControl(int gpioNum, int gpioHighLow);
	public native int unimplementedOpen();
	public native int unimplementedClose();
	public native int unimplementedGpioControl(int gpioNum, int gpioHighLow);
	
	public void TriggerHigh () {

		Open();
		GpioControl(1, 1);
		Close();
	}
	
	public void TriggerLow () {
		
		Open();
		GpioControl(1, 0);
		Close();
	}
	
	public int CoverRead () {
		
		int value;
		
		Open();
		value = GpioControl(3, 0);
		Close();
		
		return value;
	}
	
	public byte DoorCheck() {
		
		String tmpData;
		
		while(IsCheck) SerialPort.Sleep(10);
		IsCheck = true;
		
		mSerialPort = new SerialPort();
		mSerialPort.BoardTx(DOOR_SENSOR, SerialPort.CtrTarget.NormalSet);
		
		tmpData = BoardMessage("D");
		
		return (byte) Integer.parseInt(tmpData.substring(2));
	}

	public void DoorSensorScan() { // State code of door sensor
		
		DoorSensorScan mDoorSensorScan = new DoorSensorScan();
		mDoorSensorScan.start();
	}
	
	public class DoorSensorScan extends Thread {
		
		public void run() {
			
			if(DoorActState) {
				
//				Log.w("DoorSensorScan", "Door : " + DoorSensorState);
				
				switch(DoorSensorState) {		
				
				case InitialState	:
					DoorInitState = DoorCheck();
					DoorSensorState = SensorScan.DebounceState;
					break;
				
				case DebounceState	:	
					DoorSensorState = (DoorCheck() == DoorInitState) ? SensorScan.StableState : SensorScan.InitialState;
					break;
											
				case StableState	:
					if(DoorCheck() == DoorInitState) {
						
						ActionActivity.DoorCheckFlag = DoorInitState;
						
					} else DoorSensorState = SensorScan.DebounceState;
					break;
											
				default :
					DoorSensorState = SensorScan.InitialState;
					break;
				}
			}
		}
	}
	
	public byte CartridgeCheck() {
		
		String tmpData;
		
		while(IsCheck) SerialPort.Sleep(10);
		IsCheck = true;
		
		mSerialPort = new SerialPort();
		mSerialPort.BoardTx(CARTRIDGE_SENSOR, SerialPort.CtrTarget.NormalSet);
		
		tmpData = BoardMessage("C");
		
//		Log.w("CartridgeCheck", "data : " + Integer.parseInt(tmpData.substring(2)));
		
		return (byte) Integer.parseInt(tmpData.substring(2));
	}
	
	public void CartridgeSensorScan() { // State code of door sensor
		
		CartridgeSensorScan mCartridgeSensorScan = new CartridgeSensorScan();
		mCartridgeSensorScan.start();
	}
	
	public class CartridgeSensorScan extends Thread {
		
		public void run() {
			
			if(CartridgeActState) {

//				Log.w("CartridgeSensorScan", "Cartridge : " + CartridgeSensorState);
				
				switch(CartridgeSensorState) {
				
				case InitialState	:
					CartridgeInitState = CartridgeCheck();
					CartridgeSensorState = SensorScan.DebounceState;
					break;
				
				case DebounceState	:
					CartridgeSensorState = (CartridgeCheck() == CartridgeInitState) ? SensorScan.StableState : SensorScan.InitialState;
					break;
											
				case StableState	:			
					if(CartridgeCheck() == CartridgeInitState) {
						
						ActionActivity.CartridgeCheckFlag = CartridgeInitState;
						
					} else CartridgeSensorState = SensorScan.DebounceState;
					break;
											
				default :
					CartridgeSensorState = SensorScan.InitialState;
					break;
				}
			}
			
			DoorSensorScan();
		}
	}
	
	public String BoardMessage(String sensorMsg) {
		
		int time = 0;
		String temp = "";
		
		mSerialPort = new SerialPort();
		
		while(true) {
			
			temp = mSerialPort.SensorMessageOutput();
			
//			Log.w("BoardMessage", "sensor : " + sensorMsg + " temp : " + temp);
			
			if(time++ == 20) {
				
				IsCheck = false;
				return "NR2";
			}
			
			if(temp.substring(1, 2).equals(sensorMsg)) {
				
				IsCheck = false;
				return temp;
			}
			
			SerialPort.Sleep(10);
		}
	}
}
