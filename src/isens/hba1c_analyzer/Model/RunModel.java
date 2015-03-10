package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.Barcode;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;

public class RunModel {

	final static byte NORMAL_OPERATION = 0;
	
	private double A;
	
	public int calculatetHb() {
		
		A = handleAbsorb1st()*RunActivity.RF1_Slope + RunActivity.RF1_Offset;
		
		return getTHbError();
	}

	public int getTHbError() {
		
		if(A < 0.168) return R.string.e111;
		
		else if(A > 0.63) return R.string.e112;
		
		else return RunActivity.NORMAL_OPERATION;
	}
	
	public double handleAbsorb1st() {

		double abs[] = new double[3];
		
		/* Step 1st Absorbance */
		RunActivity.Step1stAbsorb1[0] = -Math.log10(RunActivity.Step1stValue1[0]/RunActivity.BlankValue[1]); // 535nm
		RunActivity.Step1stAbsorb1[1] = -Math.log10(RunActivity.Step1stValue1[1]/RunActivity.BlankValue[2]); // 660nm
		RunActivity.Step1stAbsorb1[2] = -Math.log10(RunActivity.Step1stValue1[2]/RunActivity.BlankValue[3]); // 750nm
		
		RunActivity.Step1stAbsorb2[0] = -Math.log10(RunActivity.Step1stValue2[0]/RunActivity.BlankValue[1]);
		RunActivity.Step1stAbsorb2[1] = -Math.log10(RunActivity.Step1stValue2[1]/RunActivity.BlankValue[2]);
		RunActivity.Step1stAbsorb2[2] = -Math.log10(RunActivity.Step1stValue2[2]/RunActivity.BlankValue[3]);
		
		RunActivity.Step1stAbsorb3[0] = -Math.log10(RunActivity.Step1stValue3[0]/RunActivity.BlankValue[1]);
		RunActivity.Step1stAbsorb3[1] = -Math.log10(RunActivity.Step1stValue3[1]/RunActivity.BlankValue[2]);
		RunActivity.Step1stAbsorb3[2] = -Math.log10(RunActivity.Step1stValue3[2]/RunActivity.BlankValue[3]);
		
		abs[0] = RunActivity.Step1stAbsorb1[0] - RunActivity.Step1stAbsorb1[2];
		abs[1] = RunActivity.Step1stAbsorb2[0] - RunActivity.Step1stAbsorb2[2];
		abs[2] = RunActivity.Step1stAbsorb3[0] - RunActivity.Step1stAbsorb3[2];
		
		return averageAbsorbance(abs);
	}
	
	public double handleAbsorb2nd() {
		
		double abs[] = new double[3];
		
		/* Step 2nd Absorbance */
		RunActivity.Step2ndAbsorb1[0] = -Math.log10(RunActivity.Step2ndValue1[0]/RunActivity.BlankValue[1]); // 535nm
		RunActivity.Step2ndAbsorb1[1] = -Math.log10(RunActivity.Step2ndValue1[1]/RunActivity.BlankValue[2]); // 660nm
		RunActivity.Step2ndAbsorb1[2] = -Math.log10(RunActivity.Step2ndValue1[2]/RunActivity.BlankValue[3]); // 750nm
		
		RunActivity.Step2ndAbsorb2[0] = -Math.log10(RunActivity.Step2ndValue2[0]/RunActivity.BlankValue[1]);
		RunActivity.Step2ndAbsorb2[1] = -Math.log10(RunActivity.Step2ndValue2[1]/RunActivity.BlankValue[2]);
		RunActivity.Step2ndAbsorb2[2] = -Math.log10(RunActivity.Step2ndValue2[2]/RunActivity.BlankValue[3]);
		
		RunActivity.Step2ndAbsorb3[0] = -Math.log10(RunActivity.Step2ndValue3[0]/RunActivity.BlankValue[1]);
		RunActivity.Step2ndAbsorb3[1] = -Math.log10(RunActivity.Step2ndValue3[1]/RunActivity.BlankValue[2]);
		RunActivity.Step2ndAbsorb3[2] = -Math.log10(RunActivity.Step2ndValue3[2]/RunActivity.BlankValue[3]);
		
		abs[0] = RunActivity.Step2ndAbsorb1[1] - RunActivity.Step2ndAbsorb1[2];
		abs[1] = RunActivity.Step2ndAbsorb2[1] - RunActivity.Step2ndAbsorb2[2];
		abs[2] = RunActivity.Step2ndAbsorb3[1] - RunActivity.Step2ndAbsorb3[2];
		
		return averageAbsorbance(abs);
	}
	
