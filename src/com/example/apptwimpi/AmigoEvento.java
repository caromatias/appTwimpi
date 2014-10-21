package com.example.apptwimpi;

import android.graphics.drawable.Drawable;

public class AmigoEvento {
	protected Drawable foto;
	protected String nombre;
	protected long id;

	public AmigoEvento(Drawable foto, String nombre) {
		super();
		this.foto = foto;
		this.nombre = nombre;
	}

	public AmigoEvento(Drawable foto, String nombre, long id) {
		super();
		this.foto = foto;
		this.nombre = nombre;
		this.id = id;
	}

	public Drawable getFoto() {
		return foto;
	}

	public void setFoto(Drawable foto) {
		this.foto = foto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
