package com.dingli.seegull;



/**
 * @author zhihui.lian
 */
public class SeeGullFlags {
	
	//protocol
	public static class ProtocolCodes {
		public final static int	PROTOCOL_GSM	=	0x0001;
		public final static int	PROTOCOL_IS_95_CDMA	=	0x0003;
		public final static int	PROTOCOL_3GPP_WCDMA	=	0x0004;
		public final static int	PROTOCOL_IS_2000_CDMA=	0x0005;
		public final static int	PROTOCOL_IS_856_EVDO=	0x0006;
		public final static int	PROTOCOL_TDSCDMA	=	0x0008;
		public final static int	PROTOCOL_LTE	=	0x000A;
		public final static int	PROTOCOL_TD_LTE	=	0x000B;
		public final static int	PROTOCOL_WiFi	=	0x000C;
	}	
	
	public static class ScanTypes {
		public final static int eScanType_RssiChannel	=	0	;//	RSSI Channel scan
		public final static int eScanType_RssiFrequency	=	1	;//	RSSI Frequency scan
		public final static int eScanType_EnhancedPowerScan=2	;//	Enhanced Power scan
		public final static int eScanType_TopNSignal	=	3	;//	Top N Signal scan
		public final static int eScanType_SpectrumAnalysis=	4	;//	Spectrum Analysis scan
		public final static int eScanType_TopNPilot		=	5	;//	Top N Pilot scan
		public final static int eScanType_TopNPilotBch	=	6	;//	Top N Pilot BCH scan
		public final static int eScanType_ColorCode		=	7	;//	Color Code scan
		public final static int eScanType_CodeDomain	=	8	;//	Code Domain scan
		public final static int eScanType_TimeSlot		=	9	;//	Time Slot scan
		public final static int eScanType_PilotZoom		=	10	;//	Pilot Zoom scan
		public final static int eScanType_PilotScan		=	11	;//	Pilot scan
		public final static int eScanType_eTopNSignal	=	12	;//	Enhanced Top N Signal scan
		public final static int eScanType_TopNPilotPCH	=	13	;//	Top N Pilot PCH scan
		public final static int eScanType_BlindScan		=	14	;//	Blind scan
		public final static int eScanType_PowerAnalysis	=	15	;//	Power Analysis scan
		public final static int eScanType_ClarifyBCCH	=	16	;//	Clarify BCCH scan
		public final static int eScanType_MxBlindScan	=	17	;//	Mx Blind scan
		public final static int eScanType_ClarifyPilot	=	18	;//	Clarify Pilot scan
		public final static int eScanType_TimeSlotAnalysis=	19	;//	Time Slot Analysis scan
		public final static int eScanType_WiFiThroughput=	20	;//	WiFi Throughput scan
	}
	
	
	//GPS Status Codes
	public static class GpsStatusCode {
		public final static int PON_HOLDOVER	=	0	;// Trueif Clock Card state is Power On Holdover
		public final static int HOLDOVER		=	1	;// Trueif Clock Card state is Normal Holdover
		public final static int TRAINING		=	2	;// Trueif Clock Card state is Training
		public final static int NORMAL			=	3	;// Trueif Clock Card state is Normal
		public final static int TRAINED			=	4	;// Trueif Clock Card state is Trained
		public final static int TRACKING		=	5	;// Trueif Clock Card is phase aligned with the GPS
		public final static int UTC_TIME_VALID	=	6	;// Trueif UTC seconds is valid
		public final static int ERROR			=	7	;// Trueif error on the Clock Card
	}
	
