package isens.hba1c_analyzer.View;

public interface TimeIView {

	void setImageId();
	void setImage();
	void setTextId();
	void setText(String year, String month, String day);
	void setButtonId();
	void setButtonClick();
	void setButtonLongClick();
	void setButtonState(int btnId, boolean state);
}
