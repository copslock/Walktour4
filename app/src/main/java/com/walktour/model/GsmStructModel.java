package com.walktour.model;

/**
 * Gsm结构体Model
 * @author zhihui.lian
 */
public class GsmStructModel extends BaseStructParseModel {
	
	
	public int  delay_Class;
	public int  reliability_Class;
	public int  peak_Throughput;
	public int  precedence_Class;
	public int  mean_Throughput;
	public int  iP;
	public int  radio_Priority;
	public int  lLC_SAPI;
	public int  uL_Max_bit_Rate;
	public int  dL_Max_bit_Rate;
	public int  uL_Guarante_bit_Rate;
	public int  dL_Guarante_bit_Rate;
	public int  max_SDU_size;

	
	

	@Override
	protected void init() {
		// 注意此处put队列顺序一定要与文档结构体字段顺序相同
		this.propMap.put("delay_Class", StructType.Int);
		this.propMap.put("reliability_Class", StructType.Int);
		this.propMap.put("peak_Throughput", StructType.Int);
		this.propMap.put("precedence_Class", StructType.Int);
		this.propMap.put("mean_Throughput", StructType.Int);
		this.propMap.put("iP", StructType.Int);
		this.propMap.put("radio_Priority", StructType.Int);
		this.propMap.put("lLC_SAPI", StructType.Int);
		this.propMap.put("uL_Max_bit_Rate", StructType.Int);
		this.propMap.put("dL_Max_bit_Rate", StructType.Int);
		this.propMap.put("uL_Guarante_bit_Rate", StructType.Int);
		this.propMap.put("dL_Guarante_bit_Rate", StructType.Int);
		this.propMap.put("max_SDU_size", StructType.Int);
	}




	public int getDelay_Class() {
		return delay_Class;
	}




	public void setDelay_Class(int delay_Class) {
		this.delay_Class = delay_Class;
	}




	public int getReliability_Class() {
		return reliability_Class;
	}




	public void setReliability_Class(int reliability_Class) {
		this.reliability_Class = reliability_Class;
	}




	public int getPeak_Throughput() {
		return peak_Throughput;
	}




	public void setPeak_Throughput(int peak_Throughput) {
		this.peak_Throughput = peak_Throughput;
	}




	public int getPrecedence_Class() {
		return precedence_Class;
	}




	public void setPrecedence_Class(int precedence_Class) {
		this.precedence_Class = precedence_Class;
	}




	public int getMean_Throughput() {
		return mean_Throughput;
	}




	public void setMean_Throughput(int mean_Throughput) {
		this.mean_Throughput = mean_Throughput;
	}




	public int getiP() {
		return iP;
	}




	public void setiP(int iP) {
		this.iP = iP;
	}




	public int getRadio_Priority() {
		return radio_Priority;
	}




	public void setRadio_Priority(int radio_Priority) {
		this.radio_Priority = radio_Priority;
	}




	public int getlLC_SAPI() {
		return lLC_SAPI;
	}




	public void setlLC_SAPI(int lLC_SAPI) {
		this.lLC_SAPI = lLC_SAPI;
	}




	public int getuL_Max_bit_Rate() {
		return uL_Max_bit_Rate;
	}




	public void setuL_Max_bit_Rate(int uL_Max_bit_Rate) {
		this.uL_Max_bit_Rate = uL_Max_bit_Rate;
	}




	public int getdL_Max_bit_Rate() {
		return dL_Max_bit_Rate;
	}




	public void setdL_Max_bit_Rate(int dL_Max_bit_Rate) {
		this.dL_Max_bit_Rate = dL_Max_bit_Rate;
	}

	public int getuL_Guarante_bit_Rate() {
		return uL_Guarante_bit_Rate;
	}


	public void setuL_Guarante_bit_Rate(int uL_Guarante_bit_Rate) {
		this.uL_Guarante_bit_Rate = uL_Guarante_bit_Rate;
	}


	public int getdL_Guarante_bit_Rate() {
		return dL_Guarante_bit_Rate;
	}


	public void setdL_Guarante_bit_Rate(int dL_Guarante_bit_Rate) {
		this.dL_Guarante_bit_Rate = dL_Guarante_bit_Rate;
	}


	public int getMax_SDU_size() {
		return max_SDU_size;
	}


	public void setMax_SDU_size(int max_SDU_size) {
		this.max_SDU_size = max_SDU_size;
	}

	

}