	public static class ServiceStatusCode {
		public final static int	SUCCESS						=	0	;//	Success
		public final static int	UNRECOVERABLE_ERROR			=	-1	;//	Unrecoverable error
		public final static int	SERVICE_NOT_INITIALIZED		=	-2	;//	Service not initialized
		public final static int	INITIALIZATION_SUCCEED		=	-3	;//	Initialization successful
		public final static int	INITIALIZATION_FAILED		=	-4	;//	Initialization failed
		public final static int	INVALID_TCPIP_HOST_PARAMETERS=	-5	;//	Invalid TCP/IP host parameters
		public final static int	SERVICE_CLOSED_SUCCEED		=	-6	;//	Service closed successful
		//public final static int	Reserved	=	-7	;//	Reserved
		//public final static int	Reserved	=	-8	;//	Reserved
		//public final static int	Reserved	=	-9	;//	Reserved
		public final static int	DETECTION_PROCESS_COMPLETED	=	-10	;//	Detection process completed
		public final static int	NO_BLUETOOTH_DEVICES_DETECTED=	-11	;//	No bluetooth devices detected
		public final static int	NO_USB_DEVICES_DETECTED		=	-12	;//	No USB devices detected
		public final static int	NO_PCTEL_DEVICES_DETECTED	=	-13	;//	No PCTEL devices detected
		//public final static int	Reserved	=	-14	;//	Reserved
		public final static int	BLUETOOTH_DEVICE_NOT_ENABLE	=	-15	;//	Bluetooth not enabled
		public final static int	BLUETOOTH_DEVICE_PAIRED_FAIL=	-16	;//	Failed to pair with bluetooth device
		public final static int	BLUETOOTH_DEVICE_NOT_PAIRED	=	-17	;//	Bluetooth device not paired
		public final static int	BLUETOOTH_SOCKET_CONNECTION_ESTABLISHED	=	-18	;//	Bluetooth socket connection established
		public final static int	BLUETOOTH_SOCKET_CONNECTION_NOT_ESTABLISHED=-19	;//	Bluetooth socket connection not established
		public final static int	BLUETOOTH_DEVICE_PAIRED_SOCKET_CONNECTION_FAIL=-20	;//	Bluetooth device paired socket connection failed
		public final static int	USB_DEVICE_ATTACHED			=	-21	;//	USB device attached
		public final static int	USB_DEVICE_DETECHED 	=	-22	;//	USB device detached
		public final static int	USB_CONNECTION_SUCCEED		=	-23	;//	USB connection successful
		public final static int	USB_CONNECTION_FAIL			=	-24	;//	USB ?connection failed
		//public final static int	Reserved	=	-25	;//	Reserved
		//public final static int	Reserved	=	-26	;//	Reserved
		//public final static int	Reserved	=	-27	;//	Reserved
		//public final static int	Reserved	=	-28	;//	Reserved
		//public final static int	Reserved	=	-29	;//	Reserved
		public final static int	DEVICE_NOT_CONNECTED		=	-30	;//	Device not connected
		public final static int	DEVICE_CONNECTED_SUCCEED	=	-31	;//	Device connected successful
		public final static int	INVALID_CONNECTION_TYPE		=	-32	;//	Invalid connection type
		public final static int	INVALID_DEVICE_ID			=	-33	;//	Invalid device Id
		public final static int	DEVICE_DISCONNECTED_SUCCEED	=	-34	;//	Device disconnected successful
		public final static int	DEVICE_DISCONNECTED_FAIL	=	-35	;//	Device disconnected failed
		public final static int	DEVICE_CONNECTION_LOST		=	-36	;//	Device connection lost. Trying to reconnect.
		public final static int	DEVICE_RECONNECTING_SUCCEED	=	-37	;//	Device reconnecting successful
		public final static int	DEVICE_RECONNECTING_FAIL	=	-38	;//	Device reconnecting failed
		//public final static int	Reserved	=	-39	;//	Reserved
		public final static int	MISSING_REQUEST_PARAMETERS	=	-40	;//	Missing request parameters
		public final static int	INVALID_JSON_STRING			=	-41	;//	Invalid JSON string
		public final static int	CONTROL_REQUEST_FAIL		=	-42	;//	Control request failed
		public final static int	CONTROL_REQUEST_EXCEPTION	=	-43	;//	Control request exception
		public final static int	SCAN_REQUEST_FAIL			=	-44	;//	Scan request failed
		public final static int	SCAN_REQUEST_EXCEPTION		=	-45	;//	Scan request exception
		public final static int	GPS_REQUEST_FAIL			=	-46	;//	GPS request failed
		public final static int	GPS_REQUEST_EXCEPTION		=	-47	;//	GPS request exception
		//public final static int	Reserved	=	-48	;//	Reserved
		//public final static int	Reserved	=	-49	;//	Reserved
		public final static int	TCPIP_TRANSFER_FAIL			=	-50	;//	TCP/IP data transfer failed
		public final static int	TCPIP_CONNECTION_ESTABLISHED=	-51	;//	TCP/IP connection established
		public final static int	TCPIP_CONNECTION_NOT_ESTABLISHED=-52	;//	TCP/IP connection not established
		public final static int	TCPIP_CONNECTION_LOST		=	-53	;//	TCP/IP connection lost. Trying to reconnect.
		public final static int	TCPIP_CONNECTION_CLOSE_SUCCEED=	-54	;//	TCP/IP connection closed successful
		//public final static int	Reserved	=	-55 to -59	;//	Reserved
		public final static int	SCANNER_SCAN_DATA_DROP		=	-60	;//	Scanner scan data drop
		public final static int	SCAN_DATA_DROP				=	-61	;//	Scan data drop
		public final static int	INVALID_MSG_SIGN			=	-62	;//	Invalid message signature
		public final static int	INVALID_MSG_CRC				=	-63	;//	Invalid message CRC
		public final static int	MSG_DEC_FAILED				=	-64	;//	Message decoding failed
	}
	
