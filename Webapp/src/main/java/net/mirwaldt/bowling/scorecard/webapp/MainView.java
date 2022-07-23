package net.mirwaldt.bowling.scorecard.webapp;

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
import net.mirwaldt.bowling.scorecard.BowlingGame;
import net.mirwaldt.bowling.scorecard.LazyScoringBowlingGame;

@Route
@PWA(name = "Bowling scorecard demo webapp", shortName = "Bowling scorecard")
public class MainView extends VerticalLayout implements RouterLayout {
    private final Label[] firstRollLabels = new Label[10];
    private final Label[] secondRollLabels = new Label[10];
    private final Label[] scoreLabels = new Label[10];
    private Label bonusLabel;
    private BowlingGame bowlingGame;

    public MainView() {
        final H2 arrayComparatorH2 = new H2("Bowling scorecard");
        arrayComparatorH2.setWidth("300px");
        add(arrayComparatorH2);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        add(horizontalLayout2);

        final TextField rollTextField = new TextField();
        rollTextField.setAutofocus(true);
        final Button rollButton = new Button("Roll", event -> {
            int pins;
            try {
                pins = Integer.parseInt(rollTextField.getValue());
            } catch (NumberFormatException e) {
                Notification.show("Cannot parse int from string '" + rollTextField.getValue() + "'.");
                e.printStackTrace();
                rollTextField.setValue("");
                return;
            }
            rollTextField.setValue("");

            try {
                bowlingGame.roll(pins);
            } catch (IllegalArgumentException e) {
                Notification.show("Cannot roll " + pins + " pins in frame " + bowlingGame.frame() + ".");
                e.printStackTrace();
                rollTextField.setValue("");
                return;
            }

            int indexOfFrame = bowlingGame.frame() - 1;
            if(bowlingGame.frame() < 10) {
                handleAllFramesExceptLastFrame(pins, indexOfFrame);
            } else {
                handleLastFrame(pins, indexOfFrame);
            }

            displayScores();

            if(bowlingGame.isOver()) {
                event.getSource().setEnabled(false);
            }
        });
        rollButton.addClickShortcut(Key.ENTER);

        final Button resetButton = new Button("Reset", event -> {
            reset();
            rollButton.setEnabled(true);
        });
        horizontalLayout2.add(rollTextField, rollButton, resetButton);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        add(horizontalLayout);

        for (int i = 1; i <= 9; i++) {
            createFrameBox(horizontalLayout, i);
        }

        createLastFrameBox(horizontalLayout);

        reset();
    }

    private void displayScores() {
        for (int f = 1; f <= bowlingGame.frame(); f++) {
            scoreLabels[f - 1].setText("" + bowlingGame.score(f));
        }
    }

    private void handleLastFrame(int pins, int indexOfFrame) {
        switch (bowlingGame.rollOffset()) {
            case 0:
                handleFirstRollOfLastFrame(pins, indexOfFrame);
                break;
            case 1:
                handleSecondRollOfLastFrame(pins, indexOfFrame);
                break;
            case 2:
                handleBonus(pins);
                break;
        }
    }

    private void handleBonus(int pins) {
        if(bowlingGame.isStrike()) {
            bonusLabel.setText("X");
        } else {
            bonusLabel.setText("" + pins);
        }
    }

    private void handleSecondRollOfLastFrame(int pins, int indexOfFrame) {
        if(bowlingGame.isStrike()) {
            secondRollLabels[indexOfFrame].setText("X");
        } else if(bowlingGame.isSpare()) {
            secondRollLabels[indexOfFrame].setText("/");
        } else {
            secondRollLabels[indexOfFrame].setText("" + pins);
            bonusLabel.setText("-");
        }
    }

    private void handleFirstRollOfLastFrame(int pins, int indexOfFrame) {
        if(bowlingGame.isStrike()) {
            firstRollLabels[indexOfFrame].setText("X");
        } else {
            firstRollLabels[indexOfFrame].setText("" + pins);
        }
    }

