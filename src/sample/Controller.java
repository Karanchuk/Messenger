package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Controller {

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;

    public void sendBtnClick() {
        appendToTextArea();
        textField.requestFocus();
    }

    public void textFieldEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            appendToTextArea();
        }
    }

    private void appendToTextArea() {
        if (!textField.getText().isEmpty()) {
            textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + textField.getText());
            textField.clear();
        }
    }
}
