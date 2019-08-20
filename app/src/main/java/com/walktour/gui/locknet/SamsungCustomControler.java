package com.walktour.gui.locknet;

import android.content.Context;
import android.ril.com.datangb2b.MethodManager;
import android.util.Log;

import com.b2b.rom.ISamsungDevice;
import com.walktour.control.config.Deviceinfo;

/**
 * Created by luojun on 2019/4/8.
 */

public class SamsungCustomControler extends ForceControler {
	private class ISamsungS8CustomDevice{
        private int SETBAND_TYPE_SINGLE = 0;
        private int SETBAND_TYPE_MULTI = 1;

	    private int S8_PREF_MODE_SET_GSM_ONLY 		=  16;
        private int S8_PREF_MODE_SET_CDMA_ONLY		= 22;
        private int S8_PREF_MODE_SET_WCDMA_ONLY 	= 23;
        private int S8_PREF_MODE_SET_TDSCDMA_ONLY = 35;
        private int S8_PREF_MODE_SET_HDR_ONLY 		= 36;
        private int S8_PREF_MODE_SET_LTE_ONLY 		= 37;

        private int S8_SINGLE_BAND_LOCK_GSM_FULL		= 17;
        private int S8_SINGLE_BAND_LOCK_GSM_900 = 18;
        private int S8_SINGLE_BAND_LOCK_GSM_1900 = 19;
        private int S8_SINGLE_BAND_LOCK_GSM_1800 = 20;
        private int S8_SINGLE_BAND_LOCK_GSM_850 = 21;
        private int S8_SINGLE_BAND_LOCK_WCDMA_FULL = 24;
        private int S8_SINGLE_BAND_LOCK_WCDMA_B1_IMT2K = 25;
        private int S8_SINGLE_BAND_LOCK_WCDMA_B2_PCS1900 = 26;
        private int S8_SINGLE_BAND_LOCK_WCDMA_B5_850 = 29;
        private int S8_SINGLE_BAND_LOCK_WCDMA_B8_900 = 32;
        private int S8_SINGLE_BAND_LOCK_LTE_FULL = 38;
        private int S8_SINGLE_BAND_LOCK_LTE_TDD = 39;
        private int S8_SINGLE_BAND_LOCK_LTE_FDD = 40;
        private int S8_SINGLE_BAND_LOCK_LTE_B1 = 41;
        private int S8_SINGLE_BAND_LOCK_LTE_B2 = 42;
        private int S8_SINGLE_BAND_LOCK_LTE_B3 = 43;
        private int S8_SINGLE_BAND_LOCK_LTE_B5 = 44;
        private int S8_SINGLE_BAND_LOCK_LTE_B7 = 45;
        private int S8_SINGLE_BAND_LOCK_LTE_B8 = 46;
        private int S8_SINGLE_BAND_LOCK_LTE_B12 = 47;
        private int S8_SINGLE_BAND_LOCK_LTE_B13 = 48;
        private int S8_SINGLE_BAND_LOCK_LTE_B17 = 49;
        private int S8_SINGLE_BAND_LOCK_LTE_B20 = 52;
        private int S8_SINGLE_BAND_LOCK_LTE_B25 = 53;
        private int S8_SINGLE_BAND_LOCK_LTE_B26 = 54;
        private int S8_SINGLE_BAND_LOCK_LTE_B28 = 55;
        private int S8_SINGLE_BAND_LOCK_LTE_B38 = 57;
        private int S8_SINGLE_BAND_LOCK_LTE_B39 = 58;
        private int S8_SINGLE_BAND_LOCK_LTE_B40 = 59;
        private int S8_SINGLE_BAND_LOCK_LTE_B41 = 60;

        private int S8_MULTI_BAND_LOCK_GSM_850 = 0;
        private int S8_MULTI_BAND_LOCK_GSM_900 = 1;
        private int S8_MULTI_BAND_LOCK_GSM_1800 = 2;
        private int S8_MULTI_BAND_LOCK_GSM_1900 = 3;
        private int S8_MULTI_BAND_LOCK_WCDMA_B1_IMT2K = 4;
        private int S8_MULTI_BAND_LOCK_WCDMA_B2_PCS1900 = 5;
        private int S8_MULTI_BAND_LOCK_WCDMA_B5_850 = 6;
        private int S8_MULTI_BAND_LOCK_WCDMA_B8_900 = 7;
        private int S8_MULTI_BAND_LOCK_LTE_B1 = 8;
        private int S8_MULTI_BAND_LOCK_LTE_B2  = 9;
        private int S8_MULTI_BAND_LOCK_LTE_B3  = 10;
        private int S8_MULTI_BAND_LOCK_LTE_B4  = 11;
        private int S8_MULTI_BAND_LOCK_LTE_B5  = 12;
        private int S8_MULTI_BAND_LOCK_LTE_B7  = 13;
        private int S8_MULTI_BAND_LOCK_LTE_B8  = 14;
        private int S8_MULTI_BAND_LOCK_LTE_B12 = 15;
        private int S8_MULTI_BAND_LOCK_LTE_B13 = 16;
        private int S8_MULTI_BAND_LOCK_LTE_B17 = 17;
        private int S8_MULTI_BAND_LOCK_LTE_B18 = 18;
        private int S8_MULTI_BAND_LOCK_LTE_B19 = 19;
        private int S8_MULTI_BAND_LOCK_LTE_B20 = 20;
        private int S8_MULTI_BAND_LOCK_LTE_B25 = 21;
        private int S8_MULTI_BAND_LOCK_LTE_B26 = 22;
        private int S8_MULTI_BAND_LOCK_LTE_B28 = 23;
        private int S8_MULTI_BAND_LOCK_LTE_B38 = 24;
        private int S8_MULTI_BAND_LOCK_LTE_B39 = 25;
        private int S8_MULTI_BAND_LOCK_LTE_B40 = 26;
        private int S8_MULTI_BAND_LOCK_LTE_B41 = 27;
        private int S8_MULTI_BAND_LOCK_ALL_BAND = 0xFFFFFFF;