	public static class ScannerStatusCode {
		public final static int NORMAL_OK					=	0	;//	Device System LED will illuminate green
		public final static int DEVICE_INITIALIZATION_ERROR	=	1	;//	Device System LED will illuminate orange
		public final static int LICENSE_EXPIRED				=	2	;//	
		public final static int DEVICE_ERROR				=	3	;//	System error detected.Number of Blind Scan Requests per protocol has been exceeded. CDMA/EVDO/FD-LTE/TD-LTE/WCDMA/GSM:Permitted number of scans per protocol is 8
		public final static int SYSTEM_NOT_READY			=	4	;//	Data requested cannot be provided since the current scan is not complete.
		public final static int INVALID_CHANNEL_STYLE_OR_MEASUREMENT_BANDWIDTH	=	5	;//	
		public final static int DEVICE_SCANNING_MESSAGE_NOT_VALID =6	;//	The request cannot be serviced system is currently scanning.
		public final static int DEVICE_SUSPENDED_MESSAGE_NOT_VALID=7	;//	Data request cannot be serviced since associated Scan Request has not been issued.
		public final static int REQUEST_NOT_SUPPORTED		=	8	;//	Request is not supported at this time
		public final static int ILLEGAL_MESSAGE_OPCODE_INVALID=	9	;//	
		public final static int ILLEGAL_MESSAGE_PARAMETER_OUT_OF_RANGE = 10	;//	
		//public final static int RESERVED	=	11	;//	
		public final static int DEVICE_OVERFLOW				=	12	;//	Interface data link message queue overflow detected. WCDMA: Total permitted number of channels for Top N Pilot and Clarify Pilot Scans is 18.
		//public final static int RESERVED	=	13	;//	
		public final static int CHECKSUM_ERROR				=	14	;//	Interface data link message checksum error detected.
		public final static int SCAN_GROUP_EXCEEDED			=	15	;//	Exceeded the permissible number of requests for scan group. FD-LT/TD-LTE: Permitted number of eTop N Signal Scans is 18 and 24 per protocol in SeeGull MX and SeeGull IBflexTM respectivelly. TD-LTE: Permitted number of eTop N Signal and Power Analysis Scans is 24 in SeeGull ?IBflexTM WCDMA/CDMA/EVDO: Permitted number of Top N Pilot Scans is 18 and 24 per protocol ?in SeeGull MX and SeeGull?IBflexTM respectivelly. TD-SCDMA: Permitted number of Top N Pilot Scans is 34 in SeeGull ?IBflexTM.
		//public final static int RESERVED	=	16	;//	
		public final static int MANUAL_MODE_ONLY			=	17	;//	Due to a restriction (normally using a half duplex protocol), Data response will be generated only per Data Request.
		public final static int RESPONSE_TIMEOUT			=	18	;//	
		public final static int CHANNEL_OR_FREQUENCY_OUT_OF_RANGE = 19	;//	
		public final static int INVALID_BAND				=	20	;//	
		public final static int INVALID_PROTOCOL			=	21	;//	
		//public final static int RESERVED	=	22	;//	
		public final static int DSP_SYSTEM_NOT_READY		=	23	;//	1. Invalid Out of Range measurement Type 2. Error in data 3. Resources busy 4. Missing Interrupt
		public final static int TOO_MANY_OUTPUT_MESSAGES	=	24	;//	The number of scans requested in auto mode exceeds the maximum number of allowable output messages.
		//public final static int RESERVED	=	25-27	;//	
		public final static int EXCLUSIVE_SCANNING_MODE		=	28	;//	No other scan requested can be serviced while in this mode.
		//public final static int RESERVED	=	29	;//	
		public final static int INVALID_OPTION				=	30	;//	
		//public final static int RESERVED	=	31-42	;//	
		public final static int NUMBER_OF_CHANNELS_OUT_OF_RANGE=43	;//	The number of channels in the Immediate or Manual RSSI request message is greater than 255. The number of channels in the Immediate or Manual Top N Signal Scan request message is greater than 1.
		public final static int NUMBER_OF_PILOTS_OUT_OF_RANGE =	44	;//	Number of Top Pilots Requested is out of range.
		//public final static int RESERVED	=	45	;//	
		public final static int EXCEEDED_DEVICE_CAPACITY	=	46	;//	Number of Channels/Pilots/Signals requested cannot be serviced since it exceeds device capacity.
		//public final static int RESERVED	=	47-51	;//	
		public final static int NUMBER_OF_OUTPUT_BLOCKS_OUT_OF_RANGE = 52	;//	Requested number of output blocks to return is less then minimum allowed.
		public final static int RESERVED					=	53	;//	
		//public final static int REQUEST_NOT_SUPPORTED	=	54	;//	Requested not supported by installed option(s).
		public final static int CORR_TAPS_NOT_SUPPORTED		=	55	;//	Requested Correlation Taps not supported by installed option(s).
		public final static int TIMING_MODE_NOT_SUPPORTED	=	56	;//	Requested Timing Mode not supported by installed option(s).
		public final static int DATA_NOT_AVAILABLE			=	57	;//	Data not Available to service Manual mode Data Request.
		//public final static int RESERVED	=	58-60	;//	
		public final static int DWELLING_TIME_OUT_OF_RANGE	=	61	;//	
		//public final static int RESERVED	=	62-63	;//	
		public final static int INVALID_CONFIGURATION		=	64	;//	Requested Configuration not supported by installed option
		public final static int CONFIGURATION_OPTION_NOT_INSTALLED	  =	65	;//	The band type/RF Device ID/protocol requested requires applicable Configuration Option installed.
		public final static int MULTIPLE_BANDS_SCANNING_NOT_SUPPORTED =	66	;//	The request for additional band scanning is not supported. Stop the entire currently ongoing band scanning, reset (SW/HW) the unit and request the other.
		//public final static int RESERVED	=	67	;//	
		public final static int UNSUPPORTED_OTHER_SCANS_WITH_BLIND_SCAN=68	;//	The request for additional ?protocol specific scans ?is not supported with Blind scanning on the same protocol and vice versa. For example, WCDMA Top N Pilot Scan and WCDMA Blind Scan cannot run at the same time.
		//public final static int RESERVED	=	69-79	;//	PCTEL internal use only
		public final static int SECURE_DIGITAL_MEDIA_IS_BUSY=	80	;//	
		public final static int SECURE_DIGITAL_MEDIA_IS_MISSING=81	;//	
		public final static int SECURE_DIGITAL_MEDIA_IS_FULL =	82	;//	
		public final static int SECURE_DIGITAL_MEDIA_ERROR	=	83	;//	
		public final static int USB_DEVICE_IS_BUSY			=	84	;//	USB Adaptor or drive are busy
		public final static int USB_DEVICE_IS_MISSING		=	85	;//	Missing Adaptor or Missing USB drive
		public final static int USB_MEDIA_IS_FULL			=	86	;//	
		public final static int USB_DEVICE_ERROR			=	87	;//	
		public final static int INCORRECT_KERNEL_VERSION	=	88	;//	PCTEL internal use only
		public final static int OPEN_FILE_ERROR				=	89	;//	PCTEL internal use only
		//public final static int RESERVED	=	90-99	;//	
		public final static int NUMBER_OF_CLARIFY_CHANNELS_TOO_LARGE = 100	;//	The number of requested channels in the Clarify GSM Scan Request Message is greater then 150.
		//public final static int RESERVED	=	101-127	;//	
		public final static int INPUT_POWER_TOO_HIGH		=	128	;//	Warning: The input power is too high for the device may.
		public final static int POWER_SUPPLY_VOLTAGE_TOO_LOW=	129	;//	Device power supply voltage too low
		//public final static int RESERVED	=	130-132	;//	
		public final static int NO_GPS_TIME_ALIGNMENT		=	133	;//	GPS not locked or synchronization with GPS in progress. Error: CDMA/EVDO scanning receivers Warning: GSM/WCDMA/FD-LTE/TD-LTE scanning receivers
		public final static int RF_ATTENUATION_ACTIVE		=	134	;//	Warning: RF attenuation was automatically turned on.
		//public final static int RESERVED	=	135-137	;//	
		public final static int INPUT_VALUE_ADJUSTED		=	138	;//	Warning: Requested parameter value (such as PN Threshold has been adjusted to applicable value. 
		//public final static int RESERVED	=	139-142	;//	
		public final static int NO_NETWORK_TIME_ALIGNMENT	=	143	;//	Synchronization with Network in progress or lost.
		public final static int NETWORK_TIME_ALIGNMENT		=	144	;//	Synchronization with Network acquired.
		public final static int BOOT_UP_INCOMPLETE			=	145	;//	
		//public final static int RESERVED	=	146	;//	
		public final static int CARRIER_RSSI_BELOW_THRESHOLD=	147	;//	Warning: Carrier RSSI Measured below requested Carrier RSSI Threshold
		public final static int NO_CHANNELS_WITH_PILOTS		=	148	;//	Warning: No Channels detected with Pilot above requested Threshold
		public final static int IGNITION_NOT_SENSED			=	149	;//	Warning: Ignition sensor sensed no ignition
		public final static int SCANNING_RESPONSE_RATE_ADJUSTED=150	;//	Scanning response rate adjusted to USB communication
		public final static int HW_NOT_INSTALLED			=	151	;//	Hardware required for scanning receiver to execute request is not installed

	}
	
