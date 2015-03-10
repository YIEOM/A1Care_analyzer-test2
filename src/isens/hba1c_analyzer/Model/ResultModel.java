package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.RunActivity;

public class ResultModel {

	public double convertHbA1c(byte primary) {
		
		double hbA1cValue;
		
		if(primary == ConvertModel.NGSP) return RunActivity.HbA1cValue;
		
		else {
			
			hbA1cValue = (RunActivity.HbA1cValue-2.152)/0.09148;

			return hbA1cValue;	
		}
	}
}
