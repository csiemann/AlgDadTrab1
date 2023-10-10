package br.furb.program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

import br.furb.model.utils.PilhaLista;
import br.furb.model.utils.TagException;
import br.furb.tablemodel.TableModelTag;

/*
 * Data: 16 de out de 2019
 * @author Caetano Siemann e Leonardo Schwab
 *
 */

public class HtmlAnalyser {

	private static final String DOCTYPE = "!DOCTYPE";
	private static String RG_TAG_SINGLETONS = "(?i)(meta)|(base)|(br)|(col)|(command)|(embed)|(hr)|(img)|(input)|(link)|(param)|(source)";
	private static String newLine = System.getProperty("line.separator");

	private JFrame frame;
	private JTextField txtArq;
	private JTextArea txtMsg;
	private JTable table;
	private String url = "";
	private TableModelTag tableModel = new TableModelTag();
	private JLabel lblMin;
	private JLabel lblMax;
	private JLabel lblTime;

	private long maxtime = 0;
	private long mintime = Long.MAX_VALUE;

	private PilhaLista<String> tags;
	private JLabel lblUrl;

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
				startAnalyzing();
			}
		});

		btnAnalizar.setBounds(333, 15, 137, 25);
		frame.getContentPane().add(btnAnalizar);

		JPanel msg = new JPanel();
		msg.setLayout(new BorderLayout());
		txtMsg = new JTextArea();
		txtMsg.setLineWrap(true);
		txtMsg.setEditable(false);
		txtMsg.setFont(new Font(txtMsg.getFont().getFontName(), Font.BOLD, 14));
		msg.add(new JScrollPane(txtMsg), BorderLayout.CENTER);
		msg.setBounds(30, 47, 440, 180);
		frame.getContentPane().add(msg);

		table = new JTable(tableModel);
		table.setDragEnabled(false);
		JTableHeader header = table.getTableHeader();
		JPanel tabela = new JPanel();
		tabela.setLayout(new BorderLayout());
		tabela.add(header, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setLocation(29, 0);
		tabela.add(scrollPane, BorderLayout.CENTER);
		tabela.setBounds(30, 250, 440, 180);
		frame.getContentPane().add(tabela);

		lblMin = new JLabel("");
		lblMin.setBounds(333, 442, 137, 15);
		frame.getContentPane().add(lblMin);

		lblMax = new JLabel("");
		lblMax.setBounds(184, 442, 137, 15);
		frame.getContentPane().add(lblMax);

		lblTime = new JLabel("");
		lblTime.setBounds(30, 442, 137, 15);
		frame.getContentPane().add(lblTime);

		lblUrl = new JLabel("");
		lblUrl.setHorizontalAlignment(SwingConstants.CENTER);
		lblUrl.setBounds(30, 225, 440, 25);
		frame.getContentPane().add(lblUrl);

		txtArq.addKeyListener(new KeyListener() {

			private void command(KeyEvent e) {
				int keyStatus = e.getID();
				if (keyStatus == KeyEvent.KEY_PRESSED) {
					int key = e.getKeyCode();
					int modKey = e.getModifiersEx();
					if (key == KeyEvent.VK_ENTER) {
						startAnalyzing();
					}
					if (modKey == KeyEvent.CTRL_DOWN_MASK) {
						if (key == KeyEvent.VK_D) {
							closeProgram();
						}
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				command(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				command(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				command(e);
			}
		});
	}

	protected void startAnalyzing() {
		url = getFile();
		lblUrl.setText(url);
		try {
			analyze(url);
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
	}

	private String getFile() {
		String aux = txtArq.getText();
		if (aux.startsWith("file://"))
			aux = aux.substring("file://".length());
		if (url.equals(aux)) {
			return aux;
		}
		maxtime = 0;
		mintime = Long.MAX_VALUE;
		url = aux;
		return url;
	}

	private void analyze(String url) throws IOException {
//		long timeini = new Date().getTime();
		long nanoini = System.currentTimeMillis();
		File file = new File(url);
		tableModel.resetList();
		tags.liberar();
		txtMsg.setText("");
		if (!file.exists()) {
			lblUrl.setForeground(Color.red);
			return;
		}
		lblUrl.setForeground(Color.blue);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String test;
		StringBuilder builder = new StringBuilder();
		while ((test = br.readLine()) != null) {
			builder.append(test + newLine);
		}
		br.close();
		String html = builder.toString();
		builder = new StringBuilder();
		try {
			analyzeText(html);
			if (tags.estaVazia())
				builder.append("O arquivo está bem formatado");
			txtMsg.setForeground(Color.green);
		} catch (TagException e) {
			txtMsg.setForeground(Color.red);
			builder.append(e);
		}
//		long timefin = new Date().getTime();
		long nanofin = System.currentTimeMillis();
//		builder.append(newLine + "Time: " + (timefin - timeini));
		long time = nanofin - nanoini;
		if (mintime > time)
			mintime = time;
		if (maxtime < time)
			maxtime = time;
		lblTime.setText("Time: " + time + "ms");
		lblMin.setText("Min time: " + (mintime) + "ms");
		lblMax.setText("Max time: " + (maxtime) + "ms");
		txtMsg.setText(builder.toString());
	}

	private void analyzeText(String test) throws TagException {
		boolean script = false;
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
					boolean nomeTag = true;
					boolean valido = false;
					for (; i < test.length(); i++) {
						char ant = 0;

						if (i - 1 != -1)
							ant = test.charAt(i - 1);

						ch = test.charAt(i);

						char dp = 0;
						if (i + 1 < test.length())
							dp = test.charAt(i + 1);

						if (ch == '>' && !comentario) {
							valido = true;
							break;
						} else if (ch == '<' && !comentario) {
							valido = false;
							break;
						} else if (ant == '!' && ch == '-' && dp == '-') {
							comentario = true;
						} else if (ant == '-' && ch == '-' && dp == '>') {
							valido = true;
							break;
						} else if ((ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') && !comentario) {
							nomeTag = false;
							continue;
						} else if (nomeTag) {
							builder.append(ch);
						}
					}
					String tag = builder.toString();
					if (!valido)
						throw new TagException("Erro: Tag inválida" + newLine + "<" + tag);
					if (!comentario && valido && !script) {
						if (tag.equals(DOCTYPE)) {
							tableModel.addQuantidade(tag);
						}
					}
				} else if (ch == '/') { // DEVE SER FINAL

					i++;

					if (i >= test.length()) {
						break;
					}
					boolean nomeTag = true;
					boolean valido = false;
					StringBuilder builder = new StringBuilder();
					for (; i < test.length(); i++) {
						ch = test.charAt(i);

						if (ch == '>') {
							valido = true;
							break;
						} else if (ch == '<') {
							valido = false;
							break;
						} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
							nomeTag = false;
							continue;
						} else if (nomeTag) {
							builder.append(ch);
						}
					}
					String tag = builder.toString().toLowerCase();

					if (!valido)
						throw new TagException("Erro: Tag inválida" + newLine + "</" + tag);

					if (tags.estaVazia())
						throw new TagException("Erro: Tag final a mais:" + newLine + "</" + tag + ">");

					if (tags.peek().equals(tag)) {
						if (tag.equals("script"))
							script = false;
						if (!script)
							tableModel.addQuantidade(tags.pop());
					} else {
						if (script)
							continue;
						throw new TagException("Erro: Tag inesperada" + newLine + "</" + tag + "> , tag esperada </"
								+ tags.peek() + ">");
					}
				} else {
					if (!Character.isLetter(ch))
						continue;

					StringBuilder builder = new StringBuilder();
					boolean nomeTag = true;
					boolean valido = false;
					for (; i < test.length(); i++) {
						ch = test.charAt(i);
						if (ch == '>') {// VERIFICA SE TERMINOU A TAG
							valido = true;
							break;
						} else if (ch == '<') {// VERIFICA SE EXISTE OUTRA TAG
							valido = false;
							break;
						} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {// TERMINA A NOME DA "TAG"
							nomeTag = false;
							continue;
						} else if (nomeTag) {// ADICIONA O CARÁCTER PARA O NOME DA TAG
							builder.append(ch);
						}
					}
					String tag = builder.toString().toLowerCase();// DECRALA O NOME DA TAG
					if (script)
						continue;
					if (!valido)// TAG INVÁLIDA <XX
						throw new TagException("Erro: Tag inválida" + newLine + "<" + tag);
					if (tag.matches(RG_TAG_SINGLETONS))// TAGS SEM TAG FINAIS
						tableModel.addQuantidade(tag);
					else {// PILHA A TAG INICIAL VÁLIDA
						if (tag.equals("script"))// PARA IGNORAR TEXTO DENTRO DO <script>XX</script>
							script = true;
						tags.push(tag);
					}
				}
			} else {

			}
		}
		if (tags.tamanho() != 0) {
			boolean plural = tags.tamanho() > 1;
			StringBuilder builder = new StringBuilder();
			builder.append(
					"Falta" + (plural ? "m" : "") + " a" + (plural ? "s" : "") + " tag" + (plural ? "s" : "") + " : ");
			while (tags.tamanho() != 0) {
				String tag = tags.pop();
				builder.append(newLine + "</" + tag + ">");
			}
			throw new TagException(builder.toString());
		}

	}

	private void closeProgram() {
		frame.setVisible(false); // you can't see me!
		frame.dispose(); // Destroy the JFrame object
	}
}