    private void handleAllFramesExceptLastFrame(int pins, int indexOfFrame) {
        if(bowlingGame.isStrike()) {
            secondRollLabels[indexOfFrame].setText("X");
        } else if(bowlingGame.isSpare()) {
            secondRollLabels[indexOfFrame].setText("/");
        } else {
            if(bowlingGame.rollOffset() == 0) {
                firstRollLabels[indexOfFrame].setText("" + pins);
            } else {
                secondRollLabels[indexOfFrame].setText("" + pins);
            }
        }
    }

    private void reset() {
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
        bowlingGame = new LazyScoringBowlingGame();
    }

    private void createLastFrameBox(HorizontalLayout horizontalLayout) {
        VerticalLayout frameLayout = new VerticalLayout();
        frameLayout.setSpacing(false);
        frameLayout.setMargin(false);
        frameLayout.setPadding(false);
        horizontalLayout.add(frameLayout);

        Label rollLabel = createRollLabel(10);
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
        firstRollLabels[10 - 1] = firstRollLabel;

        Label secondRollLabel = new Label("");
        secondRollLabel.setWidth("35px");
        secondRollLabel.setHeight("26px");
        secondRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(secondRollLabel);
        secondRollLabels[10 - 1] = secondRollLabel;

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
        scoreLabels[10 - 1] = scoreLabel;
    }

    private void createFrameBox(HorizontalLayout horizontalLayout, int i) {
        VerticalLayout frameLayout = new VerticalLayout();
        frameLayout.setSpacing(false);
        frameLayout.setMargin(false);
        frameLayout.setPadding(false);
        horizontalLayout.add(frameLayout);

        Label rollLabel = createRollLabel(i);
        frameLayout.add(rollLabel);

        HorizontalLayout rollHorizontalLayout = new HorizontalLayout();
        if (i == 1) {
            rollHorizontalLayout.setWidth("102px");
        } else {
            rollHorizontalLayout.setWidth("101px");
        }
        frameLayout.add(rollHorizontalLayout);

        Label noRoll2 = new Label("");
        if (i == 1) {
            noRoll2.getStyle().set("border-left", "1px solid black");
        }
        noRoll2.setWidth("30px");
        noRoll2.setHeight("26px");
        rollHorizontalLayout.add(noRoll2);

        Label firstRollLabel = new Label("");
        firstRollLabel.setWidth("40px");
        firstRollLabel.setHeight("26px");
        firstRollLabel.getStyle().set("text-align", "center");
        if (i == 1) {
            rollLabel.getStyle().set("border-left", "1px solid black");
        }
        firstRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(firstRollLabel);
        firstRollLabels[i - 1] = firstRollLabel;

        Label secondRollLabel = new Label("");
        secondRollLabel.setWidth("30px");
        secondRollLabel.setHeight("26px");
        secondRollLabel.getStyle().set("text-align", "left");
        secondRollLabel.getStyle().set("border-right", "1px solid black");
        rollHorizontalLayout.add(secondRollLabel);
        secondRollLabels[i - 1] = secondRollLabel;

        Label scoreLabel = createScoreLabel(i);
        frameLayout.add(scoreLabel);
        scoreLabels[i - 1] = scoreLabel;
    }

    private Label createRollLabel(int i) {
        Label rollLabel = new Label("" + i);
        rollLabel.setWidth("100px");
        rollLabel.getStyle().set("text-align", "center");
        rollLabel.getStyle().set("border", "1px solid black");
        if (1 < i) {
            rollLabel.getStyle().set("border-left", "none");
        }
        rollLabel.getStyle().set("background-color", "lightgray");
        return rollLabel;
    }

    private Label createScoreLabel(int i) {
        Label scoreLabel = new Label("");
        scoreLabel.setWidth("100px");
        scoreLabel.setHeight("50px");
        scoreLabel.getStyle().set("text-align", "center");
        scoreLabel.getStyle().set("border", "1px solid black");
        if (1 < i) {
            scoreLabel.getStyle().set("border-left", "none");
        }
        scoreLabel.getStyle().set("line-height", "50px");
        scoreLabel.getStyle().set("font-size", "24px");
        return scoreLabel;
    }
}
