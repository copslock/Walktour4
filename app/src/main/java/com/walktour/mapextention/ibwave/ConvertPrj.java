package com.walktour.mapextention.ibwave;

import java.util.Vector;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [RCS Client V100R001C03, 2013-3-18]
 */
public class ConvertPrj {

	// MapInfo栅格tab投影ID
	interface tkMasterMapInfoPrjID {
		public static final int idLonLat = 1, // 经度纬度--正轴等距圆柱投影
				idCylindricalEqualSpace = 2, // 圆柱等面积
				idLamBertConformalConic = 3, // Lambert等角圆锥
				idLamBertDirectionArea = 4, // Lambert方位等面积（仅面向极地）
				idZenithalEquidistant = 5, // 方位等距（仅面向极地方向）
				idConicalEquidistance = 6, // 等距圆锥，也称为简单圆锥
				idHotineObliqueMercator = 7, // Hotine 斜墨卡托
				idUniversalTransMercator = 8, // 横向墨卡托（也称为Gauss-Krugger）
				idAlbersConicalEqualArea = 9, // Albers等面积圆锥
				idMercator = 10, // 墨卡托
				idMillerCylinder = 11, // Miller圆柱
				idRobinson = 12, // Robinson
				idMollweide = 13, // Mollweide
				idEckertIV = 14, // Eckert IV
				idEckertVI = 15, // Eckert VI
				idSinusoid = 16, // 正弦曲线
				idGall = 17, // Gall
				idNewZealandGrid = 18, // 新西兰地图网格
				idLambertConic = 19, // Lambert等角圆锥（修改用于比利时，1972）
				idStereoGraphy = 20, // 立体平画法
				idUTMercator = 21, // 横向墨卡托（修改用于丹麦系统 34 Jylland-Fyn）
				idUTMSjaelland = 22, // 横向墨卡托（修改用于丹麦系统 34 Sjaelland）
				idUTMBornholm = 23, // 横向墨卡托（修改用于丹麦系统 34/35 Bornholm）
				idUTMKKJ = 24, // 横向墨卡托（修改用于芬兰系统KKJ）
				idSwissObliqueMercator = 25, // 瑞士斜墨卡托
				idRegionMercator = 26, // 地区墨卡托
				idPolyConic = 27, // 多圆锥
				idZenithalEquidistantAll = 28;// 方位等距（所有原始纬度）
	};

