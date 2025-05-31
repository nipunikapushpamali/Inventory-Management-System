
import javax.swing.SwingUtilities;

public class InventoryApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
