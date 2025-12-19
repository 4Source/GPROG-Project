import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

public class DrawStyle {
    private Color color = Color.BLACK;

    public DrawStyle color(Color color) {
        this.color = color;
        return this;
    }

    public Color color() {
        return this.color;
    }

    private Stroke stroke = new BasicStroke(1.0f);

    public DrawStyle stroke(Stroke stroke) {
        this.stroke = stroke;
        return this;
    }

    public Stroke stroke() {
        return this.stroke;
    }

    private Font font = new Font("Arial", Font.PLAIN, 24);

    public DrawStyle font(Font font) {
        this.font = font;
        return this;
    }

    public Font font() {
        return this.font;
    }
}
