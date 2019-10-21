package br.furb.model.utils;

public class PilhaLista<T> implements Pilha<T>{

	private ListaEncadeada<T> lista;

	public PilhaLista() {
		lista = new ListaEncadeada<>();
	}
	
	@Override
	public void push(T info) {
		lista.inserir(info);
	}

	@Override
	public T pop() {
		if (estaVazia()) throw new PilhaVaziaException("A pilha está vazia!");
		T info = lista.getPrimeiro().getInfo();
		lista.retirar(info);
		return info;
	}

	@Override
	public T peek() {
		return lista.getPrimeiro().getInfo();
	}

	@Override
	public boolean estaVazia() {
		return lista.estaVazia();
	}

	@Override
	public void liberar() {
		lista = new ListaEncadeada<>();
	}
	
	public int tamanho() {
		return lista.obterComprimento();
	}

	@Override
	public String toString() {
		return lista.toString();
	}
	
}