	private double averageAbsorbance(double abs[]) {
		
		double dev[] = new double[3],
			   std, 
			   sum, 
			   avg;
		int idx = 0;
			
		std = (abs[0] + abs[1] + abs[2]) / 3;
		
		for(int i = 0; i < 3; i++) {
			
			if(std > abs[i]) dev[i] = std - abs[i];
			else dev[i] = abs[i] - std;
		}
		
		if(dev[0] > dev[1]) idx = 0; 
		else idx = 1;
		
		if(dev[2] > dev[idx]) idx = 2;
		
		sum = abs[0] + abs[1] + abs[2];
		
		avg = (sum - abs[idx]) / 2;
		
		return avg;
	}
	
	public int calculateHbA1c() {
		
		double B, St, Bt, C1, C2, SLA, SMA, SHA, BLA, BMA, BHA, SLV, SMV, SHV, BLV, BMV, BHV, SV, SA, BV, BA, a3, b3, a32, b32, a4, b4;
					
		B = handleAbsorb2nd()*RunActivity.RF2_Slope + RunActivity.RF2_Offset;
		
		St = (A - Barcode.b1)/Barcode.a1;
	
		RunActivity.tHbDbl = St;
		
		Bt = (A - Barcode.b1)/Barcode.a1 + 1;
		
		C1 = St * (Barcode.Asm + Barcode.Ass) + Barcode.Aim + Barcode.Ais;
		C2 = B - C1;
		
		SLA = St * Barcode.L / 100;
		SMA = St * Barcode.M / 100;
		SHA = St * Barcode.H / 100;
		BLA = Bt * Barcode.L / 100;
		BMA = Bt * Barcode.M / 100;
		BHA = Bt * Barcode.H / 100;
		
		SLV = SLA * Barcode.a21 + Barcode.b21;
		SMV = SMA * Barcode.a22 + Barcode.b22;
		SHV = SHA * Barcode.a23 + Barcode.b23;
		BLV = BLA * Barcode.a21 + Barcode.b21;
		BMV = BMA * Barcode.a22 + Barcode.b22;
		BHV = BHA * Barcode.a23 + Barcode.b23;
		
		SV = (SLV + SMV + SHV) / 3;
		SA = (SLA + SMA + SHA) / 3;
		
		a3 = calculateSlope(SA, SV, SLA, SLV, SMA, SMV, SHA, SHV);
		b3 = SV - a3*SA;
		
		BV = (BLV + BMV + BHV) / 3;
		BA = (BLA + BMA + BHA) / 3;
		
		a32 = calculateSlope(BA, BV, BLA, BLV, BMA, BMV, BHA, BHV);
		b32 = BV - a32*BA;
		
		a4 = (b32 - b3) / (Bt - St);
		b4 = b3 - (a4 * St);
		
		RunActivity.HbA1cValue = (C2 - (St * a4 + b4)) / a3 / St * 100; // %-HbA1c(%)
		
		RunActivity.HbA1cValue = (Barcode.Sm + Barcode.Ss) * RunActivity.HbA1cValue + (Barcode.Im + Barcode.Is);
		
		RunActivity.HbA1cValue = RunActivity.CF_Slope * (RunActivity.AF_Slope * RunActivity.HbA1cValue + RunActivity.AF_Offset) + RunActivity.CF_Offset;
		
		return getHbA1cError();
	}

	private int getHbA1cError() {
		
		if(RunActivity.HbA1cValue < 4) return R.string.e121;
		else if(RunActivity.HbA1cValue > 15) return R.string.e122;
		else return NORMAL_OPERATION;
	}
	
	private double calculateSlope(double x_a, double y_a, double x1, double y1, double x2, double y2,double x3, double y3) {
		
		double slope, numerator, denominator;
		
		numerator = (y1 - y_a)*(x1 - x_a) + (y2 - y_a)*(x2 - x_a) + (y3 - y_a)*(x3 - x_a);
		denominator = (x1 - x_a)*(x1 - x_a) + (x2 - x_a)*(x2 - x_a) + (x3 - x_a)*(x3 - x_a);
		
		slope = numerator/denominator;
		
		return slope;
	}	
}
