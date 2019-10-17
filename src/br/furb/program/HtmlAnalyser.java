package br.furb.program;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import br.furb.model.Tag;
import br.furb.model.utils.PilhaLista;
import br.furb.tablemodel.TableModelTag;

public class HtmlAnalyser {

	private static String RG_TAG_SINGLETONS = "<((meta)|(base)|(br)|(col)|(command)|(embed)|(hr)|(img)|(input)|(link)|(param)|(source)|(!DOCTYPE))(\\s*([^>])*)*>";
	private static String VALID_TAG = "<(([/]?\\w+(\\s*([^>])*)*)|((!DOCTYPE)(\\s*(\\w)*)*))>";

	private JFrame frame;
	private JTextField txtArq;
	private JTextArea txtMsg;
	private JTable table;
	private String file;
	private TableModelTag tableModel = new TableModelTag();

	PilhaLista<String> tags = new PilhaLista<>();

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
		while ((test = br.readLine()) != null) {
			checkLine(test);
		}
		br.close();
		long timefin = new Date().getTime();
		txtMsg.append("\nTime: " + (timefin - timeini));
	}

	private void checkLine(String test) {
		StringBuilder builder = new StringBuilder();
		System.out.println("="+test+"=");
		test = test.replaceAll("<", "|<");
		System.out.println("=="+test+"==");
		test = test.replaceAll(">", ">;");
		System.out.println("==="+test+"===");
		test = test.replaceAll(";\\|", ";");
		System.out.println("===="+test+"====");
		test = test.replaceAll("\\|", ";");
		System.out.println("====="+test+"=====");
		String[] resu = test.split(";");
		System.out.println(resu.length);
		String erro = null;

		for (int i = 0; i < resu.length; i++) {
			String aux = resu[i].toLowerCase();
			System.out.println(aux);
			if (!aux.matches(VALID_TAG))
				continue;
			System.out.println(aux);
			if (checkTagSingleton(aux)) {
				// AQUI É TAG SINGLETON (SEM TAG FINAL)
				String tag = getNameTag(aux);
				// AQUI ESTÁ SOMENTE O NOME DA TAG SINGLETON
				// PRECISA APENAS GUARDAR E CONTABILIZAR
				tableModel.addQuantidade(tag);
				continue;
			}
			// DAQUI PRA FRENTE É TAG NORMAL
			String tag = getNameTag(aux);
			// AQUI ESTÁ SOMENTE O NOME DA TAG DUPLA (inicial e final)
			if (!tag.startsWith("/")) {
				tags.push(tag);
				System.out.println("tamanho: "+tags.tamanho());
				continue;
			}
			// PRECISA VERIFICAR SE ELA FECHA CORRETAMENTE E CONTABILIZAR
			// CASO NÃO FECHAR CORRETAMENTE RETORNA SEU RESPECTIVO ERRO
			if (tags.tamanho() == 0) {
				builder.append("final");
				break;
			}
			if (tags.peek().equals(tag.substring(1))) {
				tags.pop();
				tableModel.addQuantidade(tag.substring(1));
				continue;
			} else {
				erro = "Erro: Não é o final certo, deve ter a tag </" + tags.peek() + ">";
				break;
			}
		}
		if (tags.tamanho() != 0 && erro == null) {
			erro = "Erro: Não foi achado as tags:\n";
			while (!tags.estaVazia()) {
				erro += "</" + tags.pop() + ">\n";
			}

		}
		if (erro != null)
			builder.append(erro);
		else
			builder.append("O arquivo está bem formatado");

		txtMsg.setText(builder.toString());
	}

	private static String getNameTag(String string) {
		String[] atr = string.split("\\s");
		String tag = atr[0].substring(1);
		return tag = tag.replaceAll(">", "").toLowerCase();
	}

	private static boolean checkTagSingleton(String s) {
		return s.matches(RG_TAG_SINGLETONS);
	}
}
