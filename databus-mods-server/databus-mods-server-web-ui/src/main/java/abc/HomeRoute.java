package abc;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("test")
public class HomeRoute extends VerticalLayout {

    public HomeRoute() {
        Grid<Person> grid = new Grid<>(Person.class);

        List<Person> items = new java.util.ArrayList<>();
        items.add(new Person(20,"Peters"));
        items.add(new Person(21,"Frank"));
        items.add(new Person(21,"Frank3"));
        items.add(new Person(21,"Frank5"));
        items.add(new Person(21,"Frank7"));

        grid.setItems(items);

        add(grid);
    }
}