	// MapInfo栅格tab投影基准面ID
	interface tkMasterMapInfoPrjDatumID {
		public static final int idAdidan = 1, // Clarke 1880 Ethiopia, Senegal,
																					// Sudan
				idAfgooye = 2, // Krassovsky Somalia
				idAinElAbd1970 = 3, // International Bahrain Island
				idAnna1Astro1965 = 4, // Austalian National Cocos Islands
				idArc1950 = 5, // Clarke1880
												// Botswana,Lesotho,Malawi,Swaziland,Zaire,Zambia,Zimbabwe
				idArc1960 = 6, // Clarke1880 Kenya,Tanzania
				idAscensionIsland1958 = 7, // International Ascension Island
				idAstroBeaconE = 8, // International IwoJima Island
				idAstroB4SorolAtoll = 9, // International tern Island
				idAstroDos714 = 10, // International St,Helena Island
				idAstronomicStation1952 = 11, // International Marcus Island
				idAGD66 = 12, // Australian National Australia and Tasmania island
				idAGD84 = 13, // Australian National Australia and Tasmania island
				idBellevueIGN = 14, // International Efate and Erromango islands
				idBermuda1957 = 15, // Clarke1866 bermuda islands
				idBogotaObservatory = 16, // International Colombia
				idCampoInchauspe = 17, // International Argetina
				idCantonAstro1966 = 18, // International Phoenix islands
				idCape = 19, // Clarke1880 South Africa
				idCapeCanaveral = 20, // Clarke1866 Florida and Bahama islands
				idCarthage = 21, // Clarke1880 Tunisia
				idChatham1971 = 22, // International Chatham Island(New Zealand)
				idChuaAstro = 23, // International Paraguay
				idCorregoAlegre = 24, // International Brazil
				idDjakarta = 25, // Bessel1841 Sumatra Island(Indonesia)
				idDOS1968 = 26, // International Gizo Island(New Georgia Islands)
				idEasterIsland1967 = 27, // International Easter Islands
				idEuropean1950 = 28, // International
															// Austria,Belgium,Denmark,Finland,France,Germany,Gibraltar,Greeece,italy
				idEuropean1979 = 29, // International
															// Austria,Finland,Netherlands,Norway,Spain,Sweden,Switzerland
				idGandajikaBase = 30, // International Republic of Maldives
				idNZGD49 = 31, // International New Zealand
				idGRS1967 = 32, // GRS 67 Worldwide
				idGRS1980 = 33, // GRS80 Worldwide
				idGuam1963 = 34, // Clarke1866 Guam island
				idGUX1Astro = 35, // International guadalcanal island
				idHitoXVIII1963 = 36, // International South Chile(near 53pS)
				idHiorsey1955 = 37, // International Iceland
				idHongKong1963 = 38, // International Hong Kong
				idHuTzuShan = 39, // International Taiwan
				idIndian = 40, // Everest(India1830) Thailand and Vietnam
				idIndianB = 41, // Everest(India1830) bangladesh,India,nepal
				idIreland1965 = 42, // Modified Airy Ireland
				idISTS061Astro1969 = 43, // International Diego Garcia
				idJohnStonIsland1961 = 44, // International johnston Island
				idKandawala = 45, // Everest(India1830) Sri lanka
				idkerguelenIsland = 46, // International Kerguelen island
				idkertau1948 = 47, // Everest(W.Malaysia and Singapore 1948) West
														// malysia and Singapore
				idLC5Astro = 48, // Clarke1866 Cayman Brac Island
				idLiberia1964 = 49, // Clarke1880 Liberia
				idLuzon = 50, // Clarke1866 Philippines(Excluding Mindanao Island)
				idLuzonMindanao = 51, // Clarke1866 Mindanao Island
				idMahe1971 = 52, // Clarke1880
				idMarcoAstro = 53, // International Salvage Islands
				idMassawa = 54, // Bessel1841 Eritrea(Ethiopia)
				idMerchich = 55, // Clarke1880 Norocco
				idMidwayAstro1961 = 56, // International Midway Island
				idMinna = 57, // Clarke1880 Nigeria
				idNahrwan = 58, // Clarke1880 Masirah Island(Oman)
				idNahrwanUAE = 59, // Clarke1880 Unites Arab Emirates
				idNahrwanSA = 60, // Clarke1880 Saudi Arabia
				idNaparimaBWI = 61, // International Trinidad and Tobago
				idNAD27CUS = 62, // Clarke1866 Continental US
				idNAD27Alaska = 63, // Clarke1866 Alaska
				idNAD27Bahamas = 64, // Clarke1866 Bahamas(Excluding San Salvador
															// island)
				idNAD27SSI = 65, // Clarke1866 San Salvador Island
				idNAD27Canada = 66, // Clarke1866 Canada(including Newfoundland Island)
				idNAD27CanalZone = 67, // Clarke1866 Canal Zone
				idNAD27Caribbean = 68, // Clarke1866 Caribean(Turks and Caicos Islands)
				idNAD27CA = 69, // Clarke1866 Central America(Belize,Costa Rica,EI
												// Salvador,Guatemala,Honduras,Nicaragua)
				idNAD27Cuba = 70, // Clarke1866 Cuba
				idNAD27Greenland = 71, // Clarke1866 GreenLand(Hayes Peninsula)
				idNAD27Mexico = 72, // Clarke1866 Mexico
				idNAD27Michigan = 73, // Modified Clarke1866 Michigan(used only for
															// State Plane Coordinate System 1927)
				idNAD83 = 74, // GRS 80 Alaska,Canada, Central America, Continental
											// US,Mexico
				idObservatorio1966 = 75, // International Corvo and Flores
																	// Islands(Azores)
				idOldEgyptian = 76, // Helmert1906 Egypt
				idOldHawaiian = 77, // Clarke1866 Hawaii
				idOman = 78, // Clarke1880 Oman
				idOrdnance1936 = 79, // Airy England,Esle of Man,Scotland,Shetland
															// Islands, Wales
				idPicodeLasNieves = 80, // International Canary Islands
				idPotcaimAstro1967 = 81, // International Pitcaim Island
				idPSA1956 = 82, // International Bolivia,
												// chile,Colombia,Ecuador,Guyana,Peru,Venezuela
				idPuertoRico = 83, // Clarke1866 Puerto Rico and Virgin Islands
				idQatarNational = 84, // International Qatar
				idQomoq = 85, // International South Greenland
				idReunion = 86, // International Mascarene Island
				idRome1940 = 87, // International Sardinia Island
				idSantoDOS = 88, // International Espirito Santo Island
				idS_OBraz = 89, // International S_O Miguel, Santa Maria Islands(Azores)
				idSapperHill1943 = 90, // International East Falkland Island
				idSchwarzeck = 91, // Modified Bessel1841 Namibia
				idSouthAmerican1969 = 92, // South American1969
																	// Argentina,Bolivia,Brazil,Chile,Colombia,Ecuador,Guyana,Paraguay,Peru,Venezuela,Trinidad,and
																	// Tobago
				idSouthAsia = 93, // Modified Fischer1960 Singapore
				idSoutheastBase = 94, // International Porto Santo and Madeira Islands
				idSouthBaseFGP = 95, // International Farial,Graciosa,Pico,Sao
															// Jorge,Terceira Islands(Azores)
				idTimbalai1948 = 96, // Bverest(India1830) Brunei and East
															// Malaysia(Sarawak and Sabah)
				idTokyo = 97, // Bessel1841 Japan,Korea,Okinawa
				idTristanAstro1968 = 98, // Internatinal Tristan sa Cunha
				idVitiLevu1916 = 99, // Clarke1880 Viti Levu island(Fiji Islands)
				idWakeEniwetok1960 = 100, // Hough Marshall Islands
				idWGS60 = 101, // WGS60 Worldwide
				idWGS66 = 102, // WGS66 Worldwide
				idWGS72 = 103, // WGS72 Worldwide
				idWGS84 = 104, // WGS84 Worldwide
				idYacare = 105, // International Uruguay
				idZanderij = 106, // International Surinam
				idNTF = 107, // Modified Clarke1880 France
				idED87 = 108, // International Europe
				idNetherlands = 109, // Bessel Netherlands
				idBelgium = 110, // International Belgium
				idNWGL10 = 111, // WGS72 Worldwide
				idRT1990Datum = 112, // Bessel Sweden
				idLisboaDLX = 113, // International Portugal
				idMelrica1973 = 114, // International Portugal
				idEUREF89 = 115, // GRS80 Europe
				idGDA94 = 116, // GRS80 Australia
				idNZGD2000 = 117, // GRS80 New Zealand
				idAmericaSamoa = 118, // Clarke1866 American Samoa Islands
				idAIA1943 = 119, // Clarke1880 Antigua,Leeward Islands
				idAyabellelightHouse = 120, // Clarke1880 Djibouti
				idBukitRimpah = 121, // Bessel1841 Bangka and Belitung islands
				idCOS1937Estonia = 122, // Bessel1841 Estonia
				idDabola = 123, // Clarke1880 Guinea
				idDeceptionIsland = 124, // Clarke1880 Deception Island,Antarctica
				idFortThomas1955 = 125, // Clarke1880 Nevis,St.Kitts,Leeward islands
				idGracuosaBaseSW1948 = 126, // International1924 Faial,Graciosa,Pico,Sao
																		// Jorge,and Terceira islands(Azores)
				idHeratNorth = 127, // International1924 Afghanistan
				idHermannskogel = 128, // Bessel1841 Yugoslavia(Prior to
																// 1990),Slovenia,Croatia,Bosnia and
																// Herzegovina,Serbia
				idIndianPakistan = 129, // Everest(Pakistan) Pakistan
				idIndian1954 = 130, // Everest(Indian1830) Thailand
				idIndian1960 = 131, // Everest(Indian1830) Vietnam
				idIndian1975 = 132, // Everest(Indian1830) Thailand
				idIndonesian1974 = 133, // Indonesian1974 Indonesia
				idISTS061Astro1968 = 134, // International1924 South Georgia island
				idKusaieAstro1951 = 135, // International1924 Caroline islands,Federated
																	// States of Micronesia
				idLeigon = 136, // Clarke1880 Ghana
				idMIA1958 = 137, // Clarke1880 Montserrat,Leeward Islands
				idMPoraloko = 138, // Clarke1880 Gabon
				idNorthSahara1959 = 139, // Clarke1880 Algeria
				idOM1939 = 140, // International1924 Corvo and Flores islands(Azores)
				idPoint58 = 141, // Clarke1880 Burkina Faso and Niger
				idPointeNoire1948 = 142, // Clarke1880 Congo
				idPortoSanto1936 = 143, // International1924 Porto Santo and Madeiras
																// Islands
				idSelvagemGrande1938 = 144, // International1924 Salvage islands
				idSierraLeone1960 = 145, // Clarke1880 Sierra Leone
				idSJTSK = 146, // Bessel1841 Czech Republic
				idTO1925 = 147, // International1924 Madagascar
				idVoirol1874 = 148, // Clarke1880 Tunisia/Algeria
				idVoiro1960 = 149, // Clarke1880 Algeria
				idHartbeesthoek94 = 150, // WGS84 South Africa
				id999 = 999, // 三参数基准面定义，目前MapInfo基本不存在此投影，默认会搞成后面四个参数为0的七参数形式
				idDHDN = 1000, // Bessel Germany
				idPulkovo1942 = 1001, // Krassovsky
				idNTFPPM = 1002, // Modified Clarke1880 France
				idCH1903 = 1003, // Bessel Switzerland
				idHD72 = 1004, // GRS67 Hungary
				idCape7Parameter = 1005, // WGS84 South Africa
				idAGD847Parameter = 1006, // Australian National Australia
				idAGD667ParemeterACT = 1007, // Australian National Australia,A.C.T
				idAGD667ParaAT = 1008, // Australian National Australia,Tasmania
				idAGD667ParaAV = 1009, // Australian National Australia,Victoria/NSW
				idNZGD497Pare = 1010, // International New Zealand
				idRT1990 = 1011, // Bessel Sweden
				id9999 = 9999;// 七参数基准面定义
	};

