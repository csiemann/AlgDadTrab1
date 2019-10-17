package br.furb.model.utils;

public class NoListaE<T> {
	private NoListaE<T> proximo;
	private T info;

	public NoListaE<T> getProximo() {
		return proximo;
	}
	public void setProximo(NoListaE<T> proximo) {
		this.proximo = proximo;
	}
	public T getInfo() {
		return info;
	}
	public void setInfo(T info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return info.toString();
	}
}
