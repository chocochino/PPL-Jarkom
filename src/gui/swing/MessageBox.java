package gui.swing;

import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gui.swing.io.JTextAreaOutputStream;

/**
 * 
 * @author Steven Albert
 *
 */
public class MessageBox extends JTextArea {

	private static final long serialVersionUID = 7150795260119092984L;
	
	/**
	 * JTextAreaOutputStream Object that will write in JTextArea Object
	 */
	private JTextAreaOutputStream output;
	
	/**
	 * JScrollPane Object for scrolling the JTextArea Object
	 */
	private JScrollPane scroll;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public MessageBox(int x, int y, int width, int height) {
		this.setBounds(x, y, width, height);
		this.setMargin(new Insets(10, 10, 10, 10));
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		
		
		output = new JTextAreaOutputStream(this);
		
		scroll = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(this.getBounds());
		scroll.setViewportView(this);
		scroll.setVisible(true);
		this.setVisible(true);
	}
	
	/**
	 * 
	 * @return
	 */
	public JTextAreaOutputStream getOutputStream() {
		return output;
	}
	
	/**
	 * 
	 * @return
	 */
	public JScrollPane getScrollPane() {
		return scroll;
	}
}
