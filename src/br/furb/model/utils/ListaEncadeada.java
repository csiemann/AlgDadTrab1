package br.furb.model.utils;

public class ListaEncadeada<T> {

	private NoListaE<T> primeiro;
	
	private int comp;

	public ListaEncadeada() {
	}

	public NoListaE<T> getPrimeiro() {
		return primeiro;
	}

	public void inserir(T obj) {
		NoListaE<T> novo = new NoListaE<>();
		novo.setInfo(obj);
		novo.setProximo(primeiro);
		comp += 1;
		this.primeiro = novo;
	}

	public void exibir() {
		NoListaE<T> p = primeiro;
		while (p != null) {
			p = p.getProximo();
		}
	}

	public boolean estaVazia() {
		return primeiro == null;
	}

	public NoListaE<T> buscar(T info) {
		NoListaE<T> p = primeiro;
		while (p != null) {
			if (p.getInfo().equals(info))
				return p;
			p = p.getProximo();
		}
		return null;
	}
	
	public void retirar(T info) {
		NoListaE<T> anterior = null;
		NoListaE<T> p = primeiro;
		
		while (p != null && !p.getInfo().equals(info)) {
			anterior = p;
			p = p.getProximo();
		}
		
		if (p!= null) {
			if (anterior==null) {
				primeiro = p.getProximo();
			} else {
				anterior.setProximo(p.getProximo());
			}
			comp--;
		}
	}
	
	public int obterComprimento() {
		return comp;
	}
	
	public NoListaE<T> obterNo(int index) {
		int pos = 0;
		NoListaE<T> p = primeiro;
		while (p != null && index >= 0) {
			if(pos == index)
				return p;
			pos++;
			p = p.getProximo();
		}
		throw new IndexOutOfBoundsException("Não há essa posição nesta lista!");
	}
	
	public T obterInfo(int index){
		return obterNo(index).getInfo();
	}
	
	@Override
	public String toString() {
		NoListaE<T> p = primeiro;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < obterComprimento(); i++) {
			
			builder.append(p.getInfo()+(i==obterComprimento()-1?"":", "));
			p = p.getProximo();
		}
		return builder.toString();
	}

	public void liberar() {
 		while(comp != 0) {
 			retirar(obterInfo(0));
 		}
	}
	
}