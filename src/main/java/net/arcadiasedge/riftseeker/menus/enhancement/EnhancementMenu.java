package net.arcadiasedge.riftseeker.menus.enhancement;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Material;
import org.checkerframework.checker.units.qual.N;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class EnhancementMenu {
    private final Gui gui;
    public EnhancementMenu() {
        var gui = Gui.normal();

        // Build the GUI
        gui.setStructure(
                "# # # # # # # #"
        );

        gui.addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)));

        this.gui = gui.build();
    }
}
