package isens.hba1c_analyzer.View;

public interface LampIView {

	void setImageId();
	void setImageBgColor(String color);
	void setTextId();
	void setText(String value);
	void setButtonId();
	void setButtonClick();
	void setButtonBg(int dark, int f535nm, int f660nm, int f750nm);
	void setButtonState(int btnId, boolean state);
}