	// MapInfo栅格tab投影单位ID
	interface tkMasterMapInfoPrjUnitsID {
		public static final int idMile = 0, // 英里
				idKiloMeter = 1, // 公里
				idInches = 2, // 英寸
				idFeet = 3, // 英尺
				idYard = 4, // 码
				idMiliMeter = 5, // 毫米
				idCentiMeter = 6, // 厘米
				idMeter = 7, // 米
				idUSSfeet = 8, // US Survey Feet（用于全美平面，1927）
				idNaticalMile = 9, // 海里
				idLinker = 30, // 链接
				idLink = 31, // 链（Link）
				idRod = 32; // 杆
	};

	// dingli liqi 20120912
	// MapInfo栅格tab投影椭球体ID
	interface tkMasterMapInfoPrjEllipsoidID {
		public static int idGRS80 = 0, // a = 6378137.0 f = 298.257222101
				idWGS72Ellipsoid = 1, // a = 6378135.0 f = 298.26
				idAustralian = 2, // a = 6378160.0 f = 298.25
				idKrassovsky = 3, // a = 6378245.0 f = 298.3
				idInternational1924 = 4, // a = 6378388.0 f = 297.0
				idHayford = 5, // a = 6378388.0 f= 297.0
				idClarke1880 = 6, // a = 6378249.145 f = 293.465
				idClarke1886 = 7, // a = 6378206.4 f= 294.26068
				idClarke1866MFM = 8, // a = 6378450.047484481 f= 294.9786982
				idAiry1930 = 9, // a = 6377563.396 f= 299.3249646
				idBessel1841 = 10, // a = 6377397.115 f = 299.1528128
				idEverestIndia1830 = 11, // a = 6377276.345 f = 300.8017
				idSphere = 12, // a = 6370997.0 f = 0.0
				idAiry1930MFI = 13, // a = 6377340.189 f = 299.3249646
				idBessel1841MFS = 14, // a = 6377483.865 f = 299.1528128
				idClarke1880MFARC = 15, // a = 6378249.145 f = 293.4663076
				idClarke1880MFP = 16, // a = 6378249.2 f = 293.46598
				idEverest1948 = 17, // a = 6377304.063 f = 300.8017
				idFischer = 18, // a = 6378166.0 f = 298.3
				idFischer1960MFSA = 19, // a = 6378155.0 f = 298.3
				idFischer1968 = 20, // a = 6378150.0 f = 298.3
				idGRS67 = 21, // a = 6378160.0 f = 298.247167427
				idHelmert1906 = 22, // a = 6378200.0 f = 298.3
				idHough = 23, // a = 6378270.0 = f = 297.0
				idSouthAmerican = 24, // a = 6378160.0 f = 298.25
				idWarOffice = 25, // a = 6378300.583 f = 296.0
				idWGS60Ellipsoid = 26, // a = 6378165.0 f = 298.3
				idWGS66Ellipsoid = 27, // a = 6378145.0 f = 298.25
				idWGS84Ellipsoid = 28, // a = 6378137.0 f = 298.257223563
				idClarke1880MFIGN = 30, // a = 6378249.2 f = 293.4660213
				idIAG75 = 31, // a = 6378140.0 f = 298.257222
				idMERIT83 = 32, // a = 6378137.0 f = 298.257
				idNewInternational1967 = 33, // a = 6378157.5 f = 298.25
				idWabeck = 34, // a = 6376896.0 f = 302.78
				idBessel1981NGO = 35, // a = 6377492.0176 f = 299.15281
				idClarke1858 = 36, // a = 6378293.639 f = 294.26068
				idClarke1880MFJ = 37, // a = 6378249.136 f = 293.46631
				idClarke1880MFPsoid = 38, // a = 6378300.79 f = 293.46623
				idEverestBEM = 39, // a = 6377298.556 f = 300.8017
				idEverestIndia1956 = 40, // a = 6377301.243 f = 300.80174
				idIndonesian = 41, // a = 6378160.0 f = 298.247
				idNWL9D = 42, // a = 6378145.0 f = 298.25
				idNWL10D = 43, // a = 6378135.0 f = 298.26
				idOSU86F = 44, // a = 6378136.2 f = 298.25722
				idPSU91A = 45, // a = 6378136.3 f = 298.25722
				idPlessis1817 = 46, // a = 6376523.0 f = 308.64
				idStruve1860 = 47, // a = 6378297.0 f = 294.73
				idEverestWM1969 = 48, // a = 6377295.664 f = 300.8017
				idIrishWOFO = 49, // a = 6377542.178 f = 299.325
				idEverestPakistan = 50, // a = 6377309.613 f = 300.8017
				idATS777 = 51; // a = 6378135.0 f = 298.257
	};

