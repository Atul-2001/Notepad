package org.signature.util;

import javafx.scene.control.Alert;
import org.signature.model.Position;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ResultIterator {

    private static final ResultIterator instance = new ResultIterator();
    private boolean isControlAvailable = false;
    private boolean wrapAround, upDirection;
    private String query, replaceableText;
    private JTextArea writingPad;
    private List<Position> matchResultList;
    int index = -1, length = 0;
    int increasedSize = 0, replacedTextLength = 0;

    private ResultIterator() {}

    public static ResultIterator getInstance() {
        return instance;
    }

    public void setWrapAround(boolean isWrapAround) {
        this.wrapAround = isWrapAround;
    }

    public void setDirection(boolean isGoingUp) {
        this.upDirection = isGoingUp;
    }


    public void setSource(JTextArea textArea) {
        Objects.requireNonNull(textArea);
        this.writingPad = textArea;
    }

    public void setQueryAndResult(String query, List<Position> resultList) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(resultList);
        this.query = query;
        this.matchResultList = resultList;
        this.length = matchResultList.size();
        this.isControlAvailable = true;
    }

    public void setReplaceableText(String replaceableText) {
        Objects.requireNonNull(replaceableText);
        this.replaceableText = replaceableText;
        replacedTextLength = replaceableText.length();
        increasedSize = 0;
    }

    public void setIterationStatus(int index) {
        if (index < -1) {
            throw new IndexOutOfBoundsException();
        } else {
            this.index = index;
        }
    }

    public boolean findNext() {
        if (isControlAvailable) {
            if (matchResultList.size() == 0) {
                Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                wordNotFoundAlert.setTitle("Notepad");
                wordNotFoundAlert.setHeaderText(null);
                wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                wordNotFoundAlert.showAndWait();
            } else {
                if (wrapAround) {
                    index+=1;
                    if (index >= length) {
                        index = 0;
                    }
                    try {
                        Position pos = matchResultList.get(index);
                        writingPad.select(pos.getStart(), pos.getEnd());
                        return true;
                    } catch (IndexOutOfBoundsException ignored) {}
                } else {
                    if (upDirection) {
                        if (index >= -1 && index <= length) {
                            index-=1;
                            if (index >= 0) {
                                try {
                                    Position pos = matchResultList.get(index);
                                    writingPad.select(pos.getStart(), pos.getEnd());
                                    return true;
                                } catch (IndexOutOfBoundsException ignored) {}
                            }
                        }
                    } else {
                        if (index < length && index >= -1) {
                            index+=1;
                            if (index < length) {
                                try {
                                    Position pos = matchResultList.get(index);
                                    writingPad.select(pos.getStart(), pos.getEnd());
                                    return true;
                                } catch (IndexOutOfBoundsException ignored) {}
                            }
                        }
                    }

                    Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                    wordNotFoundAlert.setTitle("Notepad");
                    wordNotFoundAlert.setHeaderText(null);
                    wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                    wordNotFoundAlert.showAndWait();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean findPrevious() {
        if (isControlAvailable) {
            if (matchResultList.size() == 0) {
                Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                wordNotFoundAlert.setTitle("Notepad");
                wordNotFoundAlert.setHeaderText(null);
                wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                wordNotFoundAlert.showAndWait();
            } else {
                if (wrapAround) {
                    index-=1;
                    if (index < 0) {
                        index = length-1;
                    }
                    try {
                        Position pos = matchResultList.get(index);
                        writingPad.select(pos.getStart(), pos.getEnd());
                        return true;
                    } catch (IndexOutOfBoundsException ignored) {}
                } else {
                    if (index >= -1 && index <= length) {
                        index-=1;
                        if (index >= 0) {
                            try {
                                Position pos = matchResultList.get(index);
                                writingPad.select(pos.getStart(), pos.getEnd());
                                return true;
                            } catch (IndexOutOfBoundsException ignored) {}
                        }
                    }

                    Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                    wordNotFoundAlert.setTitle("Notepad");
                    wordNotFoundAlert.setHeaderText(null);
                    wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                    wordNotFoundAlert.showAndWait();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void replace() {
        if (isControlAvailable) {
            if (matchResultList.size() == 0) {
                Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                wordNotFoundAlert.setTitle("Notepad");
                wordNotFoundAlert.setHeaderText(null);
                wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                wordNotFoundAlert.showAndWait();
            } else {
                if (wrapAround) {
                    index+=1;
                    if (index >= length) {
                        index = 0;
                        increasedSize = 0;
                    }
                    try {
                        Position pos = matchResultList.get(index);
                        int start = pos.getStart() + increasedSize;
                        int end = pos.getEnd() + increasedSize;

                        writingPad.select(start, end);
                        int currentTextLength = writingPad.getSelectedText().length();
                        writingPad.replaceSelection(replaceableText);
                        increasedSize = increasedSize + (replacedTextLength - currentTextLength);

                        start = writingPad.getSelectionStart();
                        end = start+replacedTextLength;
                        writingPad.select(start, end);
                        Collections.replaceAll(matchResultList, pos, new Position(start, end));
                    } catch (IndexOutOfBoundsException | NullPointerException | IllegalArgumentException ignored) {}
                } else {
                    if (index < length && index >= -1) {
                        index+=1;
                        if (index < length) {
                            try {
                                Position pos = matchResultList.get(index);
                                int start = pos.getStart() + increasedSize;
                                int end = pos.getEnd() + increasedSize;

                                writingPad.select(start, end);
                                int currentTextLength = writingPad.getSelectedText().length();
                                writingPad.replaceSelection(replaceableText);
                                increasedSize = increasedSize + (replacedTextLength - currentTextLength);

                                start = writingPad.getSelectionStart();
                                end = start+replacedTextLength;
                                writingPad.select(start, end);
                                return;
                            } catch (IndexOutOfBoundsException | NullPointerException | IllegalArgumentException ignored) {}
                        }
                    }

                    Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                    wordNotFoundAlert.setTitle("Notepad");
                    wordNotFoundAlert.setHeaderText(null);
                    wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                    wordNotFoundAlert.showAndWait();
                }
            }
        }
    }

    public void replaceAll() {
        if (isControlAvailable) {
            if (matchResultList.size() == 0) {
                Alert wordNotFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                wordNotFoundAlert.setTitle("Notepad");
                wordNotFoundAlert.setHeaderText(null);
                wordNotFoundAlert.setContentText("Cannot find \"" + query + "\"");
                wordNotFoundAlert.showAndWait();
            } else {
                for (Position pos : matchResultList) {
                    int start = pos.getStart() + increasedSize;
                    int end = pos.getEnd() + increasedSize;
                    int currentTextLength = end - start;
                    writingPad.replaceRange(replaceableText, start, end);
                    increasedSize = increasedSize + (replacedTextLength - currentTextLength);
                }
                matchResultList.clear();
                writingPad.setCaretPosition(0);
            }
        }
    }
}
