package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.R;
import android.widget.ImageView;

public interface LanguageIView {

	void setImageId();
	void setImage();
	void setTextId();
	void setText(int language);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
}