	interface tkUnitsOfMeasure {
		public static final int umDecimalDegrees = 0, umMiliMeters = 1, umCentimeters = 2, umInches = 3, umFeets = 4,
				umYards = 5, umMeters = 6, umMiles = 7, umKilometers = 8;
	}

	private String convertPrjType(int mapinfoID, String type) {
		switch (mapinfoID) {
		case tkMasterMapInfoPrjID.idLonLat: {
			type = memcpy(type, "+proj=lonlat", strlen("+proj=lonlat"));
			break;
		}
		case tkMasterMapInfoPrjID.idCylindricalEqualSpace: {
			type = "+proj=cea";
			break;
		}
		case tkMasterMapInfoPrjID.idLamBertConformalConic: {
			type = "+proj=lcc";
			break;
		}
		case tkMasterMapInfoPrjID.idLamBertDirectionArea: {
			type = "+proj=leac";
			break;
		}
		case tkMasterMapInfoPrjID.idZenithalEquidistant: {
			type = "+proj=aeqd";
			break;
		}
		case tkMasterMapInfoPrjID.idConicalEquidistance: {
			type = "+proj=eqdc";
			break;
		}
		case tkMasterMapInfoPrjID.idHotineObliqueMercator: {
			type = "+proj=omerc";
			break;
		}
		case tkMasterMapInfoPrjID.idUniversalTransMercator: {
			type = memcpy(type, "+proj=utm", strlen("+proj=utm"));
			// type = "+proj=utm";
			break;
		}
		case tkMasterMapInfoPrjID.idAlbersConicalEqualArea: {
			type = "+proj=aea";
			break;
		}
		case tkMasterMapInfoPrjID.idMercator: {
			type = "+proj=mill";
			break;
		}
		case tkMasterMapInfoPrjID.idMillerCylinder: {
			type = "+proj=omerc";
			break;
		}
		case tkMasterMapInfoPrjID.idRobinson: {
			type = "+proj=robin";
			break;
		}
		case tkMasterMapInfoPrjID.idMollweide: {
			type = "+proj=moll";
			break;
		}
		case tkMasterMapInfoPrjID.idEckertIV: {
			type = "+proj=eck4";
			break;
		}
		case tkMasterMapInfoPrjID.idEckertVI: {
			type = "+proj=eck6";
			break;
		}
		case tkMasterMapInfoPrjID.idSinusoid: {
			type = "+proj=sinu";
			break;
		}
		case tkMasterMapInfoPrjID.idGall: {
			type = "+proj=gall";
			break;
		}
		case tkMasterMapInfoPrjID.idNewZealandGrid: {
			type = "+proj=nzmg";
			break;
		}
		case tkMasterMapInfoPrjID.idLambertConic: {
			type = "+proj=lcca";
			break;
		}
		case tkMasterMapInfoPrjID.idStereoGraphy: {
			type = "+proj=stere";
			break;
		}
		case tkMasterMapInfoPrjID.idUTMercator:
		case tkMasterMapInfoPrjID.idUTMSjaelland:
		case tkMasterMapInfoPrjID.idUTMBornholm:
		case tkMasterMapInfoPrjID.idUTMKKJ: {
			type = "+proj=etmerc";
			break;
		}
		case tkMasterMapInfoPrjID.idSwissObliqueMercator: {
			type = "+proj=somerc";
			break;
		}
		case tkMasterMapInfoPrjID.idRegionMercator: {
			type = "+proj=utm";
			break;
		}
		case tkMasterMapInfoPrjID.idPolyConic: {
			type = "+proj=poly";
			break;
		}
		case tkMasterMapInfoPrjID.idZenithalEquidistantAll: {
			type = "+proj=aeqd";
			break;
		}
		default: {
			type = "+proj=utm";
		}
		}
		return type;
	}

	private int strlen(String str) {
		return str.length();
	}

	private String memcpy(String desc, String src, int len) {
		if (src != null && src.length() > len)
			desc = src.substring(0, len);
		else
			desc = src;
		return desc;
	}

