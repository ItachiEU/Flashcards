package sample;

import javafx.beans.property.SimpleStringProperty;

public class Flashcard {
    private SimpleStringProperty keyWord;
    private SimpleStringProperty explanation;

    Flashcard(String keyword, String explanation)
    {
        this.keyWord = new SimpleStringProperty(keyword);
        this.explanation = new SimpleStringProperty(explanation);
    }

    public String getKeyWord() {
        return keyWord.get();
    }

    public SimpleStringProperty keyWordProperty() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord.set(keyWord);
    }

    public String getExplanation() {
        return explanation.get();
    }

    public SimpleStringProperty explanationProperty() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation.set(explanation);
    }
}