        private int S8_FREQ_LOCK_BAND_G_FULL = 62;
        private int S8_FREQ_LOCK_BAND_E_G900 = 63;
        private int S8_FREQ_LOCK_BAND_P_G900 = 64;
        private int S8_FREQ_LOCK_BAND_G_1900 = 65;
        private int S8_FREQ_LOCK_BAND_G_1800 = 66;
        private int S8_FREQ_LOCK_BAND_G_850  = 67;

        private int S8_FREQ_LOCK_BAND_W_FULL = 68;
        private int S8_FREQ_LOCK_BAND_W_B1   = 69;
        private int S8_FREQ_LOCK_BAND_W_B2   = 70;
        private int S8_FREQ_LOCK_BAND_W_B4   = 71;
        private int S8_FREQ_LOCK_BAND_W_B5   = 72;
        private int S8_FREQ_LOCK_BAND_W_B8   = 73;

        private int S8_FREQ_LOCK_BAND_L_FULL = 74;
        private int S8_FREQ_LOCK_BAND_L_B1   = 75;
        private int S8_FREQ_LOCK_BAND_L_B2   = 76;
        private int S8_FREQ_LOCK_BAND_L_B3   = 77;
        private int S8_FREQ_LOCK_BAND_L_B5   = 79;
        private int S8_FREQ_LOCK_BAND_L_B7   = 80;
        private int S8_FREQ_LOCK_BAND_L_B8   = 81;
        private int S8_FREQ_LOCK_BAND_L_B12  = 82;
        private int S8_FREQ_LOCK_BAND_L_B13  = 83;
        private int S8_FREQ_LOCK_BAND_L_B17  = 84;
        private int S8_FREQ_LOCK_BAND_L_B20  = 85;
        private int S8_FREQ_LOCK_BAND_L_B25  = 86;
        private int S8_FREQ_LOCK_BAND_L_B26  = 87;
        private int S8_FREQ_LOCK_BAND_L_B28  = 88;
        //private int S8_FREQ_LOCK_BAND_L_B34  = ?;
        private int S8_FREQ_LOCK_BAND_L_B38  = 89;
        private int S8_FREQ_LOCK_BAND_L_B39  = 90;
        private int S8_FREQ_LOCK_BAND_L_B40  = 91;
        private int S8_FREQ_LOCK_BAND_L_B41  = 92;

        private int S8_FREQ_LOCK_BAND_W_PCS  = 34;
        private int S8_FREQ_LOCK_BAND_L_PCI  = 61;

        private MethodManager mMethodManager = null;

        public ISamsungS8CustomDevice(Context context) {
            mMethodManager = MethodManager.from(context);
        }

