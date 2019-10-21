package br.furb.tablemodel;

import javax.swing.table.AbstractTableModel;

import br.furb.model.Tag;
import br.furb.model.utils.ListaEncadeada;

public class TableModelTag extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	// TODO NESSESITA DA LISTA ENCADEADA
	private ListaEncadeada<Tag> tags = new ListaEncadeada<>();
	private String colunas[] = {"Tag", "Número de ocorrências"};
	
	@Override
	public String getColumnName(int column) {
		return colunas[column];
	}
	
	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public int getRowCount() {
		// TODO COLOCAR TAMANHO DA LISTA
		return tags.obterComprimento();
	}

	@Override
	public Object getValueAt(int linha, int coluna) {
		// TODO PEGAR INFORMAÇÕES DA LISTA
		switch (coluna) {
		case 0:
			return tags.obterInfo(linha).getNome();
		case 1:
			return tags.obterInfo(linha).getQuantidade();
		default:
			return tags.obterInfo(linha);
		}
	}
	
	public void addRow(Tag info) {
		// TODO ADICIONAR INFORMAÇÕES NA LISTA
		tags.inserir(info);
		fireTableDataChanged();
	}
	
	public void addQuantidade(String a){
		for (int i = 0; i < tags.obterComprimento(); i++) {
			Tag aux = tags.obterInfo(i);
			if (aux.getNome().equals(a)) {
				aux.setQuantidade(aux.getQuantidade() + 1);
				fireTableDataChanged();
				return;
			}
		}
		addRow(new Tag(a, 1));
	}
	
	public void rmvQuantidade(String a){
		for (int i = 0; i < tags.obterComprimento(); i++) {
			Tag aux = tags.obterInfo(i);
			if (aux.getNome().equals(a)) {
				if(aux.getQuantidade() == 1) {
					tags.retirar(aux);
					return;
				}
				aux.setQuantidade(aux.getQuantidade() - 1);
				return;
			}
		}
		fireTableDataChanged();
	}

	public void resetList() {
		tags.liberar();
		fireTableDataChanged();
	}
}
