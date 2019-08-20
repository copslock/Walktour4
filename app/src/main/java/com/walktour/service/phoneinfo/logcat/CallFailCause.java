package com.walktour.service.phoneinfo.logcat;

import java.util.HashMap;

/**
 * 呼叫失败原因
 * 
 * @author jianchao.wang
 *
 */
public class CallFailCause {

	@SuppressWarnings("serial")
	private static HashMap<Integer, String> callCause = new HashMap<Integer, String>() {
		{
			put(-1, "");
			put(65535, "error unspecified (65535)");

			put(1, "unassigned (unallocated) number (1)");
			put(3, "no route to destination (3)");
			put(6, "channel unacceptable (6)");
			put(8, "operator determined barring (8)");
			put(16, "normal call clearing (16)");
			put(17, "user busy (17)");
			put(18, "no user responding (18)");
			put(19, "user alerting/no answer (19)");
			put(21, "call rejected (21)");
			put(22, "number changed (22)");
			put(26, "non-selected user clearing (26)");
			put(27, "destination out of order (27)");
			put(28, "invalid number format (incomplete number) (28)");
			put(29, "facility rejected (29)");
			put(30, "response to STATUS ENQUIRY (30)");
			put(31, "normal/unspecified (31)");
			put(34, "no circuit/channel available (34)");
			put(38, "network out of order (38)");
			put(41, "temporary failure (41)");
			put(42, "switching equipment congestion (42)");
			put(43, "access information discarded (43)");
			put(44, "requested circuit/channel not available (44)");
			put(47, "resource unavailable/unspecified (47)");
			put(49, "quality of service unavailable (49)");
			put(50, "requested facility not subscribed (50)");
			put(55, "incoming calls barred within the CUG (55)");
			put(57, "bearer capability not authorized (57)");
			put(58, "bearer capability not presently available (58)");
			put(63, "service or option not available/unspecified (63)");
			put(65, "bearer service not implemented (65)");
			put(68, "ACM equal to or greater than ACMmax (68)");
			put(69, "requested facility not implemented (69)");
			put(70, "only restricted digital information bearer capability is available (70)");
			put(79, "service or option not implemented/unspecified (79)");
			put(81, "invalid transaction identifier value (81)");
			put(87, "user not member of CUG (87)");
			put(88, "incompatible destination (88)");
			put(91, "invalid transit network selection (91)");
			put(95, "semantically incorrect message (95)");
			put(96, "invalid mandatory information (96)");
			put(97, "message type non-existent or not implemented (97)");
			put(98, "message type not compatible with protocol state (98)");
			put(99, "information element non-existent or not implemented (99)");
			put(100, "conditional IE error (100)");
			put(101, "message not compatible with protocol state (101)");
			put(102, "recovery on timer expiry (102)");
			put(111, "protocol error/unspecified (111)");
			put(127, "interworking/unspecified (127)");
		}
	};

	public static String getCauseByCode(int code) {
		return callCause.get(code);
	}

}