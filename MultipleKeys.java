import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class MultipleKeys extends JPanel {

private JLabel inputs = new JLabel();

public MultipleKeys() {
setLayout(new BorderLayout());
add(inputs, BorderLayout.CENTER);
setPreferredSize(new Dimension(200, 30));
setFocusable(true);
addKeyListener(new KeyHandler());
}

class KeyHandler extends KeyAdapter {
private boolean up = false;
private boolean down = false;
private boolean left = false;
private boolean right = false;
private boolean ctrldown = false;

public KeyHandler() { }

@Override
public void keyTyped(KeyEvent e) { }

@Override
public void keyPressed(KeyEvent e) {
int keyCode = e.getKeyCode();

switch (keyCode) {
case KeyEvent.VK_CONTROL:
ctrldown = true;
break;
case KeyEvent.VK_UP:
case KeyEvent.VK_DOWN:
case KeyEvent.VK_LEFT:
case KeyEvent.VK_RIGHT:
if (keyCode == KeyEvent.VK_UP) {
up = true;
}
if (keyCode == KeyEvent.VK_DOWN) {
down = true;
}
if (keyCode == KeyEvent.VK_LEFT) {
left = true;
}
if (keyCode == KeyEvent.VK_RIGHT) {
right = true;
}
//e.consume();
update();
break;
default:
}
}

@Override
public void keyReleased(KeyEvent e) {
int keyCode = e.getKeyCode();
switch (keyCode) {
case KeyEvent.VK_CONTROL:
ctrldown = false;
break;

case KeyEvent.VK_UP:
case KeyEvent.VK_DOWN:
case KeyEvent.VK_LEFT:
case KeyEvent.VK_RIGHT:
if (keyCode == KeyEvent.VK_UP) {
up = false;
} else if (keyCode == KeyEvent.VK_DOWN) {
down = false;
} else if (keyCode == KeyEvent.VK_LEFT) {
left = false;
} else if (keyCode == KeyEvent.VK_RIGHT) {
right = false;
}
//e.consume();
update();
break;
default:
}
}

private void update() {
StringBuilder str = new StringBuilder();

if (up) {
if (ctrldown) {
str.append(" [ctrl-up]");
} else {
str.append(" [up]");
}
}

if (down) {
if (ctrldown) {
str.append(" [ctrl-down]");
} else {
str.append(" [down]");
}
}

if (left) {
if (ctrldown) {
str.append(" [ctrl-left]");
} else {
str.append(" [left]");
}
}

if (right) {
if (ctrldown) {
str.append(" [ctrl-right]");
} else {
str.append(" [right]");
}
}

inputs.setText(str.toString());
}
}

public static void main(String[] args) {
JFrame frame = new JFrame("Multiple Keys");
frame.getContentPane().add(new MultipleKeys());
frame.addWindowListener(new WindowAdapter() {
@Override
public void windowClosing(WindowEvent e) {
System.exit(0);
}
});
frame.pack();
frame.setLocationRelativeTo(null);
frame.setVisible(true);
}
}