	public static class BandCodes {
		public final static int NOT_INSTALLED				=	0x0000	;//	This band is not available
		public final static int RESERVED1					=	0x0001	;//	Reserved for future use
		public final static int BAND_900_OR_1800_SIGNAL_CHECK=	0x0082	;//	Signal Check device supporting European 900/1800
		public final static int AMERICAN_CELLULAR			=	0x0100	;//	American 800 MHz Cellular, Forward Band
		public final static int REV_AMERICAN_CELLULAR		=	0x0101	;//	American 800 MHz Cellular, Reverse Band
		public final static int FULL_AMERICAN_CELLULAR		=	0x0102	;//	American 800 MHz Cellular, Full Band
		public final static int AMERICAN_PCS				=	0x0200	;//	American PCS Region (1.93 ??? 1.99 GHz), Forward
		public final static int REV_AMERICAN_PCS			=	0x0201	;//	American PCS Region (1.85 ??? 1.91 GHz), Reverse
		public final static int FULL_AMERICAN_PCS			=	0x0202	;//	American PCS Region, Full Band
		public final static int IMT_2000					=	0x0300	;//	IMT 2000, Forward Band
		public final static int REV_IMT_2000				=	0x0301	;//	IMT 2000, Reverse Band MHz)(1920-1980
		public final static int FULL_IMT_2000				=	0x0302	;//	IMT 2000, Full Band
		public final static int KOREAN_1800					=	0x0400	;//	Korean 1.8 GHz Region (1.805 ??? 1.870 GHz)
		public final static int RESERVED2					=	0x0401	;//	Reserved for expansion
		public final static int RESERVED3					=	0x0402	;//	Reserved for expansion
		public final static int JAPANESE_800_BC0_			=	0x0500	;//	Japanese 800 MHz Region (Band Class 0)
		public final static int JAPANESE_800_BC3_			=	0x0501	;//	Japanese 800 MHz Region (Band Class 3)
		public final static int RESERVED4					=	0x0502	;//	Reserved for expansion
		public final static int EUROPEAN_900				=	0x0600	;//	European 900 MHz Region (925 ??? 960 MHz), E-GSM Forward
		public final static int REV_EUROPEAN_900			=	0x0601	;//	European 900 MHz Reversed Band (880 ??? 915 MHz)
		public final static int RESERVED_FOR_EXPANSION1		=	0x0602	;//	Reserved for expansion
		public final static int B_1800MHZ					=	0x0700	;//	1800 MHz Region (1805 ??? 1880 MHz), DCS Forward
		public final static int REV_1800					=	0x0701	;//	1800 Reverse Band (1710-1785 MHz)
		public final static int RESERVED_FOR_EXPANSION2		=	0x0702	;//	Reserved for expansion
		public final static int UMTS_1700_JAPAN_DL			=	0x07F8	;//	UMTS 1700 (Japan) DL (1844.9???1879.9 MHz)
		public final static int UMTS_1700_JAPAN_UL			=	0x07F9	;//	UMTS 1700 (Japan) UL (1749.9???1784.9 MHz)
		public final static int BAND_450_MHZ				=	0x0800	;//	450 MHz Region, Forward Band
		public final static int BAND_800_EXTENDED_SMR_LOWER_EXT_850_DL=	0x0900	;//	800 MHz SMR band (Extended Frequencies from 866 to 869) ; Lower Ext 850 DL (851???869 MHz)
		public final static int LOWER_EXT_850_UL			=	0x0901	;//	Lower Ext 850 UL (806???824 MHz)
		public final static int BAND_900_SMR				=	0x0A00	;//	900 MHz SMR band
		public final static int AWS_2100					=	0x0B00	;//	2100 MHz AWS Forward Band (2110-2155 MHz)
		public final static int REV_AWS_2100				=	0x0B01	;//	2100 MHz AWS ?Reverse Band(1710-1755 MHz)
		public final static int CHINESE_2000				=	0x0C00 	;//	2000 MHz Band
		public final static int BRS_2500					=	0x0D00	;//	2.5 GHz BRS ?Band ?(Band Class 3)
		public final static int BAND_3400					=	0x0E01	;//	Band Class 5 sub band 3.4-3.6GHz
		public final static int US_700_UPPER_C_OR_D			=	0x1000	;//	American Upper 700Mhz Band (C,D Block)
		public final static int REV_US_UPPER_700_C_OR_D		=	0x1001	;//	American Reverse Upper 700MHz Band (C, D Block)
		public final static int US_700_UPPER_C				=	0x1010	;//	American Upper 700Mhz Band (C Block)
		public final static int REV_US_700_UPPER_C			=	0x1011	;//	American Reverse Upper 700Mhz Band (C Block)
		public final static int US_UPPER_700_D_OR_PS_BLOCK_DL=	0x1020	;//	US Upper 700 D/PS Block DL (758???768 MHz)
		public final static int US_UPPER_700_D_OR_PS_BLOCK_UL=	0x1021	;//	US Upper 700 D/PS Block UL (788 - 798 MHz)
		public final static int US_LOWER_700_A_OR_B_OR_C	=	0x1100	;//	American Lower 700MHz Band (A, B, ?C Block)
		public final static int REV_US_LOWER_700_A_OR_B_OR_C=	0x1101	;//	American Reverse Lower 700MHz Band (A, B, C Block)
		public final static int US_LOWER_700_B_OR_C			=	0x11F8	;//	American Lower 700MHz Band (B, ?C Block); EUTRA Band 17 (734-746MHz)
		public final static int REV_US_LOWER_700_B_OR_C		=	0x11F9	;//	American Reverse Lower 700MHz Band (B, C Block); EUTRA Band 17 (704-716MHz)
		public final static int EXT_IMT_2000				=	0x1200	;//	IMT-2000 Extension (2.6GHz) Band (2620-2690MHz)
		public final static int REV_EXT_IMT_2000			=	0x1201	;//	IMT-2000 Extension (2.6GHz) Reverse Band (2500-2570MHz)
		public final static int BAND_1500					=	0x1300	;//	1500 Band (1475.9-1500.9MHz)
		public final static int REV_1500					=	0x1301	;//	1500 Reverse Band (1427.9-1452.9MHz)
		public final static int EXT_1500					=	0x1400	;//	Extended 1500 Band (1495.9-1510.9MHz)
		public final static int REV_EXT_1500				=	0x1401	;//	Extended 1500 Reverse Band (1447.9-1462.9MHz)
		public final static int EXT_US_PCS					=	0x1500	;//	US PCS Region Forward (1.93 ??? 1.995 GHz)
		public final static int REV_EXT_US_PCS				=	0x1501	;//	US PCS Region Reverse (1.85 ??? 1.915 GHz)
		public final static int DL_800_EUTRA_6				=	0x1600	;//	Downlink EUTRA Band 6 (875-885MHz)
		public final static int UL_800_EUTRA_6				=	0x1601	;//	Uplink EUTRA Band 6 (830-840MHz)
		public final static int DL_JAPAN_LOWER_800			=	0x1700	;//	Downlink EUTRA Band 18 (860-875MHz)
		public final static int UL_JAPAN_LOWER_800			=	0x1701	;//	Uplink EUTRA Band 18 (815-830MHz)
		public final static int DL_JAPAN_UPPER_800			=	0x1800	;//	Downlink EUTRA Band 19 (875-890MHz)
		public final static int UL_JAPAN_UPPER_800			=	0x1801	;//	Uplink EUTRA Band 19 (830-845MHz)
		public final static int EUROPEAN_R_900				=	0x1900	;//	European Railways-900MHz Region (921-925 MHz)
		public final static int REV_EUROPEAN_R_900			=	0x1901	;//	European Railways-900MHz Region (876-880 MHz)
		public final static int DL_EUROPEAN_800				=	0x1A00	;//	Downlink EUTRA Band ?20 (791-821 MHz)
		public final static int UL_EUROPEAN_800				=	0x1A01	;//	Uplink EUTRA Band 20 (832-862 MHz)
		public final static int DL_1600						=	0x1B00	;//	Downlink EUTRA Band ?24 (1525-1559 MHz) Also Fwd L-Band
		public final static int UL_1600						=	0x1B01	;//	Uplink EUTRA Band 24(1626.5-1660.5 MHz) Also Rev L-Band
		public final static int DL_OR_UL_EXT_IMT_2000_CENTER_GAP=0x1C03	;//	Downlink/Uplink Ext IMT-2000 (Center Gap) EUTRA Band ?38 (2570-2620 MHz)
		public final static int DL_OR_UL_2_3_TDD			=	0x1D03	;//	Downlink/Uplink 2.3 TDD EUTRA Band ?40 (2300-2400 MHz)
		public final static int DL_OR_UL_2_5_2_6_TDD		=	0x1E03	;//	Downlink/Uplink 2.5-2.6 TDD EUTRA Band ?41?(2496-2690 MHz)
		public final static int DL_OR_UL_2_5_2_6_UPPER_HALF_TDD=0x1E33	;//	Downlink/Uplink 2.5-2.6 (Upper Half) TDD EUTRA Band ?41 Upper Half ?(2574-2690 MHz)
		public final static int DL_OR_UL_2_5_2_6_LOWER_HALF_TDD=0x1EC3	;//	Downlink/Uplink 2.5-2.6 (Lower Half) TDD EUTRA Band ?41 Lower Half ?(2496-2593 MHz)
		public final static int DL_OR_UL_1_8_TDD			=	0x1F03	;//	Downlink/Uplink 1.8 TDD EUTRA Band ?38 ?(1880-1920 MHz)
		public final static int UPPER_EXT_850_DL			=	0x2100	;//	Upper Ext 850 DL (859???894 MHz)
		public final static int UPPER_EXT_850_UL			=	0x2101	;//	Upper Ext 850 UL (814???849 MHz)
		public final static int BAND_700_DIGITAL_DIVIDEND_DL=	0x2200	;//	700 Digital Dividend DL EUTRA Band 28 (758 - 803 MHz)
		public final static int BAND_700_DIGITAL_DIVIDEND_UL=	0x2201	;//	700 Digital Dividend UL EUTRA Band 28 (703- 748 MHz)
		public final static int TDD_3_4_GHZ					=	0x2303	;//	TDD 3.4 GHz EUTRA Band 42 (3400 - 3600 MHz)
		public final static int TDD_3_6_GHZ					=	0x2403	;//	TDD 3.6 GHz EUTRA Band 43 (3600 - 3800 MHz)
		public final static int TDD_700						=	0x2503	;//	TDD 700 EUTRA Band 44 (703???803 MHz)
		public final static int TDD_1900					=	0x2603	;//	TDD 1900 EUTRA Band 33?(1900???1920 MHz)
		public final static int TDD_2000					=	0X2703	;//	TDD 2000 EUTRA Band 34?(2010???2025 MHz)
		public final static int TDD_1900_PCS_LOWER			=	0x2803	;//	TDD 1900 (PCS) Lower EUTRA Band 35?(1850???1910 MHz)
		public final static int TDD_1900_PCS_UPPER			=	0x2903	;//	TDD 1900 (PCS) Upper EUTRA Band 36?(1930???1990 MHz)
		public final static int TDD_1900_PCS_CENTER_GAP		=	0x2A03	;//	TDD 1900 (PCS) Center Gap EUTRA Band 37?(1910???1930 MHz)
		public final static int EXT_2100_AWS_DL				=	0x2B00	;//	Ext 2100 (AWS) DL EUTRA Band 10?(2110???2170 MHz)
		public final static int EXT_2100_AWS_UL				=	0x2B01	;//	Ext 2100 (AWS) UL EUTRA Band 10?(1710???1770 MHz)
		public final static int BAND_3_5_GHZ_DL				=	0x2C00	;//	3.5 GHz DL EUTRA Band 22?(3510???3590 MHz)
		public final static int BAND_3_5_GHZ_UL				=	0x2C01	;//	3.5 GHz UL EUTRA Band 22?(3410???3490 MHz)
		public final static int US_700_DL					=	0x2D00	;//	US 700 DL EUTRA Band 29?(717???728 MHz)
		public final static int R_GSM_900_DL				=	0x2E00	;//	R-GSM 900 DL (921???960 MHz)
		public final static int R_GSM_900_UL				=	0x2E01	;//	R-GSM 900 UL (876???915 MHz)
		public final static int BAND_450_DL					=	0x2F00	;//	450 DL Band Class 5 (460???470 MHz)
		public final static int BAND_450_UL					=	0x2F01	;//	450 UL Band Class 5?(450???460 MHz)
		public final static int TDD_1_4_GHZ_PROPRIETARY		=	0x3003	;//	TDD 1.4 GHz Proprietary (1447???1467 MHz)
		public final static int S_BAND_2_GHZ_DL				=	0x3100	;//	S-Band (2 GHz) DL; EUTRA Band 23?(2180???2200 MHz)
		public final static int S_BAND_2_GHZ_UL				=	0x3101	;//	S-Band (2 GHz) UL ; EUTRA Band 23?(2000???2020 MHz)
		public final static int BAND_2_3_GHZ_WCS_A_OR_B_DL	=	0x3200	;//	2.3 GHz (WCS A/B) ?DL EUTRA Band 30?(2350???2360 MHz)
		public final static int BAND_2_3_GHZ_WCS_A_OR_B_UL	=	0x3201	;//	2.3 GHz (WCS A/B) ?UL; EUTRA Band 30?(2305???2315 MHz)
		public final static int WIFI_DEVICE_BAND			=	0x3303	;//	2.4 GHz (2400-2483 MHz); 5.0 GHz (5150-5850 MHz)
		public final static int RESERVED5					=	0x7000	;//	Reserved
		public final static int BAND_3_8G_WIDEBAND1			=	0x7010	;//	3.8G Wideband (300???3800 MHz); NOTE: Use restricted to Spectrum Analysis and Enhanced Power Scan Message
		public final static int BAND_6_0G_WIDEBAND			=	0x7020	;//	6.0G Wideband (150???6000 MHz); NOTE: Use restricted to Spectrum Analysis and Enhanced Power Scan Message
		public final static int BAND_3_8G_WIDEBAND2			=	0x7030	;//	3.8G Wideband (570???3800 MHz)
	}
	
