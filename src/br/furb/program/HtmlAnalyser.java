package br.furb.program;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import br.furb.model.utils.PilhaLista;
import br.furb.tablemodel.TableModelTag;

/*
 * Data: 16 de out de 2019
 * @author Caetano Siemann e Leonardo Schwab
 *
 */

public class HtmlAnalyser {

	private static final String DOCTYPE = "!DOCTYPE";
	private static String RG_TAG_SINGLETONS = "(meta)|(base)|(br)|(col)|(command)|(embed)|(hr)|(img)|(input)|(link)|(param)|(source)";
	@SuppressWarnings("unused")
	private static String newLine = System.getProperty("line.separator");

	private JFrame frame;
	private JTextField txtArq;
	private JTextArea txtMsg;
	private JTable table;
	private String file;
	private TableModelTag tableModel = new TableModelTag();

	PilhaLista<String> tags;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HtmlAnalyser window = new HtmlAnalyser();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HtmlAnalyser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		tags = new PilhaLista<>();
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 500, 500);
		frame.setLocationRelativeTo(null);

		JLabel lblArquivo = new JLabel("Arquivo:");
		lblArquivo.setBounds(30, 20, 70, 15);
		frame.getContentPane().add(lblArquivo);

		txtArq = new JTextField();
		txtArq.setBounds(101, 15, 220, 25);
		txtArq.setColumns(10);
		frame.getContentPane().add(txtArq);

		JButton btnAnalizar = new JButton("Analizar");
		btnAnalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				file = txtArq.getText();
				try {
					analyze(file);
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
			}

		});
		btnAnalizar.setBounds(333, 15, 137, 25);
		frame.getContentPane().add(btnAnalizar);

		JPanel msg = new JPanel();
		msg.setLayout(new BorderLayout());
		txtMsg = new JTextArea();
		txtMsg.setLineWrap(true);
		txtMsg.setEditable(false);
		msg.add(new JScrollPane(txtMsg), BorderLayout.CENTER);
		msg.setBounds(30, 47, 440, 180);
		frame.getContentPane().add(msg);

		table = new JTable(tableModel);
		table.setDragEnabled(false);
		JTableHeader header = table.getTableHeader();
		JPanel tabela = new JPanel();
		tabela.setLayout(new BorderLayout());
		tabela.add(header, BorderLayout.NORTH);
		tabela.add(new JScrollPane(table), BorderLayout.CENTER);
		tabela.setBounds(30, 250, 440, 180);
		frame.getContentPane().add(tabela);

	}

	private void analyze(String url) throws IOException {
		long timeini = new Date().getTime();
		File file = new File(url);
		if (!file.exists())
			return;
		tableModel.resetList();
		tags.liberar();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String test;
		StringBuilder builder = new StringBuilder();
		while ((test = br.readLine()) != null) {
			builder.append(test);
		}
		br.close();
		String html = builder.toString();
		builder = new StringBuilder();
		try {
			analyse(html);
			if (tags.estaVazia()) 
				builder.append("O arquivo está bem formatado");
		} catch (Exception e) {
			builder.append(e);
		}
		long timefin = new Date().getTime();
		builder.append("\nTime: " + (timefin - timeini));
		txtMsg.setText(builder.toString());
	}

	private void analyse(String test) throws Exception {

		for (int i = 0; i < test.length(); i++) {
			char ch = test.charAt(i);

			if (ch == '<') {

				i++;

				if (i >= test.length()) {
					break;
				}
				ch = test.charAt(i);

				if (ch == '!') {
					// deve ser comentário
					StringBuilder builder = new StringBuilder();
					boolean comentario = false;
					for (; i < test.length(); i++) {
						char ant = test.charAt(i - 1);
						ch = test.charAt(i);
						char dp = 0;
						if (i + 1 < test.length())
							dp = test.charAt(i + 1);
						if (ch == '>' && !comentario) {
							break;
						} else if (ant == '!' && ch == '-' && dp == '-') {
							comentario = true;
						} else if (ant == '-' && ch == '-' && dp == '>') {
							break;
						} else if ((ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') && !comentario) {
							break;
						} else {
							builder.append(ch);
						}
					}
					if(!comentario) {
						String tag = builder.toString();
						if (tag.equals(DOCTYPE)) {
							System.out.println(tag);
							tableModel.addQuantidade(tag);
						}
					}
				} else if (ch == '/') {
					// deve ser final
					i++;

					if (i >= test.length()) {
						break;
					}
					StringBuilder builder = new StringBuilder();
					for (; i < test.length(); i++) {
						ch = test.charAt(i);
						if (ch == '>') {
							break;
						} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
							break;
						} else {
							builder.append(ch);
						}
					}
					String tag = builder.toString();
					if (tags.peek().equals(tag)) {
						System.out.println(tag);
						tableModel.addQuantidade(tags.pop());
					}else
						throw new Exception(
								"Erro: Tag inesperada </" + tag + "> , tag esperada </" + tags.peek() + ">");
				} else {
					StringBuilder builder = new StringBuilder();
					for (; i < test.length(); i++) {
						ch = test.charAt(i);
						if (ch == '>') {
							break;
						} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
							break;
						} else {
							builder.append(ch);
						}
					}
					String tag = builder.toString();
					if (tag.matches(RG_TAG_SINGLETONS))
						tableModel.addQuantidade(tag);
					else if (!tag.isEmpty() && tag.matches("\\w+")) {
						tags.push(tag);
					}
				}
			} else {

			}
		}
		if (tags.tamanho() != 0) {
			boolean plural = tags.tamanho() > 1;
			StringBuilder builder = new StringBuilder("Erro: ");
			builder.append("Falta" + (plural ? "m" : "") + " a" + (plural ? "s" : "") + " tag" + (plural ? "s" : "")
					+ " : \n");
			while (tags.tamanho() != 0) {
				String tag = tags.pop();
				builder.append("</" + tag + ">");
			}
			throw new Exception(builder.toString());
		}
	}
}
