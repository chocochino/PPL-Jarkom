package gui.swing.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * 
 * @author Steven Albert
 *
 */
public class JTextAreaOutputStream extends OutputStream {

	/**
	 * JTextArea Object that will be bind for writing
	 */
	private JTextArea textArea;
	
	/**
	 * 
	 * @param component
	 */
	public JTextAreaOutputStream(JTextArea component) {
		this.textArea = component;
	}
	
	@Override
	public void write(int b) throws IOException {
		textArea.append(Character.toString((char)b));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
