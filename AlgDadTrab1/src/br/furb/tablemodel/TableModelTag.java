package br.furb.tablemodel;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import br.furb.model.Tag;

public class TableModelTag extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	// TODO NESSESITA DA LISTA ENCADEADA
	ArrayList<Tag> tags = new ArrayList<>();
	String colunas[] = {"Tag", "Número de ocorrências"};
	
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
		return tags.size();
	}

	@Override
	public Object getValueAt(int linha, int coluna) {
		// TODO PEGAR INFORMAÇÕES DA LISTA
		switch (coluna) {
		case 0:
			return tags.get(linha).getNome();
		case 1:
			return tags.get(linha).getQuantidade();
		default:
			return tags.get(linha);
		}
	}
	
	public void addRow(Tag info) {
		// TODO ADICIONAR INFORMAÇÕES NA LISTA
		tags.add(info);
		fireTableDataChanged();
	}
	
	public void addQuantidade(String a){
		for (int i = 0; i < tags.size(); i++) {
			Tag aux = tags.get(i);
			if (aux.getNome().equals(a)) {
				aux.setQuantidade(aux.getQuantidade() + 1);
				fireTableDataChanged();
				return;
			}
		}
		addRow(new Tag(a, 1));
	}
	
	public void rmvQuantidade(String a){
		for (int i = 0; i < tags.size(); i++) {
			Tag aux = tags.get(i);
			if (aux.getNome().equals(a)) {
				if(aux.getQuantidade() == 1) {
					tags.remove(aux);
					return;
				}
				aux.setQuantidade(aux.getQuantidade() - 1);
				return;
			}
		}
		fireTableDataChanged();
	}

	public void resetList() {
		tags.clear();
		fireTableDataChanged();
	}
}
