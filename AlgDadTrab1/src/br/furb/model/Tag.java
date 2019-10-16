package br.furb.model;

public class Tag {
	String nome;
	int quantidade;

	public Tag(String nome, int quantidade) {
		this.nome = nome;
		this.quantidade = quantidade;
	}

	public String getNome() {
		return nome;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
}
