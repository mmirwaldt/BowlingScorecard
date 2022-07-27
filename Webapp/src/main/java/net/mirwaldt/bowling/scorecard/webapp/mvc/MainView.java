package net.mirwaldt.bowling.scorecard.webapp.mvc;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;

import static net.mirwaldt.bowling.scorecard.webapp.mvc.FormBowlingGameController.NO_BONUS_PINS;

@Route
@PWA(name = "Bowling scorecard demo webapp", shortName = "Bowling scorecard")
public class MainView extends VerticalLayout implements RouterLayout, BowlingGameView {
    public static final String STRIKE_CHAR = "X";
    public static final String SPARE_CHAR = "/";
    private final Label[] firstRollLabels = new Label[10];
    private final Label[] secondRollLabels = new Label[10];
    private final Label[] scoreLabels = new Label[10];
    private Label bonusLabel;
    private final TextField rollTextField = new TextField();
    private final Button rollButton;
    private final BowlingGameController bowlingGameController;

    public MainView() {
        bowlingGameController = new FormBowlingGameController(new LazyScoringBowlingGameModel(), this);

        final H2 arrayComparatorH2 = new H2("Bowling scorecard");
        arrayComparatorH2.setWidth("300px");
        add(arrayComparatorH2);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        add(horizontalLayout2);

        rollTextField.setAutofocus(true);
        rollButton = new Button("Roll", event -> {
            int pins;
            try {
                pins = Integer.parseInt(rollTextField.getValue());
            } catch (NumberFormatException e) {
                Notification.show("Cannot parse pins as int from string '" + rollTextField.getValue() + "'.");
                e.printStackTrace();
                rollTextField.setValue("");
                return;
            }
            rollTextField.setValue("");

            try {
                bowlingGameController.rolled(pins);
            } catch (IllegalArgumentException | IllegalStateException e) {
                Notification.show("Cannot roll '" + pins + "' because: " + e.getMessage());
                e.printStackTrace();
                rollTextField.setValue("");
            }
        });
        rollButton.addClickShortcut(Key.ENTER);

        final Button resetButton = new Button("Reset", event -> {
            bowlingGameController.reset();
            rollButton.setEnabled(true);
        });
        horizontalLayout2.add(rollTextField, rollButton, resetButton);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        add(horizontalLayout);

        for (int frame = 1; frame <= 9; frame++) {
            createFrameBox(horizontalLayout, frame);
        }

        createLastFrameBox(horizontalLayout);

        reset();
    }

    @Override
    public void setRoll(int frame, int rollInFrame, int pins) {
        String pinsAsString = String.valueOf(pins);
        if(isFirstRoll(rollInFrame)) {
            getFirstRollLabel(frame).setText(pinsAsString);
        } else if(isSecondRoll(rollInFrame)) {
            getSecondRollLabel(frame).setText(pinsAsString);
        } else {
            if(pins == NO_BONUS_PINS){
                bonusLabel.setText("-");
            } else {
                bonusLabel.setText(pinsAsString);
            }
        }
    }

    @Override
    public void setStrikeRoll(int frame, int rollInFrame) {
        if(frame < 10) {
            getSecondRollLabel(frame).setText(STRIKE_CHAR);
        } else {
            if(isFirstRoll(rollInFrame)) {
                getFirstRollLabel(frame).setText(STRIKE_CHAR);
            } else if(isSecondRoll(rollInFrame)) {
                getSecondRollLabel(frame).setText(STRIKE_CHAR);
            } else {
                bonusLabel.setText(STRIKE_CHAR);
            }
        }
    }

    @Override
    public void setSpareRoll(int frame) {
        getSecondRollLabel(frame).setText(SPARE_CHAR);
    }

    @Override
    public void setScore(int frame, int score) {
        scoreLabels[frame - 1].setText(String.valueOf(score));
    }

    @Override
    public void reset() {
        for (Label label : firstRollLabels) {
            label.setText("");
        }
        for (Label label : secondRollLabels) {
            label.setText("");
        }
        for (Label label : scoreLabels) {
            label.setText("");
        }
        bonusLabel.setText("");
    }

    @Override
    public void disableInput() {
        rollButton.setEnabled(false);
        rollTextField.setReadOnly(true);
    }

    @Override
    public void enableInput() {
        rollButton.setEnabled(true);
        rollTextField.setReadOnly(false);
        rollTextField.focus();
    }

