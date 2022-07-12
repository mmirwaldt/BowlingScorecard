package net.mirwaldt.bowling.scorecard.webapp;

import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;
import net.mirwaldt.bowling.scorecard.BowlingGame;
import net.mirwaldt.bowling.scorecard.BowlingGameByTDD;

@Route
@PWA(name = "Bowling scorecard demo webapp", shortName = "Bowling scorecard")
public class MainView extends VerticalLayout implements RouterLayout {
    private BowlingGame game = new BowlingGameByTDD();
    private Grid.Column[] firstRollFooters = new Grid.Column[10];
    private Grid.Column[] secondRollFooters = new Grid.Column[10];
    private Label[] scores = new Label[10];

    private Grid.Column thirdRollFooter;

    public MainView() {
        final H2 arrayComparatorH2 = new H2("Bowling scorecard");
        arrayComparatorH2.setWidth("300px");
        add(arrayComparatorH2);

        final Grid<Frame> grid = new Grid<>();
        grid.setWidth("220px");
        grid.setHeight("110px");
        grid.getStyle().set("text-align", "center");

        for (int i = 0; i < 9; i++) {
            Grid.Column<Frame> h1 = grid.addColumn(Frame::getFirstScore).setHeader("");
            Grid.Column<Frame> h2 = grid.addColumn(Frame::getSecondScore).setHeader("");
            Grid.Column<Frame> f1 = h1.setFooter("");
            Grid.Column<Frame> f2 = h2.setFooter("");
            firstRollFooters[i] = f1;
            secondRollFooters[i] = f2;
            scores[i] = new Label("");

            HeaderRow headerRow = grid.prependHeaderRow();
            headerRow.join(h1, h2).setComponent(new Label("" + i));
            FooterRow footerRow = grid.appendFooterRow();
            footerRow.join(f1, f2).setComponent(scores[i]);
        }

//        Grid.Column<Frame> h1 = grid.addColumn(Frame::getFirstScore).setHeader("");
//        Grid.Column<Frame> h2 = grid.addColumn(Frame::getSecondScore).setHeader("");
//        Grid.Column<Frame> h3 = grid.addColumn(Frame::getSecondScore).setHeader("");
//        Grid.Column<Frame> f1 = h1.setFooter("");
//        Grid.Column<Frame> f2 = h2.setFooter("");
//        Grid.Column<Frame> f3 = h3.setFooter("");
//        firstRollFooters[9] = f1;
//        secondRollFooters[9] = f2;
//        thirdRollFooter = f3;
//        scores[9] = new Label("");
//
//        HeaderRow headerRow = grid.prependHeaderRow();
//        headerRow.join(h1, h2, h3).setComponent(new Label("" + 10));
//        FooterRow footerRow = grid.appendFooterRow();
//        footerRow.join(f1, f2, f3).setComponent(scores[9]);

        add(grid);
    }
}