	/**
	 * Scanner界面查询ScanID，用于数据集
	 */
	public static class ScanIDShow {
		
		public final static int ScanID_RSSI                   		  = 0x0001;
		public final static int SCANID_SPECTRUM                       = 0x0002;
		public final static int SCANID_EPS                            = 0x0003;
		                                                                       
		public final static int SCANID_COLORCODE                      = 0x1001;
		public final static int SCANID_GSM_BLIND                      = 0x1002;
		                                                                       
		public final static int SCANID_CDMA_CPICH                     = 0x2001;
		public final static int SCANID_CDMA_Finger                    = 0x2002;
		public final static int SCANID_CDMA_Blind                     = 0x2003;
		                                                                       
		public final static int SCANID_WCDMA_CPICH                    = 0x3001;
		public final static int SCANID_WCDMA_Finger                   = 0x3002;
		public final static int SCANID_WCDMA_PSCH                     = 0x3003;
		public final static int SCANID_WCDMA_SSCH                     = 0x3004;
		public final static int SCANID_WCDMA_Blind                    = 0x3005;
		                                                                       
		public final static int SCANID_TDSCDMA_PCCPCH                 = 0x4001;
		public final static int SCANID_TDSCDMA_DwPTS                  = 0x4002;
		public final static int SCANID_TDSCDMA_Finger                 = 0x4003;
		public final static int SCANID_TDSCDMA_TimeSlot               = 0x4004;
		public final static int SCANID_TDSCDMA_Midamble               = 0x4005;
		public final static int SCANID_TDSCDMA_Blind                  = 0x4006;
		                                                                       