	private String convertPrjDatum(int mapinfoID, String type) {
		switch (mapinfoID) {
		case tkMasterMapInfoPrjDatumID.idAdidan:
		case tkMasterMapInfoPrjDatumID.idArc1950:
		case tkMasterMapInfoPrjDatumID.idArc1960:
		case tkMasterMapInfoPrjDatumID.idCape:
		case tkMasterMapInfoPrjDatumID.idCarthage:
		case tkMasterMapInfoPrjDatumID.idLiberia1964:
		case tkMasterMapInfoPrjDatumID.idMahe1971:
		case tkMasterMapInfoPrjDatumID.idMerchich:
		case tkMasterMapInfoPrjDatumID.idMinna:
		case tkMasterMapInfoPrjDatumID.idNahrwan:
		case tkMasterMapInfoPrjDatumID.idNahrwanUAE:
		case tkMasterMapInfoPrjDatumID.idNahrwanSA:
		case tkMasterMapInfoPrjDatumID.idOman:
		case tkMasterMapInfoPrjDatumID.idVitiLevu1916:
		case tkMasterMapInfoPrjDatumID.idAIA1943:
		case tkMasterMapInfoPrjDatumID.idAyabellelightHouse:
		case tkMasterMapInfoPrjDatumID.idDabola:
		case tkMasterMapInfoPrjDatumID.idDeceptionIsland:
		case tkMasterMapInfoPrjDatumID.idFortThomas1955:
		case tkMasterMapInfoPrjDatumID.idLeigon:
		case tkMasterMapInfoPrjDatumID.idMIA1958:
		case tkMasterMapInfoPrjDatumID.idMPoraloko:
		case tkMasterMapInfoPrjDatumID.idNorthSahara1959:
		case tkMasterMapInfoPrjDatumID.idPoint58:
		case tkMasterMapInfoPrjDatumID.idPointeNoire1948:
		case tkMasterMapInfoPrjDatumID.idSierraLeone1960:
		case tkMasterMapInfoPrjDatumID.idVoirol1874:
		case tkMasterMapInfoPrjDatumID.idVoiro1960:
		case tkMasterMapInfoPrjDatumID.idNTF:
		case tkMasterMapInfoPrjDatumID.idNTFPPM: {
			type = memcpy(type, " +ellps=clrk80", strlen(" +ellps=clrk80"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idAfgooye:
		case tkMasterMapInfoPrjDatumID.idPulkovo1942: {
			type = memcpy(type, " +ellps=krass", strlen(" +ellps=krass"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idAinElAbd1970:
		case tkMasterMapInfoPrjDatumID.idAscensionIsland1958:
		case tkMasterMapInfoPrjDatumID.idAstroBeaconE:
		case tkMasterMapInfoPrjDatumID.idAstroB4SorolAtoll:
		case tkMasterMapInfoPrjDatumID.idAstroDos714:
		case tkMasterMapInfoPrjDatumID.idAstronomicStation1952:
		case tkMasterMapInfoPrjDatumID.idBellevueIGN:
		case tkMasterMapInfoPrjDatumID.idBogotaObservatory:
		case tkMasterMapInfoPrjDatumID.idCampoInchauspe:
		case tkMasterMapInfoPrjDatumID.idCantonAstro1966:
		case tkMasterMapInfoPrjDatumID.idChatham1971:
		case tkMasterMapInfoPrjDatumID.idChuaAstro:
		case tkMasterMapInfoPrjDatumID.idCorregoAlegre:
		case tkMasterMapInfoPrjDatumID.idDOS1968:
		case tkMasterMapInfoPrjDatumID.idEasterIsland1967:
		case tkMasterMapInfoPrjDatumID.idEuropean1950:
		case tkMasterMapInfoPrjDatumID.idEuropean1979:
		case tkMasterMapInfoPrjDatumID.idGandajikaBase:
		case tkMasterMapInfoPrjDatumID.idNZGD49:
		case tkMasterMapInfoPrjDatumID.idGUX1Astro:
		case tkMasterMapInfoPrjDatumID.idHitoXVIII1963:
		case tkMasterMapInfoPrjDatumID.idHiorsey1955:
		case tkMasterMapInfoPrjDatumID.idHongKong1963:
		case tkMasterMapInfoPrjDatumID.idHuTzuShan:
		case tkMasterMapInfoPrjDatumID.idISTS061Astro1969:
		case tkMasterMapInfoPrjDatumID.idJohnStonIsland1961:
		case tkMasterMapInfoPrjDatumID.idkerguelenIsland:
		case tkMasterMapInfoPrjDatumID.idMarcoAstro:
		case tkMasterMapInfoPrjDatumID.idMidwayAstro1961:
		case tkMasterMapInfoPrjDatumID.idNaparimaBWI:
		case tkMasterMapInfoPrjDatumID.idObservatorio1966:
		case tkMasterMapInfoPrjDatumID.idPicodeLasNieves:
		case tkMasterMapInfoPrjDatumID.idPotcaimAstro1967:
		case tkMasterMapInfoPrjDatumID.idPSA1956:
		case tkMasterMapInfoPrjDatumID.idQatarNational:
		case tkMasterMapInfoPrjDatumID.idQomoq:
		case tkMasterMapInfoPrjDatumID.idReunion:
		case tkMasterMapInfoPrjDatumID.idRome1940:
		case tkMasterMapInfoPrjDatumID.idSantoDOS:
		case tkMasterMapInfoPrjDatumID.idS_OBraz:
		case tkMasterMapInfoPrjDatumID.idSapperHill1943:
		case tkMasterMapInfoPrjDatumID.idSoutheastBase:
		case tkMasterMapInfoPrjDatumID.idSouthBaseFGP:
		case tkMasterMapInfoPrjDatumID.idTristanAstro1968:
		case tkMasterMapInfoPrjDatumID.idYacare:
		case tkMasterMapInfoPrjDatumID.idZanderij:
		case tkMasterMapInfoPrjDatumID.idED87:
		case tkMasterMapInfoPrjDatumID.idBelgium:
		case tkMasterMapInfoPrjDatumID.idLisboaDLX:
		case tkMasterMapInfoPrjDatumID.idMelrica1973:
		case tkMasterMapInfoPrjDatumID.idGracuosaBaseSW1948:
		case tkMasterMapInfoPrjDatumID.idHeratNorth:
		case tkMasterMapInfoPrjDatumID.idISTS061Astro1968:
		case tkMasterMapInfoPrjDatumID.idKusaieAstro1951:
		case tkMasterMapInfoPrjDatumID.idOM1939:
		case tkMasterMapInfoPrjDatumID.idPortoSanto1936:
		case tkMasterMapInfoPrjDatumID.idSelvagemGrande1938:
		case tkMasterMapInfoPrjDatumID.idTO1925:
		case tkMasterMapInfoPrjDatumID.idNZGD497Pare: {
			type = memcpy(type, " +ellps=intl", strlen(" +ellps=intl"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idAnna1Astro1965:
		case tkMasterMapInfoPrjDatumID.idAGD66:
		case tkMasterMapInfoPrjDatumID.idAGD84:
		case tkMasterMapInfoPrjDatumID.idSouthAmerican1969:
		case tkMasterMapInfoPrjDatumID.idAGD847Parameter:
		case tkMasterMapInfoPrjDatumID.idAGD667ParemeterACT:
		case tkMasterMapInfoPrjDatumID.idAGD667ParaAT:
		case tkMasterMapInfoPrjDatumID.idAGD667ParaAV: {
			type = memcpy(type, " +ellps=aust_SA", strlen(" +ellps=aust_SA"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idCapeCanaveral:
		case tkMasterMapInfoPrjDatumID.idGuam1963:
		case tkMasterMapInfoPrjDatumID.idLC5Astro:
		case tkMasterMapInfoPrjDatumID.idLuzon:
		case tkMasterMapInfoPrjDatumID.idLuzonMindanao:
		case tkMasterMapInfoPrjDatumID.idNAD27CUS:
		case tkMasterMapInfoPrjDatumID.idNAD27Alaska:
		case tkMasterMapInfoPrjDatumID.idNAD27Bahamas:
		case tkMasterMapInfoPrjDatumID.idNAD27SSI:
		case tkMasterMapInfoPrjDatumID.idNAD27Canada:
		case tkMasterMapInfoPrjDatumID.idNAD27CanalZone:
		case tkMasterMapInfoPrjDatumID.idNAD27Caribbean:
		case tkMasterMapInfoPrjDatumID.idNAD27CA:
		case tkMasterMapInfoPrjDatumID.idNAD27Cuba:
		case tkMasterMapInfoPrjDatumID.idNAD27Greenland:
		case tkMasterMapInfoPrjDatumID.idNAD27Mexico:
		case tkMasterMapInfoPrjDatumID.idNAD27Michigan:
		case tkMasterMapInfoPrjDatumID.idBermuda1957:
		case tkMasterMapInfoPrjDatumID.idOldHawaiian:
		case tkMasterMapInfoPrjDatumID.idPuertoRico:
		case tkMasterMapInfoPrjDatumID.idAmericaSamoa: {
			type = memcpy(type, " +ellps=clrk66", strlen(" +ellps=clrk66"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idDjakarta:
		case tkMasterMapInfoPrjDatumID.idMassawa:
		case tkMasterMapInfoPrjDatumID.idTokyo:
		case tkMasterMapInfoPrjDatumID.idNetherlands:
		case tkMasterMapInfoPrjDatumID.idRT1990Datum:
		case tkMasterMapInfoPrjDatumID.idBukitRimpah:
		case tkMasterMapInfoPrjDatumID.idCOS1937Estonia:
		case tkMasterMapInfoPrjDatumID.idHermannskogel:
		case tkMasterMapInfoPrjDatumID.idSJTSK:
		case tkMasterMapInfoPrjDatumID.idDHDN:
		case tkMasterMapInfoPrjDatumID.idCH1903:
		case tkMasterMapInfoPrjDatumID.idRT1990: {
			type = memcpy(type, " +ellps=bessel", strlen(" +ellps=bessel"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idGRS1967:
		case tkMasterMapInfoPrjDatumID.idIndonesian1974:
		case tkMasterMapInfoPrjDatumID.idHD72: {
			type = memcpy(type, " +ellps=GRS67", strlen(" +ellps=GRS67"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idGRS1980:
		case tkMasterMapInfoPrjDatumID.idNAD83:
		case tkMasterMapInfoPrjDatumID.idEUREF89:
		case tkMasterMapInfoPrjDatumID.idGDA94:
		case tkMasterMapInfoPrjDatumID.idNZGD2000: {
			type = memcpy(type, " +ellps=GRS80", strlen(" +ellps=GRS80"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idIndian:
		case tkMasterMapInfoPrjDatumID.idIndianB:
		case tkMasterMapInfoPrjDatumID.idKandawala:
		case tkMasterMapInfoPrjDatumID.idTimbalai1948:
		case tkMasterMapInfoPrjDatumID.idIndian1954:
		case tkMasterMapInfoPrjDatumID.idIndian1960:
		case tkMasterMapInfoPrjDatumID.idIndian1975: {
			type = memcpy(type, " +ellps=evrst30", strlen(" +ellps=evrst30"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idIreland1965: {
			type = memcpy(type, " +ellps=mod_airy", strlen(" +ellps=mod_airy"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idkertau1948:
		case tkMasterMapInfoPrjDatumID.idIndianPakistan: {
			type = memcpy(type, " +ellps=evrst48", strlen(" +ellps=evrst48"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idOldEgyptian: {
			type = memcpy(type, " +ellps=helmert", strlen(" +ellps=helmert"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idOrdnance1936: {
			type = memcpy(type, " +ellps=airy", strlen(" +ellps=airy"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idSchwarzeck: {
			type = memcpy(type, " +ellps=bess_nam", strlen(" +ellps=bess_nam"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idSouthAsia: {
			type = memcpy(type, " +ellps=fschr60m", strlen(" +ellps=fschr60m"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idWakeEniwetok1960: {
			type = memcpy(type, " +ellps=hough", strlen(" +ellps=hough"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idWGS60: {
			type = memcpy(type, " +ellps=WGS60", strlen(" +ellps=WGS60"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idWGS66: {
			type = memcpy(type, " +ellps=WGS66", strlen(" +ellps=WGS66"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idWGS72:
		case tkMasterMapInfoPrjDatumID.idNWGL10: {
			type = memcpy(type, " +ellps=WGS72", strlen(" +ellps=WGS72"));
			break;
		}
		case tkMasterMapInfoPrjDatumID.idWGS84:
		case tkMasterMapInfoPrjDatumID.idHartbeesthoek94:
		case tkMasterMapInfoPrjDatumID.idCape7Parameter: {
			type = memcpy(type, " +ellps=WGS84", strlen(" +ellps=WGS84"));
			break;
		}
		default: {
			type = memcpy(type, " +ellps=WGS84", strlen(" +ellps=WGS84"));
			break;
		}
		}
		return type;
	}

	// private void convertPrjUntis(String units, int unitM) {
	// switch (unitM) {
	// case tkUnitsOfMeasure.umMiliMeters: {
	// units = memcpy(units, " +units=mm", strlen(" +units=mm"));
	// break;
	// }
	// case tkUnitsOfMeasure.umCentimeters: {
	// units = memcpy(units, " +units=cm", strlen(" +units=cm"));
	// break;
	// }
	// case tkUnitsOfMeasure.umInches: {
	// units = memcpy(units, " +units=in", strlen(" +units=in"));
	// break;
	// }
	// case tkUnitsOfMeasure.umFeets: {
	// units = memcpy(units, " +units=ft", strlen(" +units=ft"));
	// break;
	// }
	// case tkUnitsOfMeasure.umYards: {
	// units = memcpy(units, " +units=yd", strlen(" +units=yd"));
	// break;
	// }
	// case tkUnitsOfMeasure.umMeters: {
	// units = memcpy(units, " +units=m", strlen(" +units=m"));
	// break;
	// }
	// case tkUnitsOfMeasure.umMiles: {
	// units = memcpy(units, " +units=kmi", strlen(" +units=kmi"));
	// break;
	// }
	// case tkUnitsOfMeasure.umKilometers: {
	// units = memcpy(units, " +units=km", strlen(" +units=km"));
	// break;
	// }
	// default: {
	// units = memcpy(units, " +units=m", strlen(" +units=m"));
	// break;
	// }
	// }
	// }

	public String ParseProjParas(String path, String paras) {
		// string paraline( path );
		String paraline = path;
		int num = 0;

		int found = -1;
		Vector<Integer> pos = new Vector<Integer>();
		num = paraline.split(",").length - 1;
		while (true) {
			found = paraline.indexOf(",", found + 1);
			if (found == paraline.lastIndexOf(",")) {
				break;
			}
			pos.add(found);

		}

		/*
		 * size_t found = 0; std::vector<size_t> pos; while ( 1 ) { found =
		 * paraline.find( ',',found+1 ); if ( found == string::npos ) { break; }
		 * num++; pos.push_back( found ); }
		 */

		int mapinfotype = 0;
		int datumid = 0;
		// int userdefine = 0;
		// float midlon = 0;
		// float midlat = 0;
		// int extent = 0;
		// float paralat1 = 0;
		// float paralat2 = 0;
		/*
		 * char datum[64] = { '0' }; char lon0[256] = {'0'}; char lat0[256] = {'0'};
		 * char x0[256] = {'0'}; char y0[256] = {'0'}; char scale0[256] = {'0'};
		 * char lat1[256] = {'0'};XS char lat2[256] = {'0'}; char FE[256] = {'0'};
		 * char FN[256] = {'0'}; char towgs84[256] = {'0'};
		 */

		/*
		 * char datum[] = new char[64];; char lon0[] = new char[256]; char lat0[] =
		 * new char[256]; char x0[] = new char[256]; char y0[] = new char[256]; char
		 * scale0[] = new char[256]; char lat1[] = new char[256]; char lat2[] = new
		 * char[256]; char FE[] = new char[256]; char FN[] = new char[256]; char
		 * towgs84[] = new char[256];
		 */

		String datum = "";
		String lon0 = "";
		String lat0 = "";
		String x0 = "";
		String y0 = "";
		String scale0 = "";
		String lat1 = "";
		String lat2 = "";
		String FE = "";
		String FN = "";
		String towgs84 = "";

//		int falseE = 0;
//		int falseN = 0;
		lon0 = memcpy(lon0, " +lon_0=", 8);
		lat0 = memcpy(lat0, " +lat_0=", 8);
		x0 = memcpy(x0, " +x_0=", 6);
		y0 = memcpy(y0, " +y_0=", 6);
		scale0 = memcpy(scale0, " +k_0=", 6);
		lat1 = memcpy(lat1, " +lat_1=", 8);
		lat2 = memcpy(lat2, " +lat_2=", 8);
		FE = memcpy(FE, " +x_0=", 6);
		FN = memcpy(FN, " +y_0=", 6);
		towgs84 = memcpy(towgs84, " +towgs84=", 10);
		boolean setlonlat0 = false;
		boolean setpara = false;
		boolean setfalseEN = false;
		boolean setscale = false;
		boolean settowgs = false;

		switch (num) {
		case 0:
		case 2:
			return null;
		case 1: // lonlat
		{
			// sscanf( path," CoordSys Earth Projection %d,%d",&mapinfotype,&datumid);
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			break;
		}
		case 3:// Projections of the World Conformal Projection (Africa)
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s,%f",&mapinfotype,&datumid,datum,&midlon );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];
//			midlon = Float.valueOf(strings[3].trim());
			break;
		}
		case 4:// Regional Mercator Systems Equal-Area Projection (Africa)Behrmann
						// Cylindrical Equal-Area WGS84
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s%s%s",&mapinfotype,&datumid,datum,lon0+8,lat0+8 );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];
			lon0 = lon0.concat(strings[3].trim()).concat(",");
			lat0 = lat0.concat(strings[4].trim());

			setlonlat0 = true;
			break;
		}
		case 5:// Projections of a Hemisphere Azimuthal Systems
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s%s%s",&mapinfotype,&datumid,datum,lon0+8,lat0+8 );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];
			lon0 = lon0.concat(strings[3].trim()).concat(",");
			lat0 = lat0.concat(strings[4].trim());

			setlonlat0 = true;
			break;
		}
		case 6:// American Polyconic Systems Brazil - Polyconic systems
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s%s%s%s%s",&mapinfotype,&datumid,datum,lon0+8,lat0+8,lat1+8,lat2+8
			// );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];

			lon0 = lon0.concat(strings[3].trim()).concat(",");
			lat0 = lat0.concat(strings[4].trim()).concat(",");
			lat1 = lat1.concat(strings[5].trim()).concat(",");
			lat2 = lat2.concat(strings[6].trim());

			setlonlat0 = true;
			setpara = true;
			break;
		}
		case 7: // UTM Australian Map Grid (AGD 66)
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s%s%s%s%s%s",&mapinfotype,&datumid,datum,lon0+8,lat0+8,scale0+6,FE+6,FN+6
			// );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0]);
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];
			lon0 = lon0.concat(strings[3].trim()).concat(",");
			lat0 = lat0.concat(strings[4].trim()).concat(",");
			scale0 = scale0.concat(strings[5].trim()).concat(",");
			FE = FE.concat(strings[6].trim()).concat(",");
			FN = FN.concat(strings[7].trim());

			setlonlat0 = true;
			setfalseEN = true;
			setscale = true;
			break;
		}
		case 8: // Regional Conformal Projections Belgian Coordinate Systems
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%s%s%s%s%s%s%s",&mapinfotype,&datumid,datum,lon0+8,lat0+8,lat1+8,lat2+8,FE+6,FN+6
			// );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
			datumid = Integer.valueOf(strings[1].trim());
			datum = strings[2];
			lon0 = lon0.concat(strings[3].trim()).concat(",");
			lat0 = lat0.concat(strings[4].trim()).concat(",");
			lat1 = lat1.concat(strings[5].trim()).concat(",");
			lat2 = lat2.concat(strings[6].trim()).concat(",");
			FE = lat2.concat(strings[7].trim()).concat(",");
			FN = lat2.concat(strings[8].trim());

			setlonlat0 = true;
			setpara = true;
			setfalseEN = true;
			break;
		}
		case 16:// Austrian Coordinate Systems
		{
			// sscanf( path," CoordSys Earth Projection
			// %d,%d,%d,%*s%*s%*s%*s%*s%*s%*s%*s%s%s%s%s%s%s",&mapinfotype,&userdefine,&datumid,datum,lon0+8,lat0+8,scale0+6,FE+6,FN+6
			// );
			String strings[] = path.split(" CoordSys Earth Projection ")[1].split(",");
			mapinfotype = Integer.valueOf(strings[0].trim());
//			userdefine = Integer.valueOf(strings[1].trim());
			datumid = Integer.valueOf(strings[2].trim());

			// memcpy( towgs84 + 10,path+pos[2]+1,pos[9]-pos[2]-1);
			path = String.valueOf(path).substring(pos.get(2) + 1, path.length());
			towgs84 = towgs84.substring(0, 10).concat(path)
					.concat(towgs84.substring(10 + pos.get(9) - pos.get(2) - 1, towgs84.length()));

			settowgs = true;
			setlonlat0 = true;
			setfalseEN = true;
			setscale = true;
			break;
		}
		default:
			return null;
		}

		paras = convertPrjType(mapinfotype, paras);
		datum = convertPrjDatum(datumid, datum);
		// memcpy( paras+strlen(paras),datum,strlen(datum) );
		paras = memcpy(paras, datum, strlen(datum));
		if (setlonlat0) {
			// memcpy( paras+strlen(paras),lon0,strlen(lon0) );
			// memcpy( paras+strlen(paras),lat0,strlen(lat0) );
			paras = memcpy(paras, lon0, strlen(lon0));
			paras = memcpy(paras, lat0, strlen(lat0));
		}

		if (setpara) {
			// memcpy( paras+strlen(paras),lat1,strlen(lat1) );
			// memcpy( paras+strlen(paras),lat2,strlen(lat2) );
			paras = memcpy(paras, lat1, strlen(lat1));
			paras = memcpy(paras, lat2, strlen(lat2));
		}

		if (setfalseEN) {
			// memcpy( paras+strlen(paras),FE,strlen(FE) );
			// memcpy( paras+strlen(paras),FN,strlen(FN) );
			paras = memcpy(paras, FE, strlen(FE));
			paras = memcpy(paras, FN, strlen(FN));
		}

		if (setscale) {
			// memcpy( paras+strlen(paras),scale0,strlen(scale0) );
			paras = memcpy(paras, scale0, strlen(scale0));
		}

		if (settowgs) {
			// memcpy( paras+strlen(paras),towgs84,strlen(towgs84) );
			paras = memcpy(paras, towgs84, strlen(towgs84));
		}

		return paras;
	}

}
