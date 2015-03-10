package isens.hba1c_analyzer.View;

public interface FactorIView {

	void setImageId();
	void setImage();
	void setEditTextId();
	void setEditText(String fct1stVal, String fct2ndVal);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	float getFactor1st();
	float getFactor2nd();
}