		public final static int SCANID_LTE_CellInfo                   = 0x5001;
		public final static int SCANID_LTE_PSS                        = 0x5002;
		public final static int SCANID_LTE_SSS                        = 0x5003;
		public final static int SCANID_LTE_RS                         = 0x5004;
		public final static int SCANID_LTE_PDCCH                      = 0x5005;
		public final static int SCANID_LTE_PDSCH                      = 0x5006;
		public final static int SCANID_LTE_PBCH                       = 0x5007;
		public final static int SCANID_LTE_Blind                      = 0x5008;
		public final static int SCANID_LTE_MIMO                       = 0x5009;
		public final static int SCANID_LTE_CIRPeak                    = 0x500A;
		public final static int SCANID_LTE_SubBand                    = 0x500B;
		public final static int SCANID_LTEAntennaPath                 = 0x500C;
		public final static int SCANID_LTE_MIMOTranDiv                = 0x500D;


	}
	
	
	private static String[] cdmaBandArray = new String[] { "1.25MHz", "30kHz" }; //0,1
	private static String[] wcdmaBandArray = new String[] { "5MHz", "200kHz" };  //0,1
	private static String[] tdBandArray = new String[] { "1.28MHz", "200kHz","20kHz" }; //0,1,7  //这个比较特殊20khz对应下标为7
	private static String[] gsmBandArray = new String[] { "200kHz", "30kHz"};  //0,1
	private static String[] lteBandArray = new String[] { "1.4MHz", "100kHz","3MHz","5MHz","10MHz","15MHz","20MHz"};  //0,1,2,3,4,5,6
	
	/**
	 * 生成带宽数组，这里的带宽选择很重要。根据扫频仪文档带宽相当于style，如果选错会导致扫频无结果
	 * @return
	 */
	public static String[] produceBandArray(int protocolCode){
		switch (protocolCode) {
		case ProtocolCodes.PROTOCOL_IS_95_CDMA:		//cdma与evdo带宽一样
		case ProtocolCodes.PROTOCOL_IS_2000_CDMA:	
		case ProtocolCodes.PROTOCOL_IS_856_EVDO:	
			return cdmaBandArray;
		case ProtocolCodes.PROTOCOL_3GPP_WCDMA:
			return wcdmaBandArray;
		case ProtocolCodes.PROTOCOL_TDSCDMA:	
			return tdBandArray;
		case ProtocolCodes.PROTOCOL_GSM:	
			return gsmBandArray;
		case ProtocolCodes.PROTOCOL_LTE:	
		case ProtocolCodes.PROTOCOL_TD_LTE:	
			return lteBandArray;
		default:
			return lteBandArray;
		}
		
	}
	
}