    private void createLastFrameBox(HorizontalLayout horizontalLayout) {
        VerticalLayout frameLayout = new VerticalLayout();
        frameLayout.setSpacing(false);
        frameLayout.setMargin(false);
        frameLayout.setPadding(false);
        horizontalLayout.add(frameLayout);

        final int lastFrame = 10;
        final int indexOfLastFrame = lastFrame - 1;
        Label rollLabel = createRollLabel(lastFrame);
        frameLayout.add(rollLabel);

        HorizontalLayout rollHorizontalLayout = new HorizontalLayout();
        rollHorizontalLayout.setWidth("101px");
        frameLayout.add(rollHorizontalLayout);

        Label firstRollLabel = new Label("");
        firstRollLabel.setWidth("55px");
        firstRollLabel.setHeight("26px");
        firstRollLabel.getStyle().set("text-align", "center");
        firstRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(firstRollLabel);
        firstRollLabels[indexOfLastFrame] = firstRollLabel;

        Label secondRollLabel = new Label("");
        secondRollLabel.setWidth("35px");
        secondRollLabel.setHeight("26px");
        secondRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(secondRollLabel);
        secondRollLabels[indexOfLastFrame] = secondRollLabel;

        Label bonusRollLabel = new Label("");
        bonusRollLabel.setWidth("35px");
        bonusRollLabel.setHeight("26px");
        bonusRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(bonusRollLabel);
        this.bonusLabel = bonusRollLabel;

        Label scoreLabel = new Label("");
        scoreLabel.setWidth("100px");
        scoreLabel.setHeight("50px");
        scoreLabel.getStyle().set("text-align", "center");
        scoreLabel.getStyle().set("border", "1px solid black");
        scoreLabel.getStyle().set("border-left", "none");
        scoreLabel.getStyle().set("line-height", "50px");
        scoreLabel.getStyle().set("font-size", "24px");
        frameLayout.add(scoreLabel);
        scoreLabels[indexOfLastFrame] = scoreLabel;
    }

    private void createFrameBox(HorizontalLayout horizontalLayout, int frame) {
        VerticalLayout frameLayout = new VerticalLayout();
        frameLayout.setSpacing(false);
        frameLayout.setMargin(false);
        frameLayout.setPadding(false);
        horizontalLayout.add(frameLayout);

        int index = frame - 1;
        Label rollLabel = createRollLabel(frame);
        frameLayout.add(rollLabel);

        HorizontalLayout rollHorizontalLayout = new HorizontalLayout();
        if (isFirstFrame(frame)) {
            rollHorizontalLayout.setWidth("102px");
        } else {
            rollHorizontalLayout.setWidth("101px");
        }
        frameLayout.add(rollHorizontalLayout);

        Label noRoll2 = new Label("");
        if (isFirstFrame(frame)) {
            noRoll2.getStyle().set("border-left", "1px solid black");
        }
        noRoll2.setWidth("30px");
        noRoll2.setHeight("26px");
        rollHorizontalLayout.add(noRoll2);

        Label firstRollLabel = new Label("");
        firstRollLabel.setWidth("40px");
        firstRollLabel.setHeight("26px");
        firstRollLabel.getStyle().set("text-align", "center");
        if (isFirstFrame(frame)) {
            rollLabel.getStyle().set("border-left", "1px solid black");
        }
        firstRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(firstRollLabel);
        firstRollLabels[index] = firstRollLabel;

        Label secondRollLabel = new Label("");
        secondRollLabel.setWidth("30px");
        secondRollLabel.setHeight("26px");
        secondRollLabel.getStyle().set("text-align", "left");
        secondRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(secondRollLabel);
        secondRollLabels[index] = secondRollLabel;

        Label scoreLabel = createScoreLabel(frame);
        frameLayout.add(scoreLabel);
        scoreLabels[index] = scoreLabel;
    }

    private Label createRollLabel(int frame) {
        Label rollLabel = new Label("" + frame);
        rollLabel.setWidth("100px");
        rollLabel.getStyle().set("text-align", "center");
        rollLabel.getStyle().set("border", "1px solid black");
        if (1 < frame) {
            rollLabel.getStyle().set("border-left", "none");
        }
        rollLabel.getStyle().set("background-color", "lightgray");
        return rollLabel;
    }

    private Label createScoreLabel(int frame) {
        Label scoreLabel = new Label("");
        scoreLabel.setWidth("100px");
        scoreLabel.setHeight("50px");
        scoreLabel.getStyle().set("text-align", "center");
        scoreLabel.getStyle().set("border", "1px solid black");
        if (1 < frame) {
            scoreLabel.getStyle().set("border-left", "none");
        }
        scoreLabel.getStyle().set("line-height", "50px");
        scoreLabel.getStyle().set("font-size", "24px");
        return scoreLabel;
    }

    private Label getFirstRollLabel(int frame) {
        return firstRollLabels[frame - 1];
    }

    private Label getSecondRollLabel(int frame) {
        return secondRollLabels[frame - 1];
    }

    private boolean isFirstRoll(int rollInFrame) {
        return rollInFrame == 1;
    }

    private boolean isSecondRoll(int rollInFrame) {
        return rollInFrame == 2;
    }

    private boolean isFirstFrame(int frame) {
        return frame == 1;
    }
}