        public boolean lockNetwork(Context context,ForceNet networkType){
            if (null == mMethodManager)
                return false;

            switch (networkType) {
                case NET_AUTO:
                    mMethodManager.setBand(SETBAND_TYPE_MULTI, S8_MULTI_BAND_LOCK_ALL_BAND);
                    break;
                case NET_GSM:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_GSM_ONLY);
                    break;
                case NET_CDMA:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_CDMA_ONLY);
                    break;
                case NET_WCDMA:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_WCDMA_ONLY);
                    break;
                case NET_TDSCDMA:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_TDSCDMA_ONLY);
                    break;
                case NET_EVDO:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_HDR_ONLY);
                    break;
                case NET_TDD_LTE:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_SINGLE_BAND_LOCK_LTE_TDD);
                    break;
                case NET_FDD_LTE:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_SINGLE_BAND_LOCK_LTE_FDD);
                    break;
                case NET_LTE:
                    mMethodManager.setBand(SETBAND_TYPE_SINGLE, S8_PREF_MODE_SET_LTE_ONLY);
                    break;
            }
            return true;
        }

        /*若使用多选功能完成单个Band的锁定，在脱网无覆盖的情况下，会切换网络，而使用锁单个Band的功能，则不会切换网络*/
        public boolean lockBand(Context context, ForceNet netType, Band[] band){
            if (null == mMethodManager)
                return false;

            if (0 == band.length)
                return  false;

            int type = SETBAND_TYPE_SINGLE;
            if (band.length > 1)
                type = SETBAND_TYPE_MULTI;

            int networkMode_Multi = 0, networkMode_Single = -1;
            boolean isAuto = false;

            for (Band band0 : band) {
                if (isAuto)
                    break;
                switch (band0) {
                    case Auto: {
                        type = SETBAND_TYPE_SINGLE;
                        if (netType == ForceNet.NET_GSM)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_GSM_FULL;
                        else if (netType == ForceNet.NET_LTE)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_FULL;
                        else if (netType == ForceNet.NET_WCDMA)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_WCDMA_FULL;

                        isAuto = true;

                        break;
                    }
                    case G900:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_GSM_900;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_GSM_900);
                        break;
                    case G1900:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_GSM_1900;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_GSM_1900);
                        break;
                    case G1800:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_GSM_1800;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_GSM_1800);
                        break;
                    case G850:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_GSM_850;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_GSM_850);
                        break;
                    case W2100:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_WCDMA_B1_IMT2K;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_WCDMA_B1_IMT2K);
                        break;
                    case W1900:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_WCDMA_B2_PCS1900;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_WCDMA_B2_PCS1900);
                        break;
                    case W850:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_WCDMA_B5_850;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_WCDMA_B5_850);
                        break;
                    case W900:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_WCDMA_B8_900;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_WCDMA_B8_900);
                        break;
                    case L1:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B1;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B1);
                        break;
                    case L2:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B2;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B2);
                        break;
                    case L3:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B3;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B3);
                        break;
                    case L4: //单频点中没有
                        if (SETBAND_TYPE_MULTI == type)
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B4);
                        break;
                    case L5:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B5;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B5);
                        break;
                    case L7:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B7;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B7);
                        break;
                    case L8:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B8;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B8);
                        break;
                    case L12:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B12;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B12);
                        break;
                    case L13:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B13;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B13);
                        break;
                    case L17:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B17;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B17);
                        break;
                    case L18:
                        if (SETBAND_TYPE_MULTI == type)
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B18);
                        break;
                    case L19:
                        if (SETBAND_TYPE_MULTI == type)
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B19);
                        break;
                    case L20:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B20;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B20);
                        break;
                    case L25:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B25;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B25);
                        break;
                    case L26:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B26;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B26);
                        break;
                    case L28:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B28;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B28);
                        break;
                    case L38:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B38;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B38);
                        break;
                    case L39:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B39;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B39);
                        break;
                    case L40:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B40;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B40);
                        break;
                    case L41:
                        if (SETBAND_TYPE_SINGLE == type)
                            networkMode_Single = S8_SINGLE_BAND_LOCK_LTE_B41;
                        else
                            networkMode_Multi += (1 << S8_MULTI_BAND_LOCK_LTE_B41);
                        break;
                }
            }

            if ((SETBAND_TYPE_SINGLE == type) && (networkMode_Single >= 0))
                mMethodManager.setBand(SETBAND_TYPE_SINGLE, networkMode_Single);
            else if ((SETBAND_TYPE_MULTI == type) && (networkMode_Multi >= 0))
                mMethodManager.setBand(SETBAND_TYPE_MULTI, networkMode_Multi);

            return true;
        }

        public boolean lockFrequency(Context context, ForceNet netType, String... args){
            if  (args.length < 2)
                return false;
            if (null == mMethodManager)
                return false;

            String strBand = args[0];
            int iFreq = Integer.parseInt(args[1]);

            switch (netType){
                case NET_GSM:{
                    if (strBand.equals(Band.Auto.des)) {
                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_G_FULL, iFreq);
                    } else	if (strBand.equals(Band.G900.des)) {
                        boolean bVFreq = (((iFreq >= 0) && (iFreq <= 124)) || ((iFreq >= 975) && (iFreq <= 1023)));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_P_G900, iFreq);
                    } else if (strBand.equals(Band.GE900.des)) {
                        boolean bVFreq = ((iFreq >= 1) && (iFreq <= 124));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_E_G900, iFreq);
                    } else if (strBand.equals(Band.G1900.des)) {
                        boolean bVFreq = ((iFreq >= 512) && (iFreq <= 810));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_G_1900, iFreq);
                    } else if (strBand.equals(Band.G1800.des)) {
                        boolean bVFreq = ((iFreq >= 512) && (iFreq <= 885));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_G_1800, iFreq);
                    } else if (strBand.equals(Band.G850.des)) {
                        boolean bVFreq = ((iFreq >= 128) && (iFreq <= 251));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_G_850, iFreq);
                    }
                    break;
                }
                case NET_WCDMA: {
                    if (strBand.equals(Band.Auto.des)) {
                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_FULL, iFreq);
                    } else	if (strBand.equals(Band.W2100.des)) {
                        boolean bVFreq = ((iFreq >= 10562) && (iFreq <= 10838));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_B1, iFreq);
                    } else	if (strBand.equals(Band.W1900.des)) {
                        //(9662~9938)  412,438,462,487,512,537,562,587,612,637,	662,687
                        boolean bVFreq = (((iFreq >= 9662) && (iFreq <= 9938)) || ((iFreq >= 412) && (iFreq <= 687)));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_B2, iFreq);
                    } else	if (strBand.equals(Band.W1700.des)) {
                        //(1537,1738) 	1887,1912,1937,1962,1987,2012,2037,2062, 2087
                        boolean bVFreq = (((iFreq >= 1537) && (iFreq <= 1738)) || ((iFreq >= 1887) && (iFreq <= 2087)));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_B4, iFreq);
                    } else	if (strBand.equals(Band.W850.des)) {
                        //(4357,4458)   1007,1012,1032,1037,1062,1087
                        boolean bVFreq = (((iFreq >= 4357) && (iFreq <= 4458)) || ((iFreq >= 1007) && (iFreq <= 1087)));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_B5, iFreq);
                    }else	if (strBand.equals(Band.W900.des)) {
                        //(4357,4458)   1007,1012,1032,1037,1062,1087
                        boolean bVFreq = ((iFreq >= 2937) && (iFreq <= 3088));
                        if (false == bVFreq)
                            return false;

                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_W_B8, iFreq);
                    }

                    break;
                }
                case NET_LTE: {
                    if (strBand.equals(Band.Auto.des)) {
                        mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_FULL, iFreq);
                    } else	{
                        int iBand = Integer.parseInt(strBand);
                        switch (iBand) {
                            case 1: {
                                boolean bVFreq = ((iFreq >= 1) && (iFreq <= 599));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B1, iFreq);
                                break;
                            }
                            case 2: {
                                boolean bVFreq = ((iFreq >= 600) && (iFreq <= 1199));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B2, iFreq);
                                break;
                            }
                            case 3: {
                                boolean bVFreq = ((iFreq >= 1200) && (iFreq <= 1949));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B3, iFreq);
                                break;
                            }
                            case 5: {
                                boolean bVFreq = ((iFreq >= 2400) && (iFreq <= 2649));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B5, iFreq);
                                break;
                            }
                            case 7: {
                                boolean bVFreq = ((iFreq >= 2750) && (iFreq <= 3449));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B7, iFreq);
                                break;
                            }
                            case 8: {
                                boolean bVFreq = ((iFreq >= 3450) && (iFreq <= 3799));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B8, iFreq);
                                break;
                            }
                            case 12: {
                                boolean bVFreq = ((iFreq >= 5000) && (iFreq <= 5179));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B12, iFreq);
                                break;
                            }
                            case 13: {
                                boolean bVFreq = ((iFreq >= 5180) && (iFreq <= 5279));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B13, iFreq);
                                break;
                            }
                            case 17: {
                                boolean bVFreq = ((iFreq >= 5730) && (iFreq <= 5849));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B17, iFreq);
                                break;
                            }
                            case 20: {
                                boolean bVFreq = ((iFreq >= 6150) && (iFreq <= 6449));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B20, iFreq);
                                break;
                            }
                            case 25: {
                                boolean bVFreq = ((iFreq >= 8040) && (iFreq <= 8689));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B25, iFreq);
                                break;
                            }
                            case 26: {
                                boolean bVFreq = ((iFreq >= 8690) && (iFreq <= 9039));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B26, iFreq);
                                break;
                            }
                            case 28: {
                                boolean bVFreq = ((iFreq >= 9210) && (iFreq <= 9659));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B28, iFreq);
                                break;
                            }
                            case 38: {
                                boolean bVFreq = ((iFreq >= 37750) && (iFreq <= 38249));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B38, iFreq);
                                break;
                            }
                            case 39: {
                                boolean bVFreq = ((iFreq >= 38250) && (iFreq <= 38649));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B39, iFreq);
                                break;
                            }
                            case 40: {
                                boolean bVFreq = ((iFreq >= 38650) && (iFreq <= 39649));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B40, iFreq);
                                break;
                            }
                            case 41: {
                                boolean bVFreq = ((iFreq >= 39650) && (iFreq <= 41589));
                                if (false == bVFreq)
                                    return false;

                                mMethodManager.setFrequency(S8_FREQ_LOCK_BAND_L_B41, iFreq);
                                break;
                            }
                            default:
                                return false;
                        }
                    }
                    break;
                }
            }

            return true;
        }

        public boolean lockCell(Context context, ForceNet netType, String... args){
            if (null == mMethodManager)
                return false;
            if (args.length < 3)
                return false;

            String strBand = args[0];
            int iFreq = Integer.parseInt(args[1]);
            int iCellNumber = Integer.parseInt(args[2]);
            switch (netType) {
                case NET_WCDMA:{
                    if (strBand.equals(Band.Auto.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_FULL, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    } else if (strBand.equals(Band.W2100.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_B1, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    } else if (strBand.equals(Band.W1900.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_B2, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    } else if (strBand.equals(Band.W1700.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_B4, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    } else if (strBand.equals(Band.W850.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_B5, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    } else if (strBand.equals(Band.W900.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_W_B8, iFreq, S8_FREQ_LOCK_BAND_W_PCS, iCellNumber);
                    }
                    break;
                }
                case NET_LTE:{
                    if (strBand.equals(Band.Auto.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_FULL, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L1.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B1, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L2.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B2, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L3.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B3, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L5.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B5, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L7.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B7, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L8.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B8, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L12.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B12, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L13.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B13, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L17.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B17, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L20.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B20, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L25.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B25, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L26.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B26, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L28.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B28, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L38.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B38, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L39.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B39, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L40.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B40, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    } else if (strBand.equals(Band.L41.des)) {
                        mMethodManager.setQuarters(S8_FREQ_LOCK_BAND_L_B41, iFreq, S8_FREQ_LOCK_BAND_L_PCI, iCellNumber);
                    }
                    break;
                }
                default:
                    return false;
            }

            return  true;
        }

        public boolean unlockFrequency(Context context, ForceNet networkType) {
            if (null == mMethodManager)
                return false;

            mMethodManager.setLockReset();

            return true;
        }

        public boolean unlockCell(Context context, ForceNet networkType) {
            if (null == mMethodManager)
                return false;

            mMethodManager.setLockReset();

            return true;
        }
    }

    private class ISamsungS9CustomDevice {
        /*SAMSUNG S9 Cumstom Start*/
        private static final int S9_NETWORK_MODE_WCDMA_PREF = 0; 						// GSM/WCDMA (WCDMA preferred)
        private static final int S9_NETWORK_MODE_GSM_ONLY = 1; 							//	GSM only
        private static final int S9_NETWORK_MODE_WCDMA_ONLY = 2; 						//	WCDMA only
        private static final int S9_NETWORK_MODE_GSM_UMTS = 3; 							//	GSM/WCDMA (auto mode, according to PRL)
        private static final int S9_NETWORK_MODE_CDMA = 4; 								//	CDMA and EvDo (auto mode, according to PRL)
        private static final int S9_NETWORK_MODE_CDMA_NO_EVDO = 5; 						//	CDMA only
        private static final int S9_NETWORK_MODE_EVDO_NO_CDMA = 6; 						//	EvDo only
        private static final int S9_NETWORK_MODE_GLOBAL = 7; 							//	GSM/WCDMA, CDMA, and EvDo (auto mode, according to PRL)
        private static final int S9_NETWORK_MODE_LTE_CDMA_EVDO = 8; 					//	LTE, CDMA and EvDo
        private static final int S9_NETWORK_MODE_LTE_GSM_WCDMA = 9; 					//	LTE, GSM/WCDMA
        private static final int S9_NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 10; 			//	LTE, CDMA, EvDo, GSM/WCDMA
        private static final int S9_NETWORK_MODE_LTE_ONLY = 11; 						//	LTE Only mode.
        private static final int S9_NETWORK_MODE_LTE_WCDMA = 12; 						//	LTE/WCDMA
        private static final int S9_NETWORK_MODE_TDSCDMA_ONLY = 13; 					//	TD-SCDMA only
        private static final int S9_NETWORK_MODE_TDSCDMA_WCDMA = 14; 					//	TD-SCDMA and WCDMA
        private static final int S9_NETWORK_MODE_LTE_TDSCDMA = 15; 						//	TD-SCDMA and LTE
        private static final int S9_NETWORK_MODE_TDSCDMA_GSM = 16; 						//	TD-SCDMA and GSM
        private static final int S9_NETWORK_MODE_LTE_TDSCDMA_GSM = 17; 					//	TD-SCDMA,GSM and LTE
        private static final int S9_dNETWORK_MODE_TDSCDMA_GSM_WCDMA = 18; 				//	TD-SCDMA, GSM/WCDMA
        private static final int S9_NETWORK_MODE_LTE_TDSCDMA_WCDMA = 19; 				//	TD-SCDMA, WCDMA and LTE
        private static final int S9_NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA = 20; 			//	TD-SCDMA, GSM/WCDMA and LTE
        private static final int S9_NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 21; 		//	TD-SCDMA,EvDo,CDMA,GSM/WCDMA
        private static final int S9_NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22; 	//	TD-SCDMA/LTE/GSM/WCDMA, CDMA, and EvDo
        /*SAMSUNG S9 Cumstom End*/

	    private ISamsungDevice mISamsungDevice = null;
	    public ISamsungS9CustomDevice(Context context) {
            mISamsungDevice = new ISamsungDevice(context);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean lockNetwork(Context context, ForceNet networkType){
	        if (null == mISamsungDevice)
	            return false;

            int iPhoneId = 0, iNetworkType = -1;

            switch (networkType) {
                case NET_AUTO:
                    iNetworkType = S9_NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA;
                    break;
                case NET_GSM:
                    iNetworkType = S9_NETWORK_MODE_GSM_ONLY;
                    break;
                case NET_WCDMA:
                    iNetworkType = S9_NETWORK_MODE_WCDMA_ONLY;
                    break;
                case NET_GSM_WCDMA:
                    iNetworkType = S9_NETWORK_MODE_GSM_UMTS;
                    break;
                case NET_TDSCDMA:
                    iNetworkType = S9_NETWORK_MODE_TDSCDMA_ONLY;
                    break;
                case NET_GSM_TDSCDMA:
                    iNetworkType = S9_NETWORK_MODE_TDSCDMA_GSM;
                    break;
                case NET_WCDMA_LTE:
                    iNetworkType = S9_NETWORK_MODE_LTE_WCDMA;
                    break;
                case NET_TDSCDMA_LTE:
                    iNetworkType = S9_NETWORK_MODE_LTE_TDSCDMA;
                    break;
                case NET_CDMA:
                    iNetworkType = S9_NETWORK_MODE_CDMA_NO_EVDO;
                    break;
                case NET_EVDO:
                    iNetworkType = S9_NETWORK_MODE_EVDO_NO_CDMA;
                    break;
                case NET_CDMA_EVDO:
                    iNetworkType = S9_NETWORK_MODE_CDMA;
                    break;
                case NET_LTE:
                case NET_FDD_LTE:
                case NET_TDD_LTE:
                    iNetworkType = S9_NETWORK_MODE_LTE_ONLY;
                    break;
            }

            boolean bResult = mISamsungDevice.setPreferredNetworkType(iPhoneId, iNetworkType);

            mISamsungDevice.Release();

            return bResult;
        }

        public boolean lockBand(Context context, ForceNet netType, Band[] band) {
            if (null == mISamsungDevice)
                return false;

            long iband_pref = 0, itdscdma_band_pref = 0, ilte_band_pref = 0;
            boolean isAuto = false;
            for (Band b : band) {
                if (isAuto)
                    break;

                switch (b) {
                    case Auto:
                        isAuto = true;
                        break;
                    case G850:
                        iband_pref += ((long) 1 << 19);
                        break;
                    case G900:
                        iband_pref += ((long) 1 << 8);
                        iband_pref += ((long) 1 << 9);
                        iband_pref += ((long) 1 << 20);
                        break;
                    case G1800:
                        iband_pref += ((long) 1 << 7);
                        break;
                    case G1900:
                        iband_pref += ((long) 1 << 21);
                        break;
                    case G450:
                        iband_pref += ((long) 1 << 16);
                        break;
                    case G480:
                        iband_pref += ((long) 1 << 17);
                        break;
                    case G750:
                        iband_pref += ((long) 1 << 18);
                        break;
                    case G9000:
                        iband_pref += ((long) 1 << 20);
                        break;

                    case W2100:
                        iband_pref += ((long) 1 << 22);
                        break;
                    case W1900:
                        iband_pref += ((long) 1 << 23);
                        break;
                    case W1800:
                        iband_pref += ((long) 1 << 24);
                        break;
                    case W1700:
                        iband_pref += ((long) 1 << 25);		//WCDMA 1700(U.S.)
                        iband_pref += ((long) 1 << 50);	    //WCDMA 1700(Japan.)
                        break;
                    case W850:
                        iband_pref += ((long) 1 << 26);
                        break;
                    case W800:
                        iband_pref += ((long) 1 << 27);
                        break;
                    case W2600:
                        iband_pref += ((long) 1 << 48);
                        break;
                    case W900:
                        iband_pref += ((long) 1 << 49);
                        break;
                    case TBandA:
                        itdscdma_band_pref += ((long)0x01);
                        break;
                    case TBandF:
                        itdscdma_band_pref += ((long)0x20);
                        break;
                    default:
                        if (b.name().startsWith("L")) {
                            String strBand = b.name();
                            strBand = strBand.substring(strBand.indexOf("L") + 1);
                            int iBv = Integer.parseInt(strBand);
                            switch (iBv) {
                                case 15:
                                case 16:
                                case 22:
                                case 42:
                                case 44:
                                case 45:
                                    ilte_band_pref += 0; //不支持
                                    break;
                                default: {
                                    if ((iBv >= 49) && (iBv <= 64))
                                        ilte_band_pref += 0; //不支持
                                    else
                                        ilte_band_pref += ((long) 1 << (iBv - 1));
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
            //String preIf = "1";
            String stringBand = "0000000000000000000000000000000000000000000000000000000000000000" +
                    "0000000000000000000000000000000000000000000000000000000000000000" +
                    "0000000000000000000000000000000000000000000000000000000000000000";
            if (isAuto) {
                stringBand = "1111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111" +
                        "0000000000000000111001011111111111111111110111110011111111111111"; //lte_band
            } else {
                StringBuilder stringBandBuilder = new StringBuilder(stringBand);
                int iLength = Long.SIZE;
                for (int iLp = 0; iLp < iLength; iLp++) {
                    if (0x01 == ((iband_pref >> iLp) & 0x01)) {
                        stringBandBuilder.setCharAt(64 - (iLp + 1), '1');
                    }
                    if (0x01 == ((itdscdma_band_pref >> iLp) & 0x01)) {
                        stringBandBuilder.setCharAt(64 * 2 - (iLp + 1), '1');
                    }
                    if (0x01 == ((ilte_band_pref >> iLp) & 0x01)) {
                        stringBandBuilder.setCharAt(64 * 3 - (iLp + 1), '1');
                    }
                }
                stringBand = stringBandBuilder.toString();
            }

            boolean bResult = mISamsungDevice.sendMiscInfo(0, 20, stringBand);

            Log.e("ISamsungDevice", "lock band result" + Boolean.toString(bResult));

            mISamsungDevice.Release();

            return bResult;
        }

        public boolean lockFrequency(Context context, ForceNet netType, String... args) {
            if (null == mISamsungDevice)
                return false;

            boolean bResult = false;
            int iPhoneID = 0, iFreq = Integer.parseInt(args[1]);

            if (netType == ForceNet.NET_GSM) {
                bResult = mISamsungDevice.lockGsmEarfcn(iPhoneID, iFreq);
            } else if (netType == ForceNet.NET_WCDMA) {
                bResult = mISamsungDevice.lockWcdmaUArfcn(iPhoneID, iFreq);
            } else if (netType == ForceNet.NET_LTE) {
                bResult = mISamsungDevice.lockLteEarfcn(iPhoneID, iFreq);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.modemReboot();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.Release();

            return bResult;
        }

        public boolean lockCell(Context context, ForceNet netType, String... args) {
            if (null == mISamsungDevice)
                return false;

            boolean bResult = false;
            int iPhoneID = 0;
            int iFreq = Integer.parseInt(args[1]);
            int iCellNumber = Integer.parseInt(args[2]);

            if (netType == ForceNet.NET_WCDMA) {
                bResult = mISamsungDevice.lockWcdmaPsc(iPhoneID, iFreq, iCellNumber);
            } else if (netType == ForceNet.NET_LTE) {
                bResult = mISamsungDevice.lockLtePci(iPhoneID, iFreq, iCellNumber);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.modemReboot();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.Release();

            return bResult;
        }

        public boolean unlockFrequency(Context context, ForceNet networkType) {
            if (null == mISamsungDevice)
                return false;

            boolean bResult = false;
            int iPhoneID = 0;
            if (networkType == ForceNet.NET_GSM) {
                bResult = mISamsungDevice.clearGsmEarfcn(iPhoneID);
            } else if (networkType == ForceNet.NET_WCDMA) {
                bResult = mISamsungDevice.clearWcdmaUarfcn(iPhoneID);
            } else if (networkType == ForceNet.NET_LTE) {
                bResult = mISamsungDevice.clearLteEarfcn(iPhoneID);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.modemReboot();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.Release();

            return bResult;
        }

        private boolean unlockCell(Context context, ForceNet networkType) {
            if (null == mISamsungDevice)
                return false;

            boolean bResult = false;
            int iPhoneID = 0;
            if (networkType == ForceNet.NET_WCDMA) {
                bResult = mISamsungDevice.unlockWcdmaPsc(iPhoneID);
            } else if (networkType == ForceNet.NET_LTE) {
                bResult = mISamsungDevice.clearLockLtePci(iPhoneID);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.modemReboot();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mISamsungDevice.Release();

            return bResult;
        }

        private boolean airPlaneMode(Context context, boolean bFlag) {
            if (null == mISamsungDevice)
                return false;

            boolean bResult = mISamsungDevice.setRadioPower(0, !bFlag);

            mISamsungDevice.Release();

            return  bResult;
        }
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void release() {

    }

    @Override
    public boolean lockNetwork(ForceNet networkType) {
        return false;
    }

    @Override
    public boolean unLockAll(ForceNet forceNets) {
        return false;
    }

    @Override
    public boolean queryBand(ForceNet netType) {
        return false;
    }

    @Override
    public boolean queryFrequency(ForceNet netType) {
        return false;
    }

    @Override
    public boolean queryCell(ForceNet netType) {
        return false;
    }

    @Override
    public boolean lockBand(ForceNet netType, String arg) {
        return false;
    }

    @Override
    public boolean lockBand(ForceNet netType, Band[] band) {
        return false;
    }


    @Override
    public boolean campCell(ForceNet netType, String arg1, String arg2) {
        return false;
    }

    @Override
    public boolean setAirplaneModeSwitch(Context context, boolean flag) {
	    Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS9CustomRom())
            return  airplane_S9CustomRom(context, flag);

        return false;
    }

    @Override
    public boolean setVolteSwitch(Context context, boolean flag) {
        return false;
    }

    @Override
    public boolean setScrambleState(Context context, boolean flag) {
        return false;
    }

    @Override
    public boolean setAPN(Context context, String arg) {
        return false;
    }

    @Override
    public void makeVideoCall(Context context, String number) {

    }

    @Override
    public boolean lockNetwork(Context context, ForceNet networkType) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
            return lockNetwork_S8CustomRom(context, networkType);

        if (deviceinfo.isS9CustomRom())
            return  lockNetwork_S9CustomRom(context, networkType);

        return false;
    }

    @Override
    public boolean lockBand(Context context, ForceNet netType, Band[] band) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
           return lockBand_S8CustomRom(context, netType, band);

        if (deviceinfo.isS9CustomRom())
            return lockBand_S9CustomRom(context, netType, band);

        return false;
    }

    @Override
    public boolean lockFrequency(Context context, ForceNet netType, String... args) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
            return lockFrequency_S8CustomRom(context, netType, args);

        if (deviceinfo.isS9CustomRom())
            return  lockFrequency_S9CustomRom(context, netType, args);

        return false;
    }

    @Override
    public boolean lockCell(Context context, ForceNet netType, String... args) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
           return lockCell_S8CustomRom(context, netType, args);

        if (deviceinfo.isS9CustomRom())
            return  lockCell_S9CustomRom(context, netType, args);

        return  false;
    }

    @Override
    public boolean unlockFrequency(Context context, ForceNet networkType) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
            return unlockFrequency_S8CustomRom(context, networkType);

        if (deviceinfo.isS9CustomRom())
            return unlockFrequency_S9CustomRom(context, networkType);

        return false;
    }

    @Override
    public boolean unlockCell(Context context, ForceNet networkType) {
        Deviceinfo deviceinfo = Deviceinfo.getInstance();

        if (deviceinfo.isS8CustomRom())
            return unlockCell_S8CustomRom(context, networkType);

        if (deviceinfo.isS9CustomRom())
            return unlockCell_S9CustomRom(context, networkType);

        return false;
    }

   //.............//

    private boolean lockNetwork_S8CustomRom(Context context,ForceNet networkType){
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.lockNetwork(context, networkType);
    }

    private boolean lockNetwork_S9CustomRom(Context context,ForceNet networkType){
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.lockNetwork(context, networkType);
    }

    private boolean lockBand_S8CustomRom(Context context, ForceNet netType, Band[] band) {
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.lockBand(context, netType, band);
    }

    private boolean lockBand_S9CustomRom(Context context, ForceNet netType, Band[] band) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.lockBand(context, netType, band);
    }

    private boolean lockFrequency_S8CustomRom(Context context, ForceNet netType, String... args) {
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.lockFrequency(context, netType, args);
    }

    private boolean lockFrequency_S9CustomRom(Context context, ForceNet netType, String... args) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.lockFrequency(context, netType, args);
    }

    private boolean lockCell_S8CustomRom(Context context, ForceNet netType, String... args) {
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.lockCell(context, netType, args);
    }

    private boolean lockCell_S9CustomRom(Context context, ForceNet netType, String... args) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.lockCell(context, netType, args);
    }

    private boolean unlockFrequency_S8CustomRom(Context context, ForceNet networkType) {
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.unlockFrequency(context, networkType);

    }

    private boolean unlockFrequency_S9CustomRom(Context context, ForceNet networkType) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.unlockFrequency(context, networkType);

    }

    private boolean unlockCell_S8CustomRom(Context context, ForceNet networkType) {
        ISamsungS8CustomDevice iSamsungS8CustomDevice = new ISamsungS8CustomDevice(context);

        if (null == iSamsungS8CustomDevice)
            return false;

        return iSamsungS8CustomDevice.unlockCell(context, networkType);

    }

    private boolean unlockCell_S9CustomRom(Context context, ForceNet networkType) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.unlockCell(context, networkType);

    }

    private boolean airplane_S9CustomRom(Context context, boolean flag) {
        ISamsungS9CustomDevice iSamsungS9CustomDevice = new ISamsungS9CustomDevice(context);

        if (null == iSamsungS9CustomDevice)
            return false;

        return iSamsungS9CustomDevice.airPlaneMode(context, flag);
    }
}
